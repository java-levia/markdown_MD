package com.qz.springSocialCore.weixin.connect;

import com.qz.springSocialCore.weixin.api.WeiXin;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

public class WeixinServiceProvider extends AbstractOAuth2ServiceProvider<WeiXin> {

    private static final  String GET_CODE = "https://open.weixin.qq.com/connect/qrconnect";

    private static final String GET_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";



    public WeixinServiceProvider(String appId, String appSecret) {

        super(new WeixinOAuth2Template(appId, appSecret, GET_CODE, GET_ACCESS_TOKEN));


    }

    @Override
    public WeiXin getApi(String accessToken) {
        return new WeiXinImpl(accessToken);
    }
}
