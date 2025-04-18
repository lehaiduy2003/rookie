package com.example.assignment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created_on")
    private Date createdOn;
    @Column(name = "updated_on")
    private Date updatedOn;

    @PrePersist
    public void prePersist() {
        Date now = new Date();
        this.createdOn = now;
        this.updatedOn = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedOn = new Date();
    }
}
