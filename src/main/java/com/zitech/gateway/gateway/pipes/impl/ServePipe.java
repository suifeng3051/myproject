package com.zitech.gateway.gateway.pipes.impl;

import com.zitech.gateway.apiconfig.model.Serve;
import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.gateway.cache.ServeCache;
import com.zitech.gateway.gateway.exception.CallException;
import com.zitech.gateway.gateway.excutor.HttpAsyncClient;
import com.zitech.gateway.gateway.excutor.Pipeline;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.gateway.model.RequestType;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;


@Service
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

        Serve serve = serveCache.get(event.getId(), event.getApi().getId());
        String url = serve.getUrl();
        if (!url.endsWith("/")) {
            url = url + "/";
        }

        String service = StringUtils.replace(serve.getService(), ".", "/");
        String requestUrl = url + service + "/" + serve.getMethod();

        if (event.getRequestType() == RequestType.POST) {
            HttpPost httpPost = new HttpPost(requestUrl);
            StringEntity entity = new StringEntity(event.getBody(), "utf-8");
            entity.setContentType("application/json;charset=utf-8");
            httpPost.setEntity(entity);
            HttpAsyncClient.client.execute(httpPost, new HttpAsyncCallback(event));
        } else if (event.getRequestType() == RequestType.GET) {
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(requestUrl));
            HttpAsyncClient.client.execute(httpGet, new HttpAsyncCallback(event));
        }
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
                String resultStr = EntityUtils.toString(result.getEntity());
                event.setResultStr(resultStr);
                Pipeline.getInstance().process(event);
            } catch (IOException e) {
                event.setException(e);
                Pipeline.getInstance().process(event);
            }
        }

        @Override
        public void failed(Exception ex) {
            requestNum.decrementAndGet();
            logger.error("http client call error: {}", event.getId(), ex);
            this.event.getTicTac().tac(Constants.ST_CALL);
            event.setException(new CallException(5016, "async http client exception :" + ex.toString()));
            Pipeline.getInstance().process(event);
        }

        @Override
        public void cancelled() {
            requestNum.decrementAndGet();
            logger.error("http client call cancelled: {}", event.getId());
            this.event.getTicTac().tac(Constants.ST_CALL);
            event.setException(new CallException(5015, "request is cancelled"));
            Pipeline.getInstance().process(event);
        }
    }

}
