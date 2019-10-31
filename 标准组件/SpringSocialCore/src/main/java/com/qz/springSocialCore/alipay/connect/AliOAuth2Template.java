package com.qz.springSocialCore.alipay.connect;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qz.exception.QZException;
import com.qz.springSocialCore.alipay.entity.AliAccessGrant;
import com.qz.springSocialCore.properties.AliProperties;
import com.qz.springSocialCore.properties.LeviaSocialProperties;
import com.qz.springSocialCore.weixin.connect.WeixinAccessGrant;
import com.qz.utils.EmptyUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName AliOAuth2Template
 * @Author Levia
 * @Date 2019-10-14 20:06
 **/
public class AliOAuth2Template extends OAuth2Template {

    private static Logger log = LoggerFactory.getLogger(AliOAuth2Template.class);

    private String appId;

    private String accessTokenUrl;

    private String appPrivateKey;

    private String aliPublicKey;

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AliOAuth2Template(String clientId, String appPrivateKey, String aliPublicKey, String authorizeUrl, String accessTokenUrl) {
        super(clientId, appPrivateKey, authorizeUrl, accessTokenUrl);
        this.appId = clientId;
        this.accessTokenUrl = accessTokenUrl;
        this.appPrivateKey = appPrivateKey;
        this.aliPublicKey = aliPublicKey;
    }


    /**
     * 按照支付宝第三方登陆的官方文档内容，拼接获取access_token的请求
     * @param authorizationCode code
     * @param redirectUri 回调uri
     * @param additionalParameters
     * @return
     */
    @Override
    public AliAccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> additionalParameters) {
        return postForAccessGrant(authorizationCode);
    }

    /**
     * 发送请求获取accessToken
     * @param authorizationCode
     * @return
     */
    protected AliAccessGrant postForAccessGrant(String authorizationCode) {

        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                appId, appPrivateKey, "json", "utf-8", aliPublicKey, "RSA2");
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(authorizationCode);
        request.setGrantType("authorization_code");
        AlipaySystemOauthTokenResponse oauthTokenResponse = null;
        try {
            oauthTokenResponse = alipayClient.execute(request);
            if(oauthTokenResponse.getCode()!=null){
                throw new QZException("支付宝登陆失败");
            }
        } catch (AlipayApiException e) {
            //处理异常
            e.printStackTrace();
            throw new QZException("支付宝登陆失败");
        }
        return createAccessGrant(oauthTokenResponse.getAccessToken(),
                oauthTokenResponse.getExpiresIn(),
                oauthTokenResponse.getRefreshToken(),
                oauthTokenResponse.getReExpiresIn(),
                oauthTokenResponse.getUserId());
    }


    protected AliAccessGrant createAccessGrant(String access_token, String expires_in, String refresh_token, String re_expires_in, String user_id) {
        return new AliAccessGrant(access_token, expires_in, refresh_token, re_expires_in, user_id);
    }


    /**
     * 构建获取授权码的请求。也就是引导用户跳转到支付宝的地址。
     */
    public String buildAuthenticateUrl(OAuth2Parameters parameters) {
        String url = super.buildAuthenticateUrl(parameters);
        url = url + "&app_id="+appId+"&scope=auth_user&redirect_uri=http%3a%2f%2fwww.qzdatasoft.com%3a18380%2fauth%2falipay";
        return url;
    }

    /**
     * 支付宝返回的contentType是html/text，添加相应的HttpMessageConverter来处理。
     */
    protected RestTemplate createRestTemplate() {
        RestTemplate restTemplate = super.createRestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }
}