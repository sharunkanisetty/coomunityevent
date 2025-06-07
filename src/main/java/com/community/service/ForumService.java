package com.community.service;

import com.community.model.ForumPost;
import java.util.List;

public interface ForumService {
    List<ForumPost> findLatestPosts();
    ForumPost findById(Long id);
    ForumPost save(ForumPost post);
    void delete(Long id);
    List<ForumPost> findByCategory(String category);
    void addLike(Long postId, Long userId);
    void removeLike(Long postId, Long userId);
    List<ForumPost> findReplies(Long parentPostId);
    void incrementViewCount(Long postId);
} 