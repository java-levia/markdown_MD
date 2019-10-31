package com.qz.springSocialCore.qq.connect;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

public class QQOAuth2Template extends OAuth2Template {

    private Logger logger = LoggerFactory.getLogger(getClass());


    public QQOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
        super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
        //OAuth2Template这个类中有一个属性useParametersForClientAuthentication，只有这个属性为true的时候，才会带上client_id/client_secret，所以需要在子类构造函数对这个属性默认为true
        setUseParametersForClientAuthentication(true);
    }

    /**
     * 由于QQ互联返回的数据格式并不是Social期望的Json字符串，而是[text/html]所以需要添加一个转换器用于解析这种格式的数据
     * @return
     */
    @Override
    protected RestTemplate createRestTemplate() {
        RestTemplate template = super.createRestTemplate();
        template.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));

        return template;
    }

    /**
     * 由于QQ互联返回AccessToken是一个&符号隔开的字符串，并不是标准的Social格式，所以在这里对字符串做处理
     * @param accessTokenUrl
     * @param parameters
     * @return
     */
    @Override
    protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
        String responseStr = getRestTemplate().postForObject(accessTokenUrl, parameters, String.class);
        logger.info("获取到的accessToken的响应："+ responseStr);
        String[] items = StringUtils.split(responseStr, "&");

        String accessToken = StringUtils.substringAfterLast(items[0], "=");
        Long expireIn = new Long(StringUtils.substringAfterLast(items[1], "="));
        String refreshToken = StringUtils.substringAfterLast(items[2], "=");
        return new AccessGrant(accessToken, null, refreshToken, expireIn);
    }
}
