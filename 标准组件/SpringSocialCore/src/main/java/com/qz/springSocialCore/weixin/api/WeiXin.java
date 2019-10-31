package com.qz.springSocialCore.weixin.api;

import java.io.IOException;

public interface WeiXin {

    WXInfo getWXInfo(String openId) throws IOException;
}
