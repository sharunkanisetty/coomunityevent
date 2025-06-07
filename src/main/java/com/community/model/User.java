package com.community.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String bio;

    private String profilePicture;

    @Column(nullable = false)
    private Integer points = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @ManyToMany(mappedBy = "participants")
    private Set<Event> participatedEvents = new HashSet<>();

    @OneToMany(mappedBy = "organizer")
    private Set<Event> organizedEvents = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<VolunteerHours> volunteerHours = new HashSet<>();

    @OneToMany(mappedBy = "author")
    private Set<ForumPost> forumPosts = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "user_badges",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "badge_id")
    )
    private Set<Badge> badges = new HashSet<>();

    @Transient
    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    public enum Role {
        USER,
        ADMIN
    }

    // Helper method to check if passwords match
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }
} 