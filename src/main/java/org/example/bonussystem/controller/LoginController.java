package org.example.bonussystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.bonussystem.model.User;
import org.example.bonussystem.repository.EmployeeRepository;
import org.example.bonussystem.repository.PerformanceIndicatorRepository;
import org.example.bonussystem.service.BonusService;
import org.example.bonussystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Scope("prototype")
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final ApplicationContext springContext;
    private final UserService userService;

    @Autowired
    public LoginController(ApplicationContext springContext, UserService userService) {
        this.springContext = springContext;
        this.userService = userService;
    }

    @FXML
    private void initialize() {
        System.out.println("Initializing LoginController - usernameField: " + (usernameField != null) +
                ", passwordField: " + (passwordField != null) +
                ", errorLabel: " + (errorLabel != null));

        if (errorLabel != null) {
            errorLabel.setText("");
        }
    }

    @FXML
    private void login() {
        errorLabel.setText("");  // Очищаем сообщение об ошибке

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Пожалуйста, заполните все поля.");
            return;
        }

        User user = userService.findByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            errorLabel.setText("Неверное имя пользователя или пароль.");
            return;
        }

        Stage stage = (Stage) usernameField.getScene().getWindow();

        try {
            if ("Admin".equals(user.getRole() != null ? user.getRole().getName() : "")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin.fxml"));
                loader.setControllerFactory(springContext::getBean);
                Parent root = loader.load();

                AdminController controller = loader.getController();
                System.out.println("AdminController initialized with user: " + user.getUsername());
                controller.setCurrentUser(user);

                stage.setScene(new Scene(root, 600, 400));
                stage.setTitle("Панель администратора");
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user.fxml"));

                // Проверяем, что все зависимости доступны
                ApplicationContext ctx = springContext;
                PerformanceIndicatorRepository perfRepo = ctx.getBean(PerformanceIndicatorRepository.class);
                EmployeeRepository empRepo = ctx.getBean(EmployeeRepository.class);
                BonusService bonusService = ctx.getBean(BonusService.class);

                System.out.println("Creating UserController with user: " + user.getUsername());
                loader.setControllerFactory(param -> new UserController(
                        ctx, user, perfRepo, empRepo, bonusService
                ));

                Parent root = loader.load();
                UserController controller = loader.getController();
                System.out.println("UserController loaded with user: " + user.getUsername());

                stage.setScene(new Scene(root, 600, 400));
                stage.setTitle("Пользовательский интерфейс");
            }

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Ошибка загрузки интерфейса. Попробуйте еще раз.");
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Внутренняя ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void showRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 300));
            stage.setTitle("Регистрация");

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Ошибка загрузки страницы регистрации.");
        }
    }
}