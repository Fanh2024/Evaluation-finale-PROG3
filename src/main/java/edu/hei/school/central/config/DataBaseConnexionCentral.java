package edu.hei.school.central.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnexionCentral {
    public static Connection connect() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/football_championship_central";
        String user = "postgres";
        String password = "fefefe";

        return DriverManager.getConnection(url, user, password);
    }

    public static Connection getCentralConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/football_championship_central", "postgres", "fefefe");
    }
}
