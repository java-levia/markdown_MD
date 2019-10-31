package com.qz.springSocialCore.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * 如果使用的是app，则将这个
 * @ClassName AppSocialAuthenticationFilterPostProcessor
 * @Author Levia
 * @Date 2019-09-08 20:36
 **/
@Component
public class AppSocialAuthenticationFilterPostProcessor {
    @Autowired
    private  AuthenticationSuccessHandler successHandler;

    public void processor(SocialAuthenticationFilter socialAuthenticationFilter){
        socialAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);
    }
}    