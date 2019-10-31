package com.qz.springSocialCore.properties;

import lombok.Data;

/**
 * 社交绑定公共配置
 * @ClassName SocialPublicProperties
 * @Author Levia
 * @Date 2019-09-07 14:27
 **/
@Data
public class SocialPublicProperties {

    private String signUpUrl = "/guideUser/unBindOrRegist"; //社交账号没有与平台账号绑定时，引导用户进行绑定

    private String socialBindUrl = "/appUser/socialBind";

    private String socialRegistUrl= "/appUser/socialRegist";

    private Boolean isApp=true;
}    