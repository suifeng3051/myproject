package com.zitech.gateway;

import com.zitech.gateway.console.InstanceMonitor;
import com.zitech.gateway.gateway.excutor.Pipeline;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


public class Initialization implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (event.getApplicationContext().getParent() == null) {
            Pipeline.getInstance().start();
            InstanceMonitor.getInstance().start();
        }
    }

}
