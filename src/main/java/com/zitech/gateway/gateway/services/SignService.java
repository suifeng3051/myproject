package com.zitech.gateway.gateway.services;

import com.zitech.gateway.AppConfig;
import com.zitech.gateway.apiconfig.model.CarmenApi;
import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.gateway.cache.CarmenApiCache;
import com.zitech.gateway.gateway.cache.OpenOauthClientsCache;
import com.zitech.gateway.gateway.exception.SignValidateException;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.oauth.model.OpenOauthClients;
import com.zitech.gateway.oauth.oauthex.OAuthConstants;
import com.zitech.gateway.utils.AppUtils;
import com.zitech.gateway.utils.DateUtil;

import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

//import com.zitech.gateway.apiconfig.model.OpenResource;
//import com.zitech.gateway.gateway.cache.OpenResourceCache;

@Service
public class SignService {

    private static final Logger logger = LoggerFactory.getLogger(SignService.class);

    @Autowired
    private OpenOauthClientsCache clientsCache;

//    @Autowired
//    private OpenResourceCache resourceCache;

    @Autowired
    private CarmenApiCache carmenApiCache;

    @Autowired
    private AppConfig appConfig;

    /**
     * validate sign, please see http://gateway.zitech.com/doc/api/protocol
     */
    public OpenOauthClients validateSign(RequestEvent event) throws SignValidateException, ExecutionException {

        String clientId = event.getValue(Constants.CLIENTID);
        String sign = event.getSign();
        Map<String, String> params = event.getSignParams();
        String resource = event.getNamespace() + "." + event.getMethod();

        if (StringUtils.isEmpty(sign)) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_SIGN,
                    OAuthConstants.OAuthDescription.INVALID_NO_SIGN);
        }

        if (StringUtils.isEmpty(params.get("timestamp"))) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_TIME_STAMP,
                    OAuthConstants.OAuthDescription.INVALID_TIME_STAMP);
        }

        OpenOauthClients clients = clientsCache.get(event.getId(), clientId);
        if (clients == null) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_CLIENT,
                    OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION);
        }

        /**
         * check time stamp
         */
        String timeStamp = params.get("timestamp");
        Date time = null;
        try {
            time = DateUtil.string2Date(timeStamp, "yyyy-MM-dd HH:mm:ss");
        } catch (RuntimeException e) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_TIME_STAMP,
                    OAuthConstants.OAuthDescription.INVALID_TIME_STAMP);
        }

        if (time == null) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_TIME_STAMP,
                    OAuthConstants.OAuthDescription.INVALID_TIME_STAMP);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.MINUTE, 10);

        if (Calendar.getInstance().after(calendar)) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_TIME_STAMP,
                    OAuthConstants.OAuthDescription.INVALID_TIME_STAMP);
        }

        Set<String> keys = params.keySet();
        Set<String> sortedKeys = new TreeSet<>();
        sortedKeys.addAll(keys);

        /**
         * calculate sign and check the sign from parameters
         */
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(clients.getClientSecret());
        for (String s : sortedKeys) {
            stringBuilder.append(s);
            stringBuilder.append(params.get(s));
        }
        stringBuilder.append(clients.getClientSecret());
        String paramsString = stringBuilder.toString();
        String encodedSign = AppUtils.MD5(paramsString);

        if (encodedSign == null || !encodedSign.equalsIgnoreCase(sign)) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_SIGN_FOR_VALID,
                    OAuthConstants.OAuthDescription.INVALID_SIGN_FOR_VALID);
        }

        /**
         * check api resource
         */
//        OpenResource openResource = resourceCache.get(event.getId(), resource);
//        if (openResource == null) {
//            throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_RESOURCE,
//                    OAuthConstants.OAuthDescription.INVALID_RESOURCE);
//        }
        CarmenApi carmenApi = carmenApiCache.get(event.getId(), event.getNamespace(), event.getMethod(),
                event.getVersion(), appConfig.env);
        //OpenResource openResource = resourceCache.get(event.getId(), resource);
        if (carmenApi == null) {
            throw new SignValidateException(OAuthConstants.OAuthResponse.NO_API,
                    OAuthConstants.OAuthDescription.INVALID_API);
        }

        /**
         * check scope info
         */
        Set<String> scopes = OAuthUtils.decodeScopes(clients.getDefaultScope());
        for (String scope : scopes) {
            if (scope.equals("all") || scope.equals(carmenApi.getApiGroup())) {
                return clients;
            }
        }

        // there may be some better ways, but...
        throw new SignValidateException(OAuthConstants.OAuthResponse.INVALID_SIGN,
                OAuthConstants.OAuthDescription.INVALID_RESOURCE);
    }

}