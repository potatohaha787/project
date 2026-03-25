package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("b_food")
public class Food {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField
    private String name;
    @TableField
    private String description;
    @TableField
    private String image;
    @TableField
    private String content;
    @TableField("grid_class")
    private String gridClass;
    @TableField
    public String createTime;
}