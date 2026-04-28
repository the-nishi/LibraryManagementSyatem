package iublibrary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class DB {
    private static volatile DB db;
    private final Connection connection;
    private ObservableList<LibraryMember> libraryMembers;
    private int initialSize = 0;

    private DB() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:db/library.db");
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(60000); // wait for 60 seconds before committing updates
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                    commitUpdates();
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static DB getInstance() {
        synchronized (DB.class) {
            if (db == null) {
                db = new DB();
            }
            return db;
        }
    }

    public ObservableList<LibraryMember> getLibraryMembers() {
        synchronized (DB.class) {
            if (libraryMembers == null) {
                libraryMembers = FXCollections.observableArrayList();
                String query = "SELECT * FROM members;";
                try (Statement statement = connection.createStatement()) {
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        libraryMembers.add(
                                new LibraryMember(
                                        resultSet.getInt("id"),
                                        resultSet.getString("name"),
                                        resultSet.getString("contact_number"),
                                        resultSet.getString("department"),
                                        resultSet.getString("gender"),
                                        LocalDate.parse(resultSet.getString("joining_date"))
                                )
                        );
                    }
                    initialSize = libraryMembers.size();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return libraryMembers;
        }
    }

    private void commitUpdates() {
        synchronized (DB.class) {
            if ((initialSize = libraryMembers.size() - initialSize) > 0) {
                String query = "INSERT INTO members (name, contact_number, department, gender, joining_date) VALUES (?, ?, ?, ?, ?);";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    libraryMembers.stream().skip(initialSize).forEach(libraryMember -> {
                        try {
                            preparedStatement.setString(1, libraryMember.getName());
                            preparedStatement.setString(2, libraryMember.getContactNumber());
                            preparedStatement.setString(3, libraryMember.getDepartment());
                            preparedStatement.setString(4, libraryMember.getGender());
                            preparedStatement.setString(5, libraryMember.getJoiningDate().toString());
                            preparedStatement.execute();
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }
                    });
                    initialSize = libraryMembers.size();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public void closeConnection() {
        commitUpdates();
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}