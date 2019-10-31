package com.qz.springSocialCore.weixin.connect;

import lombok.Data;
import org.springframework.social.oauth2.AccessGrant;

/**
 * AccessGrant这个类缺少微信的openId字段，所以需要继承并添加这个字段
 */
@Data
public class WeixinAccessGrant extends AccessGrant {

    public WeixinAccessGrant(String accessToken, String scope, String refreshToken, Long expiresIn) {
        super(accessToken, scope, refreshToken, expiresIn);
    }

    private String openId;
}
