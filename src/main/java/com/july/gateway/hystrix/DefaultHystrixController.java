package com.july.gateway.hystrix;

import com.july.gateway.constant.ApiCode;
import com.july.gateway.constant.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 断路器响应信息
 * @author zengxueqi
 * @date: 2020-03-18 16:35
 */
@RestController
@Slf4j
public class DefaultHystrixController {

    /**
     * 当服务无法访问时，重定向到此Api(下一步支持服务重连)
     * @param
     * @return com.july.gateway.constant.ApiResult<java.lang.String>
     * @author zengxueqi
     * @since 2020/4/20
     */
    @RequestMapping("/fallback")
    public ApiResult<String> fallback() {
        log.error("触发熔断机制》》》");
        return ApiResult.fail(ApiCode.DEFAULT_HYSTRIX.getMsg());
    }

}
