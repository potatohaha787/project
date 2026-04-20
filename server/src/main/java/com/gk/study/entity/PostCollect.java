package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("b_post_collect")
public class PostCollect implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    @TableField
    public Long postId;

    @TableField
    public Long userId;

    @TableField
    public String collectTime;
}