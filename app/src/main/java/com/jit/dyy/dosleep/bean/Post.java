package com.jit.dyy.dosleep.bean;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 帖子id
     */
    private Integer postId;

    /**
     * 发帖用户id
     */
    private Integer userId;

    /**
     * 帖子内容
     */
    private String postContent;

    /**
     * 帖子附件
     */
    private String postAnnex;

    /**
     * 发帖时间
     */
    private Date postTime;

    /**
     * 浏览次数
     */
    private Integer postViews;

    /**
     * 被点赞次数
     */
    private Integer postLike;

    /**
     * 被评论数
     */
    private Integer postComment;

    /**
     * 帖子热度
     */
    private Integer postClout;

    private Integer state;

    private String userName;

    private String headImg;

    private Boolean islike;
}
