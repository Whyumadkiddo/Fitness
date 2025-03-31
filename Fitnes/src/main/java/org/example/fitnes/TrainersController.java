package org.example.fitnes;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TrainersController {

    @FXML
    private TableView<TrainerClient> trainersTableView;

    @FXML
    private TableColumn<TrainerClient, String> trainerColumn;

    @FXML
    private TableColumn<TrainerClient, String> clientColumn;

    @FXML
    private TableColumn<TrainerClient, String> workoutTypeColumn;

    @FXML
    private TableColumn<TrainerClient, String> workoutTimeColumn;

    @FXML
    private Button addClientButton;

    private ObservableList<TrainerClient> trainerClientList = FXCollections.observableArrayList();

    private static TrainersController instance;

    @FXML
    public void initialize() {
        instance = this;
        trainerColumn.setCellValueFactory(new PropertyValueFactory<>("trainerName"));
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        workoutTypeColumn.setCellValueFactory(new PropertyValueFactory<>("workoutType"));
        workoutTimeColumn.setCellValueFactory(new PropertyValueFactory<>("workoutTime"));

        trainersTableView.setItems(trainerClientList);

        loadTrainersAndClientsWithWorkouts();
    }

    public void loadTrainersAndClientsWithWorkouts() {
        trainerClientList.clear();

        try (Connection conn = DatabaseConnection.connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT t.first_name, t.last_name, c.id as client_id, c.name as client_name, " +
                    "w.workout_type, w.workout_time " +
                    "FROM trainers t " +
                    "JOIN clients c ON t.id = c.trainer_id " +
                    "LEFT JOIN workouts w ON c.id = w.client_id");

            while (rs.next()) {
                String trainerName = rs.getString("first_name") + " " + rs.getString("last_name");
                String clientName = rs.getString("client_name");
                int clientId = rs.getInt("client_id");
                String workoutType = rs.getString("workout_type");
                String workoutTime = rs.getString("workout_time");
                trainerClientList.add(new TrainerClient(trainerName, clientName, clientId, workoutType, workoutTime));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onAddWorkoutButtonClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add_workout.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Добавить тренировку");
        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }

    @FXML
    protected void onAddClientButtonClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add_client.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Добавить клиента");
        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }

    public static TrainersController getInstance() {
        return instance;
    }

    public static class TrainerClient {
        private final SimpleStringProperty trainerName;
        private final SimpleStringProperty clientName;
        private final int clientId;
        private final SimpleStringProperty workoutType;
        private final SimpleStringProperty workoutTime;

        public TrainerClient(String trainerName, String clientName, int clientId, String workoutType, String workoutTime) {
            this.trainerName = new SimpleStringProperty(trainerName);
            this.clientName = new SimpleStringProperty(clientName);
            this.clientId = clientId;
            this.workoutType = new SimpleStringProperty(workoutType != null ? workoutType : "");
            this.workoutTime = new SimpleStringProperty(workoutTime != null ? workoutTime : "");
        }

        public String getTrainerName() {
            return trainerName.get();
        }

        public String getClientName() {
            return clientName.get();
        }

        public int getClientId() {
            return clientId;
        }

        public String getWorkoutType() {
            return workoutType.get();
        }

        public String getWorkoutTime() {
            return workoutTime.get();
        }
    }
}
