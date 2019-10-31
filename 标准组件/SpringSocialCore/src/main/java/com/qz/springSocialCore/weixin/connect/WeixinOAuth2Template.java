package com.qz.springSocialCore.weixin.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qz.mybatis.mapper.ThirdPlatformInfoMapper;
import org.apache.commons.collections.MapUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 在微信第三方登陆的流程中，在获取accessToken的同时就获取到了openId，与默认的OAuth2Template的实现不同，所以需要自定义实现
 *
 * 1.微信在获取accessToken的同时，页返回了用户的唯一Id  openId，所以我们得重写AccessGrant这个类，添加一个openId字段用来接收这个参数
 * 2.Social标准实现的OAuth2Template与微信实现的OAuth2在参数的命名上有差异，所以需要对涉及到请求的方法进行重写，这些方法包括
 *      * 刷新Token的方法
 *      * 获取accessToken的方法
 *
 */
public class WeixinOAuth2Template extends OAuth2Template {

    Logger logger = LoggerFactory.getLogger(WeixinOAuth2Template.class);

    private String appId;

    private String appSecret;

    private String accessTokenUrl;

    private static final String REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token";

    public WeixinOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
        super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
        setUseParametersForClientAuthentication(true);

        this.appId = clientId;
        this.appSecret = clientSecret;
        this.accessTokenUrl = accessTokenUrl;
    }

    /**
     * 按照微信第三方登陆的官方文档内容，拼接获取access_token的请求
     * @param authorizationCode code
     * @param redirectUri 回调uri
     * @param additionalParameters
     * @return
     */
    @Override
    public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> additionalParameters) {
        StringBuilder accessTokenRequestUrl = new StringBuilder(accessTokenUrl);

        //TODO 根据学校id查询学校的商户号相关信息
        String resource = "mybatis-config.xml";
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //和前端约定在authorizationCode尾端加上“_{学校id}”,
        String schoolCode = StringUtils.substringAfter(authorizationCode,"&");
        String code = StringUtils.substringBefore(authorizationCode,"&");
        ArrayList parameter = new ArrayList();
        parameter.add(schoolCode);
        parameter.add("4");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(reader);
        SqlSession session = factory.openSession();
        HashMap<String, Object> thirdPlatformInfo = session.selectOne(
                "com.qz.mybatis.mapper.ThirdPlatformInfoMapper.queryInfoList", parameter);

        accessTokenRequestUrl.append("?appid="+thirdPlatformInfo.get("appid"));
        //accessTokenRequestUrl.append("&redirect_uri="+redirectUri);
        accessTokenRequestUrl.append("&code="+code);
        accessTokenRequestUrl.append("&secret="+thirdPlatformInfo.get("appsecret"));
        accessTokenRequestUrl.append("&grant_type=authorization_code");
        return getAccessToken(accessTokenRequestUrl);
    }

    /**
     * 获取accessToken等信息的具体请求
     * @param accessTokenRequestUrl
     * @return
     */
    private AccessGrant getAccessToken(StringBuilder accessTokenRequestUrl) {
        logger.info("获取AccessToken的请求Url："+accessTokenRequestUrl.toString());
        String response = getRestTemplate().getForObject(accessTokenRequestUrl.toString(), String.class);
        logger.info("微信平台响应内容："+response);
        Map<String, Object> result = null;
        try {
            result = new ObjectMapper().readValue(response, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //返回错误码时直接返回空
        if(result.get("errcode")!=null){
            int errcode = (int)result.get("errcode");
            String errmsg =  (String)result.get("errmsg");
            throw new RuntimeException("获取accessToken失败，errcode="+errcode+"错误信息："+errmsg);
        }

        WeixinAccessGrant accessGrant = new WeixinAccessGrant(
                MapUtils.getString(result, "access_token"),
                MapUtils.getString(result, "scope"),
                MapUtils.getString(result, "refresh_token"),
                MapUtils.getLong(result, "expires_in")
        );
        accessGrant.setOpenId(MapUtils.getString(result, "openid"));


        return accessGrant;
    }

    /**
     * 刷新token
     * @param refreshToken
     * @param additionalParameters
     * @return
     */
    @Override
    public AccessGrant refreshAccess(String refreshToken, MultiValueMap<String, String> additionalParameters) {
        StringBuilder refreshTokenUrl = new StringBuilder(REFRESH_TOKEN_URL);
        refreshTokenUrl.append("?appid="+appId);
        refreshTokenUrl.append("&grant_type=refresh_token");
        refreshTokenUrl.append("&refresh_token="+refreshToken);
        return getAccessToken(refreshTokenUrl);
    }

    /**
     * 构建获取授权码的请求。也就是引导用户跳转到微信的地址。
     */
    public String buildAuthenticateUrl(OAuth2Parameters parameters) {
        String url = super.buildAuthenticateUrl(parameters);
        url = url + "&appid="+appId+"&scope=snsapi_login";
        return url;
    }

    public String buildAuthorizeUrl(OAuth2Parameters parameters) {
        return buildAuthenticateUrl(parameters);
    }

    /**
     * 微信返回的contentType是html/text，添加相应的HttpMessageConverter来处理。
     */
    protected RestTemplate createRestTemplate() {
        RestTemplate restTemplate = super.createRestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }
}
