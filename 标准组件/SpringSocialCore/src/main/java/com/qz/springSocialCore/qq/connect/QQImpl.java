package com.qz.springSocialCore.qq.connect;

import com.qz.springSocialCore.qq.api.QQ;
import com.qz.springSocialCore.qq.entity.QQUserInfo;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

/**
 * @ClassName QQImpl
 * @Author Levia
 * @Date 2019/7/28 15:58
 **/
public class QQImpl extends AbstractOAuth2ApiBinding implements QQ {

    private Logger logger = LoggerFactory.getLogger(QQImpl.class);

    private String openid;

    private String appid;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String GET_OPENID_URL="https://graph.qq.com/oauth2.0/me?access_token=%s";

    private static final String GET_USER_INFO_URL="https://graph.qq.com/user/get_user_info?oauth_consumer_key=%s&openid=%s";

    public QQImpl(String access_token, String appid){
        super(access_token, TokenStrategy.ACCESS_TOKEN_PARAMETER);
        this.appid=appid;
        String url = String.format(GET_OPENID_URL, access_token);
        String result = getRestTemplate().getForObject(url, String.class);
        logger.info("获取到的openId信息："+result);

        this.openid= StringUtils.substringBetween(result, "\"openid\":\"","\"}");
    }

    @Override
    public QQUserInfo getUserInfo() {
        String url = String.format(GET_USER_INFO_URL, appid, openid);
        String userInfo= getRestTemplate().getForObject(url, String.class);

        try {
            QQUserInfo user = objectMapper.readValue(userInfo, QQUserInfo.class);
            logger.info("获取到的用户信息："+ user);
            user.setOpenId(openid);
            return user;
        } catch (Exception e) {
            throw new RuntimeException("获取用户信息失败", e);
        }
    }
}