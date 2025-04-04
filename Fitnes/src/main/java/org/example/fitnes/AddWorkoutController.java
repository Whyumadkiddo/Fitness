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
import java.time.LocalTime;

public class AddWorkoutController {

    @FXML
    private ComboBox<String> clientComboBox;

    @FXML
    private ComboBox<String> trainerComboBox;

    @FXML
    private TextField workoutTypeField;

    @FXML
    private ComboBox<LocalTime> workoutTimeComboBox;

    private ObservableList<String> clients = FXCollections.observableArrayList();
    private ObservableList<String> trainers = FXCollections.observableArrayList();
    private ObservableList<LocalTime> workoutTimes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadClients();
        loadTrainers();
        loadWorkoutTimes();
    }

    private void loadClients() {
        clients.clear();

        try (Connection conn = DatabaseConnection.connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM clients");

            while (rs.next()) {
                clients.add(rs.getString("name"));
            }

            clientComboBox.setItems(clients);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void loadWorkoutTimes() {
        workoutTimes.clear();

        for (int hour = 9; hour <= 22; hour++) {
            workoutTimes.add(LocalTime.of(hour, 0));
        }

        workoutTimeComboBox.setItems(workoutTimes);
    }

    @FXML
    protected void onSaveWorkoutButtonClick() {
        String clientName = clientComboBox.getValue();
        String trainerName = trainerComboBox.getValue();
        String workoutType = workoutTypeField.getText();
        LocalTime workoutTime = workoutTimeComboBox.getValue();

        if (clientName != null && trainerName != null && workoutType != null && workoutTime != null) {
            try (Connection conn = DatabaseConnection.connect()) {
                String insertWorkout = "INSERT INTO workouts (client_id, trainer_id, workout_type, workout_time) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(insertWorkout);

                int clientId = getClientId(clientName);
                int trainerId = getTrainerId(trainerName);

                stmt.setInt(1, clientId);
                stmt.setInt(2, trainerId);
                stmt.setString(3, workoutType);
                stmt.setTime(4, java.sql.Time.valueOf(workoutTime));

                stmt.executeUpdate();

                notifyDataChanged();

                Stage stage = (Stage) clientComboBox.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int getClientId(String clientName) throws SQLException {
        try (Connection conn = DatabaseConnection.connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id FROM clients WHERE name = '" + clientName + "'");

            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
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
        TrainersController.getInstance().loadTrainersAndClientsWithWorkouts();
    }
}
