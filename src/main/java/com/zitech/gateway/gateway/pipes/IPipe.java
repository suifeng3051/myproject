package com.zitech.gateway.gateway.pipes;


import com.zitech.gateway.gateway.model.RequestEvent;

public interface IPipe {

    /*
        process request event
     */
    void onEvent(RequestEvent event) throws Exception;

}
