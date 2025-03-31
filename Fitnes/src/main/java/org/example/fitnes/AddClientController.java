package org.example.fitnes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AddClientController {

    @FXML
    private TextField clientNameField;

    @FXML
    private ComboBox<String> trainerComboBox;

    private ObservableList<String> trainers = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadTrainers();
    }

    private void loadTrainers() {
        trainers.clear();

        try (Connection conn = DatabaseConnection.connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM trainers");

            while (rs.next()) {
                trainers.add(rs.getString("first_name") + " " + rs.getString("last_name"));
            }

            trainerComboBox.setItems(trainers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onSaveClientButtonClick() {
        String clientName = clientNameField.getText();
        String trainerName = trainerComboBox.getValue();

        if (clientName != null && !clientName.isEmpty() && trainerName != null) {
            try (Connection conn = DatabaseConnection.connect()) {
                String insertClient = "INSERT INTO clients (name, trainer_id) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(insertClient);

                // Получить ID тренера
                int trainerId = getTrainerId(trainerName);

                stmt.setString(1, clientName);
                stmt.setInt(2, trainerId);

                stmt.executeUpdate();

                // Уведомляем основное окно о необходимости обновления данных
                notifyDataChanged();

                // Закрываем окно после успешного добавления клиента
                Stage stage = (Stage) clientNameField.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int getTrainerId(String trainerName) throws SQLException {
        try (Connection conn = DatabaseConnection.connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id FROM trainers WHERE first_name || ' ' || last_name = '" + trainerName + "'");

            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    private void notifyDataChanged() {
        // Уведомляем основное окно о необходимости обновления данных
        TrainersController.getInstance().loadTrainersAndClientsWithWorkouts();
    }
}
