package org.example.bonussystem.controller;

import org.example.bonussystem.model.Role;
import org.example.bonussystem.model.User;
import org.example.bonussystem.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private ApplicationContext springContext;
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setSpringContext(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    @FXML
    private void register() throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Пожалуйста, заполните все поля.");
            return;
        }

        if (userService.findByUsername(username) != null) {
            errorLabel.setText("Пользователь с таким именем уже существует.");
            return;
        }

        Role userRole = userService.findOrCreateRoleByName("User", 0.1);
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(userRole);
        userService.save(user);

        // Переход к окну входа после успешной регистрации
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Вход");
    }
}