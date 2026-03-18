package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
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

    // 👇 新增的四个字段
    @TableField
    private String birthplace;
    @TableField
    private String birthDate;
    @TableField
    private String deathDate;
    @TableField
    private String achievements;
    // 👆

    @TableField
    private String detail;
    @TableField
    private String createTime;

    @TableField(exist = false)
    private MultipartFile imageFile;
}