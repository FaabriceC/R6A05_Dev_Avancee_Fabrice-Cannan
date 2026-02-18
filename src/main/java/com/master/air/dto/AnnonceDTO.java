package com.master.air.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.master.air.model.Annonce;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnnonceDTO {

    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 64, message = "Le titre ne doit pas depasser 64 caracteres")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 256, message = "La description ne doit pas depasser 256 caracteres")
    private String description;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 64)
    private String adress;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit etre valide")
    @Size(max = 64)
    private String mail;

    private String date;
    private String status;

    @NotNull(message = "La categorie est obligatoire")
    private Long categoryId;
    private String categoryLabel;

    private Long authorId;
    private String authorUsername;

    private Long version;

    public AnnonceDTO() {}

    private AnnonceDTO(Builder b) {
        this.id = b.id;
        this.title = b.title;
        this.description = b.description;
        this.adress = b.adress;
        this.mail = b.mail;
        this.date = b.date;
        this.status = b.status;
        this.categoryId = b.categoryId;
        this.categoryLabel = b.categoryLabel;
        this.authorId = b.authorId;
        this.authorUsername = b.authorUsername;
        this.version = b.version;
    }

    public static AnnonceDTO fromEntity(Annonce a) {
        return new Builder()
                .id(a.getId())
                .title(a.getTitle())
                .description(a.getDescription())
                .adress(a.getAdress())
                .mail(a.getMail())
                .date(a.getDate() != null ? a.getDate().toInstant().toString() : null)
                .status(a.getStatus() != null ? a.getStatus().name() : null)
                .categoryId(a.getCategory() != null ? a.getCategory().getId() : null)
                .categoryLabel(a.getCategory() != null ? a.getCategory().getLabel() : null)
                .authorId(a.getAuthor() != null ? a.getAuthor().getId() : null)
                .authorUsername(a.getAuthor() != null ? a.getAuthor().getUsername() : null)
                .version(a.getVersion())
                .build();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private String adress;
        private String mail;
        private String date;
        private String status;
        private Long categoryId;
        private String categoryLabel;
        private Long authorId;
        private String authorUsername;
        private Long version;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String desc) { this.description = desc; return this; }
        public Builder adress(String adress) { this.adress = adress; return this; }
        public Builder mail(String mail) { this.mail = mail; return this; }
        public Builder date(String date) { this.date = date; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder categoryId(Long id) { this.categoryId = id; return this; }
        public Builder categoryLabel(String l) { this.categoryLabel = l; return this; }
        public Builder authorId(Long id) { this.authorId = id; return this; }
        public Builder authorUsername(String u) { this.authorUsername = u; return this; }
        public Builder version(Long v) { this.version = v; return this; }
        public AnnonceDTO build() { return new AnnonceDTO(this); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAdress() { return adress; }
    public void setAdress(String adress) { this.adress = adress; }
    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryLabel() { return categoryLabel; }
    public void setCategoryLabel(String categoryLabel) { this.categoryLabel = categoryLabel; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
