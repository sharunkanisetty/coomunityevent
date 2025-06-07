package com.community.controller;

import com.community.model.ForumPost;
import com.community.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @GetMapping
    public ResponseEntity<List<ForumPost>> getLatestPosts() {
        return ResponseEntity.ok(forumService.findLatestPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ForumPost> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(forumService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ForumPost> createPost(@RequestBody ForumPost post) {
        return ResponseEntity.ok(forumService.save(post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ForumPost> updatePost(@PathVariable Long id, @RequestBody ForumPost post) {
        post.setId(id);
        return ResponseEntity.ok(forumService.save(post));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        forumService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ForumPost>> getPostsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(forumService.findByCategory(category));
    }

    @PostMapping("/{postId}/likes/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable Long postId, @PathVariable Long userId) {
        forumService.addLike(postId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/likes/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable Long postId, @PathVariable Long userId) {
        forumService.removeLike(postId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/replies")
    public ResponseEntity<List<ForumPost>> getReplies(@PathVariable Long postId) {
        return ResponseEntity.ok(forumService.findReplies(postId));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long id) {
        forumService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }
} 