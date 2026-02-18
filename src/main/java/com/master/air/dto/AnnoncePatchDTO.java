package com.master.air.dto;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnnoncePatchDTO {
    private String title;
    private String description;
    private String adress;
    private String mail;
    private Long categoryId;
    private String status;

    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public String getAdress() { return adress; }
    public void setAdress(String a) { this.adress = a; }
    public String getMail() { return mail; }
    public void setMail(String m) { this.mail = m; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long c) { this.categoryId = c; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
}
