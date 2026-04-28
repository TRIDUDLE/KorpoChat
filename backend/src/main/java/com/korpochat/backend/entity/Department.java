package com.korpochat.backend.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Mapowanie kolumny 'name' ze skryptu SQL
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    public Department() {}

    public Department(String name) {
        this.name = name;
    }

    // Gettery i Settery
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}