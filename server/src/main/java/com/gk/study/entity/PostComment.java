package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("b_post_comment")
public class PostComment implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    public Long id;
    @TableField
    public String content;
    @TableField
    public String commentTime;
    @TableField
    public Integer likeCount;
    @TableField
    public String userId;
    @TableField
    public String postId;

    // 数据库中没有，用于联合查询返回给前端展示
    @TableField(exist = false)
    public String username;
}