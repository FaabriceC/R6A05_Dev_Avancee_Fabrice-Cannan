package com.master.air.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "category")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 100)
    @Column(unique = true, nullable = false, length = 100)
    private String label;

    public Category() {}
    public Category(String label) { this.label = label; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLabel() { return label; }
    public void setLabel(String l) { this.label = l; }
}
