package com.qz.springSocialCore.alipay.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.qz.springSocialCore.alipay.connect.AliConnectFactory;
import com.qz.springSocialCore.properties.AliProperties;
import com.qz.springSocialCore.properties.LeviaSocialProperties;
import com.qz.springSocialCore.properties.QQProperties;
import com.qz.springSocialCore.qq.connect.QQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * @ClassName AlipayLoginInfoConfig
 * @Author Levia
 * @Date 2019-10-15 12:49
 **/
@Configuration
public class AlipayLoginInfoConfig extends SocialConfigurerAdapter {

    @Autowired
    private LeviaSocialProperties socialProperties;
    @Autowired
    private DataSource dataSource;

    private static Logger log = LoggerFactory.getLogger(AlipayLoginInfoConfig.class);

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        connectionFactoryConfigurer.addConnectionFactory(this.createConnectionFactory());
    }

    public ConnectionFactory<?> createConnectionFactory() {
        AliProperties alipay = socialProperties.getAlipay();
        return new AliConnectFactory(alipay.getProviderId(),alipay.getAppid(), alipay.getAlipayPublicKey(), alipay.getAppPrivateKey());
    }

    // 后补：做到处理注册逻辑的时候发现的一个bug：登录完成后，数据库没有数据，但是再次登录却不用注册了
    // 就怀疑是否是在内存中存储了。结果果然发现这里父类的内存ConnectionRepository覆盖了SocialConfig中配置的jdbcConnectionRepository
    @Override
    public UsersConnectionRepository getUsersConnectionRepository(
            ConnectionFactoryLocator connectionFactoryLocator) {
        return new JdbcUsersConnectionRepository(dataSource,connectionFactoryLocator, Encryptors.noOpText());
    }


}    