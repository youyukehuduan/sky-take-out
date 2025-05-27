package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 非 Controller 请求直接放行（如静态资源）
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 获取请求路径
        String requestURI = request.getRequestURI();
        log.info("拦截请求: {}", requestURI);

        // 白名单放行（登录接口、店铺状态等）
        if ("/user/user/login".equals(requestURI) || "/user/shop/status".equals(requestURI)) {
            return true;
        }

        // 从 header 中获取 token
        String token = request.getHeader(jwtProperties.getUserTokenName());

        log.info("jwt校验:{}", token);

        if (token == null || token.isEmpty()) {
            log.warn("缺少 token，拒绝访问");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            return false;
        }

        // 解析 token
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            BaseContext.setCurrentId(userId); // 将用户ID存入线程上下文
            return true;
        } catch (Exception ex) {
            log.warn("token 解析失败", ex);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}