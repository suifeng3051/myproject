package com.zitech.gateway.apiconfig.model;

/**
 * Created by hy on 16-3-25.
 */
public class CarmenOauthClient {
    private Long id;
    private String client_id;
    private String client_secret;
    private
    `client_id` varchar(100) DEFAULT NULL,
    `client_secret` varchar(100) DEFAULT NULL,
    `client_name` varchar(100) DEFAULT NULL,
    `redirect_uri` varchar(1000) DEFAULT NULL,
    `grant_types` varchar(100) DEFAULT NULL,
    `scope` varchar(1000) DEFAULT NULL,
    `user_id` varchar(100) DEFAULT NULL,
    `client_num` int(1) DEFAULT '1',
            `client_source` varchar(100) DEFAULT NULL,
    `default_scope` varchar(1000) DEFAULT NULL,
    `client_type` varchar(100) DEFAULT NULL,
}
