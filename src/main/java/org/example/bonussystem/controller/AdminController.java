package org.example.bonussystem.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory; // Добавляем импорт
import javafx.stage.Stage;
import org.example.bonussystem.model.*;
import org.example.bonussystem.repository.UserRepository;
import org.example.bonussystem.service.BonusService;
import org.example.bonussystem.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AdminController {

    private User currentUser;
    private ApplicationContext springContext;
    private EmployeeService employeeService;

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    @Autowired
    public void setSpringContext(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    @Autowired
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Отделы
    @FXML private TableView<Department> departmentTable;
    @FXML private TableColumn<Department, Long> departmentIdColumn;
    @FXML private TableColumn<Department, String> departmentNameColumn;
    @FXML private TextField departmentNameField;
    @FXML private TextField editDepartmentIdField;
    @FXML private TextField editDepartmentNameField;

    // Роли
    @FXML private TableView<Role> roleTable;
    @FXML private TableColumn<Role, Long> roleIdColumn;
    @FXML private TableColumn<Role, String> roleNameColumn;
    @FXML private TableColumn<Role, Double> roleBaseBonusRateColumn;
    @FXML private TextField roleNameField;
    @FXML private TextField roleBaseBonusRateField;
    @FXML private TextField editRoleIdField;
    @FXML private TextField editRoleNameField;
    @FXML private TextField editRoleBaseBonusRateField;

    // Сотрудники
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

    // Заявки
    @FXML private TableView<Employee> pendingEmployeeTable;
    @FXML private TableColumn<Employee, Long> pendingEmployeeIdColumn;
    @FXML private TableColumn<Employee, String> pendingEmployeeNameColumn;
    @FXML private TableColumn<Employee, String> pendingEmployeeRoleColumn;
    @FXML private TableColumn<Employee, String> pendingEmployeeDepartmentColumn;
    @FXML private TableColumn<Employee, String> pendingEmployeeStatusColumn;
    @FXML private TableColumn<Employee, Long> pendingEmployeeActionsColumn;

    // Показатели
    @FXML private TableView<PerformanceIndicator> piTable;
    @FXML private TableColumn<PerformanceIndicator, Long> piIdColumn;
    @FXML private TableColumn<PerformanceIndicator, String> piEmployeeNameColumn;
    @FXML private TableColumn<PerformanceIndicator, Double> piKpiColumn;
    @FXML private TableColumn<PerformanceIndicator, Double> piRevenueColumn;
    @FXML private TableColumn<PerformanceIndicator, Integer> piYearColumn;
    @FXML private ComboBox<String> piEmployeeComboBox;
    @FXML private TextField piKpiField;
    @FXML private TextField piRevenueField;
    @FXML private TextField piYearField;
    @FXML private TextField editPiIdField;
    @FXML private ComboBox<String> editPiEmployeeComboBox;
    @FXML private TextField editPiKpiField;
    @FXML private TextField editPiRevenueField;
    @FXML private TextField editPiYearField;

    // Премии
    @FXML private TableView<Bonus> bonusTable;
    @FXML private TableColumn<Bonus, Long> bonusIdColumn;
    @FXML private TableColumn<Bonus, String> bonusEmployeeNameColumn;
    @FXML private TableColumn<Bonus, Double> bonusAmountColumn;
    @FXML private TableColumn<Bonus, Integer> bonusYearColumn;
    @FXML private TextField editBonusIdField;
    @FXML private ComboBox<String> editBonusEmployeeComboBox;
    @FXML private TextField editBonusAmountField;
    @FXML private TextField editBonusYearField;

    @Autowired
    private BonusService bonusService;
    private ObservableList<Department> departmentData = FXCollections.observableArrayList();
    private ObservableList<Role> roleData = FXCollections.observableArrayList();
    private ObservableList<Employee> employeeData = FXCollections.observableArrayList();
    private ObservableList<Employee> pendingEmployeeData = FXCollections.observableArrayList();
    private ObservableList<PerformanceIndicator> piData = FXCollections.observableArrayList();
    private ObservableList<Bonus> bonusData = FXCollections.observableArrayList();

    @Autowired
    private UserRepository userRepository;

    public void setBonusService(BonusService bonusService) {
        this.bonusService = bonusService;
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
        piYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        piTable.setItems(piData);

        // Премии
        bonusIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bonusEmployeeNameColumn.setCellValueFactory(cellData -> {
            Employee emp = cellData.getValue().getEmployee();
            return new SimpleStringProperty(emp != null ? emp.getName() : "N/A");
        });
        bonusAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        bonusYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        bonusTable.setItems(bonusData);

        // Заполняем ComboBox и загружаем данные
        loadComboBoxes();
        loadAllData();
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
            departmentData.addAll(bonusService.getAllDepartments());
            System.out.println("Loaded " + departmentData.size() + " departments");

            roleData.clear();
            roleData.addAll(bonusService.getAllRoles());
            System.out.println("Loaded " + roleData.size() + " roles");

            employeeData.clear();
            employeeData.addAll(bonusService.getAllEmployees());
            System.out.println("Loaded " + employeeData.size() + " employees");

            pendingEmployeeData.clear();
            pendingEmployeeData.addAll(employeeService.getPendingRequests());
            System.out.println("Loaded " + pendingEmployeeData.size() + " pending requests");

            piData.clear();
            piData.addAll(bonusService.getAllPerformanceIndicators());
            System.out.println("Loaded " + piData.size() + " performance indicators");

            bonusData.clear();
            bonusData.addAll(bonusService.getAllBonuses());
            System.out.println("Loaded " + bonusData.size() + " bonuses");
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
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
        departmentData.add(department);
        departmentNameField.clear();
        loadComboBoxes();
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
            roleData.add(role);
            roleNameField.clear();
            roleBaseBonusRateField.clear();
            loadComboBoxes();
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
            employee.setStatus("APPROVED");
            bonusService.saveEmployee(employee);
            employeeData.add(employee);
            employeeNameField.clear();
            employeeDepartmentComboBox.setValue(null);
            employeeRoleComboBox.setValue(null);
            loadComboBoxes();
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
            int year = Integer.parseInt(piYearField.getText().trim());

            if (employeeName == null || kpi < 0 || kpi > 100 || revenue < 0 || year < 2000 || year > 2025) {
                showAlert("Error", "Invalid input. Check all fields.");
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
            pi.setYear(year);
            bonusService.savePerformanceIndicator(pi);
            piData.add(pi);
            piEmployeeComboBox.setValue(null);
            piKpiField.clear();
            piRevenueField.clear();
            piYearField.clear();
            loadComboBoxes();
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
            int year = Integer.parseInt(editPiYearField.getText().trim());

            if (employeeName == null || kpi < 0 || kpi > 100 || revenue < 0 || year < 2000 || year > 2025) {
                showAlert("Error", "Invalid input. Check all fields.");
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
        editPiYearField.clear();
    }

    // CRUD для премий
    @FXML
    private void editBonus() {
        try {
            Long id = Long.parseLong(editBonusIdField.getText().trim());
            String employeeName = editBonusEmployeeComboBox.getValue();
            double amount = Double.parseDouble(editBonusAmountField.getText().trim());
            int year = Integer.parseInt(editBonusYearField.getText().trim());

            if (employeeName == null || amount < 0 || year < 2000 || year > 2025) {
                showAlert("Error", "Invalid input. Check all fields.");
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
}