package com.qz.springSocialCore.properties;

import lombok.Data;

@Data
public class WeixinProperties {
    private String appId;

    private String appSecret;

    //providerId和filterProcessUrl两个属性决定了发送qq登陆请求的链接和code等信息返回的回调链接
    private String providerId="weixin";

    private String filterProcessUrl = "/qqLogin";
}
