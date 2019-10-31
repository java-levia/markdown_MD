package com.qz.springSocialCore.properties;

import lombok.Data;

/**
 * @ClassName QQProperties
 * @Author Levia
 * @Date 2019/7/28 18:33
 **/
@Data
public class QQProperties {

    private String providerId="qq";

    private String appid;

    private String appsecret;

    private String filterProcessUrl="auth";
}    