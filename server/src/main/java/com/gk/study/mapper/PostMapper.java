package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
    // 如果后续需要手写复杂的 SQL（如关联查询用户头像等），可以定义在这里，并在 XML 中实现
}