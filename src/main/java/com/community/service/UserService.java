package com.community.service;

import com.community.model.User;
import com.community.model.Badge;
import java.util.List;

public interface UserService {
    List<User> findTopContributors();
    User findById(Long id);
    User findByEmail(String email);
    User findByUsername(String username);
    User registerNewUser(User user);
    User save(User user);
    User update(User user);
    void delete(Long id);
    void addPoints(Long userId, Integer points);
    void awardBadge(Long userId, Long badgeId);
    List<Badge> getUserBadges(Long userId);
    Double getTotalVolunteerHours(Long userId);
    void updatePassword(Long userId, String newPassword);
    User updateUser(Long id, User user);
} 