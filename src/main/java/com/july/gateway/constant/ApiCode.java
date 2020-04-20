package com.july.gateway.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * REST API响应码
 * @author zengxueqi
 * @since 2020/4/14
 */
@Getter
@AllArgsConstructor
public enum ApiCode {

    /**
     * 操作成功
     */
    SUCCESS("0", "操作成功"),
    /**
     * 非法访问
     */
    UNAUTHORIZED("401", "非法访问"),
    /**
     * 没有权限
     */
    NOT_PERMISSION("403", "没有权限"),
    /**
     * 你请求的资源不存在
     */
    NOT_FOUND("404", "你请求的资源不存在"),
    /**
     * 请求超时
     */
    DEFAULT_HYSTRIX("500", "请求超时。"),
    /**
     * 操作失败
     */
    FAIL("-1", "操作失败"),
    /**
     * 登陆失败
     */
    LOGIN_EXCEPTION("4000", "登陆失败"),
    /**
     * 系统异常，请联系管理员
     */
    SYSTEM_EXCEPTION("5000", "系统异常,请联系管理员"),
    /**
     * 请求参数娇艳异常
     */
    PARAMETER_EXCEPTION("5001", "请求参数校验异常"),
    /**
     * 请求参数解析异常
     */
    PARAMETER_PARSE_EXCEPTION("5002", "请求参数解析异常"),
    /**
     * HTTP Media 类型异常
     */
    HTTP_MEDIA_TYPE_EXCEPTION("5003", "HTTP Media 类型异常"),
    /**
     * 数据库语句执行异常
     */
    DATEBASE_EXECUTION_EXCEPTION("5004", "数据库语句执行异常"),
    /**
     * Token已过期
     */
    REFRESH_TOKEN_TIME_OUT("401", "刷新Token过期"),
    /**
     * 刷新Token错误
     */
    REFRESH_TOKEN_ERROR("402", "刷新Token错误");

    private final String code;
    private final String msg;

    public static ApiCode getApiCode(String code) {
        ApiCode[] apiCodes = ApiCode.values();
        for (ApiCode ec : apiCodes) {
            if (code.equals(ec.getCode())) {
                return ec;
            }
        }
        return SUCCESS;
    }

}
