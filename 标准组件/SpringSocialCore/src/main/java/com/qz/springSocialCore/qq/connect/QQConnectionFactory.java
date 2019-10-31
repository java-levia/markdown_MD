package com.qz.springSocialCore.qq.connect;

import com.qz.springSocialCore.qq.api.QQ;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;

/**
 * @ClassName QQConnectionFactory
 * @Author Levia
 * @Date 2019/7/28 17:27
 **/
public class QQConnectionFactory extends OAuth2ConnectionFactory<QQ> {

    public QQConnectionFactory(String providerId, String appid, String appsecret) {
        super(providerId, new QQOAuth2ServiceProvider(appid, appsecret), new QQApiAdapter());
    }
}