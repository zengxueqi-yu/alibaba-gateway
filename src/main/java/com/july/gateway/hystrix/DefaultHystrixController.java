package com.july.gateway.hystrix;

import com.july.gateway.constant.ApiCode;
import com.july.gateway.constant.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zengxueqi
 * @date: 2020-03-18 16:35
 */
@RestController
@Slf4j
public class DefaultHystrixController {

    @RequestMapping("/fallback")
    public ApiResult<String> fallback() {
        log.error("触发熔断机制》》》");
        return ApiResult.fail(ApiCode.DEFAULT_HYSTRIX.getMsg());
    }
}
