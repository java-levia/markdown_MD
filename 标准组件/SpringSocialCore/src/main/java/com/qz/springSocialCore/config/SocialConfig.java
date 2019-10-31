package com.qz.springSocialCore.config;

import com.qz.springSocialCore.properties.LeviaSocialProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;

/**
 * @ClassName SocialConfig
 * @Author Levia
 * @Date 2019/7/28 17:53
 **/
@Configuration
@EnableSocial
public class SocialConfig extends SocialConfigurerAdapter {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private LeviaSocialProperties socialProperties;

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        JdbcUsersConnectionRepository connectionRepository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
        return connectionRepository;
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }



    @Bean
    public SpringSocialConfigurer leviaSocialSecurityConfig(){
        //此处可定义social的拦截规则，默认/auth/{providerId},可在构造函数中传入参数改变该默认值auth
        SpringSocialConfigurer springSocialConfigurer = new LeviaSpringSocialConfigurer(socialProperties.getFilterProcessUrl());
        //社交登陆的用户未注册平台账号时处理
        springSocialConfigurer.signupUrl(socialProperties.getPublicProperties().getSignUpUrl());

        return springSocialConfigurer;
    }

    /**
     * 通过这个工具类可以在注册过程中拿到SpringSocial，同时在注册完成之后将业务系统的用户id传给springSocial
     * @param connectionFactoryLocator
     * @return
     */
    @Bean
    public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator){

        return new ProviderSignInUtils(connectionFactoryLocator, getUsersConnectionRepository(connectionFactoryLocator));
    }

}