package com.jit.dyy.dosleep.bean;


import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户密码
     */
    private String userPwd;

    /**
     * 用户手机
     */
    private String userTel;

    /**
     * 用户性别
     */
    private String sex;

    /**
     * 用户生日
     */
    private Date birth;

    /**
     * 用户地区
     */
    private String area;

    /**
     * 用户头像
     */
    private String headImg;

    /**
     * 用户签名
     */
    private String slogan;

    /**
     * 注册日期
     */
    private Date registration;

    /**
     * 用于判断用户是否存在
     */
    private Integer state;

    public User() {
    }

    public User(Integer userId, String userName, String userPwd, Integer state) {
        this.userId = userId;
        this.userName = userName;
        this.userPwd = userPwd;
        this.state = state;
    }
}
