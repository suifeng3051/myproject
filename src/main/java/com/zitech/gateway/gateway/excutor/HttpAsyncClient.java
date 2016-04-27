package com.zitech.gateway.gateway.excutor;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;

public class HttpAsyncClient {

    public static CloseableHttpAsyncClient client = null;

    public HttpAsyncClient() throws IOReactorException {
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setIoThreadCount(getCpuCount() * 2).build();
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        PoolingNHttpClientConnectionManager cm = new PoolingNHttpClientConnectionManager(ioReactor);
        cm.setDefaultMaxPerRoute(1000);
        cm.setMaxTotal(1000);

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(60000)
                .setConnectTimeout(60000).build();

        client = HttpAsyncClients.custom().setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .build();

        client.start();
    }

    public static int getCpuCount() {
        return Runtime.getRuntime().availableProcessors();
    }
}
