package com.zitech.gateway.gateway.pipes.impl;

import com.zitech.gateway.apiconfig.model.Serve;
import com.zitech.gateway.common.RequestType;
import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.gateway.cache.ServeCache;
import com.zitech.gateway.gateway.exception.ServeException;
import com.zitech.gateway.gateway.excutor.HttpAsyncClient;
import com.zitech.gateway.gateway.excutor.Pipeline;
import com.zitech.gateway.gateway.model.RequestEvent;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Pipe(Group = 'A', Order = 5)
public class ServePipe extends AbstractPipe {

    private final static Logger logger = LoggerFactory.getLogger(ServePipe.class);

    private static AtomicInteger requestNum = new AtomicInteger(0);

    @Autowired
    private ServeCache serveCache;

    public static AtomicInteger getRequestNum() {
        return requestNum;
    }

    @Override
    public void onEvent(RequestEvent event) throws Exception {

        Serve serve = serveCache.get(event.getApi().getId());
        String url = serve.getUrl();
        if (!url.endsWith("/")) {
            url = url + "/";
        }

        String service = StringUtils.replace(serve.getService(), ".", "/");
        String requestUrl = url + service + "/" + serve.getMethod();

        List<Header> headerList = createHeaders(event, serve);

        String contentType = event.getRequest().getContentType();
        if (event.getRequestType() == RequestType.POST) {
            HttpPost httpPost = new HttpPost(requestUrl);
            StringEntity entity = new StringEntity(event.getBody(), "utf-8");
            entity.setContentType(contentType);
            httpPost.setEntity(entity);
            headerList.forEach(httpPost::setHeader);
            httpPost.setHeader("Content-Type", contentType);
            HttpAsyncClient.client.execute(httpPost, new HttpAsyncCallback(event));
        } else if (event.getRequestType() == RequestType.GET) {
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(requestUrl));
            headerList.forEach(httpGet::setHeader);
            httpGet.setHeader("Content-Type", contentType);
            HttpAsyncClient.client.execute(httpGet, new HttpAsyncCallback(event));
        }
    }

    private List<Header> createHeaders(RequestEvent event, Serve serve) {
        List<Header> headerList = new ArrayList<>();
        String[] names = serve.getInnerParams().split(" ");
        Map<String, String> contextMap = event.getContextMap();
        for (String name : names) {
            Header header = new BasicHeader(name, contextMap.get(name));
            headerList.add(header);
        }
        return headerList;
    }

    private class HttpAsyncCallback implements FutureCallback<HttpResponse> {
        private RequestEvent event;

        public HttpAsyncCallback(RequestEvent event) {
            requestNum.incrementAndGet();
            this.event = event;
            this.event.getTicTac().tic(Constants.ST_CALL);
        }

        @Override
        public void completed(HttpResponse result) {
            requestNum.decrementAndGet();
            this.event.getTicTac().tac(Constants.ST_CALL);
            try {
                int code = result.getStatusLine().getStatusCode();
                if (code >= HttpStatus.SC_BAD_REQUEST && code < HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                    event.setException(new ServeException(5210, "serve not found"));
                } else if (code >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                    event.setException(new ServeException(5211, "serve internal error"));
                } else {
                    HttpEntity entity = result.getEntity();
                    if (entity != null) {
                        String resultStr = EntityUtils.toString(entity);
                        event.setResultStr(resultStr);
                    } else {
                        event.setException(new ServeException(5216, "empty response from serve"));
                        Pipeline.getInstance().process(event);
                    }
                }
                Pipeline.getInstance().process(event);
            } catch (Exception e) {
                event.setException(e);
                Pipeline.getInstance().process(event);
            }
        }

        @Override
        public void failed(Exception ex) {
            logger.error("http client call error: {}", event.uuid, ex);
            requestNum.decrementAndGet();
            this.event.getTicTac().tac(Constants.ST_CALL);
            event.setException(new ServeException(5016, "async http client exception :" + ex.toString()));
            Pipeline.getInstance().process(event);
        }

        @Override
        public void cancelled() {
            logger.error("http client call cancelled: {}", event.uuid);
            requestNum.decrementAndGet();
            this.event.getTicTac().tac(Constants.ST_CALL);
            event.setException(new ServeException(5015, "request is cancelled"));
            Pipeline.getInstance().process(event);
        }
    }

}
