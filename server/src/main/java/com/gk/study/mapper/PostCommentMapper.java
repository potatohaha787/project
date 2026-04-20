package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.PostComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface PostCommentMapper extends BaseMapper<PostComment> {

    // 👇 在 SELECT 语句中加上 u.nickname
    @Select("SELECT c.*, u.username, u.avatar, u.nickname " +
            "FROM b_post_comment c " +
            "LEFT JOIN b_user u ON c.user_id = u.id " +
            "WHERE c.post_id = #{postId} " +
            "ORDER BY c.comment_time DESC")
    List<PostComment> listPostComments(String postId);

    @org.apache.ibatis.annotations.Update("UPDATE b_post_comment SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(Long id);
    @Select("SELECT c.*, u.username, p.title, p.cover " +
            "FROM b_post_comment c " +
            "LEFT JOIN b_user u ON c.user_id = u.id " +
            "LEFT JOIN b_post p ON c.post_id = p.id " +
            "WHERE c.user_id = #{userId} " +
            "ORDER BY c.comment_time DESC")
    List<PostComment> listUserPostComments(String userId);

    // 👇 新增：查询别人对我发布的文章的评论
    @Select("SELECT c.*, u.username, u.nickname, u.avatar, p.title " +
            "FROM b_post_comment c " +
            "LEFT JOIN b_user u ON c.user_id = u.id " +
            "JOIN b_post p ON c.post_id = p.id " +
            "WHERE p.user_id = #{userId} AND c.user_id != #{userId} " +
            "ORDER BY c.comment_time DESC")
    List<PostComment> listCommentsOnMyPosts(String userId);

    // 👇 新增：后台查询所有游记评论（支持关键字搜索）
    @Select("<script>" +
            "SELECT c.*, u.username, p.title " +
            "FROM b_post_comment c " +
            "LEFT JOIN b_user u ON c.user_id = u.id " +
            "LEFT JOIN b_post p ON c.post_id = p.id " +
            "<where>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "c.content LIKE CONCAT('%', #{keyword}, '%') " +
            "</if>" +
            "</where>" +
            "ORDER BY c.comment_time DESC" +
            "</script>")
    List<PostComment> getAdminPostCommentList(@org.apache.ibatis.annotations.Param("keyword") String keyword);
}