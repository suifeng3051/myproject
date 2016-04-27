package com.zitech.gateway.gateway.pipes;


import com.zitech.gateway.gateway.model.RequestEvent;

import java.net.URISyntaxException;

public interface IPipe {

    /*
        process request event
     */
    void onEvent(RequestEvent event) throws Exception;

}
