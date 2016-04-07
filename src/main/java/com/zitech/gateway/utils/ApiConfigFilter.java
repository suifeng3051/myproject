package com.zitech.gateway.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.zitech.gateway.AppConfig;
import com.zitech.gateway.cache.RedisOperate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zitech.gateway.cache.RedisOperateImp;


public class ApiConfigFilter implements Filter {

    private AppConfig appConfig = SpringContext.getBean(AppConfig.class);

    private static final Logger logger = LoggerFactory.getLogger(ApiConfigFilter.class);

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
    	HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        logger.info(req.getServletPath());

        String addr = request.getRemoteAddr();

        logger.debug("remote address: {}", addr);

        if (StringUtils.isNotEmpty(appConfig.allowAccess) && !appConfig.allowAccess.contains(addr)) {
            res.sendRedirect("/");
        } else {
            HttpSession httpSession= req.getSession(true);
            String username = (String) httpSession.getAttribute("username");
            if (StringUtils.isEmpty(username)){
                request.getRequestDispatcher("/user/login").forward(request,response);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    public void init(FilterConfig config) throws ServletException {
    }

}
