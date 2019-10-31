package com.qz.springSocialCore.qq.connect;

import com.qz.springSocialCore.qq.api.QQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

/**
 * @ClassName QQOAuth2ServiceProvider
 * @Author Levia
 * @Date 2019/7/28 17:11
 **/
public class QQOAuth2ServiceProvider extends AbstractOAuth2ServiceProvider<QQ> {

    private static Logger logger = LoggerFactory.getLogger(QQOAuth2ServiceProvider.class);

    private static final String GET_AUTHORIZE_CODE_URL = "https://graph.qq.com/oauth2.0/authorize";

    private static final String GET_ACCESS_TOKEN_URL="https://graph.qq.com/oauth2.0/token";

    private String appid;

    public QQOAuth2ServiceProvider(String appid, String appsecret) {
        super(new QQOAuth2Template(appid, appsecret, GET_AUTHORIZE_CODE_URL, GET_ACCESS_TOKEN_URL));
        this.appid = appid;
    }

    @Override
    public QQ getApi(String accessToken) {
        return new QQImpl(accessToken, appid);
    }
}