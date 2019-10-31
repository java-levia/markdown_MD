package com.qz.springSocialCore.alipay.connect;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.qz.exception.QZException;
import com.qz.springSocialCore.alipay.api.AliPay;
import com.qz.springSocialCore.alipay.entity.AliPayUserinfo;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;


/**
 * @ClassName AliImpl
 * @Author Levia
 * @Date 2019-10-14 18:59
 **/
public class AliImpl extends AbstractOAuth2ApiBinding implements AliPay {

    private static final String REQUEST_URL = "https://openapi.alipay.com/gateway.do";

    private AlipayUserInfoShareResponse response;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static Logger log = LoggerFactory.getLogger(AliImpl.class);

    public AliImpl(String app_id, String auth_token, String aliPublicKey, String appPrivateKey){
        super(auth_token, TokenStrategy.OAUTH_TOKEN_PARAMETER);

        AlipayClient alipayClient = new DefaultAlipayClient(
                "https://openapi.alipay.com/gateway.do", app_id, appPrivateKey, "json", "utf-8",
                aliPublicKey, "RSA2");
        AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();
        AlipayUserInfoShareResponse response = null;
        try {
            response = alipayClient.execute(request, auth_token);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new QZException("第三方登陆获取支付宝信息失败");
        }
        if (response.isSuccess()) {
            log.info("调用成功");
            log.info(ReflectionToStringBuilder.toString(response));
            String userId = response.getUserId();
        } else {
            log.error("调用失败");
            log.info(response.getSubCode() + ":" + response.getSubMsg());
            throw new QZException("第三方登陆获取支付宝信息失败");
        }

        this.response = response;
    }


    @Override
    public AliPayUserinfo getAlipayUserinfo() {
        AliPayUserinfo aliPayUserinfo = new AliPayUserinfo();
        try {
            aliPayUserinfo.setAvatar(response.getAvatar());
            aliPayUserinfo.setNick_name(response.getNickName());
            aliPayUserinfo.setUser_id(response.getUserId());
            aliPayUserinfo.setCity(response.getCity());
            aliPayUserinfo.setProvince(response.getProvince());
            aliPayUserinfo.setIs_student_certified(response.getIsStudentCertified());
            aliPayUserinfo.setIs_certified(response.getIsCertified());
            aliPayUserinfo.setUser_status(response.getUserStatus());
            aliPayUserinfo.setUser_type(response.getUserType());
            log.info("获取到的用户信息:" + aliPayUserinfo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new QZException("获取支付宝用户信息失败");
        }

        return aliPayUserinfo;
    }
}