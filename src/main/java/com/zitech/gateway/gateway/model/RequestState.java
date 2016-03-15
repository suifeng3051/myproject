package com.zitech.gateway.gateway.model;


public enum RequestState {
    NONE,
    PARSE,
    TOKEN,
    SIGN,
    DISPATCH,
    ASYNC,
    RESULT,
    COMPLETE,
    ERROR,
    POST
}
