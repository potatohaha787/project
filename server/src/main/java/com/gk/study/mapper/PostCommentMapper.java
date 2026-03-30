package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.PostComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface PostCommentMapper extends BaseMapper<PostComment> {

    // 多表联查，直接连 user 表拿到发帖人名字
    @Select("SELECT c.*, u.username " +
            "FROM b_post_comment c " +
            "LEFT JOIN b_user u ON c.user_id = u.id " +
            "WHERE c.post_id = #{postId} " +
            "ORDER BY c.comment_time DESC")
    List<PostComment> listPostComments(String postId);
    @org.apache.ibatis.annotations.Update("UPDATE b_post_comment SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(Long id);
}