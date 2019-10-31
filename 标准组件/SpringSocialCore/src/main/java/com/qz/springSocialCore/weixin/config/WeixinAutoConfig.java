package com.qz.springSocialCore.weixin.config;

import com.qz.springSocialCore.properties.LeviaSocialProperties;
import com.qz.springSocialCore.properties.WeixinProperties;
import com.qz.springSocialCore.weixin.connect.WeixinConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;

import javax.sql.DataSource;

@Configuration
public class WeixinAutoConfig extends SocialConfigurerAdapter {

    @Autowired
    private LeviaSocialProperties socialProperties;
    @Autowired
    private DataSource dataSource;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        connectionFactoryConfigurer.addConnectionFactory(this.createConnectionFactory());
    }

    public ConnectionFactory<?> createConnectionFactory() {
        WeixinProperties weixin = socialProperties.getWeixin();
        return new WeixinConnectionFactory(weixin.getProviderId(),weixin.getAppId(), weixin.getAppSecret());
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(
            ConnectionFactoryLocator connectionFactoryLocator) {
        return new JdbcUsersConnectionRepository(dataSource,connectionFactoryLocator, Encryptors.noOpText());
    }

}
