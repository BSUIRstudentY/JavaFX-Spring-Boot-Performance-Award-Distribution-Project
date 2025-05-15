package org.example.bonussystem.controller;

import javax.swing.JPanel;

import javafx.embed.swing.SwingNode;
import javafx.scene.layout.BorderPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.bonussystem.model.*;
import org.example.bonussystem.repository.*;
import org.example.bonussystem.service.BonusService;
import org.example.bonussystem.service.EmployeeService;
import org.example.bonussystem.service.UserService;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.codehaus.janino.ScriptEvaluator;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AdminController {

    @FXML private TabPane tabPane;
    @FXML private TableView<Department> departmentTable;
    @FXML private TableColumn<Department, Long> departmentIdColumn;
    @FXML private TableColumn<Department, String> departmentNameColumn;
    @FXML private TextField departmentNameField;
    @FXML private TextField editDepartmentIdField;
    @FXML private TextField editDepartmentNameField;

    @FXML private TableView<Role> roleTable;
    @FXML private TableColumn<Role, Long> roleIdColumn;
    @FXML private TableColumn<Role, String> roleNameColumn;
    @FXML private TableColumn<Role, Double> roleBaseBonusRateColumn;
    @FXML private TextField roleNameField;
    @FXML private TextField roleBaseBonusRateField;
    @FXML private TextField editRoleIdField;
    @FXML private TextField editRoleNameField;
    @FXML private TextField editRoleBaseBonusRateField;

    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Long> employeeIdColumn;
    @FXML private TableColumn<Employee, String> employeeNameColumn;
    @FXML private TableColumn<Employee, String> employeeDepartmentColumn;
    @FXML private TableColumn<Employee, String> employeeRoleColumn;
    @FXML private TableColumn<Employee, String> employeeStatusColumn;
    @FXML private TextField employeeNameField;
    @FXML private ComboBox<String> employeeDepartmentComboBox;
    @FXML private ComboBox<String> employeeRoleComboBox;
    @FXML private TextField editEmployeeIdField;
    @FXML private TextField editEmployeeNameField;
    @FXML private ComboBox<String> editEmployeeDepartmentComboBox;
    @FXML private ComboBox<String> editEmployeeRoleComboBox;

    @FXML private TableView<Employee> pendingEmployeeTable;
    @FXML private TableColumn<Employee, Long> pendingEmployeeIdColumn;
    @FXML private TableColumn<Employee, String> pendingEmployeeNameColumn;
    @FXML private TableColumn<Employee, String> pendingEmployeeRoleColumn;
    @FXML private TableColumn<Employee, String> pendingEmployeeDepartmentColumn;
    @FXML private TableColumn<Employee, String> pendingEmployeeStatusColumn;
    @FXML private TableColumn<Employee, Long> pendingEmployeeActionsColumn;

    @FXML private TableView<PerformanceIndicator> piTable;
    @FXML private TableColumn<PerformanceIndicator, Long> piIdColumn;
    @FXML private TableColumn<PerformanceIndicator, String> piEmployeeNameColumn;
    @FXML private TableColumn<PerformanceIndicator, Double> piKpiColumn;
    @FXML private TableColumn<PerformanceIndicator, Double> piRevenueColumn;
    @FXML private TableColumn<PerformanceIndicator, String> piMonthYearColumn;
    @FXML private ComboBox<String> piEmployeeComboBox;
    @FXML private TextField piKpiField;
    @FXML private TextField piRevenueField;
    @FXML private TextField piMonthField;
    @FXML private TextField piYearField;
    @FXML private TextField editPiIdField;
    @FXML private ComboBox<String> editPiEmployeeComboBox;
    @FXML private TextField editPiKpiField;
    @FXML private TextField editPiRevenueField;
    @FXML private TextField editPiMonthField;
    @FXML private TextField editPiYearField;

    @FXML private TableView<Bonus> bonusTable;
    @FXML private TableColumn<Bonus, Long> bonusIdColumn;
    @FXML private TableColumn<Bonus, String> bonusEmployeeNameColumn;
    @FXML private TableColumn<Bonus, Double> bonusAmountColumn;
    @FXML private TableColumn<Bonus, String> bonusMonthYearColumn;
    @FXML private TextField editBonusIdField;
    @FXML private ComboBox<String> editBonusEmployeeComboBox;
    @FXML private TextField editBonusAmountField;
    @FXML private TextField editBonusMonthField;
    @FXML private TextField editBonusYearField;

    @FXML private TextArea consoleInput;
    @FXML private Button executeButton;
    @FXML private TextArea consoleOutput;

    // Поля для аналитики заявок
    @FXML private TableView<Employee> analyticsTable;
    @FXML private TableColumn<Employee, Long> analyticsIdColumn;
    @FXML private TableColumn<Employee, String> analyticsNameColumn;
    @FXML private TableColumn<Employee, String> analyticsRoleColumn;
    @FXML private TableColumn<Employee, String> analyticsDepartmentColumn;
    @FXML private TableColumn<Employee, String> analyticsStatusColumn;
    @FXML private TableColumn<Employee, String> analyticsRequestDateColumn;

    // Новые поля для диаграммы по дням
    @FXML private BarChart<String, Number> dailyAnalyticsChart;
    @FXML private CategoryAxis dailyAnalyticsDateAxis;
    @FXML private NumberAxis dailyAnalyticsCountAxis;

    @FXML private Pane revenueWindRosePane; // Панель для розы ветров

    private Stage stage; // Ссылка на окно

    private User currentUser;
    private ApplicationContext springContext;
    private EmployeeService employeeService;
    private BonusService bonusService;
    private UserService userService;
    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private DepartmentRepository departmentRepository;
    private RoleRepository roleRepository;
    private PerformanceIndicatorRepository performanceIndicatorRepository;
    private BonusRepository bonusRepository;

    private ObservableList<Department> departmentData = FXCollections.observableArrayList();
    private ObservableList<Role> roleData = FXCollections.observableArrayList();
    private ObservableList<Employee> employeeData = FXCollections.observableArrayList();
    private ObservableList<Employee> pendingEmployeeData = FXCollections.observableArrayList();
    private ObservableList<PerformanceIndicator> piData = FXCollections.observableArrayList();
    private ObservableList<Bonus> bonusData = FXCollections.observableArrayList();
    private ObservableList<Employee> analyticsData = FXCollections.observableArrayList();

    @Autowired
    public AdminController(
            ApplicationContext springContext,
            EmployeeService employeeService,
            BonusService bonusService,
            UserService userService,
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            RoleRepository roleRepository,
            PerformanceIndicatorRepository performanceIndicatorRepository,
            BonusRepository bonusRepository
    ) {
        this.springContext = springContext;
        this.employeeService = employeeService;
        this.bonusService = bonusService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
        this.performanceIndicatorRepository = performanceIndicatorRepository;
        this.bonusRepository = bonusRepository;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        System.out.println("AdminController initialized with user: " + (currentUser != null ? currentUser.getUsername() : "null"));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        // Отделы
        departmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        departmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        departmentTable.setItems(departmentData);

        // Роли
        roleIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        roleNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleBaseBonusRateColumn.setCellValueFactory(new PropertyValueFactory<>("baseBonusRate"));
        roleTable.setItems(roleData);

        // Сотрудники
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        employeeDepartmentColumn.setCellValueFactory(cellData -> {
            Department dept = cellData.getValue().getDepartment();
            return new SimpleStringProperty(dept != null ? dept.getName() : "N/A");
        });
        employeeRoleColumn.setCellValueFactory(cellData -> {
            Role role = cellData.getValue().getRole();
            return new SimpleStringProperty(role != null ? role.getName() : "N/A");
        });
        employeeStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        employeeTable.setItems(employeeData);

        // Заявки
        pendingEmployeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        pendingEmployeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        pendingEmployeeRoleColumn.setCellValueFactory(cellData -> {
            Role role = cellData.getValue().getRole();
            return new SimpleStringProperty(role != null ? role.getName() : "N/A");
        });
        pendingEmployeeDepartmentColumn.setCellValueFactory(cellData -> {
            Department dept = cellData.getValue().getDepartment();
            return new SimpleStringProperty(dept != null ? dept.getName() : "N/A");
        });
        pendingEmployeeStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        pendingEmployeeActionsColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        pendingEmployeeActionsColumn.setCellFactory(new ButtonCellFactory(employeeService, this));
        pendingEmployeeTable.setItems(pendingEmployeeData);

        // Показатели
        piIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        piEmployeeNameColumn.setCellValueFactory(cellData -> {
            Employee emp = cellData.getValue().getEmployee();
            return new SimpleStringProperty(emp != null ? emp.getName() : "N/A");
        });
        piKpiColumn.setCellValueFactory(new PropertyValueFactory<>("kpi"));
        piRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        piMonthYearColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMonthYear()));
        piTable.setItems(piData);

        // Премии
        bonusIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bonusEmployeeNameColumn.setCellValueFactory(cellData -> {
            Employee emp = cellData.getValue().getEmployee();
            return new SimpleStringProperty(emp != null ? emp.getName() : "N/A");
        });
        bonusAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        bonusMonthYearColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMonthYear()));
        bonusTable.setItems(bonusData);

        // Аналитика заявок (Все заявки)
        analyticsIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        analyticsNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        analyticsRoleColumn.setCellValueFactory(cellData -> {
            Role role = cellData.getValue().getRole();
            return new SimpleStringProperty(role != null ? role.getName() : "N/A");
        });
        analyticsDepartmentColumn.setCellValueFactory(cellData -> {
            Department dept = cellData.getValue().getDepartment();
            return new SimpleStringProperty(dept != null ? dept.getName() : "N/A");
        });
        analyticsStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        analyticsRequestDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getRequestDate();
            return new SimpleStringProperty(date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A");
        });
        analyticsTable.setItems(analyticsData);

        // Аналитика заявок по дням (BarChart)
        dailyAnalyticsDateAxis.setLabel("Дата");
        dailyAnalyticsCountAxis.setLabel("Количество заявок");
        dailyAnalyticsChart.setTitle("Аналитика заявок по дням");

        // Устанавливаем ось Y как целочисленную
        dailyAnalyticsCountAxis.setAutoRanging(true);
        dailyAnalyticsCountAxis.setTickUnit(1); // Шаг в 1 единицу (целые числа)
        dailyAnalyticsCountAxis.setForceZeroInRange(true); // Начинаем с 0

        // Настраиваем ширину столбцов
        dailyAnalyticsChart.setBarGap(0); // Расстояние между столбцами (в пикселях)

        // Слушатель на изменение ширины окна
        if (stage != null) {
            stage.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                updateCategoryGap(newWidth.doubleValue());
            });
            // Устанавливаем начальное значение categoryGap
            updateCategoryGap(stage.getWidth());
        } else {
            System.err.println("Stage is null, categoryGap cannot be set dynamically.");
        }

        // Инициализация розы ветров для выручки
        initializeRevenueWindRose();

        // Заполняем ComboBox и загружаем данные
        loadComboBoxes();
        loadAllData();
    }

    private void updateCategoryGap(double windowWidth) {
        // Получаем количество категорий (дней)
        int numberOfCategories = (int) analyticsData.stream()
                .filter(emp -> emp.getRequestDate() != null)
                .map(emp -> emp.getRequestDate().toLocalDate())
                .distinct()
                .count();

        if (numberOfCategories <= 1) {
            // Если категорий 0 или 1, устанавливаем фиксированное значение
            dailyAnalyticsChart.setCategoryGap(10);
            return;
        }

        // Желаемая ширина столбца (можно настроить)
        double desiredBarWidth = 20; // Фиксированная ширина столбца (в пикселях)

        // Учитываем, что часть окна занимает интерфейс (например, панели и отступы)
        double availableChartWidth = windowWidth * 0.7; // Предполагаем, что 70% ширины окна доступно для диаграммы

        // Общая ширина всех столбцов
        double totalBarWidth = desiredBarWidth * numberOfCategories;

        // Оставшаяся ширина для промежутков
        double totalGapWidth = availableChartWidth - totalBarWidth;

        // Рассчитываем categoryGap
        double categoryGap = totalGapWidth / (numberOfCategories - 1);

        // Ограничиваем categoryGap диапазоном (например, от 5 до 350)
        dailyAnalyticsChart.setCategoryGap(Math.max(5, Math.min(categoryGap, 350)));

        System.out.println("Updated categoryGap to: " + dailyAnalyticsChart.getCategoryGap() + " for window width: " + windowWidth);
    }

    private void initializeRevenueWindRose() {
        revenueWindRosePane.getChildren().clear();
        List<PerformanceIndicator> indicators = bonusService.getAllPerformanceIndicators();
        if (indicators == null || indicators.isEmpty()) {
            return;
        }

        // Группируем выручку по сотрудникам
        Map<String, Double> revenueByEmployee = indicators.stream()
                .collect(Collectors.groupingBy(
                        pi -> pi.getEmployee().getName(),
                        Collectors.summingDouble(PerformanceIndicator::getRevenue)
                ));

        int employeeCount = revenueByEmployee.size();
        if (employeeCount == 0) return;

        double centerX = revenueWindRosePane.getPrefWidth() / 2;
        double centerY = revenueWindRosePane.getPrefHeight() / 2;
        double maxRadius = Math.min(centerX, centerY) * 0.8;
        double maxRevenue = revenueByEmployee.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        double angleStep = 360.0 / employeeCount;

        // Рисуем круги для масштаба
        for (int i = 1; i <= 5; i++) {
            Circle circle = new Circle(centerX, centerY, maxRadius * (i / 5.0));
            circle.setFill(null);
            circle.setStroke(javafx.scene.paint.Color.LIGHTGRAY);
            revenueWindRosePane.getChildren().add(circle);
        }

        // Рисуем оси
        for (int i = 0; i < employeeCount; i++) {
            double angle = Math.toRadians(i * angleStep);
            double x = centerX + maxRadius * Math.cos(angle);
            double y = centerY + maxRadius * Math.sin(angle);
            Line line = new Line(centerX, centerY, x, y);
            line.setStroke(javafx.scene.paint.Color.LIGHTGRAY);
            revenueWindRosePane.getChildren().add(line);
        }

        // Рисуем лепестки
        int index = 0;
        for (Map.Entry<String, Double> entry : revenueByEmployee.entrySet()) {
            String employeeName = entry.getKey();
            double revenue = entry.getValue();
            double normalizedRadius = (revenue / maxRevenue) * maxRadius;
            double angle = index * angleStep;

            // Создаём "лепесток" как сектор
            double startAngle = angle - (angleStep / 2);
            Arc arc = new Arc(centerX, centerY, normalizedRadius, normalizedRadius, startAngle, angleStep);
            arc.setType(ArcType.ROUND);
            arc.setFill(javafx.scene.paint.Color.color(Math.random(), Math.random(), Math.random(), 0.5));
            arc.setStroke(javafx.scene.paint.Color.BLACK);

            // Добавляем метку
            double labelAngle = Math.toRadians(angle);
            double labelRadius = maxRadius + 20;
            double labelX = centerX + labelRadius * Math.cos(labelAngle);
            double labelY = centerY + labelRadius * Math.sin(labelAngle);
            Text label = new Text(labelX, labelY, employeeName);
            label.setRotate(angle - 90); // Корректировка угла для читаемости
            label.setFill(javafx.scene.paint.Color.BLACK);

            revenueWindRosePane.getChildren().addAll(arc, label);

            index++;
        }
    }

    void loadAllData() {
        try {
            if (bonusService == null) {
                System.err.println("BonusService is not injected!");
                return;
            }
            if (employeeService == null) {
                System.err.println("EmployeeService is not injected!");
                return;
            }

            departmentData.clear();
            List<Department> departments = bonusService.getAllDepartments();
            if (departments == null) {
                System.err.println("Departments list is null!");
            } else {
                departmentData.addAll(departments);
                System.out.println("Loaded " + departmentData.size() + " departments");
            }

            roleData.clear();
            List<Role> roles = bonusService.getAllRoles();
            if (roles == null) {
                System.err.println("Roles list is null!");
            } else {
                roleData.addAll(roles);
                System.out.println("Loaded " + roleData.size() + " roles");
            }

            employeeData.clear();
            List<Employee> employees = bonusService.getAllEmployees();
            if (employees == null) {
                System.err.println("Employees list is null!");
            } else {
                employeeData.addAll(employees);
                System.out.println("Loaded " + employeeData.size() + " employees");
            }

            pendingEmployeeData.clear();
            List<Employee> pendingRequests = employeeService.getPendingRequests();
            if (pendingRequests == null) {
                System.err.println("Pending requests list is null!");
            } else {
                pendingEmployeeData.addAll(pendingRequests);
                System.out.println("Loaded " + pendingEmployeeData.size() + " pending requests");
            }

            piData.clear();
            List<PerformanceIndicator> performanceIndicators = bonusService.getAllPerformanceIndicators();
            if (performanceIndicators == null) {
                System.err.println("Performance indicators list is null!");
            } else {
                piData.addAll(performanceIndicators);
                System.out.println("Loaded " + piData.size() + " performance indicators");
            }

            bonusData.clear();
            List<Bonus> bonuses = bonusService.getAllBonuses();
            if (bonuses == null) {
                System.err.println("Bonuses list is null!");
            } else {
                bonusData.addAll(bonuses);
                System.out.println("Loaded " + bonusData.size() + " bonuses");
            }

            analyticsData.clear();
            List<Employee> analyticsRecords = employeeRepository.findAll();
            if (analyticsRecords == null) {
                System.err.println("Analytics records list is null!");
            } else {
                analyticsData.addAll(analyticsRecords);
                System.out.println("Loaded " + analyticsData.size() + " analytics records");
            }

            // Обновляем диаграмму по дням
            updateDailyAnalytics();

            // Обновляем розу ветров
            initializeRevenueWindRose();
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateDailyAnalytics() {
        dailyAnalyticsChart.getData().clear();

        // Группируем заявки по дням
        Map<LocalDate, Long> requestsByDay = analyticsData.stream()
                .filter(emp -> emp.getRequestDate() != null)
                .collect(Collectors.groupingBy(
                        emp -> emp.getRequestDate().toLocalDate(),
                        Collectors.counting()
                ));

        // Создаём серию данных для диаграммы
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Заявки по дням");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        requestsByDay.forEach((date, count) -> {
            series.getData().add(new XYChart.Data<>(dateFormatter.format(date), count.intValue()));
        });

        // Добавляем серию в диаграмму
        dailyAnalyticsChart.getData().add(series);

        // Обновляем categoryGap на основе текущей ширины окна
        if (stage != null) {
            updateCategoryGap(stage.getWidth());
        }

        System.out.println("Loaded " + requestsByDay.size() + " daily analytics entries for chart");
    }

    private void loadComboBoxes() {
        employeeDepartmentComboBox.getItems().clear();
        employeeDepartmentComboBox.getItems().addAll(departmentData.stream().map(Department::getName).toList());
        editEmployeeDepartmentComboBox.getItems().clear();
        editEmployeeDepartmentComboBox.getItems().addAll(departmentData.stream().map(Department::getName).toList());

        employeeRoleComboBox.getItems().clear();
        employeeRoleComboBox.getItems().addAll(roleData.stream().map(Role::getName).toList());
        editEmployeeRoleComboBox.getItems().clear();
        editEmployeeRoleComboBox.getItems().addAll(roleData.stream().map(Role::getName).toList());

        piEmployeeComboBox.getItems().clear();
        piEmployeeComboBox.getItems().addAll(employeeData.stream().map(Employee::getName).toList());
        editPiEmployeeComboBox.getItems().clear();
        editPiEmployeeComboBox.getItems().addAll(employeeData.stream().map(Employee::getName).toList());

        editBonusEmployeeComboBox.getItems().clear();
        editBonusEmployeeComboBox.getItems().addAll(employeeData.stream().map(Employee::getName).toList());
    }

    // CRUD для отделов
    @FXML
    private void addDepartment() {
        String name = departmentNameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("Error", "Department name cannot be empty.");
            return;
        }
        Department department = new Department();
        department.setName(name);
        bonusService.saveDepartment(department);
        loadAllData();
        loadComboBoxes();
        departmentNameField.clear();
    }

    @FXML
    private void editDepartment() {
        try {
            Long id = Long.parseLong(editDepartmentIdField.getText().trim());
            String name = editDepartmentNameField.getText().trim();
            if (name.isEmpty()) {
                showAlert("Error", "Department name cannot be empty.");
                return;
            }
            Department department = bonusService.getDepartmentById(id);
            if (department == null) {
                showAlert("Error", "Department not found.");
                return;
            }
            department.setName(name);
            bonusService.saveDepartment(department);
            loadAllData();
            loadComboBoxes();
            clearEditDepartmentFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Department ID.");
        }
    }

    @FXML
    private void deleteDepartment() {
        try {
            Long id = Long.parseLong(editDepartmentIdField.getText().trim());
            bonusService.deleteDepartment(id);
            loadAllData();
            loadComboBoxes();
            clearEditDepartmentFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Department ID.");
        }
    }

    private void clearEditDepartmentFields() {
        editDepartmentIdField.clear();
        editDepartmentNameField.clear();
    }

    // CRUD для ролей
    @FXML
    private void addRole() {
        try {
            String name = roleNameField.getText().trim();
            double baseBonusRate = Double.parseDouble(roleBaseBonusRateField.getText().trim());
            if (name.isEmpty()) {
                showAlert("Error", "Role name cannot be empty.");
                return;
            }
            if (baseBonusRate <= 0) {
                showAlert("Error", "Base bonus rate must be positive.");
                return;
            }
            Role role = new Role();
            role.setName(name);
            role.setBaseBonusRate(baseBonusRate);
            bonusService.saveRole(role);
            loadAllData();
            loadComboBoxes();
            roleNameField.clear();
            roleBaseBonusRateField.clear();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid base bonus rate.");
        }
    }

    @FXML
    private void editRole() {
        try {
            Long id = Long.parseLong(editRoleIdField.getText().trim());
            String name = editRoleNameField.getText().trim();
            double baseBonusRate = Double.parseDouble(editRoleBaseBonusRateField.getText().trim());
            if (name.isEmpty()) {
                showAlert("Error", "Role name cannot be empty.");
                return;
            }
            if (baseBonusRate <= 0) {
                showAlert("Error", "Base bonus rate must be positive.");
                return;
            }
            Role role = bonusService.getRoleById(id);
            if (role == null) {
                showAlert("Error", "Role not found.");
                return;
            }
            role.setName(name);
            role.setBaseBonusRate(baseBonusRate);
            bonusService.saveRole(role);
            loadAllData();
            loadComboBoxes();
            clearEditRoleFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Role ID or base bonus rate.");
        }
    }

    @FXML
    private void deleteRole() {
        try {
            Long id = Long.parseLong(editRoleIdField.getText().trim());
            bonusService.deleteRole(id);
            loadAllData();
            loadComboBoxes();
            clearEditRoleFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Role ID.");
        }
    }

    private void clearEditRoleFields() {
        editRoleIdField.clear();
        editRoleNameField.clear();
        editRoleBaseBonusRateField.clear();
    }

    // CRUD для сотрудников
    @FXML
    private void addEmployee() {
        try {
            String name = employeeNameField.getText().trim();
            String departmentName = employeeDepartmentComboBox.getValue();
            String roleName = employeeRoleComboBox.getValue();

            if (name.isEmpty() || departmentName == null || roleName == null) {
                showAlert("Error", "All fields must be filled.");
                return;
            }

            Department department = bonusService.getAllDepartments().stream()
                    .filter(d -> d.getName().equals(departmentName))
                    .findFirst()
                    .orElse(null);
            Role role = bonusService.getAllRoles().stream()
                    .filter(r -> r.getName().equals(roleName))
                    .findFirst()
                    .orElse(null);

            if (department == null || role == null) {
                showAlert("Error", "Selected department or role does not exist.");
                return;
            }

            Employee employee = new Employee();
            employee.setName(name);
            employee.setDepartment(department);
            employee.setRole(role);
            employee.setStatus("PENDING");
            employee.setRequestDate(LocalDateTime.now());
            bonusService.saveEmployee(employee);
            loadAllData();
            loadComboBoxes();
            employeeNameField.clear();
            employeeDepartmentComboBox.setValue(null);
            employeeRoleComboBox.setValue(null);
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void editEmployee() {
        try {
            Long id = Long.parseLong(editEmployeeIdField.getText().trim());
            String name = editEmployeeNameField.getText().trim();
            String departmentName = editEmployeeDepartmentComboBox.getValue();
            String roleName = editEmployeeRoleComboBox.getValue();

            if (name.isEmpty() || departmentName == null || roleName == null) {
                showAlert("Error", "All fields must be filled.");
                return;
            }

            Employee employee = bonusService.getEmployeeById(id);
            if (employee == null) {
                showAlert("Error", "Employee not found.");
                return;
            }

            Department department = bonusService.getAllDepartments().stream()
                    .filter(d -> d.getName().equals(departmentName))
                    .findFirst()
                    .orElse(null);
            Role role = bonusService.getAllRoles().stream()
                    .filter(r -> r.getName().equals(roleName))
                    .findFirst()
                    .orElse(null);

            if (department == null || role == null) {
                showAlert("Error", "Selected department or role does not exist.");
                return;
            }

            employee.setName(name);
            employee.setDepartment(department);
            employee.setRole(role);
            User user = userRepository.findById(employee.getUserId()).get();
            user.setRole(role);
            userRepository.save(user);
            bonusService.saveEmployee(employee);
            loadAllData();
            loadComboBoxes();
            clearEditEmployeeFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Employee ID.");
        }
    }

    @FXML
    private void deleteEmployee() {
        try {
            Long id = Long.parseLong(editEmployeeIdField.getText().trim());
            bonusService.deleteEmployee(id);
            loadAllData();
            loadComboBoxes();
            clearEditEmployeeFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Employee ID.");
        }
    }

    private void clearEditEmployeeFields() {
        editEmployeeIdField.clear();
        editEmployeeNameField.clear();
        editEmployeeDepartmentComboBox.setValue(null);
        editEmployeeRoleComboBox.setValue(null);
    }

    // CRUD для показателей
    @FXML
    private void addPerformanceIndicator() {
        try {
            String employeeName = piEmployeeComboBox.getValue();
            double kpi = Double.parseDouble(piKpiField.getText().trim());
            double revenue = Double.parseDouble(piRevenueField.getText().trim());
            int month = Integer.parseInt(piMonthField.getText().trim());
            int year = Integer.parseInt(piYearField.getText().trim());

            if (employeeName == null || kpi < 0 || kpi > 100 || revenue < 0 || month < 1 || month > 12 || year < 2000 || year > 2025) {
                showAlert("Error", "Invalid input. Check all fields (month: 1-12, year: 2000-2025).");
                return;
            }

            Employee employee = bonusService.getAllEmployees().stream()
                    .filter(e -> e.getName().equals(employeeName))
                    .findFirst()
                    .orElse(null);

            if (employee == null) {
                showAlert("Error", "Selected employee does not exist.");
                return;
            }

            PerformanceIndicator pi = new PerformanceIndicator();
            pi.setEmployee(employee);
            pi.setKpi(kpi);
            pi.setRevenue(revenue);
            pi.setMonth(month);
            pi.setYear(year);
            bonusService.savePerformanceIndicator(pi);
            loadAllData();
            loadComboBoxes();
            piEmployeeComboBox.setValue(null);
            piKpiField.clear();
            piRevenueField.clear();
            piMonthField.clear();
            piYearField.clear();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid numeric input.");
        }
    }

    @FXML
    private void editPerformanceIndicator() {
        try {
            Long id = Long.parseLong(editPiIdField.getText().trim());
            String employeeName = editPiEmployeeComboBox.getValue();
            double kpi = Double.parseDouble(editPiKpiField.getText().trim());
            double revenue = Double.parseDouble(editPiRevenueField.getText().trim());
            int month = Integer.parseInt(editPiMonthField.getText().trim());
            int year = Integer.parseInt(editPiYearField.getText().trim());

            if (employeeName == null || kpi < 0 || kpi > 100 || revenue < 0 || month < 1 || month > 12 || year < 2000 || year > 2025) {
                showAlert("Error", "Invalid input. Check all fields (month: 1-12, year: 2000-2025).");
                return;
            }

            PerformanceIndicator pi = bonusService.getPerformanceIndicatorById(id);
            if (pi == null) {
                showAlert("Error", "Performance Indicator not found.");
                return;
            }

            Employee employee = bonusService.getAllEmployees().stream()
                    .filter(e -> e.getName().equals(employeeName))
                    .findFirst()
                    .orElse(null);

            if (employee == null) {
                showAlert("Error", "Selected employee does not exist.");
                return;
            }

            pi.setEmployee(employee);
            pi.setKpi(kpi);
            pi.setRevenue(revenue);
            pi.setMonth(month);
            pi.setYear(year);
            bonusService.savePerformanceIndicator(pi);
            loadAllData();
            loadComboBoxes();
            clearEditPiFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid numeric input.");
        }
    }

    @FXML
    private void deletePerformanceIndicator() {
        try {
            Long id = Long.parseLong(editPiIdField.getText().trim());
            bonusService.deletePerformanceIndicator(id);
            loadAllData();
            loadComboBoxes();
            clearEditPiFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Performance Indicator ID.");
        }
    }

    private void clearEditPiFields() {
        editPiIdField.clear();
        editPiEmployeeComboBox.setValue(null);
        editPiKpiField.clear();
        editPiRevenueField.clear();
        editPiMonthField.clear();
        editPiYearField.clear();
    }

    // CRUD для премий
    @FXML
    private void editBonus() {
        try {
            Long id = Long.parseLong(editBonusIdField.getText().trim());
            String employeeName = editBonusEmployeeComboBox.getValue();
            double amount = Double.parseDouble(editBonusAmountField.getText().trim());
            int month = Integer.parseInt(editBonusMonthField.getText().trim());
            int year = Integer.parseInt(editBonusYearField.getText().trim());

            if (employeeName == null || amount < 0 || month < 1 || month > 12 || year < 2000 || year > 2025) {
                showAlert("Error", "Invalid input. Check all fields (month: 1-12, year: 2000-2025).");
                return;
            }

            Bonus bonus = bonusService.getBonusById(id);
            if (bonus == null) {
                showAlert("Error", "Bonus not found.");
                return;
            }

            Employee employee = bonusService.getAllEmployees().stream()
                    .filter(e -> e.getName().equals(employeeName))
                    .findFirst()
                    .orElse(null);

            if (employee == null) {
                showAlert("Error", "Selected employee does not exist.");
                return;
            }

            bonus.setEmployee(employee);
            bonus.setAmount(amount);
            bonus.setMonth(month);
            bonus.setYear(year);
            bonusService.saveBonus(bonus);
            loadAllData();
            loadComboBoxes();
            clearEditBonusFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid numeric input.");
        }
    }

    @FXML
    private void deleteBonus() {
        try {
            Long id = Long.parseLong(editBonusIdField.getText().trim());
            bonusService.deleteBonus(id);
            loadAllData();
            loadComboBoxes();
            clearEditBonusFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Bonus ID.");
        }
    }

    @FXML
    private void calculateBonuses() {
        bonusService.deleteAllBonuses();
        for (PerformanceIndicator pi : piData) {
            if (pi.getEmployee() == null || pi.getKpi() == null || pi.getRevenue() == null) {
                System.err.println("Skipping PerformanceIndicator with missing data: " + pi.getId());
                continue;
            }
            bonusService.calculateBonus(pi);
        }
        loadAllData();
        loadComboBoxes();
    }

    private void clearEditBonusFields() {
        editBonusIdField.clear();
        editBonusEmployeeComboBox.setValue(null);
        editBonusAmountField.clear();
        editBonusMonthField.clear();
        editBonusYearField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();
        Stage stage = (Stage) departmentTable.getScene().getWindow();
        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Вход");
    }

    @FXML
    private void executeCommand() {
        String command = consoleInput.getText().trim();
        if (command.isEmpty()) {
            consoleOutput.setText("Ошибка: Введите команду.");
            return;
        }

        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream captureOut = new PrintStream(baos);
        System.setOut(captureOut);

        try {
            ScriptEvaluator scriptEvaluator = new ScriptEvaluator();
            scriptEvaluator.setDefaultImports(new String[]{
                    "org.example.bonussystem.service.*",
                    "org.example.bonussystem.repository.*",
                    "org.example.bonussystem.model.*",
                    "org.example.bonussystem.config.*",
                    "org.example.bonussystem.controller.*",
                    "org.springframework.context.ApplicationContext",
                    "java.time.*",
                    "java.util.*",
                    "java.io.*"
            });

            String[] parameterNames = new String[]{
                    "employeeService",
                    "bonusService",
                    "userService",
                    "userRepository",
                    "employeeRepository",
                    "departmentRepository",
                    "roleRepository",
                    "performanceIndicatorRepository",
                    "bonusRepository",
                    "springContext"
            };
            Class<?>[] parameterTypes = new Class<?>[]{
                    EmployeeService.class,
                    BonusService.class,
                    UserService.class,
                    UserRepository.class,
                    EmployeeRepository.class,
                    DepartmentRepository.class,
                    RoleRepository.class,
                    PerformanceIndicatorRepository.class,
                    BonusRepository.class,
                    ApplicationContext.class
            };
            Object[] parameterValues = new Object[]{
                    employeeService,
                    bonusService,
                    userService,
                    userRepository,
                    employeeRepository,
                    departmentRepository,
                    roleRepository,
                    performanceIndicatorRepository,
                    bonusRepository,
                    springContext
            };

            scriptEvaluator.setParameters(parameterNames, parameterTypes);
            scriptEvaluator.setReturnType(void.class);
            scriptEvaluator.cook(command);
            scriptEvaluator.evaluate(parameterValues);

            System.out.flush();
            String output = baos.toString();
            consoleOutput.clear();
            if (!output.isEmpty()) {
                consoleOutput.appendText("Вывод: " + output + "\n");
            } else {
                consoleOutput.appendText("Команда выполнена без вывода.\n");
            }
        } catch (Exception e) {
            consoleOutput.clear();
            consoleOutput.appendText("Ошибка: " + e.getMessage() + "\n");
        } finally {
            System.setOut(originalOut);
        }
    }

    @FXML
    private void downloadAnalyticsAsCsv() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить CSV файл");
            fileChooser.setInitialFileName("employee_requests_analytics.csv");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

            File file = fileChooser.showSaveDialog(analyticsTable.getScene().getWindow());
            if (file == null) {
                return;
            }

            FileWriter writer = new FileWriter(file);
            writer.write("ID,Name,Role,Department,Status,Request Date\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (Employee emp : analyticsData) {
                String roleName = emp.getRole() != null ? emp.getRole().getName() : "N/A";
                String deptName = emp.getDepartment() != null ? emp.getDepartment().getName() : "N/A";
                String requestDate = emp.getRequestDate() != null ? emp.getRequestDate().format(formatter) : "N/A";
                writer.write(String.format("%d,%s,%s,%s,%s,%s\n",
                        emp.getId(), emp.getName(), roleName, deptName, emp.getStatus(), requestDate));
            }
            writer.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успех");
            alert.setHeaderText(null);
            alert.setContentText("Файл успешно сохранён: " + file.getAbsolutePath());
            alert.showAndWait();
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось сохранить файл: " + e.getMessage());
        }
    }
}