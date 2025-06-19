package com.community.service.impl;

import com.community.model.User;
import com.community.model.Badge;
import com.community.model.VolunteerHours;
import com.community.model.Event;
import com.community.repository.UserRepository;
import com.community.repository.BadgeRepository;
import com.community.repository.VolunteerHoursRepository;
import com.community.repository.EventRepository;
import com.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final PasswordEncoder passwordEncoder;
    private final VolunteerHoursRepository volunteerHoursRepository;
    private final EventRepository eventRepository;

    @Override
    public List<User> findTopContributors() {
        return userRepository.findTopContributors();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    @Transactional
    public User registerNewUser(User user) {
        // Set initial values
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.USER);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(User user) {
        User existingUser = findByUsername(user.getUsername());
        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void addPoints(Long userId, Integer points) {
        User user = findById(userId);
        user.setPoints(user.getPoints() + points);
        userRepository.save(user);
    }

    @Override
    public void awardBadge(Long userId, Long badgeId) {
        User user = findById(userId);
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge not found"));

        if (user.getPoints() >= badge.getRequiredPoints()) {
            user.getBadges().add(badge);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Insufficient points for this badge");
        }
    }

    @Override
    public List<Badge> getUserBadges(Long userId) {
        User user = findById(userId);
        return user.getBadges().stream().toList();
    }

    @Override
    public Double getTotalVolunteerHours(Long userId) {
        User user = findById(userId);
        return user.getVolunteerHours().stream()
                .mapToDouble(VolunteerHours::getHoursLogged)
                .sum();
    }

    @Override
    public void updatePassword(Long userId, String newPassword) {
        User user = findById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getFullName() != null) {
            existingUser.setFullName(user.getFullName());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userRepository.save(existingUser);
    }

    @Override
    public void addVolunteerHours(Long userId, Long eventId, Integer hours) {
        User user = findById(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        VolunteerHours vh = new VolunteerHours();
        vh.setUser(user);
        vh.setEvent(event);
        vh.setCheckInTime(event.getDate());
        vh.setHoursLogged(hours.doubleValue());
        vh.setVerified(true);
        volunteerHoursRepository.save(vh);
    }

    @Override
    public int getTotalParticipationHours(Long userId) {
        User user = findById(userId);
        return user.getParticipatedEvents().stream()
                .filter(e -> e.getDurationInHours() != null)
                .mapToInt(Event::getDurationInHours)
                .sum();
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
} 