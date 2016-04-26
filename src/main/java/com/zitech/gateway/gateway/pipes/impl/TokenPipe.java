package com.zitech.gateway.gateway.pipes.impl;

import com.zitech.gateway.AppConfig;
import com.zitech.gateway.apiconfig.model.CarmenParamMapping;
import com.zitech.gateway.exception.BaseException;
import com.zitech.gateway.gateway.cache.CarmenParamMappingCache;
import com.zitech.gateway.gateway.cache.ClientCache;
import com.zitech.gateway.gateway.exception.PipelineException;
import com.zitech.gateway.gateway.excutor.Pipeline;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.gateway.model.RequestState;
import com.zitech.gateway.gateway.model.ValidateType;
import com.zitech.gateway.gateway.services.ContextService;
import com.zitech.gateway.gateway.services.TokenService;
import com.zitech.gateway.oauth.model.AccessToken;
import com.zitech.gateway.oauth.model.Client;
import com.zitech.gateway.utils.SpringContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class TokenPipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(TokenPipe.class);

    private TokenService tokenService;
    private AppConfig appConfig;

    private ClientCache clientsCache;
    private CarmenParamMappingCache paramMappingCache;

    public TokenPipe() {
        tokenService = SpringContext.getBean(TokenService.class);
        clientsCache = SpringContext.getBean(ClientCache.class);
        paramMappingCache = SpringContext.getBean(CarmenParamMappingCache.class);
        appConfig = SpringContext.getBean(AppConfig.class);
    }

    @Override
    public void onEvent(RequestEvent event) {
        try {
            logger.debug("begin of token validation: {}", event);

            // must be token type
            if (event.getValidateType() != ValidateType.TOKEN)
                return;

            // check access token
            AccessToken openOauthAccessTokens = tokenService.validateAccessToken(event);

            // get client
            Client openOauthClients = clientsCache.get(event.getId(), openOauthAccessTokens.getClientId());

            if (openOauthClients == null) {
                throw new PipelineException(5017, "client is null");
            }

            // used for frequency control
            event.setClientId(openOauthClients.getId());
            event.setClientName(openOauthClients.getClientId());

            // get param mappings
            List<CarmenParamMapping> paramMappings = paramMappingCache.get(event.getId(), event.getNamespace(),
                    event.getMethod(), event.getVersion(), appConfig.env);

            // set context parameter from access token
            ContextService.prepareContext(openOauthAccessTokens, event, paramMappings);

            // set context parameter from OpenOauthClients
            ContextService.prepareContext(openOauthClients, event, paramMappings);

        } catch (BaseException e) {
            event.setException(e);
            logger.info("exception happened when validating token: {}", event.getId(), e);
        } catch (Exception e) {
            event.setException(e);
            logger.error("exception happened when validating token: {}", event.getId(), e);
        } finally {
            logger.info("complete token validation: {}", event.getId());
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
