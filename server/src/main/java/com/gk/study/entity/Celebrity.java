package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("b_celebrity")
public class Celebrity implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField
    private String title;
    @TableField
    private String image;
    @TableField
    private String brief;
    @TableField
    private String tags;
    @TableField
    private String detail;
    @TableField
    private String createTime;
}