package org.example.bonussystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.bonussystem.controller.LoginController;
import org.example.bonussystem.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MainApp extends Application {

    private ApplicationContext springContext;

    @Override
    public void init() {
        springContext = SpringApplication.run(MainApp.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));

        // Получаем зависимости для LoginController
        UserService userService = springContext.getBean(UserService.class);

        // Передаём springContext через конструктор
        loader.setControllerFactory(param -> new LoginController(springContext, userService));

        Parent root = loader.load();
        LoginController controller = loader.getController();
        System.out.println("LoginController loaded");

        primaryStage.setTitle("Вход");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Закрытие Spring-контекста при завершении приложения
        if (springContext != null) {
            SpringApplication.exit(springContext, () -> 0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}