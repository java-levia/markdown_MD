package com.qz.springSocialCore.config;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * 不重写这个类会报错  我也不知道为什么。。。。。。
 */
@Component
public class SignInAdapterImpl implements SignInAdapter {
    @Override
    public String signIn(String s, Connection<?> connection, NativeWebRequest nativeWebRequest) {
        return null;
    }
}