package org.example.bonussystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class PerformanceIndicator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private Double kpi;    // Изменено на Double
    private Double revenue; // Изменено на Double
    private int year;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public Double getKpi() { return kpi; }
    public void setKpi(Double kpi) { this.kpi = kpi; }
    public Double getRevenue() { return revenue; }
    public void setRevenue(Double revenue) { this.revenue = revenue; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}