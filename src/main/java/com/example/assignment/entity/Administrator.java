package com.example.assignment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "administrators")
@NoArgsConstructor
public class Administrator extends User {

}
