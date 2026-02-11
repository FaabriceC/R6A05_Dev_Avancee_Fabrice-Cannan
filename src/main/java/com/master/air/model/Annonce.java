package com.master.air.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "annonce")
@NamedQueries({
        @NamedQuery(
                name = "Annonce.findByKeyword",
                query = "SELECT a FROM Annonce a WHERE LOWER(a.title) LIKE LOWER(:keyword) OR LOWER(a.description) LIKE LOWER(:keyword) ORDER BY a.date DESC"
        ),
        @NamedQuery(
                name = "Annonce.countByKeyword",
                query = "SELECT COUNT(a) FROM Annonce a WHERE LOWER(a.title) LIKE LOWER(:keyword) OR LOWER(a.description) LIKE LOWER(:keyword)"
        ),
        @NamedQuery(
                name = "Annonce.findByCategoryAndStatus",
                query = "SELECT a FROM Annonce a WHERE (:categoryId IS NULL OR a.category.id = :categoryId) AND (:status IS NULL OR a.status = :status) ORDER BY a.date DESC"
        ),
        @NamedQuery(
                name = "Annonce.countByCategoryAndStatus",
                query = "SELECT COUNT(a) FROM Annonce a WHERE (:categoryId IS NULL OR a.category.id = :categoryId) AND (:status IS NULL OR a.status = :status)"
        )
})
public class Annonce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 64, message = "Le titre ne doit pas dépasser 64 caractères")
    @Column(nullable = false, length = 64)
    private String title;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 256, message = "La description ne doit pas dépasser 256 caractères")
    @Column(nullable = false, length = 256)
    private String description;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 64, message = "L'adresse ne doit pas dépasser 64 caractères")
    @Column(nullable = false, length = 64)
    private String adress;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Size(max = 64, message = "L'email ne doit pas dépasser 64 caractères")
    @Column(nullable = false, length = 64)
    private String mail;

    @Column(nullable = false, updatable = false)
    private Timestamp date;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AnnonceStatus status = AnnonceStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public Annonce() {
    }

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = new Timestamp(System.currentTimeMillis());
        }
        if (status == null) {
            status = AnnonceStatus.DRAFT;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public AnnonceStatus getStatus() {
        return status;
    }

    public void setStatus(AnnonceStatus status) {
        this.status = status;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Annonce annonce = (Annonce) o;
        return Objects.equals(id, annonce.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Annonce{id=" + id + ", title='" + title + "', status=" + status + "}";
    }
}
