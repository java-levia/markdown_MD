package com.qz.springSocialCore.weixin.connect;

import com.qz.springSocialCore.weixin.api.WXInfo;
import com.qz.springSocialCore.weixin.api.WeiXin;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

import java.io.IOException;

public class WeixinApiAdapter implements ApiAdapter<WeiXin> {

    private String openId;

    public WeixinApiAdapter(){}

    /**
     * 与QQ不同的是，这里需要注入openId
     * @param openId
     */
    public WeixinApiAdapter(String openId) {
        this.openId = openId;
    }

    @Override
    public boolean test(WeiXin weiXin) {
        return true;
    }

    @Override
    public void setConnectionValues(WeiXin weiXin, ConnectionValues values) {
        try {
            WXInfo info = weiXin.getWXInfo(openId);
            values.setDisplayName(info.getNickname());
            values.setProviderUserId(info.getOpenid());
            values.setProfileUrl(null);
            values.setImageUrl(info.getHeadimgurl());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public UserProfile fetchUserProfile(WeiXin weiXin) {
        return null;
    }

    @Override
    public void updateStatus(WeiXin weiXin, String s) {

    }
}
