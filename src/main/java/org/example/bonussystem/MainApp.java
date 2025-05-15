package org.example.bonussystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.bonussystem.controller.LoginController;
import org.example.bonussystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
        BCryptPasswordEncoder bCryptPasswordEncoder = springContext.getBean(BCryptPasswordEncoder.class);
        // Передаём springContext через конструктор
        loader.setControllerFactory(param -> new LoginController(springContext, userService, bCryptPasswordEncoder));

        Parent root = loader.load();
        LoginController controller = loader.getController();
        System.out.println("LoginController loaded");

        // Добавление фона с GIF
        Image gifImage = new Image(getClass().getResource("/gif1.gif").toExternalForm());
        ImageView gifImageView = new ImageView(gifImage);

        // Настройка размеров для GIF
        gifImageView.setFitWidth(primaryStage.getWidth() * 2);
        gifImageView.setFitHeight(primaryStage.getHeight() * 2);

        // Добавление GIF в панель
        Pane backgroundPane = new Pane();
        backgroundPane.getChildren().add(gifImageView);

        // Убедитесь, что другие элементы (например, элементы интерфейса) накладываются поверх фона
        backgroundPane.getChildren().add(root);  // Добавляем root на фон

        // Создание сцены с фоновым изображением
        Scene scene = new Scene(backgroundPane, 400, 300);
        primaryStage.setTitle("Вход");
        primaryStage.setScene(scene);
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
