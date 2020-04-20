package com.july.gateway.filter;

import com.july.gateway.constant.OrderedConstant;
import com.july.gateway.log.CacheServerHttpRequestDecorator;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 日志过滤器
 * @author zengxueqi
 * @program alibaba-gateway
 * @since 2020-04-20 15:24
 **/
//@Component
public class LogFilter implements GlobalFilter, Ordered {

    /**
     * 日志过滤器
     * @param exchange
     * @param chain
     * @return reactor.core.publisher.Mono<java.lang.Void>
     * @author zengxueqi
     * @since 2020/4/20
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        CacheServerHttpRequestDecorator cacheServerHttpRequestDecorator = new CacheServerHttpRequestDecorator(exchange.getRequest());

        return chain.filter(exchange.mutate().request(cacheServerHttpRequestDecorator).build());
    }

    @Override
    public int getOrder() {
        return OrderedConstant.LOGGING_FILTER;
    }

}