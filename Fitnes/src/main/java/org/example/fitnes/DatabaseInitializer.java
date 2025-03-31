package org.example.fitnes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        try (Connection conn = DatabaseConnection.connect()) {
            createTables(conn);
            insertSampleDataIfNotExists(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        String createTrainersTable = "CREATE TABLE IF NOT EXISTS trainers (" +
                "id SERIAL PRIMARY KEY," +
                "first_name VARCHAR(100) NOT NULL," +
                "last_name VARCHAR(100) NOT NULL" +
                ")";

        String createClientsTable = "CREATE TABLE IF NOT EXISTS clients (" +
                "id SERIAL PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "trainer_id INTEGER REFERENCES trainers(id)" +
                ")";

        String createWorkoutsTable = "CREATE TABLE IF NOT EXISTS workouts (" +
                "id SERIAL PRIMARY KEY," +
                "client_id INTEGER REFERENCES clients(id)," +
                "trainer_id INTEGER REFERENCES trainers(id)," +
                "workout_type VARCHAR(100) NOT NULL," +
                "workout_time TIME NOT NULL" +
                ")";

        try (PreparedStatement stmt = conn.prepareStatement(createTrainersTable)) {
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement(createClientsTable)) {
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement(createWorkoutsTable)) {
            stmt.executeUpdate();
        }
    }

    private static void insertSampleDataIfNotExists(Connection conn) throws SQLException {
        if (isTableEmpty(conn, "trainers")) {
            String insertTrainer1 = "INSERT INTO trainers (first_name, last_name) VALUES ('Иван', 'Иванов')";
            String insertTrainer2 = "INSERT INTO trainers (first_name, last_name) VALUES ('Анна', 'Петрова')";
            String insertTrainer3 = "INSERT INTO trainers (first_name, last_name) VALUES ('Дмитрий', 'Сидоров')";

            try (PreparedStatement stmt = conn.prepareStatement(insertTrainer1)) {
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertTrainer2)) {
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertTrainer3)) {
                stmt.executeUpdate();
            }
        }

        if (isTableEmpty(conn, "clients")) {
            String insertClient1 = "INSERT INTO clients (name, trainer_id) VALUES ('Алексей', 1)";
            String insertClient2 = "INSERT INTO clients (name, trainer_id) VALUES ('Мария', 1)";
            String insertClient3 = "INSERT INTO clients (name, trainer_id) VALUES ('Елена', 2)";
            String insertClient4 = "INSERT INTO clients (name, trainer_id) VALUES ('Сергей', 3)";

            try (PreparedStatement stmt = conn.prepareStatement(insertClient1)) {
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertClient2)) {
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertClient3)) {
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertClient4)) {
                stmt.executeUpdate();
            }
        }
    }

    private static boolean isTableEmpty(Connection conn, String tableName) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return true;
    }
}
