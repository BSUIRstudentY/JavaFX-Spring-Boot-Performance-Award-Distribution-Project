package org.example.bonussystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_efficiency")
public class UserEfficiency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kpi;
    private Double revenue;
    private Integer year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Предполагаем, что связь через user_id
    private User user;

    // Геттеры, сеттеры, конструкторы
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKpi() { return kpi; }
    public void setKpi(String kpi) { this.kpi = kpi; }

    public Double getRevenue() { return revenue; }
    public void setRevenue(Double revenue) { this.revenue = revenue; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}