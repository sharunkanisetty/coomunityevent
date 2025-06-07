package com.community.repository;

import com.community.model.Badge;
import com.community.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    List<Badge> findByUser(User user);
    List<Badge> findByUserAndType(User user, Badge.BadgeType type);
    long countByUser(User user);
} 