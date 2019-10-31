package com.qz.springSocialCore.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName SocialProperties
 * @Author Levia
 * @Date 2019/7/28 18:32
 **/
@Component
@ConfigurationProperties(prefix = "levia.social")
@Data
public class LeviaSocialProperties {

    private QQProperties qq = new QQProperties();

    private WeixinProperties weixin = new WeixinProperties() ;

    private AliProperties alipay = new AliProperties();

    private SocialPublicProperties publicProperties = new SocialPublicProperties();

    private String filterProcessUrl = "/auth";
}    