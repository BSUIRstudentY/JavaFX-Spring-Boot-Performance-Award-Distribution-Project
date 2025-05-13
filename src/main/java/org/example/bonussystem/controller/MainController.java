package org.example.bonussystem.controller;

import org.example.bonussystem.model.*;
import org.example.bonussystem.service.BonusService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

@Component
public class MainController {
    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, String> nameColumn;
    @FXML private TableColumn<Employee, String> departmentColumn;
    @FXML private TableColumn<Employee, String> roleColumn;
    @FXML private TextField nameField;
    @FXML private TextField departmentIdField;
    @FXML private TextField roleIdField;

    @FXML private TableView<PerformanceIndicator> piTable;
    @FXML private TableColumn<PerformanceIndicator, String> employeeNameColumn;
    @FXML private TableColumn<PerformanceIndicator, Double> kpiColumn;
    @FXML private TableColumn<PerformanceIndicator, Double> revenueColumn;
    @FXML private TableColumn<PerformanceIndicator, Integer> yearColumn;
    @FXML private TableColumn<PerformanceIndicator, Double> bonusColumn;
    @FXML private TextField employeeIdField;
    @FXML private TextField kpiField;
    @FXML private TextField revenueField;
    @FXML private TextField yearField;

    private BonusService bonusService;
    private ObservableList<Employee> employeeData = FXCollections.observableArrayList();
    private ObservableList<PerformanceIndicator> piData = FXCollections.observableArrayList();

    public void setBonusService(BonusService bonusService) {
        this.bonusService = bonusService;
        loadEmployees();
        loadPerformanceIndicators();
    }

    @FXML
    private void initialize() {
        // Таблица сотрудников
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        departmentColumn.setCellValueFactory(cellData -> {
            Department dept = cellData.getValue().getDepartment();
            return new javafx.beans.property.SimpleStringProperty(dept != null ? dept.getName() : "N/A");
        });
        roleColumn.setCellValueFactory(cellData -> {
            Role role = cellData.getValue().getRole();
            return new javafx.beans.property.SimpleStringProperty(role != null ? role.getName() : "N/A");
        });
        employeeTable.setItems(employeeData);

        // Таблица показателей
        employeeNameColumn.setCellValueFactory(cellData -> {
            Employee emp = cellData.getValue().getEmployee();
            return new javafx.beans.property.SimpleStringProperty(emp != null ? emp.getName() : "N/A");
        });
        kpiColumn.setCellValueFactory(new PropertyValueFactory<>("kpi"));
        revenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        bonusColumn.setCellValueFactory(cellData -> {
            PerformanceIndicator pi = cellData.getValue();
            Bonus bonus = bonusService.getAllBonuses().stream()
                    .filter(b -> b.getEmployee().getId().equals(pi.getEmployee().getId()) &&
                            b.getYear() == pi.getYear())
                    .findFirst()
                    .orElse(null);
            return new javafx.beans.property.SimpleDoubleProperty(
                    bonus != null ? bonus.getAmount() : 0.0
            ).asObject();
        });
        piTable.setItems(piData);
    }

    @FXML
    private void addEmployee() {
        try {
            String name = nameField.getText().trim();
            Long departmentId = Long.parseLong(departmentIdField.getText().trim());
            Long roleId = Long.parseLong(roleIdField.getText().trim());

            if (name.isEmpty()) {
                showAlert("Error", "Name cannot be empty.");
                return;
            }

            Employee employee = new Employee();
            employee.setName(name);
            Department dept = new Department();
            dept.setId(departmentId);
            employee.setDepartment(dept);
            Role role = new Role();
            role.setId(roleId);
            employee.setRole(role);
            bonusService.saveEmployee(employee);
            employeeData.add(employee);
            clearEmployeeFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Department ID and Role ID must be valid numbers.");
        }
    }

    @FXML
    private void addPerformanceIndicator() {
        try {
            Long employeeId = Long.parseLong(employeeIdField.getText().trim());
            double kpi = Double.parseDouble(kpiField.getText().trim());
            double revenue = Double.parseDouble(revenueField.getText().trim());
            int year = Integer.parseInt(yearField.getText().trim());

            if (kpi < 0 || kpi > 100) {
                showAlert("Error", "KPI must be between 0 and 100.");
                return;
            }
            if (revenue < 0) {
                showAlert("Error", "Revenue cannot be negative.");
                return;
            }
            if (year < 2000 || year > 2025) {
                showAlert("Error", "Year must be between 2000 and 2025.");
                return;
            }

            PerformanceIndicator pi = new PerformanceIndicator();
            Employee employee = new Employee();
            employee.setId(employeeId);
            pi.setEmployee(employee);
            pi.setKpi(kpi);
            pi.setRevenue(revenue);
            pi.setYear(year);
            bonusService.savePerformanceIndicator(pi);
            piData.add(pi);
            clearPiFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Employee ID, KPI, Revenue, and Year must be valid numbers.");
        }
    }

    @FXML
    private void calculateBonuses() {
        for (PerformanceIndicator pi : piData) {
            bonusService.calculateBonus(pi);
        }
        piTable.refresh();
    }

    private void loadEmployees() {
        employeeData.clear();
        employeeData.addAll(bonusService.getAllEmployees());
    }

    private void loadPerformanceIndicators() {
        piData.clear();
        piData.addAll(bonusService.getAllPerformanceIndicators());
    }

    private void clearEmployeeFields() {
        nameField.clear();
        departmentIdField.clear();
        roleIdField.clear();
    }

    private void clearPiFields() {
        employeeIdField.clear();
        kpiField.clear();
        revenueField.clear();
        yearField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}