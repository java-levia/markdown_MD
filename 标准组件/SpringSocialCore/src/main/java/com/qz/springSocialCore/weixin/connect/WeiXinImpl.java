package com.qz.springSocialCore.weixin.connect;

import com.qz.springSocialCore.weixin.api.WXInfo;
import com.qz.springSocialCore.weixin.api.WeiXin;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class WeiXinImpl extends AbstractOAuth2ApiBinding implements WeiXin {

    private String accessToken;

    private String openId;

    private String GET_WEIXIN_USERINFO = "https://api.weixin.qq.com/sns/userinfo?openid=%s";

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 构造方法中需要传入accessToken和
     */
    public WeiXinImpl(String accessToken){
        super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
    }
    /**
     * 这个方法用于获取微信平台的用户信息
     * @return
     */
    @Override
    public WXInfo getWXInfo(String openId) {
        String url = String.format(GET_WEIXIN_USERINFO, openId);
        String wxInfo = this.getRestTemplate().getForObject(url, String.class);
        if(StringUtils.contains(wxInfo, "errcode")){
            return null;
        }
        WXInfo wxInfo1 = null;
        try {
           wxInfo1 = objectMapper.readValue(wxInfo, WXInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wxInfo1;
    }

    /**
     * 这里需要覆盖一个方法，因为标准实现中默认注册的转换器字符集为ISO8859-1,而微信返回的用户信息字符集是UTF-8,所以这里需要覆盖原来的方法
     */
    @Override
    protected List<HttpMessageConverter<?>> getMessageConverters(){
        List<HttpMessageConverter<?>> messageConverters = super.getMessageConverters();
        messageConverters.remove(0);
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return messageConverters;
    }

}
