package org.example.bonussystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "bonus_rate")
    private Double baseBonusRate;

    // Геттеры и сеттеры
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

    public Double getBaseBonusRate() {
        return baseBonusRate;
    }

    public void setBaseBonusRate(Double baseBonusRate) {
        this.baseBonusRate = baseBonusRate;
    }
}