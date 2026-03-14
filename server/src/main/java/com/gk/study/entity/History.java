package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("b_history")
public class History implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @TableField
    private String year;
    @TableField
    private String title;
    @TableField
    private String brief;
    @TableField
    private String image;
    @TableField
    private String detail; // 数据库里是JSON字符串
}