package com.master.air.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 50)
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.ROLE_USER;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public User() {}
    public User(String username, String email, String password, Role role) {
        this.username = username; this.email = email;
        this.password = password; this.role = role;
    }

    @PrePersist void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
    public Role getRole() { return role; }
    public void setRole(Role r) { this.role = r; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime c) { this.createdAt = c; }
}
