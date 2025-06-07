package com.community.repository;

import com.community.model.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumRepository extends JpaRepository<ForumPost, Long> {
    List<ForumPost> findByCategory(String category);
    
    @Query("SELECT p FROM ForumPost p WHERE p.parentPost IS NULL ORDER BY p.createdAt DESC")
    List<ForumPost> findLatestPosts();
    
    List<ForumPost> findByParentPost(ForumPost parentPost);
} 