package org.example.fitnes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginApplication extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DatabaseConnection.connect()) {
            System.out.println("Database connected successfully!");
            DatabaseInitializer.initialize();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the database!");
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Вход в систему");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
