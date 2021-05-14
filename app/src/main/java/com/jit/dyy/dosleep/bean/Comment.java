package com.jit.dyy.dosleep.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论id
     */
    private Integer commentId;

    /**
     * 被评论帖子id
     */
    private Integer postId;

    /**
     * 评论者id
     */
    private Integer userId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论时间
     */
    private Date time;

    private Integer state;

    private String userName;

    private String headImg;
}
