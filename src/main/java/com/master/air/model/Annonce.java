package com.master.air.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "annonce")
public class Annonce {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 64)
    @Column(nullable = false, length = 64)
    private String title;

    @NotBlank @Size(max = 256)
    @Column(nullable = false, length = 256)
    private String description;

    @NotBlank @Size(max = 64)
    @Column(nullable = false, length = 64)
    private String adress;

    @NotBlank @Email @Size(max = 64)
    @Column(nullable = false, length = 64)
    private String mail;

    @Column(nullable = false, updatable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AnnonceStatus status = AnnonceStatus.DRAFT;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @PrePersist void onCreate() {
        if (date == null) date = LocalDateTime.now();
        if (status == null) status = AnnonceStatus.DRAFT;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public String getAdress() { return adress; }
    public void setAdress(String a) { this.adress = a; }
    public String getMail() { return mail; }
    public void setMail(String m) { this.mail = m; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime d) { this.date = d; }
    public AnnonceStatus getStatus() { return status; }
    public void setStatus(AnnonceStatus s) { this.status = s; }
    public Long getVersion() { return version; }
    public void setVersion(Long v) { this.version = v; }
    public User getAuthor() { return author; }
    public void setAuthor(User a) { this.author = a; }
    public Category getCategory() { return category; }
    public void setCategory(Category c) { this.category = c; }
}
