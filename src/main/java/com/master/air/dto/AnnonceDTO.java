package com.master.air.dto;

import com.master.air.model.AnnonceStatus;
import java.sql.Timestamp;

public class AnnonceDTO {
    public Long id;
    public String title;
    public String description;
    public String adress;
    public String mail;
    public Timestamp date;
    public AnnonceStatus status;

    public Long authorId;
    public Long categoryId;
}