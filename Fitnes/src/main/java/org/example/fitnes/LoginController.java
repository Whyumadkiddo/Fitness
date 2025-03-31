package org.example.fitnes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "123";

    @FXML
    protected void onLoginButtonClick() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            errorLabel.setText("Успешный вход!");
            openTrainersWindow();
        } else {
            errorLabel.setText("Неверное имя пользователя или пароль!");
        }
    }

    private void openTrainersWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("trainers.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Тренеры и их клиенты");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}
