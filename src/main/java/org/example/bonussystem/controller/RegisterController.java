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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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

        // Проверка на заполнение полей
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Пожалуйста, заполните все поля.");
            return;
        }

        // Простая валидация пароля (минимум 6 символов)
        if (password.length() < 6) {
            errorLabel.setText("Пароль должен содержать минимум 6 символов.");
            return;
        }

        // Проверка уникальности имени пользователя
        if (userService.findByUsername(username) != null) {
            errorLabel.setText("Пользователь с таким именем уже существует.");
            return;
        }

        try {
            // Получаем или создаём роль "User"
            Role userRole = userService.findOrCreateRoleByName("User", 0.1);
            User user = new User();
            user.setUsername(username);
            user.setPassword(bCryptPasswordEncoder.encode(password)); // Здесь можно добавить хеширование, например, через BCrypt
            user.setRole(userRole);

            // Сохраняем пользователя
            userService.save(user);
            errorLabel.setText("Регистрация успешна! Переход к входу...");
            errorLabel.setStyle("-fx-text-fill: green;");

            // Задержка перед переходом (опционально для обратной связи)
            Thread.sleep(1000);

            // Переход к окну входа
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 300));
            stage.setTitle("Вход");
        } catch (Exception e) {
            errorLabel.setText("Ошибка при регистрации: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
}