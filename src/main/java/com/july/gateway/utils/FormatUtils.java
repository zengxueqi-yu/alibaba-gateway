package com.july.gateway.utils;

/**
 * 格式化工具类
 * @author zengxueqi
 * @date: 2020/3/13 17:42
 */
public interface FormatUtils {

    /**
     * 将字符串用中括号括起来
     * @param s
     * @return java.lang.String
     * @author zengxueqi
     * @since 2020/4/20
     */
    static String wrapStringWithBracket(String s) {
        return "[" + s + "] ";
    }

}
