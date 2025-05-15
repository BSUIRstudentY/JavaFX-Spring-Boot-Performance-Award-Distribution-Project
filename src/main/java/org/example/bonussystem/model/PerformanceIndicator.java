package org.example.bonussystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import java.time.Month;
import java.util.Locale;

@Entity
public class PerformanceIndicator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private Double kpi;
    private Double revenue;
    private int month; // Новое поле: месяц (1-12)
    private int year;  // Год

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public Double getKpi() { return kpi; }
    public void setKpi(Double kpi) { this.kpi = kpi; }
    public Double getRevenue() { return revenue; }
    public void setRevenue(Double revenue) { this.revenue = revenue; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    // Метод для форматированного отображения "месяц, год"
    public String getMonthYear() {
        if (month < 1 || month > 12) {
            return "Неверный месяц, " + year;
        }
        String monthName = Month.of(month).getDisplayName(
                java.time.format.TextStyle.FULL_STANDALONE,
                new Locale("ru")
        );
        return monthName.substring(0, 1).toUpperCase() + monthName.substring(1) + ", " + year;
    }
}