package com.community.service.impl;

import com.community.model.ForumPost;
import com.community.model.User;
import com.community.repository.ForumRepository;
import com.community.repository.UserRepository;
import com.community.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ForumServiceImpl implements ForumService {

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ForumPost> findLatestPosts() {
        return forumRepository.findLatestPosts();
    }

    @Override
    public ForumPost findById(Long id) {
        return forumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Forum post not found"));
    }

    @Override
    public ForumPost save(ForumPost post) {
        if (post.getId() == null) {
            post.setCreatedAt(LocalDateTime.now());
        } else {
            post.setUpdatedAt(LocalDateTime.now());
        }
        return forumRepository.save(post);
    }

    @Override
    public void delete(Long id) {
        forumRepository.deleteById(id);
    }

    @Override
    public List<ForumPost> findByCategory(String category) {
        return forumRepository.findByCategory(category);
    }

    @Override
    public void addLike(Long postId, Long userId) {
        ForumPost post = findById(postId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!post.getLikes().contains(user)) {
            post.getLikes().add(user);
            forumRepository.save(post);
        }
    }

    @Override
    public void removeLike(Long postId, Long userId) {
        ForumPost post = findById(postId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        post.getLikes().remove(user);
        forumRepository.save(post);
    }

    @Override
    public List<ForumPost> findReplies(Long parentPostId) {
        ForumPost parentPost = findById(parentPostId);
        return forumRepository.findByParentPost(parentPost);
    }

    @Override
    public void incrementViewCount(Long postId) {
        ForumPost post = findById(postId);
        post.setViewCount(post.getViewCount() + 1);
        forumRepository.save(post);
    }
} 