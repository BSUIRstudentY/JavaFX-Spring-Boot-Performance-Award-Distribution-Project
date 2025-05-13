package org.example.bonussystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.bonussystem.model.Department;
import org.example.bonussystem.model.Role;
import org.example.bonussystem.model.User;
import org.example.bonussystem.repository.DepartmentRepository;
import org.example.bonussystem.repository.EmployeeRepository;
import org.example.bonussystem.repository.PerformanceIndicatorRepository;
import org.example.bonussystem.repository.RoleRepository;
import org.example.bonussystem.service.BonusService;
import org.example.bonussystem.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Scope("prototype")
public class EmployeeRegistrationController {

    @FXML private TextField employeeNameField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private ComboBox<Department> departmentComboBox;
    @FXML private Label errorLabel;

    private final ApplicationContext springContext;
    private final User currentUser;
    private final EmployeeService employeeService;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public EmployeeRegistrationController(ApplicationContext springContext, User currentUser,
                                          EmployeeService employeeService, RoleRepository roleRepository,
                                          DepartmentRepository departmentRepository) {
        this.springContext = springContext;
        this.currentUser = currentUser;
        if (currentUser == null) {
            throw new IllegalArgumentException("currentUser cannot be null in EmployeeRegistrationController");
        }
        this.employeeService = employeeService;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        System.out.println("EmployeeRegistrationController created with User ID: " + currentUser.getId() + ", Username: " + currentUser.getUsername());
    }

    @FXML
    private void initialize() {
        System.out.println("Initializing EmployeeRegistrationController - employeeNameField: " + (employeeNameField != null) +
                ", roleComboBox: " + (roleComboBox != null) +
                ", departmentComboBox: " + (departmentComboBox != null) +
                ", errorLabel: " + (errorLabel != null));

        if (errorLabel != null) {
            errorLabel.setText("");
        }

        // Заполняем ComboBox для ролей
        List<Role> roles = roleRepository.findAll();
        roleComboBox.getItems().addAll(roles);
        roleComboBox.setCellFactory(lv -> new RoleListCell());
        roleComboBox.setButtonCell(new RoleListCell());

        // Заполняем ComboBox для отделов
        List<Department> departments = departmentRepository.findAll();
        departmentComboBox.getItems().addAll(departments);
        departmentComboBox.setCellFactory(lv -> new DepartmentListCell());
        departmentComboBox.setButtonCell(new DepartmentListCell());
    }

    @FXML
    private void registerEmployee() {
        String employeeName = employeeNameField.getText().trim();
        Role selectedRole = roleComboBox.getSelectionModel().getSelectedItem();
        Department selectedDepartment = departmentComboBox.getSelectionModel().getSelectedItem();

        if (employeeName.isEmpty()) {
            errorLabel.setText("Имя сотрудника не может быть пустым");
            return;
        }
        if (selectedRole == null) {
            errorLabel.setText("Выберите роль");
            return;
        }
        if (selectedDepartment == null) {
            errorLabel.setText("Выберите отдел");
            return;
        }

        // Сохраняем заявку
        employeeService.submitEmployeeRequest(
                employeeName,
                currentUser.getId(),
                selectedRole.getId(),
                selectedDepartment.getId()
        );
        System.out.println("Employee request submitted: Name=" + employeeName + ", Role=" + selectedRole.getName() + ", Department=" + selectedDepartment.getName());

        // Переходим обратно в UserController и обновляем данные
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user.fxml"));

            // Получаем зависимости для UserController
            PerformanceIndicatorRepository perfRepo = springContext.getBean(PerformanceIndicatorRepository.class);
            EmployeeRepository empRepo = springContext.getBean(EmployeeRepository.class);
            BonusService bonusService = springContext.getBean(BonusService.class);

            // Передаём currentUser через конструктор
            loader.setControllerFactory(param -> new UserController(springContext, currentUser, perfRepo, empRepo, bonusService));

            Parent root = loader.load();
            UserController controller = loader.getController();
            System.out.println("UserController loaded with user: " + currentUser.getUsername());

            Stage stage = (Stage) employeeNameField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("Пользовательский интерфейс");

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Ошибка загрузки интерфейса. Попробуйте еще раз.");
        }
    }

    @FXML
    private void goBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/user.fxml"));

        // Получаем зависимости для UserController
        PerformanceIndicatorRepository perfRepo = springContext.getBean(PerformanceIndicatorRepository.class);
        EmployeeRepository empRepo = springContext.getBean(EmployeeRepository.class);
        BonusService bonusService = springContext.getBean(BonusService.class);

        // Передаём currentUser через конструктор
        loader.setControllerFactory(param -> new UserController(springContext, currentUser, perfRepo, empRepo, bonusService));

        Parent root = loader.load();
        UserController controller = loader.getController();
        System.out.println("UserController loaded with user: " + currentUser.getUsername());

        Stage stage = (Stage) employeeNameField.getScene().getWindow();
        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Пользовательский интерфейс");
    }
}

// Классы для отображения ролей и отделов в ComboBox
class RoleListCell extends javafx.scene.control.ListCell<Role> {
    @Override
    protected void updateItem(Role item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            setText(item.getName());
        }
    }
}

class DepartmentListCell extends javafx.scene.control.ListCell<Department> {
    @Override
    protected void updateItem(Department item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            setText(item.getName());
        }
    }
}