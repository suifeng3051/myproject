package com.zitech.gateway.gateway.pipes.impl;

import com.zitech.gateway.AppConfig;
import com.zitech.gateway.apiconfig.model.CarmenParamMapping;
import com.zitech.gateway.exception.CarmenException;
import com.zitech.gateway.gateway.cache.CarmenParamMappingCache;
import com.zitech.gateway.gateway.excutor.Pipeline;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.gateway.model.RequestState;
import com.zitech.gateway.gateway.model.ValidateType;
import com.zitech.gateway.gateway.services.ContextService;
import com.zitech.gateway.gateway.services.SignService;
import com.zitech.gateway.oauth.model.OpenOauthClients;
import com.zitech.gateway.utils.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class SignPipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(SignPipe.class);

    private SignService signService;
    private CarmenParamMappingCache paramMappingCache;
    private AppConfig appConfig;

    public SignPipe() {
        signService = SpringContext.getBean(SignService.class);
        paramMappingCache = SpringContext.getBean(CarmenParamMappingCache.class);
        appConfig = SpringContext.getBean(AppConfig.class);
    }

    @Override
    public void onEvent(RequestEvent event) {
        try {
            logger.debug("begin of sign validation: {}", event);

            // must be sign type
            if (event.getValidateType() != ValidateType.SIGN)
                return;

            // validate sign
            OpenOauthClients clients = signService.validateSign(event);

            // get param mappings
            List<CarmenParamMapping> paramMappings = paramMappingCache.get(event.getId(), event.getNamespace(),
                    event.getMethod(), event.getVersion(), appConfig.env);

            // set context parameter from OpenApp
            ContextService.prepareContext(clients, event, paramMappings);

        } catch (CarmenException e) {
            event.setException(e);
            logger.info("exception happened when validating sign: {}", event.getId(), e);
        } catch (Exception e) {
            event.setException(e);
            logger.error("exception happened when validating sign: {}", event.getId(), e);
        } finally {
            logger.info("complete sign validation: {}", event.getId());
            onNext(event);
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    protected void onNext(RequestEvent event) {
        if (event.getException() != null)
            event.setState(RequestState.ERROR);
        else
            event.setState(RequestState.DISPATCH);

        // go on
        Pipeline.getInstance().process(event);
    }
}
