package org.example.bonussystem.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.bonussystem.model.*;
import org.example.bonussystem.repository.EmployeeRepository;
import org.example.bonussystem.repository.PerformanceIndicatorRepository;
import org.example.bonussystem.service.BonusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Component
@Scope("prototype")
public class UserController {

    @FXML public TableView<PerformanceIndicator> efficiencyTable;
    @FXML private TableColumn<PerformanceIndicator, Long> efficiencyIdColumn;
    @FXML private TableColumn<PerformanceIndicator, Double> efficiencyKpiColumn;
    @FXML private TableColumn<PerformanceIndicator, Double> efficiencyRevenueColumn;
    @FXML private TableColumn<PerformanceIndicator, Integer> efficiencyYearColumn;

    @FXML private TableView<Bonus> bonusTable;
    @FXML private TableColumn<Bonus, Long> bonusIdColumn;
    @FXML private TableColumn<Bonus, String> bonusEmployeeNameColumn;
    @FXML private TableColumn<Bonus, Double> bonusAmountColumn;
    @FXML private TableColumn<Bonus, Integer> bonusYearColumn;

    private final ApplicationContext springContext;
    private final User currentUser;
    private final PerformanceIndicatorRepository userEfficiencyRepository;
    private final EmployeeRepository employeeRepository;
    private final BonusService bonusService;

    private ObservableList<PerformanceIndicator> efficiencyData = FXCollections.observableArrayList();
    private ObservableList<Bonus> bonusData = FXCollections.observableArrayList();

    @Autowired
    public UserController(ApplicationContext springContext, User currentUser,
                          PerformanceIndicatorRepository userEfficiencyRepository,
                          EmployeeRepository employeeRepository,
                          BonusService bonusService) {
        this.springContext = springContext;
        this.currentUser = currentUser;
        if (currentUser == null) {
            throw new IllegalArgumentException("currentUser cannot be null in UserController");
        }
        this.userEfficiencyRepository = userEfficiencyRepository;
        this.employeeRepository = employeeRepository;
        this.bonusService = bonusService;
        System.out.println("UserController created with User ID: " + currentUser.getId() + ", Username: " + currentUser.getUsername());
    }

    @FXML
    private void initialize() {
        System.out.println("Initializing UserController - efficiencyTable: " + (efficiencyTable != null) +
                ", bonusTable: " + (bonusTable != null) +
                ", currentUser: " + (currentUser != null ? currentUser.getId() + ", Username: " + currentUser.getUsername() : "null"));

        if (efficiencyTable == null || bonusTable == null) {
            System.err.println("One or both tables are null in initialize method");
            return;
        }

        // Настройка efficiencyTable
        efficiencyIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        efficiencyKpiColumn.setCellValueFactory(new PropertyValueFactory<>("kpi"));
        efficiencyRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        efficiencyYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        // Настройка bonusTable
        bonusIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bonusEmployeeNameColumn.setCellValueFactory(cellData -> {
            Employee emp = cellData.getValue().getEmployee();
            return new SimpleStringProperty(emp != null ? emp.getName() : "N/A");
        });
        bonusAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        bonusYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        // Загружаем данные сразу после инициализации
        loadUserData();
    }

    @Transactional(readOnly = true)
    private void loadUserData() {
        System.out.println("Attempting to load efficiencies for User ID: " + currentUser.getId());
        efficiencyData.clear();
        Employee employee = employeeRepository.findByUserId(currentUser.getId());
        if (employee == null) {
            System.err.println("No Employee found for User ID: " + currentUser.getId());
            return;
        }
        List<PerformanceIndicator> efficiencies = userEfficiencyRepository.findAllByEmployeeId(employee.getId());
        if (efficiencies == null) {
            System.err.println("Efficiencies list is null for Employee ID: " + employee.getId());
        } else if (efficiencies.isEmpty()) {
            System.out.println("No efficiencies found for Employee ID: " + employee.getId());
        } else {
            efficiencyData.addAll(efficiencies);
            System.out.println("Loaded " + efficiencyData.size() + " efficiencies for Employee ID: " + employee.getId());
            efficiencies.forEach(e -> System.out.println("Efficiency: ID=" + e.getId() + ", KPI=" + e.getKpi() + ", Revenue=" + e.getRevenue() + ", Year=" + e.getYear()));
        }
        efficiencyTable.setItems(efficiencyData);
        efficiencyTable.refresh();

        // Загрузка премий
        bonusData.clear();
        if (!"APPROVED".equals(employee.getStatus())) {
            System.out.println("Employee status is not APPROVED for User ID: " + currentUser.getId() + ", Status: " + employee.getStatus());
            return;
        }
        List<Bonus> bonuses = bonusService.getAllBonuses().stream()
                .filter(bonus -> bonus.getEmployee() != null && bonus.getEmployee().getId().equals(employee.getId()))
                .toList();
        if (bonuses.isEmpty()) {
            System.out.println("No bonuses found for Employee ID: " + employee.getId());
        } else {
            bonusData.addAll(bonuses);
            System.out.println("Loaded " + bonusData.size() + " bonuses for Employee ID: " + employee.getId());
            bonuses.forEach(b -> System.out.println("Bonus: ID=" + b.getId() + ", Employee=" + (b.getEmployee() != null ? b.getEmployee().getName() : "N/A") + ", Amount=" + b.getAmount() + ", Year=" + b.getYear()));
        }
        bonusTable.setItems(bonusData);
        bonusTable.refresh();
    }

    @FXML
    private void showEmployeeRegistrationForm() throws IOException {
        if (currentUser == null) {
            System.err.println("Attempt to show registration form without a current user");
            return;
        }

        Employee employee = employeeRepository.findByUserId(currentUser.getId());
        if (employee != null) {
            System.out.println("User ID: " + currentUser.getId() + " is already linked to Employee ID: " + employee.getId());
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/employee_registration.fxml"));
        if (loader.getLocation() == null) {
            System.err.println("Could not find employee_registration.fxml");
            return;
        }
        loader.setControllerFactory(param -> springContext.getBean(EmployeeRegistrationController.class, springContext, currentUser));
        Parent root = loader.load();

        EmployeeRegistrationController controller = loader.getController();
        System.out.println("EmployeeRegistrationController initialized with user: " + (currentUser != null ? currentUser.getUsername() : "null"));

        Stage stage = new Stage();
        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Подача заявки на регистрацию сотрудника");
        stage.show();
    }

    @FXML
    private void goBack() throws IOException {
        if (efficiencyTable == null || efficiencyTable.getScene() == null || efficiencyTable.getScene().getWindow() == null) {
            System.err.println("Cannot go back: efficiencyTable or its scene/window is null");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        if (loader.getLocation() == null) {
            System.err.println("Could not find login.fxml");
            return;
        }
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        Stage stage = (Stage) efficiencyTable.getScene().getWindow();
        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Вход");
    }
}