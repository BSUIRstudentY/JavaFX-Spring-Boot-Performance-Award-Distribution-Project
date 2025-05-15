package org.example.bonussystem.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.bonussystem.model.*;
import org.example.bonussystem.repository.DepartmentRepository;
import org.example.bonussystem.repository.EmployeeRepository;
import org.example.bonussystem.repository.PerformanceIndicatorRepository;
import org.example.bonussystem.repository.RoleRepository;
import org.example.bonussystem.service.BonusService;
import org.example.bonussystem.service.EmployeeService;
import org.example.bonussystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javafx.util.Duration;

@Component
@Scope("prototype")
public class UserController {

    @FXML private SplitPane mainPane;
    @FXML private Accordion sideMenu;
    @FXML private TitledPane profilePane;
    @FXML private TitledPane refreshPane;
    @FXML private TitledPane backPane;
    @FXML private TitledPane analyticsPane;
    @FXML private TitledPane registerPane;

    @FXML public TableView<PerformanceIndicator> efficiencyTable;
    @FXML private TableColumn<PerformanceIndicator, Long> efficiencyIdColumn;
    @FXML private TableColumn<PerformanceIndicator, Double> efficiencyKpiColumn;
    @FXML private TableColumn<PerformanceIndicator, Double> efficiencyRevenueColumn;
    @FXML private TableColumn<PerformanceIndicator, String> efficiencyMonthYearColumn;

    @FXML private TableView<Bonus> bonusTable;
    @FXML private TableColumn<Bonus, Long> bonusIdColumn;
    @FXML private TableColumn<Bonus, String> bonusEmployeeNameColumn;
    @FXML private TableColumn<Bonus, Double> bonusAmountColumn;
    @FXML private TableColumn<Bonus, String> bonusMonthYearColumn;

    @FXML private TableView<PerformanceIndicator> departmentKpiTable;
    @FXML private TableColumn<PerformanceIndicator, String> departmentEmployeeNameColumn;
    @FXML private TableColumn<PerformanceIndicator, Double> departmentKpiColumn;
    @FXML private TableColumn<PerformanceIndicator, String> departmentMonthYearColumn;

    @FXML private TableView<Employee> employeeListTable;
    @FXML private TableColumn<Employee, Long> employeeIdColumn;
    @FXML private TableColumn<Employee, String> employeeNameColumn;
    @FXML private TableColumn<Employee, String> employeeDepartmentColumn;
    @FXML private TableColumn<Employee, String> employeeRoleColumn;

    @FXML private Label employeeNameLabel;
    @FXML private Label currentDateTimeLabel;
    @FXML private Label notificationLabel;

    private ApplicationContext springContext;
    private User currentUser;
    private PerformanceIndicatorRepository userEfficiencyRepository;
    private EmployeeRepository employeeRepository;
    private BonusService bonusService;
    private DepartmentRepository departmentRepository;
    private RoleRepository roleRepository;
    private UserService userService;

    private ObservableList<PerformanceIndicator> efficiencyData = FXCollections.observableArrayList();
    private ObservableList<Bonus> bonusData = FXCollections.observableArrayList();
    private ObservableList<PerformanceIndicator> departmentKpiData = FXCollections.observableArrayList();
    private ObservableList<Employee> employeeListData = FXCollections.observableArrayList();

    @Autowired
    public UserController(ApplicationContext springContext, User currentUser,
                          PerformanceIndicatorRepository userEfficiencyRepository,
                          EmployeeRepository employeeRepository,
                          BonusService bonusService,
                          DepartmentRepository departmentRepository,
                          RoleRepository roleRepository,
                          UserService userService) {
        this.springContext = springContext;
        this.currentUser = currentUser;
        this.userEfficiencyRepository = userEfficiencyRepository;
        this.employeeRepository = employeeRepository;
        this.bonusService = bonusService;
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
        System.out.println("UserController created with User: " + (currentUser != null ? "ID: " + currentUser.getId() + ", Username: " + currentUser.getUsername() : "null"));
    }

    public UserController() {}

    @FXML
    private void initialize() {
        System.out.println("Initializing UserController - efficiencyTable: " + (efficiencyTable != null) +
                ", bonusTable: " + (bonusTable != null) +
                ", departmentKpiTable: " + (departmentKpiTable != null) +
                ", employeeListTable: " + (employeeListTable != null) +
                ", employeeNameLabel: " + (employeeNameLabel != null) +
                ", currentDateTimeLabel: " + (currentDateTimeLabel != null) +
                ", registerPane: " + (registerPane != null) +
                ", currentUser: " + (currentUser != null ? "ID: " + currentUser.getId() + ", Username: " + currentUser.getUsername() : "null"));

        if (efficiencyTable == null || bonusTable == null || departmentKpiTable == null || employeeListTable == null ||
                employeeNameLabel == null || currentDateTimeLabel == null || registerPane == null) {
            System.err.println("One or more UI elements are null in initialize method");
            return;
        }

        // Настройка efficiencyTable
        efficiencyIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        efficiencyKpiColumn.setCellValueFactory(new PropertyValueFactory<>("kpi"));
        efficiencyRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        efficiencyMonthYearColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMonthYear()));

        // Настройка bonusTable
        bonusIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bonusEmployeeNameColumn.setCellValueFactory(cellData -> {
            Employee emp = cellData.getValue().getEmployee();
            return new SimpleStringProperty(emp != null ? emp.getName() : "N/A");
        });
        bonusAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        bonusMonthYearColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMonthYear()));

        // Настройка departmentKpiTable
        departmentEmployeeNameColumn.setCellValueFactory(cellData -> {
            Employee emp = cellData.getValue().getEmployee();
            return new SimpleStringProperty(emp != null ? emp.getName() : "N/A");
        });
        departmentKpiColumn.setCellValueFactory(new PropertyValueFactory<>("kpi"));
        departmentMonthYearColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMonthYear()));

        // Настройка employeeListTable
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        employeeDepartmentColumn.setCellValueFactory(cellData -> {
            Department dept = departmentRepository.findById(cellData.getValue().getDepartmentId()).orElse(null);
            return new SimpleStringProperty(dept != null ? dept.getName() : "N/A");
        });
        employeeRoleColumn.setCellValueFactory(cellData -> {
            Role role = roleRepository.findById(cellData.getValue().getRoleId()).orElse(null);
            return new SimpleStringProperty(role != null ? role.getName() : "N/A");
        });

        // Настройка текущей даты и времени
        updateDateTime();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateDateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Проверяем, зарегистрирован ли пользователь как сотрудник
        if (currentUser == null) {
            System.err.println("Current user is null, cannot proceed with initialization");
            employeeNameLabel.setVisible(false);
            employeeNameLabel.setManaged(false);
            employeeNameLabel.setText("Сотрудник: Неизвестно");
            registerPane.setVisible(true);
            registerPane.setManaged(true);
        } else {
            Employee employee = employeeRepository.findByUserId(currentUser.getId());
            if (employee == null || employee.getStatus().equals("REJECTED")) {
                // Пользователь не зарегистрирован как сотрудник, показываем панель регистрации
                registerPane.setVisible(true);
                registerPane.setManaged(true);
                employeeNameLabel.setVisible(false);
                employeeNameLabel.setManaged(false);
                employeeNameLabel.setText("Сотрудник: Не зарегистрирован");
            } else {
                // Пользователь зарегистрирован как сотрудник, скрываем панель регистрации
                registerPane.setVisible(false);
                registerPane.setManaged(false);
                employeeNameLabel.setVisible(true);
                employeeNameLabel.setManaged(true);
                employeeNameLabel.setText("Сотрудник: " + employee.getName());
                loadUserData();
                loadDepartmentKpiData();
                loadEmployeeListData();
                checkNotifications();
            }
        }
    }

    private void updateDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss");
        currentDateTimeLabel.setText("Сегодня: " + dateFormat.format(new Date()));
    }

    @FXML
    private void refreshData() {
        if (currentUser == null) {
            System.err.println("Cannot refresh data: currentUser is null");
            return;
        }
        System.out.println("Refreshing data for User ID: " + currentUser.getId());
        loadUserData();
        loadDepartmentKpiData();
        loadEmployeeListData();
        checkNotifications();
    }

    @Transactional(readOnly = true)
    private void loadUserData() {
        if (currentUser == null) {
            System.err.println("Cannot load data: currentUser is null");
            return;
        }
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
        }
        efficiencyTable.setItems(efficiencyData);
        efficiencyTable.refresh();

        bonusData.clear();
        if (!"APPROVED".equals(employee.getStatus())) {
            System.out.println("Employee status is not APPROVED for User ID: " + currentUser.getId() + ", Status: " + employee.getStatus());
            return;
        }
        List<Bonus> bonuses = bonusService.getAllBonuses().stream()
                .filter(bonus -> bonus.getEmployee() != null && bonus.getEmployee().getId().equals(employee.getId()))
                .collect(Collectors.toList());
        if (bonuses.isEmpty()) {
            System.out.println("No bonuses found for Employee ID: " + employee.getId());
        } else {
            bonusData.addAll(bonuses);
            System.out.println("Loaded " + bonusData.size() + " bonuses for Employee ID: " + employee.getId());
        }
        bonusTable.setItems(bonusData);
        bonusTable.refresh();
    }

    @Transactional(readOnly = true)
    private void loadDepartmentKpiData() {
        if (currentUser == null) {
            System.err.println("Cannot load department KPI data: currentUser is null");
            return;
        }
        Employee currentEmployee = employeeRepository.findByUserId(currentUser.getId());
        if (currentEmployee == null || currentEmployee.getStatus().equals("REJECTED")) {
            System.err.println("No Employee found for User ID: " + currentUser.getId());
            return;
        }
        Long departmentId = currentEmployee.getDepartmentId();
        if (departmentId == null) {
            System.err.println("Department ID is null for Employee ID: " + currentEmployee.getId());
            return;
        }
        departmentKpiData.clear();
        List<Employee> departmentEmployees = employeeRepository.findAllByDepartment(currentEmployee.getDepartment());
        if (departmentEmployees.isEmpty() || currentEmployee.getStatus().equals("REJECTED")) {
            System.out.println("No employees found in department ID: " + departmentId);
            return;
        }
        for (Employee employee : departmentEmployees) {
            List<PerformanceIndicator> efficiencies = userEfficiencyRepository.findAllByEmployeeId(employee.getId());
            if (efficiencies != null && !efficiencies.isEmpty()) {
                PerformanceIndicator latestEfficiency = efficiencies.stream()
                        .max(Comparator.comparingInt(PerformanceIndicator::getYear)
                                .thenComparingInt(PerformanceIndicator::getMonth))
                        .orElse(null);
                if (latestEfficiency != null) {
                    departmentKpiData.add(latestEfficiency);
                }
            }
        }
        System.out.println("Loaded " + departmentKpiData.size() + " KPI records for department ID: " + departmentId);
        departmentKpiTable.setItems(departmentKpiData);
        departmentKpiTable.refresh();
    }

    @Transactional(readOnly = true)
    private void loadEmployeeListData() {
        employeeListData.clear();
        List<Employee> employees = employeeRepository.findAll();
        if (employees.isEmpty()) {
            System.out.println("No employees found in the company");
        } else {
            employeeListData.addAll(employees);
            System.out.println("Loaded " + employeeListData.size() + " employees");
        }
        employeeListTable.setItems(employeeListData);
        employeeListTable.refresh();
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
        EmployeeService employeeService = springContext.getBean(EmployeeService.class);
        RoleRepository roleRepo = springContext.getBean(RoleRepository.class);
        DepartmentRepository deptRepo = springContext.getBean(DepartmentRepository.class);
        loader.setControllerFactory(param -> new EmployeeRegistrationController(springContext, currentUser, employeeService, roleRepo, deptRepo));
        Parent root = loader.load();
        EmployeeRegistrationController controller = loader.getController();
        System.out.println("EmployeeRegistrationController initialized with user: " + (currentUser != null ? currentUser.getUsername() : "null"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Подача заявки на регистрацию сотрудника");
        stage.show();
    }

    @FXML
    private void showProfile() throws IOException {
        if (currentUser == null) {
            System.err.println("Cannot show profile: currentUser is null");
            return;
        }
        Stage profileStage = new Stage();
        VBox profileLayout = new VBox(10);
        profileLayout.setAlignment(Pos.CENTER);
        profileLayout.setPadding(new Insets(10));
        Employee employee = employeeRepository.findByUserId(currentUser.getId());
        Department department = employee != null ? departmentRepository.findById(employee.getDepartmentId()).orElse(null) : null;
        Role role = employee != null ? roleRepository.findById(employee.getRoleId()).orElse(null) : null;
        Label titleLabel = new Label("Профиль пользователя");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label userIdLabel = new Label("ID пользователя: " + currentUser.getId());
        Label usernameLabel = new Label("Имя пользователя: " + currentUser.getUsername());
        Label employeeNameLabel = new Label("Имя сотрудника: " + (employee != null && !employee.getStatus().equals("REJECTED") ? employee.getName() : "Не определено"));
        Label departmentLabel = new Label("Отдел: " + (department != null && !employee.getStatus().equals("REJECTED") ? department.getName() : "Не определено"));
        Label roleLabel = new Label("Роль: " + (role != null && !employee.getStatus().equals("REJECTED") ? role.getName() : "Не определено"));
        profileLayout.getChildren().addAll(titleLabel, userIdLabel, usernameLabel, employeeNameLabel, departmentLabel, roleLabel);
        profileStage.setScene(new Scene(profileLayout, 350, 400));
        profileStage.setTitle("Профиль");
        profileStage.show();
    }

    @FXML
    private void showAnalytics() throws IOException {
        if (currentUser == null) {
            System.err.println("Cannot show analytics: currentUser is null");
            return;
        }
        Stage analyticsStage = new Stage();
        VBox analyticsLayout = new VBox(10);
        analyticsLayout.setAlignment(Pos.CENTER);
        analyticsLayout.setPadding(new Insets(10));
        ComboBox<String> dataTypeCombo = new ComboBox<>();
        dataTypeCombo.getItems().addAll("KPI", "Выручка", "Премия");
        dataTypeCombo.setValue("KPI");
        ComboBox<String> periodCombo = new ComboBox<>();
        periodCombo.getItems().addAll("Месяц", "Год");
        periodCombo.setValue("Месяц");
        Button generateChartButton = new Button("Построить график");
        generateChartButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Аналитика");
        xAxis.setLabel("Год");
        yAxis.setLabel("Значение");
        Employee employee = employeeRepository.findByUserId(currentUser.getId());
        List<PerformanceIndicator> efficiencies = userEfficiencyRepository.findAllByEmployeeId(employee.getId());
        List<Bonus> bonuses = bonusService.getAllBonuses().stream()
                .filter(bonus -> bonus.getEmployee() != null && bonus.getEmployee().getId().equals(employee.getId()))
                .collect(Collectors.toList());

        // Вычисляем минимальный и максимальный годы
        int minYear = Math.min(
                efficiencies.stream().mapToInt(PerformanceIndicator::getYear).min().orElse(2020),
                bonuses.stream().mapToInt(Bonus::getYear).min().orElse(2020)
        );
        int maxYear = Math.max(
                efficiencies.stream().mapToInt(PerformanceIndicator::getYear).max().orElse(2025),
                bonuses.stream().mapToInt(Bonus::getYear).max().orElse(2025)
        );

        generateChartButton.setOnAction(event -> {
            lineChart.getData().clear();
            if (employee == null) {
                System.err.println("No Employee found for User ID: " + currentUser.getId());
                return;
            }
            String dataType = dataTypeCombo.getValue();
            String period = periodCombo.getValue();

            // Настройка X-оси в зависимости от периода
            if (period.equals("Месяц")) {
                xAxis.setLabel("Месяц");
                xAxis.setLowerBound(1); // Начало с 1 для месяцев
                xAxis.setUpperBound(12); // Конец с 12 для месяцев
                xAxis.setTickUnit(1); // Каждый месяц
                xAxis.setAutoRanging(false);
            } else {
                xAxis.setLabel("Год");
                xAxis.setLowerBound(minYear);
                xAxis.setUpperBound(maxYear);
                xAxis.setTickUnit(1); // Один год
                xAxis.setAutoRanging(false);
            }

            // Настройка Y-оси в зависимости от типа данных
            double minValue, maxValue;
            if (dataType.equals("Премия")) {
                minValue = bonuses.stream().mapToDouble(Bonus::getAmount).min().orElse(0);
                maxValue = bonuses.stream().mapToDouble(Bonus::getAmount).max().orElse(1000);
            } else {
                minValue = efficiencies.stream()
                        .mapToDouble(pi -> dataType.equals("KPI") ? pi.getKpi() : pi.getRevenue())
                        .min().orElse(0);
                maxValue = efficiencies.stream()
                        .mapToDouble(pi -> dataType.equals("KPI") ? pi.getKpi() : pi.getRevenue())
                        .max().orElse(1000);
            }
            yAxis.setLowerBound(minValue); // Минимальное значение
            yAxis.setUpperBound(maxValue * 1.1); // +10% для отступа
            yAxis.setAutoRanging(false);
            yAxis.setTickUnit((maxValue - minValue) / 10); // Делим на 10 для читаемости
            yAxis.setLabel(dataType); // Устанавливаем метку Y-оси в зависимости от типа данных

            if (dataType.equals("Премия")) {
                if (bonuses.isEmpty()) {
                    System.out.println("No bonuses available for analytics");
                    return;
                }
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(dataType + " за " + period);
                List<XYChart.Data<Number, Number>> dataPoints = new ArrayList<>();
                for (Bonus bonus : bonuses) {
                    double xValue;
                    if (period.equals("Месяц")) {
                        xValue = bonus.getMonth() + 1; // Сдвигаем с 0-11 на 1-12
                    } else {
                        xValue = bonus.getYear();
                    }
                    double yValue = bonus.getAmount();
                    dataPoints.add(new XYChart.Data<>(xValue, yValue));
                }
                // Сортировка с лямбда-выражением и приведением типа
                dataPoints.sort((d1, d2) -> Double.compare(d1.getXValue().doubleValue(), d2.getXValue().doubleValue()));
                series.getData().addAll(dataPoints);
                lineChart.getData().add(series);
            } else {
                if (efficiencies.isEmpty()) {
                    System.out.println("No efficiencies available for analytics");
                    return;
                }
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(dataType + " за " + period);
                List<XYChart.Data<Number, Number>> dataPoints = new ArrayList<>();
                for (PerformanceIndicator pi : efficiencies) {
                    double xValue;
                    if (period.equals("Месяц")) {
                        xValue = pi.getMonth() + 1; // Сдвигаем с 0-11 на 1-12
                    } else {
                        xValue = pi.getYear();
                    }
                    double yValue = dataType.equals("KPI") ? pi.getKpi() : pi.getRevenue();
                    dataPoints.add(new XYChart.Data<>(xValue, yValue));
                }
                // Сортировка с лямбда-выражением и приведением типа
                dataPoints.sort((d1, d2) -> Double.compare(d1.getXValue().doubleValue(), d2.getXValue().doubleValue()));
                series.getData().addAll(dataPoints);
                lineChart.getData().add(series);
            }
        });
        analyticsLayout.getChildren().addAll(dataTypeCombo, periodCombo, generateChartButton, lineChart);
        analyticsStage.setScene(new Scene(analyticsLayout, 600, 400));
        analyticsStage.setTitle("Аналитика");
        analyticsStage.show();
    }

    @FXML
    private void goBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();
        Stage stage = (Stage) efficiencyTable.getScene().getWindow();
        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Вход");
    }

    private void checkNotifications() {
        if (currentUser == null) return;
        Employee employee = employeeRepository.findByUserId(currentUser.getId());
        if (employee == null) return;
        List<Bonus> newBonuses = bonusService.getAllBonuses().stream()
                .filter(b -> b.getEmployee() != null && b.getEmployee().getId().equals(employee.getId()))
                .filter(b -> !bonusData.contains(b))
                .collect(Collectors.toList());
        if (!newBonuses.isEmpty()) {
            notificationLabel.setText("Новые премии доступны! (" + newBonuses.size() + ")");
            notificationLabel.setVisible(true);
            notificationLabel.setStyle("-fx-text-fill: green;");
            new Timeline(new KeyFrame(Duration.seconds(5), e -> notificationLabel.setVisible(false))).play();
            bonusData.addAll(newBonuses);
            bonusTable.setItems(bonusData);
            bonusTable.refresh();
        }
    }
}