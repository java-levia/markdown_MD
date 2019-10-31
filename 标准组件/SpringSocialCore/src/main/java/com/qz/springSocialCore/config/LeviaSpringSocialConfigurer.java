package com.qz.springSocialCore.config;

import com.qz.springSocialCore.filter.AppSocialAuthenticationFilterPostProcessor;
import com.qz.springSocialCore.properties.LeviaSocialProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * 这个类存在的目的是自定义过滤器在加入到过滤器链之前的行为，
 *
 */
public class LeviaSpringSocialConfigurer extends SpringSocialConfigurer {

    private String filterProcessUrl;
    @Autowired
    private AppSocialAuthenticationFilterPostProcessor appSocialAuthenticationFilterPostProcessor;

    @Autowired
    private LeviaSocialProperties leviaSocialProperties;

    public LeviaSpringSocialConfigurer(String url){
        this.filterProcessUrl = url;
    }
    @Override
    protected <T> T postProcess(T object) {
        //在这里要自定义我的网站回调域
        SocialAuthenticationFilter filter =(SocialAuthenticationFilter) super.postProcess(object);
        filter.setFilterProcessesUrl(filterProcessUrl);

        //由于social的默认行为只适用于浏览器，如果要在手机端使用（也就是通过successHandler返回jwtTokan)则需要配置一个FilterPostProcessor
        if(leviaSocialProperties.getPublicProperties().getIsApp()){
            //如果是app  则将自定义的成功处理器配置到拦截器链中
            appSocialAuthenticationFilterPostProcessor.processor(filter);
        }
        return (T)filter;
    }

}
