package com.zitech.gateway.oauth.service.impl;

import com.alibaba.fastjson.JSON;
import com.zitech.gateway.AppConfig;
import com.zitech.gateway.apiconfig.model.OpenResource;
import com.zitech.gateway.apiconfig.service.IOpenResourceService;
import com.zitech.gateway.cache.RedisOperate;
import com.zitech.gateway.oauth.Constants;
import com.zitech.gateway.oauth.LoginType;
import com.zitech.gateway.oauth.dao.user.AccountMapper;
import com.zitech.gateway.oauth.dao.user.IOauthUserDAO;
import com.zitech.gateway.oauth.exception.OAuthException;
import com.zitech.gateway.oauth.oauthex.OAuthASResponseEx;
import com.zitech.gateway.oauth.oauthex.OAuthConstants;
import com.zitech.gateway.utils.AppUtils;
import com.zitech.gateway.utils.SpringContext;
import com.zitech.gateway.utils.StrUtils;

import com.zitech.gateway.oauth.model.*;
import com.zitech.gateway.oauth.service.*;
import org.apache.commons.lang.StringUtils;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class OAuthService implements IOAuthService {

    public static final Logger logger = LoggerFactory
            .getLogger(OAuthService.class);

    @Autowired
    private RedisOperate redis;

    @Autowired
    private IOpenOauthClientsService openOauthClientsService;

    @Autowired
    private IOpenOauthAccessTokensService openOauthAccessTokensService;

    @Autowired
    private IOpenOauthRefreshTokensService openOauthRefreshTokensService;

    @Autowired
    private IOpenResourceService openResourceService;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private IOauthUserDAO userDAO;

    public static String encodeStringWithUtf8(String str) throws OAuthException {
        if (str == null)
            return null;
        byte[] bytes = null;
        try {
            bytes = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("编码错误，不支持的编码");
        }
        String ans = "";
        for (byte tbyte : bytes) {
            ans = ans + ":" + Integer.toString(tbyte & 0xff);
        }
        return ans;
    }

    public static String decodeStringFromUtf8(String ans) throws OAuthException {
        if (ans == null)
            return null;
        String[] out = ans.split(":");
        byte[] result = new byte[100];
        int cnt = 0;
        for (String anOut : out) {
            if (anOut.equals("")) {
                continue;
            }
            result[cnt++] = (byte) Integer.parseInt(anOut);
        }
        try {
            return new String(result, 0, cnt, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("解码错误，不支持的编码");
        }
    }

    /**
     * get client by client id
     *
     * @param clientId
     * @return
     */
    public OpenOauthClients getClientByClientId(String clientId) {
        String key = String
                .format(Constants.CACHE_CLIENT_KEY_PATTERN, clientId);
        String content = redis.getStringByKey(key);
        if (!StringUtils.isEmpty(content)) {
            return JSON.parseObject(content, OpenOauthClients.class);
        }
        OpenOauthClients clients = openOauthClientsService
                .getByClientId(clientId);
        if (clients != null)
            redis.set(key, JSON.toJSONString(clients));
        return clients;
    }

    /**
     * save client
     *
     * @param openOauthClients
     * @return
     */
    public int saveClient(OpenOauthClients openOauthClients) {
        String key = String.format(Constants.CACHE_CLIENT_KEY_PATTERN,
                openOauthClients.getClientId());
        redis.del(key);
        return openOauthClientsService.save(openOauthClients);
    }

    /**
     * save access token to db
     *
     * @param accessToken
     * @param oAuthAuthzParameters
     * @return
     */
    public int saveAccessToken(String accessToken,
                               OAuthAuthzParameters oAuthAuthzParameters) {
        OpenOauthAccessTokens openOauthAccessTokens = new OpenOauthAccessTokens();
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

        return openOauthAccessTokensService.save(openOauthAccessTokens);
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
     * save refresh token to db
     *
     * @param refreshToken
     * @param oAuthAuthzParameters
     * @return
     */
    public int saveRefreshToken(String refreshToken, String accessToken,
                                OAuthAuthzParameters oAuthAuthzParameters) {
        OpenOauthRefreshTokens openOauthRefreshTokens = new OpenOauthRefreshTokens();
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

        return openOauthRefreshTokensService.save(openOauthRefreshTokens);
    }

    /**
     * the api of login
     *
     * @param account
     * @param password
     * @return
     */
    public OauthUser login(String account, String password) {
        UserService userService = SpringContext.getBean(UserService.class);
        OauthUser user = new OauthUser();
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
    public OauthUser login(OAuthAuthzParameters oAuthAuthzParameters,
                           HttpServletRequest request) throws OAuthException {

        OauthUser oauthUser;

        if(StringUtils.isNotEmpty(oAuthAuthzParameters.getLoginName())) {
            oauthUser = userDAO.getUserByName(oAuthAuthzParameters.getLoginName());
        } else if (StringUtils.isNotEmpty(oAuthAuthzParameters.getLoginPhone())) {
            oauthUser = userDAO.getUserByMobile(oAuthAuthzParameters.getLoginPhone());
        } else {
            oauthUser = userDAO.getUserByMail(oAuthAuthzParameters.getLoginMail());
        }

        if (oauthUser == null) {
            throw new OAuthException(
                    OAuthConstants.OAuthResponse.INVALID_USER, "用户不存在");
        }

        String password = AppUtils.enCodePassword(oAuthAuthzParameters.getPassword());

        if (!oauthUser.getPassword().equalsIgnoreCase(password)) {
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
    public OpenOauthRefreshTokens getRefreshToken(String token) {
        String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token);
        String content = redis.getStringByKey(key);
        if (!StringUtils.isEmpty(content)) {
            return JSON.parseObject(content, OpenOauthRefreshTokens.class);
        }
        OpenOauthRefreshTokens tokens = openOauthRefreshTokensService
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
    public OpenOauthAccessTokens getAccessToken(String token) {
        String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token);
        String content = redis.getStringByKey(key);
        logger.info("get token from redis: {}", content);
        if (!StringUtils.isEmpty(content)) {
            return JSON.parseObject(content, OpenOauthAccessTokens.class);
        }
        OpenOauthAccessTokens tokens = openOauthAccessTokensService
                .getByToken(token);
        logger.info("get token from db: {}", tokens);
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
    public OpenResource getOpenResourceByUri(String uri) {
        return openResourceService.getByUri(uri);
    }

    /**
     * delete access token by token string
     *
     * @param token
     */
    public void delAccessToken(String token) {
        redis.del(String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token));
        openOauthAccessTokensService.deleteByToken(token);
    }

    /**
     * delete refresh token by token string
     *
     * @param token
     */
    public void delRefreshToken(String token) {
        redis.del(String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token));
        openOauthRefreshTokensService.deleteByToken(token);
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
        List<OpenOauthAccessTokens> tokensList = openOauthAccessTokensService
                .getByClientIdAndUserId(clientId, adminId);
        Comparator<OpenOauthAccessTokens> comparator = (
                OpenOauthAccessTokens t1, OpenOauthAccessTokens t2) -> {
            return t1.getExpires().compareTo(t2.getExpires());
        };
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
        List<OpenOauthRefreshTokens> refreshTokensList = openOauthRefreshTokensService
                .getByAccessTokens(tkList);

        openOauthAccessTokensService.deleteByIds(idList);
        openOauthRefreshTokensService.deleteByAccessTokens(tkList);

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
