package com.jit.dyy.dosleep.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Diary implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日记id
     */
    private Integer diaryId;

    /**
     * 日记所有者id
     */
    private Integer userId;

    /**
     * 日记内容
     */
    private String content;

    /**
     * 日记创建日期
     */
    private Date createtime;

    /**
     * 日记修改日期
     */
    private Date updatetime;

    private Integer state;


}
