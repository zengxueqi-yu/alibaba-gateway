package com.july.gateway.filter;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.july.gateway.exception.BnException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 鉴权过滤器
 * @author zengxueqi
 * @program alibaba-gateway
 * @since 2020-04-20 16:22
 **/
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    /**
     * JWT生成密钥
     */
    @Value("${jwt.secret.key}")
    private String secretKey;
    /**
     * 白名单(无需验证Token信息)
     */
    @Value("${whitelist.authExcludeUrl}")
    private String[] skipAuthUrls;
    /**
     * Token黑名单，存储Key(用处狭隘)
     */
    @Value("${jwt.blacklist.key.format}")
    private String jwtBlacklistKeyFormat;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public int getOrder() {
        return -100;
    }

    /**
     * Token过滤与验证
     * @param exchange
     * @param chain
     * @return reactor.core.publisher.Mono<java.lang.Void>
     * @author zengxueqi
     * @since 2020/4/20
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();
        //跳过不需要验证的路径
        if (Arrays.asList(skipAuthUrls).contains(url)) {
            return chain.filter(exchange);
        }
        //从请求头中取出token
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        //未携带token或token在黑名单内
        if (token == null ||
                token.isEmpty() ||
                isBlackToken(token)) {
            ServerHttpResponse originalResponse = exchange.getResponse();
            originalResponse.setStatusCode(HttpStatus.OK);
            originalResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            byte[] response = "{\"code\": \"401\",\"msg\": \"401 Unauthorized.\"}"
                    .getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
            return originalResponse.writeWith(Flux.just(buffer));
        }
        //取出token包含的身份
        String userName = verifyJWT(token);
        if (userName.isEmpty()) {
            ServerHttpResponse originalResponse = exchange.getResponse();
            originalResponse.setStatusCode(HttpStatus.OK);
            originalResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            byte[] response = "{\"code\": \"10002\",\"msg\": \"invalid token.\"}"
                    .getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
            return originalResponse.writeWith(Flux.just(buffer));
        }
        //将现在的request，添加当前身份
        ServerHttpRequest mutableReq = exchange.getRequest().mutate().header("Authorization-UserName", userName).build();
        ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
        return chain.filter(mutableExchange);
    }

    /**
     * JWT验证
     * @param token
     * @return java.lang.String
     * @author zengxueqi
     * @since 2020/4/20
     */
    private String verifyJWT(String token) {
        String userName = "";
        try {
            token = token.replace("Bearer ", "").trim();
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("July")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            userName = jwt.getClaim("userName").asString();
        } catch (JWTVerificationException e) {
            throw new BnException("Token已过期或者未启用！");
        }
        return userName;
    }

    /**
     * 判断token是否在黑名单内
     * @param token
     * @return boolean
     * @author zengxueqi
     * @since 2020/4/20
     */
    private boolean isBlackToken(String token) {
        assert token != null;
        return stringRedisTemplate.hasKey(String.format(jwtBlacklistKeyFormat, token));
    }

}