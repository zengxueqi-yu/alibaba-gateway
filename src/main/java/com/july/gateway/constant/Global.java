package com.july.gateway.constant;

import com.july.gateway.utils.EnvironmentUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * 全局信息配置
 * @author zengxueqi
 * @since 2020/4/20
 */
@Configuration
public class Global {

    /**
     * 服务名称
     */
    @Value("${spring.application.name}")
    public String applicationName;
    /**
     * 暂定为激活环境
     */
    @Value("${spring.profiles.active}")
    public String env;
    /**
     * 请求白名单
     */
    @Value("${whitelist.authExcludeUrl}")
    public String authExcludeUrl;
    private List<String> excludeUrlList;

    /**
     * 初始化静态参数
     * @param
     * @return int
     * @author zengxueqi
     * @since 2020/4/20
     */
    @Bean
    public int initStatic() {
        EnvironmentUtils.setApplicationName(applicationName);
        EnvironmentUtils.setEnv(env);
        excludeUrlList = Arrays.asList(authExcludeUrl.split(","));
        return 0;
    }

    /**
     * 获取需要排除的URL的列表
     * @param
     * @return java.util.List<java.lang.String>
     * @author zengxueqi
     * @since 2020/4/20
     */
    public List<String> getExcludeUrlList() {
        return excludeUrlList;
    }

}
