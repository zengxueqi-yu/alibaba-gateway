package com.july.gateway.utils;

import java.util.UUID;

/**
 * ID生成工具类
 * @author zengxueqi
 * @date: 2020/4/20
 */
public class GenerateIdUtils {

    /**
     * 使用UUID生成RequestId
     * @param
     * @return java.lang.String
     * @author zengxueqi
     * @since 2020/4/20
     */
    public static String requestIdWithUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
