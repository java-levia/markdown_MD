package com.qz.springSocialCore.alipay.entity;

import lombok.Data;

/**
 * @ClassName AliPayUserinfo
 * @Author Levia
 * @Date 2019-10-14 18:48
 **/
@Data
public class AliPayUserinfo {
    private String user_id;
    private String avatar;
    private String province;
    private String city;
    private String nick_name;
    private String is_student_certified;
    private String user_type;
    private String user_status;
    private String is_certified;
    private String gender;

    private String code;
    private String msg;
    private String sign;
}    