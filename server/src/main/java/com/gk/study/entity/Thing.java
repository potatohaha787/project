package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("b_thing")
public class Thing implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    public Long id;
    @TableField
    public String title;
    @TableField
    public String cover;
    @TableField
    public String description;
    @TableField
    public String price;
    @TableField
    public String level;
    @TableField
    public String address;
    @TableField
    public String status;
    @TableField
    public String createTime;
    @TableField
    public String score;
    @TableField
    public String pv;
    @TableField
    public String repertory; // 库存
    @TableField
    public String recommendCount;
    @TableField
    public String wishCount;
    @TableField
    public String collectCount;
    // 必须要有的分类 ID 映射
    @TableField("classification_id")
    private Long classificationId;

    // 前面加过的 location 映射
    @TableField("location")
    private String location;

    @TableField(exist = false)
    public List<Long> tags; // 标签

    @TableField(exist = false)
    public MultipartFile imageFile;

}
