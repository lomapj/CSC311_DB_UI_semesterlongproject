package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Person;

import java.sql.*;

public class DbConnectivityClass {
    final static String DB_NAME = "CSC311_BD_TEMP";
    final static String SQL_SERVER_URL = "jdbc:mysql://csc311lomanginoserver.mysql.database.azure.com";
    final static String DB_URL = "jdbc:mysql://csc311lomanginoserver.mysql.database.azure.com/" + DB_NAME;
    final static String USERNAME = "lomapj";
    final static String PASSWORD = "2ManyTools";

    private final ObservableList<Person> data = FXCollections.observableArrayList();

    public ObservableList<Person> getData() {
        connectToDatabase();
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (!resultSet.isBeforeFirst()) {
                System.out.println("No data");
            }

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                String department = resultSet.getString("department");
                String major = resultSet.getString("major");
                String email = resultSet.getString("email");
                String imageURL = resultSet.getString("imageURL");
                data.add(new Person(id, first_name, last_name, department, major, email, imageURL));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public boolean connectToDatabase() {
        boolean hasRegisteredUsers = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(SQL_SERVER_URL, USERNAME, PASSWORD);
                 Statement statement = conn.createStatement()) {
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                 Statement statement = conn.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT(10) NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                        "first_name VARCHAR(200) NOT NULL," +
                        "last_name VARCHAR(200) NOT NULL," +
                        "department VARCHAR(200)," +
                        "major VARCHAR(200)," +
                        "email VARCHAR(200) NOT NULL UNIQUE," +
                        "imageURL VARCHAR(200))";
                statement.executeUpdate(sql);

                try (ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users")) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        hasRegisteredUsers = true;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasRegisteredUsers;
    }

    public void queryUserByLastName(String name) {
        connectToDatabase();
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM users WHERE last_name = ?")) {

            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                String major = resultSet.getString("major");
                String department = resultSet.getString("department");
                System.out.println("ID: " + id + ", Name: " + first_name + " " + last_name +
                        ", Major: " + major + ", Department: " + department);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listAllUsers() {
        connectToDatabase();
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                String department = resultSet.getString("department");
                String major = resultSet.getString("major");
                String email = resultSet.getString("email");
                System.out.println("ID: " + id + ", Name: " + first_name + " " + last_name +
                        ", Department: " + department + ", Major: " + major + ", Email: " + email);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertUser(Person person) {
        connectToDatabase();
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(
                     "INSERT INTO users (first_name, last_name, department, major, email, imageURL) VALUES (?, ?, ?, ?, ?, ?)")) {

            preparedStatement.setString(1, person.getFirstName());
            preparedStatement.setString(2, person.getLastName());
            preparedStatement.setString(3, person.getDepartment());
            preparedStatement.setString(4, person.getMajor());
            preparedStatement.setString(5, person.getEmail());
            preparedStatement.setString(6, person.getImageURL());

            int row = preparedStatement.executeUpdate();
            if (row > 0) {
                System.out.println("A new user was inserted successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editUser(int id, Person p) {
        connectToDatabase();
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(
                     "UPDATE users SET first_name=?, last_name=?, department=?, major=?, email=?, imageURL=? WHERE id=?")) {

            preparedStatement.setString(1, p.getFirstName());
            preparedStatement.setString(2, p.getLastName());
            preparedStatement.setString(3, p.getDepartment());
            preparedStatement.setString(4, p.getMajor());
            preparedStatement.setString(5, p.getEmail());
            preparedStatement.setString(6, p.getImageURL());
            preparedStatement.setInt(7, id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRecord(Person person) {
        connectToDatabase();
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM users WHERE id=?")) {

            preparedStatement.setInt(1, person.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int retrieveId(Person p) {
        connectToDatabase();
        int id = -1;
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT id FROM users WHERE email=?")) {

            preparedStatement.setString(1, p.getEmail());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("User ID: " + id);
        return id;
    }
}
