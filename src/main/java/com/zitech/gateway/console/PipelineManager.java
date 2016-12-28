package com.zitech.gateway.console;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zitech.gateway.gateway.excutor.Pipeline;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PipelineManager {

    private static Logger logger = LoggerFactory.getLogger(PipelineManager.class);

    private static PipelineManager pipelineManager;

    private ZkFramework framework = ZkFramework.getInstance();

    private Pipeline pipeline;

    private String threadNode;
    private String threadAllNode;
    private String threadPoolNode;

    private String requestNode;
    private String requestAllNode;
    private String requestHttpAsyncNode;

    private String iNode;

    private int threadAllCount;
    private int threadPoolCount;

    private int requestAllCount;
    private int requestHttpAsyncCount;


    private PipelineManager(String instanceNode) {
        this.threadNode = instanceNode + Constants.THREAD_NODE;
        this.threadAllNode = this.threadNode + Constants.THREAD_ALL_NODE;
        this.threadPoolNode = this.threadNode + Constants.THREAD_POOL_NODE;

        this.requestNode = instanceNode + Constants.REQUEST_NODE;
        this.requestAllNode = this.requestNode + Constants.REQUEST_ALL_NODE;
        this.requestHttpAsyncNode = this.requestNode + Constants.REQUEST_HTTP_ASYNC;

        this.iNode = instanceNode + "/i";
        this.createNode(iNode);

        this.createNodes();

        pipeline = Pipeline.getInstance();
    }

    public static PipelineManager getInstance(String instanceNode) {
        if (pipelineManager == null)
            pipelineManager = new PipelineManager(instanceNode);
        return pipelineManager;
    }

    public void start() {
        Timer timer = new Timer();
        CuratorFramework client = framework.getClient();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {

                    int tmp = 0;
                    tmp = Thread.activeCount();
                    if (tmp != threadAllCount) {
                        client.setData()
                                .inBackground()
                                .forPath(threadAllNode, String.valueOf(tmp).getBytes());
                        threadAllCount = tmp;
                    }

                    tmp = pipeline.getThreadCount();
                    if (tmp != threadPoolCount) {
                        client.setData()
                                .inBackground()
                                .forPath(threadPoolNode, String.valueOf(tmp).getBytes());
                        threadPoolCount = tmp;
                    }

                    tmp = pipeline.getTaskCount();
                    if (tmp != requestAllCount) {
                        client.setData()
                                .inBackground()
                                .forPath(requestAllNode, String.valueOf(tmp).getBytes());
                        requestAllCount = tmp;
                    }

                    tmp = pipeline.getHttpAsyncTask();
                    if (tmp != requestHttpAsyncCount) {
                        client.setData()
                                .inBackground()
                                .forPath(requestHttpAsyncNode, String.valueOf(tmp).getBytes());
                        requestHttpAsyncCount = tmp;
                    }

                } catch (Exception e) {
                    logger.error("update pipe monitor data error", e);
                }
            }
        }, 0, 10 * 1000);
    }

    public String readNodes() throws Exception {
        CuratorFramework client = framework.getClient();
        JSONArray jsonArray = new JSONArray();

        JSONObject thread = new JSONObject();
        jsonArray.add(thread);
        List<String> pathList = client
                .getChildren()
                .forPath(threadNode);
        for (String path : pathList) {
            byte[] bytes = client.getData()
                    .forPath(path);
            String name = path.substring(path.lastIndexOf('/'));
            thread.put(name, new String(bytes));
        }

        JSONObject request = new JSONObject();
        jsonArray.add(request);
        pathList = client.getChildren()
                .forPath(requestNode);
        for (String path : pathList) {
            byte[] bytes = client.getData()
                    .forPath(path);
            String name = path.substring(path.lastIndexOf('/'));
            thread.put(name, new String(bytes));
        }

        return jsonArray.toJSONString();
    }

    public void createNodes() {
        this.createNode(threadAllNode, "0");
        this.createNode(threadPoolNode, "0");

        this.createNode(requestAllNode, "0");
        this.createNode(requestHttpAsyncNode, "0");
    }

    public void createNode(String node) {
        try {
            Stat stat = framework.getClient()
                    .checkExists()
                    .forPath(node);

            if (stat == null) {
                framework.getClient()
                        .create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(node);
            }
        } catch (Exception e) {
            logger.error("create node error", e);
        }
    }

    public void createNode(String node, String data) {
        try {
            Stat stat = framework.getClient()
                    .checkExists()
                    .forPath(node);
            if (stat == null) {
                framework.getClient()
                        .create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(node, data.getBytes());
            }
        } catch (Exception e) {
            logger.error("create node error", e);
        }
    }

}
