package com.qz.springSocialCore.alipay.connect;

import com.qz.springSocialCore.alipay.api.AliPay;
import com.qz.springSocialCore.alipay.entity.AliPayUserinfo;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

/**
 * @ClassName AliApiAdapter
 * @Author Levia
 * @Date 2019-10-15 12:38
 **/
public class AliApiAdapter implements ApiAdapter<AliPay> {


    @Override
    public boolean test(AliPay api) {
        return true;
    }

    @Override
    public void setConnectionValues(AliPay api, ConnectionValues values) {
        AliPayUserinfo userInfo = api.getAlipayUserinfo();
        values.setImageUrl(userInfo.getAvatar());
        values.setProfileUrl(null);
        values.setProviderUserId(userInfo.getUser_id());
        values.setDisplayName(userInfo.getNick_name());
    }

    @Override
    public UserProfile fetchUserProfile(AliPay api) {
        return null;
    }

    @Override
    public void updateStatus(AliPay api, String message) {

    }
}