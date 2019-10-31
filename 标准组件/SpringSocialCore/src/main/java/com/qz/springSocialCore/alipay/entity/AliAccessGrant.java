package com.qz.springSocialCore.alipay.entity;

import lombok.Data;
import org.springframework.social.oauth2.AccessGrant;

/**
 * @ClassName AliAccessGrant
 * @Author Levia
 * @Date 2019-10-15 0:39
 **/
@Data
public class AliAccessGrant extends AccessGrant {

    private String user_id;

    private String re_expires_in;

    public AliAccessGrant(String access_token, String expires_in, String refresh_token, String re_expires_in, String user_id) {
        super(access_token, null, refresh_token, Long.valueOf(expires_in));
        this.user_id = user_id;
        this.re_expires_in = re_expires_in;
    }
}