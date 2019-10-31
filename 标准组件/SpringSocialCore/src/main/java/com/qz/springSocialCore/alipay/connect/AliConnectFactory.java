package com.qz.springSocialCore.alipay.connect;

import com.qz.springSocialCore.alipay.api.AliPay;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;

/**
 * @ClassName AliConnectFactory
 * @Author Levia
 * @Date 2019-10-15 12:45
 **/
public class AliConnectFactory extends OAuth2ConnectionFactory<AliPay> {
    /**
     * Create a {@link OAuth2ConnectionFactory}.
     *
     * @param providerId
     */
    public AliConnectFactory(String providerId, String appid, String aliPublicKey, String appPrivateKey) {
        super(providerId, new AliOAuth2ServiceProvider(appid, appPrivateKey, aliPublicKey), new AliApiAdapter());

    }


}