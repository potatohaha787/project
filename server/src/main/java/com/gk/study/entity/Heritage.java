package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("b_heritage")
public class Heritage {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField
    private String title;
    @TableField
    private String level;
    @TableField
    private String tag;
    @TableField
    private String description;
    @TableField
    private String content;
    @TableField
    private String image;
    @TableField
    public String createTime;
}