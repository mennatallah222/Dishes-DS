package com.dishes.entities;

import jakarta.persistence.*;
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "uniqueName"))
public class ShippingCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(nullable = false)
    private String uniqueName;

    @PrePersist
    @PreUpdate
    private void setUniqueName(){
        this.uniqueName=this.name.trim().toLowerCase();
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
