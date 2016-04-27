package com.zitech.gateway.gateway.entry;

import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.gateway.excutor.Pipeline;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.gateway.model.RequestState;
import com.zitech.gateway.gateway.model.ValidateType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;


@Controller
public class AcceptorController {

    private static final Logger logger = LoggerFactory.getLogger(AcceptorController.class);

    /**
     * the token entry for gateway
     */
    @RequestMapping(value = "/gw/oauthentry/{namespace}/{version}/{method}",
            consumes = "application/json",
            produces = "application/json;charset=utf-8",
            method = {RequestMethod.GET, RequestMethod.POST})
    public DeferredResult<Object> token(HttpServletRequest request,
                                        @PathVariable String namespace,
                                        @PathVariable Integer version,
                                        @PathVariable String method) {
        DeferredResult<Object> deferredResult = new DeferredResult<>();

        RequestEvent event = new RequestEvent(deferredResult, request);
        event.getTicTac().tic(Constants.ST_ALL);

        if (StringUtils.isEmpty(namespace) || StringUtils.isEmpty(method)) {
            ResponseEntity<String> responseEntity = new ResponseEntity<String>(
                    String.format(Constants.ERROR_RESPONSE, 5001, "namespace or method is null"),
                    HttpStatus.valueOf(200));
            deferredResult.setResult(responseEntity);
            return deferredResult;
        }

        if (StringUtils.isEmpty(request.getParameter(Constants.ACCESS_TOKEN))) {
            ResponseEntity<String> responseEntity = new ResponseEntity<String>(
                    String.format(Constants.ERROR_RESPONSE, 5125, "no access token"),
                    HttpStatus.valueOf(200));
            deferredResult.setResult(responseEntity);
            return deferredResult;
        }

        event.setNamespace(namespace);
        event.setMethod(method);
        event.setVersion(version);
        Pipeline.getInstance().process(event);
        logger.debug("a request has been putted to queue: " + event);
        return deferredResult;
    }
}
