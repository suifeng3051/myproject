package com.zitech.gateway.gateway.pipes.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
            HttpAsyncClient.client.execute(httpPost, new HttpAsyncCallback(event, requestUrl));
        } else if (event.getRequestType() == RequestType.GET) {
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(requestUrl));
            headerList.forEach(httpGet::setHeader);
            httpGet.setHeader("Content-Type", contentType);
            HttpAsyncClient.client.execute(httpGet, new HttpAsyncCallback(event, requestUrl));
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
        private String requestUrl;

        public HttpAsyncCallback(RequestEvent event, String requestUrl) {
            requestNum.incrementAndGet();
            this.event = event;
            this.requestUrl = requestUrl;
            this.event.getTicTac().tic(Constants.ST_CALL);
        }

        @Override
        public void completed(HttpResponse result) {
            requestNum.decrementAndGet();
            this.event.getTicTac().tac(Constants.ST_CALL);
            try {
                int code = result.getStatusLine().getStatusCode();

                String fmtMsg = null;
                Integer errorCode = null;
                if (code >= HttpStatus.SC_BAD_REQUEST && code < HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                    fmtMsg = "serve not found(%s): %s";
                    errorCode = 5210;
                } else if (code >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                    fmtMsg = "internal serve error(%s): %s";
                    errorCode = 5211;
                }

                if(fmtMsg != null) {
                    HttpEntity entity = result.getEntity();
                    JSONObject jsonObject = null;
                    if (entity != null) {
                        String resultStr = EntityUtils.toString(entity);
                        try {
                            jsonObject = JSON.parseObject(resultStr);
                        } catch (Exception ignore) {
                        }
                    }

                    String specialMsg = null;
                    if(jsonObject != null) {
                        specialMsg = jsonObject.getString("data");
                    }

                    String msg;
                    if (specialMsg != null) {
                        msg = String.format(fmtMsg, code, specialMsg);
                    } else {
                        msg = String.format(fmtMsg, code, requestUrl);
                    }
                    event.setException(new ServeException(errorCode, msg));
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
