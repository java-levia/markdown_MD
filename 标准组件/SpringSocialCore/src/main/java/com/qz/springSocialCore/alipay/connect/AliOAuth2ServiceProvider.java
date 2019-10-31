package com.qz.springSocialCore.alipay.connect;

import com.qz.springSocialCore.alipay.api.AliPay;
import com.qz.springSocialCore.alipay.entity.AliPayUserinfo;
import com.qz.springSocialCore.qq.api.QQ;
import com.qz.springSocialCore.qq.connect.QQOAuth2Template;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Template;

/**
 * @ClassName AliOAuth2ServiceProvider
 * @Author Levia
 * @Date 2019-10-14 20:09
 **/
public class AliOAuth2ServiceProvider extends AbstractOAuth2ServiceProvider<AliPay> {

    private static final String REQUEST_LOGIN_URL = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm";

    private static final String REQUEST_AUTH_URL =
            "https://openapi.alipay.com/gateway.do?app_id=%s&charset=utf-8&code=%s&grant_type=authorization_code&method=alipay.system.oauth.token&sign_type=RSA2&timestamp=%s&version=1.0";

    private String app_id;

    private String appPrivateKey;

    private String aliPublicKey;
    /**
     * Create a new {@link }.
     *
     * @param
     */
    public AliOAuth2ServiceProvider(String app_id, String appPrivateKey, String aliPublicKey) {
        super(new AliOAuth2Template(app_id, appPrivateKey, aliPublicKey, REQUEST_LOGIN_URL, REQUEST_AUTH_URL));
        this.app_id = app_id;
        this.aliPublicKey = aliPublicKey;
        this.appPrivateKey = appPrivateKey;
    }

    @Override
    public AliPay getApi(String accessToken) {
        return new AliImpl(app_id, accessToken, aliPublicKey, appPrivateKey);
    }
}