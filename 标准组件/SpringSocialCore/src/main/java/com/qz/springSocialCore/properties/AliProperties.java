package com.qz.springSocialCore.properties;

import lombok.Data;

/**
 * @ClassName AliProperties
 * @Author Levia
 * @Date 2019-10-15 12:30
 **/
@Data
public class AliProperties {

    private String appid;
    private String providerId = "alipay";
    private String appPrivateKey;
    private String alipayPublicKey;
    private String signType = "RSA2";

}    