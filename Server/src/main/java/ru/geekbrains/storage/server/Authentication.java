package ru.geekbrains.storage.server;


import ru.geekbrains.storage.AuthRequest;
import ru.geekbrains.storage.RegRequest;

import java.sql.*;

import static ru.geekbrains.storage.server.Server.getLogger;

public class Authentication {
    private Connection connection;
    private Statement stmt;

    Authentication() {
        try {
            connectDB();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void connectDB() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:users.db");
        stmt = connection.createStatement();
        getLogger().info("Database connected");
    }

    public void disconnectDB() {
        try {
            if (!stmt.isClosed()) {
                stmt.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
            Server.getLogger().info("Database disconnected");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean registration(RegRequest regData) {

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT login, password FROM users_data WHERE login = ? AND password = ?;");
            ps.setString(1, regData.getLogin());
            ps.setString(2, regData.getPassword());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return false;
            }
            PreparedStatement ps2 = connection.prepareStatement("INSERT INTO users_data (login, password, path)  VALUES (?,?,?);");
            ps2.setString(1, regData.getLogin());
            ps2.setString(2, regData.getPassword());
            ps2.setString(3,String.format("C:\\Users\\Stray\\Desktop\\Cloud-storage\\%s", regData.getLogin()));
            ps2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
    public boolean login(AuthRequest authData) {

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT login, password FROM users_data WHERE login = ? AND password = ?;");
            ps.setString(1, authData.getLogin());
            ps.setString(2, authData.getPassword());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getLogin(AuthRequest authRequest) {
        return authRequest.getLogin();
    }

    public String getPath(AuthRequest authRequest) throws SQLException {
        ResultSet rs = null;
        try {
            PreparedStatement ps3 = connection.prepareStatement("SELECT path FROM users_data WHERE login = ? AND password = ?;");
            ps3.setString(1, authRequest.getLogin());
            ps3.setString(2, authRequest.getPassword());
            rs = ps3.executeQuery();
            if (rs.next()) {
                rs.getString("path");
        }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return rs.getString("path");
    }


}