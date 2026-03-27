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
@TableName("b_post")
public class Post implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    @TableField("user_id")
    public Long userId;

    @TableField("type")
    public String type; // 类型: guide(游记), ask(求助), share(分享), mate(结伴)

    @TableField("title")
    public String title;

    @TableField("cover")
    public String cover;

    @TableField("content")
    public String content;

    @TableField("location")
    public String location;

    @TableField("pv")
    public Integer pv;

    @TableField("like_count")
    public Integer likeCount;

    @TableField("collect_count")
    public Integer collectCount;

    @TableField("status")
    public String status; // 0草稿 1正常 2隐藏

    @TableField("create_time")
    public String createTime;

    // ----- 下面是非数据库映射字段，用于前端展示 -----

    @TableField(exist = false)
    public String authorName; // 作者昵称

    @TableField(exist = false)
    public String authorAvatar; // 作者头像

    @TableField(exist = false)
    public List<Long> tags; // 标签ID列表

    @TableField(exist = false)
    public MultipartFile imageFile; // 用于接收上传的封面图片
}