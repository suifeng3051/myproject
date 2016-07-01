package com.zitech.gateway.oauth.service.impl;

import com.alibaba.fastjson.JSON;
import com.zitech.gateway.AppConfig;
import com.zitech.gateway.cache.RedisOperate;
import com.zitech.gateway.oauth.Constants;
import com.zitech.gateway.oauth.dao.user.AccountDAO;
import com.zitech.gateway.oauth.exception.OAuthException;
import com.zitech.gateway.oauth.model.*;
import com.zitech.gateway.oauth.oauthex.OAuthConstants;
import com.zitech.gateway.oauth.service.OAuthService;
import com.zitech.gateway.utils.AppUtils;
import com.zitech.gateway.utils.SpringContext;
import org.apache.commons.lang.StringUtils;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class OAuthServiceImpl implements OAuthService {

    public static final Logger logger = LoggerFactory
            .getLogger(OAuthServiceImpl.class);

    @Autowired
    private RedisOperate redis;

    @Autowired
    private ClientServiceImpl clientService;

    @Autowired
    private AccessTokenServiceImpl accessTokenService;

    @Autowired
    private RefreshTokenServiceImpl refreshTokenService;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private AccountDAO accountDAO;


    /**
     * get client by client id
     *
     * @param clientId
     * @return
     */
    public Client getClientByClientId(String clientId) {
        String key = String
                .format(Constants.CACHE_CLIENT_KEY_PATTERN, clientId);
        String content = redis.getStringByKey(key);
        if (!StringUtils.isEmpty(content)) {
            return JSON.parseObject(content, Client.class);
        }
        Client clients = clientService
                .getByClientId(clientId);
        if (clients != null)
            redis.set(key, JSON.toJSONString(clients));
        return clients;
    }

    /**
     * clear client from cache
     * @param clientId id
     */
    @Override
    public void clearClientCacheByClientId(String clientId) {
        String key = String
                .format(Constants.CACHE_CLIENT_KEY_PATTERN, clientId);
        redis.del(key);
    }

    /**
     * insert client
     *
     * @param openOauthClients
     * @return
     */
    public int saveClient(Client openOauthClients) {
        String key = String.format(Constants.CACHE_CLIENT_KEY_PATTERN,
                openOauthClients.getClientId());
        redis.del(key);
        return clientService.save(openOauthClients);
    }

    /**
     * insert access token to db
     *
     * @param accessToken
     * @param oAuthAuthzParameters
     * @return
     */
    public int saveAccessToken(String accessToken,
                               OAuthAuthzParameters oAuthAuthzParameters) {
        AccessToken openOauthAccessTokens = new AccessToken();
        openOauthAccessTokens.setAccessToken(accessToken);
        openOauthAccessTokens.setClientId(oAuthAuthzParameters.getClientId());
        openOauthAccessTokens.setUserId(oAuthAuthzParameters.getUserId()); // default
        // is
        // 0

        String userId = String.valueOf(oAuthAuthzParameters.getUserId());

        openOauthAccessTokens.setExtra(userId); // not used
        openOauthAccessTokens.setScope(oAuthAuthzParameters.getScope());

        // expire in 7 days
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, appConfig.tokenAccessExpire);
        openOauthAccessTokens.setExpires(calendar.getTime());

        return accessTokenService.save(openOauthAccessTokens);
    }

    // private void encodeUtf8mb4Str(User user, UserThirdInfo userThirdInfo) {
    // if (null != user.getWechatNick()) {
    // user.setWechatNick(encodeStringWithUtf8(user.getWechatNick()));
    // }
    // if (null != user.getWeiboNick()) {
    // user.setWeiboNick(encodeStringWithUtf8(user.getWeiboNick()));
    // }
    // if (null != user.getQqNick()) {
    // user.setQqNick(encodeStringWithUtf8(user.getQqNick()));
    // }
    //
    // if (null != userThirdInfo.getWeixinNickname()) {
    // userThirdInfo.setWeixinNickname(encodeStringWithUtf8(userThirdInfo
    // .getWeixinNickname()));
    // }
    // if (null != userThirdInfo.getQqScreenName()) {
    // userThirdInfo.setQqScreenName(encodeStringWithUtf8(userThirdInfo
    // .getQqScreenName()));
    // }
    // if (null != userThirdInfo.getWeiboScreenName()) {
    // userThirdInfo.setWeiboScreenName(encodeStringWithUtf8(userThirdInfo
    // .getWeiboScreenName()));
    // }
    // }

    /**
     * insert refresh token to db
     *
     * @param refreshToken
     * @param oAuthAuthzParameters
     * @return
     */
    public int saveRefreshToken(String refreshToken, String accessToken,
                                OAuthAuthzParameters oAuthAuthzParameters) {
        RefreshToken openOauthRefreshTokens = new RefreshToken();
        openOauthRefreshTokens.setRefreshToken(refreshToken);
        openOauthRefreshTokens.setClientId(oAuthAuthzParameters.getClientId());
        openOauthRefreshTokens.setUserId(oAuthAuthzParameters.getUserId()); // default
        // is
        // 0

        String userId = String.valueOf(oAuthAuthzParameters.getUserId());

        openOauthRefreshTokens.setExtra(userId); // not used
        openOauthRefreshTokens.setScope(oAuthAuthzParameters.getScope());
        openOauthRefreshTokens.setAccessToken(accessToken);

        // expire in 28 days
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, appConfig.tokenRefreshExpire);
        openOauthRefreshTokens.setExpires(calendar.getTime());

        return refreshTokenService.save(openOauthRefreshTokens);
    }

    /**
     * the api of login
     *
     * @param account
     * @param password
     * @return
     */
    public Account login(String account, String password) {
        AccountServiceImpl userService = SpringContext.getBean(AccountServiceImpl.class);
        Account user = new Account();
        user.setLoginName(account);
        user.setPassword(password);
        user = userService.getUserByUserId(user);
        return user;
    }

    /**
     * the api of login
     *
     * @param oAuthAuthzParameters
     * @return
     */
    // public OauthUser login(String account, String password,Integer type)
    // throws OAuthException {
    public Account login(OAuthAuthzParameters oAuthAuthzParameters,
                         HttpServletRequest request) throws OAuthException {

        Account oauthUser;
        String password="";
        if (StringUtils.isNotEmpty(oAuthAuthzParameters.getLoginName())&&!oAuthAuthzParameters.getLoginName().equals("openId")) {
            oauthUser = accountDAO.getUserByName(oAuthAuthzParameters.getLoginName());
            password = AppUtils.enCodePassword(oAuthAuthzParameters.getPassword());
        } else if (StringUtils.isNotEmpty(oAuthAuthzParameters.getLoginPhone())) {
            oauthUser = accountDAO.getUserByMobile(oAuthAuthzParameters.getLoginPhone());
            password = AppUtils.enCodePassword(oAuthAuthzParameters.getPassword());
        } else if("openId".equals(oAuthAuthzParameters.getLoginName())){
            //通过openId登陆
            oauthUser = accountDAO.getUserByOpenId(oAuthAuthzParameters.getPassword());
            oAuthAuthzParameters.setPassword(oauthUser.getPassword());
            password = oAuthAuthzParameters.getPassword();
            oAuthAuthzParameters.setLoginName(oauthUser.getLoginName());
        }else {
            oauthUser = accountDAO.getUserByMail(oAuthAuthzParameters.getLoginMail());
            password = AppUtils.enCodePassword(oAuthAuthzParameters.getPassword());
        }


        if (oauthUser == null) {
            throw new OAuthException(
                    OAuthConstants.OAuthResponse.INVALID_USER, "用户不存在");
        }

        if (!StringUtils.equalsIgnoreCase(oauthUser.getPassword(), password)) {
            throw new OAuthException(
                    OAuthConstants.OAuthResponse.INVALID_PASSWORD, "密码错误");
        }
        return oauthUser;
    }

    /**
     * get refresh token by token string
     *
     * @param token
     * @return
     */
    public RefreshToken getRefreshToken(String token) {
        String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token);
        String content = redis.getStringByKey(key);
        if (!StringUtils.isEmpty(content)) {
            return JSON.parseObject(content, RefreshToken.class);
        }
        RefreshToken tokens = refreshTokenService
                .getByToken(token);
        if (tokens != null)
            redis.set(key, JSON.toJSONString(tokens));
        return tokens;
    }

    /**
     * retain the elements which are in scopes1
     *
     * @param scopes1
     * @param scopes2
     * @return
     */
    public Set<String> getRetainScopes(String scopes1, String scopes2) {
        Set<String> scopes_1 = OAuthUtils.decodeScopes(scopes1);
        Set<String> scopes_2 = OAuthUtils.decodeScopes(scopes2);

        Set<String> result = new HashSet<String>();
        result.addAll(scopes_2);
        result.retainAll(scopes_1);
        return result;
    }

    /**
     * put to redis cache, the key will expire in 2 minutes
     *
     * @param token
     * @param parameters
     */
    public void putOAuthAuthzParameters(String token,
                                        OAuthAuthzParameters parameters) {
        String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token);
        redis.set(key, JSON.toJSONString(parameters), 10 * 60);
    }

    /**
     * delete parameter after getting token
     *
     * @param token
     */
    public void delOAuthAuthzParameters(String token) {
        String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token);
        redis.del(key);
    }

    /**
     * check refresh token frequency
     *
     * @param token
     * @return
     */
    public boolean checkRefreshFrequency(String token) {
        String key = String.format(Constants.CACHE_REFRESH_FREQUENCY_PATTERN,
                token);
        String content = redis.getStringByKey(key);
        if (content != null) {
            return false;
        }
        redis.set(key, token, appConfig.frequency);
        return true;
    }

    public boolean checkLoginFrequency(String account) {
        String key = String.format(Constants.CACHE_LOGIN_FREQUENCY_PATTERN,
                account);
        String content = redis.getStringByKey(key);
        if (content != null) {
            return false;
        }
        redis.set(key, key, appConfig.frequency);
        return true;
    }

    /**
     * get from redis cache
     *
     * @param token
     * @return
     */
    public OAuthAuthzParameters getOAuthAuthzParameters(String token) {
        String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token);
        String content = redis.getStringByKey(key);
        if (!StringUtils.isEmpty(content)) {
            return JSON.parseObject(content, OAuthAuthzParameters.class);
        }
        return null;
    }

    /**
     * get access token by token string
     *
     * @param token
     * @return
     */
    public AccessToken getAccessToken(String token) {
        String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token);
        String content = redis.getStringByKey(key);
        if (!StringUtils.isEmpty(content)) {
            return JSON.parseObject(content, AccessToken.class);
        }
        AccessToken tokens = accessTokenService
                .getByToken(token);
        if (tokens != null)
            redis.set(key, JSON.toJSONString(tokens));
        return tokens;
    }

    /**
     * get resource by uri
     *
     * @param uri
     * @return
     */
//    public OpenResource getOpenResourceByUri(String uri) {
//        return openResourceService.getByUri(uri);
//    }

    /**
     * delete access token by token string
     *
     * @param token
     */
    public void delAccessToken(String token) {
        redis.del(String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token));
        accessTokenService.deleteByToken(token);
    }

    /**
     * delete refresh token by token string
     *
     * @param token
     */
    public void delRefreshToken(String token) {
        redis.del(String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token));
        refreshTokenService.deleteByToken(token);
    }

    /**
     * limit client access token number
     *
     * @param clientId
     * @param adminId
     * @param adminNum
     * @return
     */
    public boolean limitAccessToken(String clientId, int adminId, int adminNum) {
        /**
         * no limit
         */
        if (adminNum == 0)
            return true;

        /**
         * sort by expires desc
         */
        List<AccessToken> tokensList = accessTokenService
                .getByClientIdAndUserId(clientId, adminId);
        Comparator<AccessToken> comparator = (AccessToken t1, AccessToken t2) -> t1.getExpires().compareTo(t2.getExpires());
        tokensList.sort(comparator.reversed());

        /**
         * remove redundant tokens
         */
        if (tokensList.size() > adminNum) {
            tokensList = tokensList.subList(adminNum, tokensList.size());
        } else {
            return true;
        }
        List<Integer> idList = new ArrayList<>();
        List<String> tkList = new ArrayList<>();
        tokensList.forEach(e -> {
            idList.add(e.getId());
            tkList.add(e.getAccessToken());
        });

        /**
         * remove data from db firstly and then flush cache
         */
        List<RefreshToken> refreshTokensList = refreshTokenService
                .getByAccessTokens(tkList);

        accessTokenService.saveAccessTokenToMongoDb(tokensList);

        accessTokenService.deleteByIds(idList);
        refreshTokenService.deleteByAccessTokens(tkList);

        refreshTokensList.forEach(e -> {
            String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN,
                    e.getRefreshToken());
            redis.del(key);
        });
        tkList.forEach(e -> {
            String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN, e);
            redis.del(key);
        });

        return true;
    }

}
