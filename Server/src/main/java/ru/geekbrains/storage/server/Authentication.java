package ru.geekbrains.storage.server;

import at.favre.lib.crypto.bcrypt.BCrypt;
import ru.geekbrains.storage.AuthRequest;
import ru.geekbrains.storage.ChangePasswordRequest;
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
            PreparedStatement ps = connection.prepareStatement("SELECT login FROM users_data WHERE login = ?;");
            ps.setString(1, regData.getLogin());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return false;
            }
            PreparedStatement ps2 = connection.prepareStatement("INSERT INTO users_data (login, password)  VALUES (?,?);");
            ps2.setString(1, regData.getLogin());
            ps2.setString(2, BCrypt.withDefaults().hashToString(12, regData.getPassword().toCharArray()));
            ps2.executeUpdate();
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean login(AuthRequest authData) {

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT login, password FROM users_data WHERE login = ?;");
            ps.setString(1, authData.getLogin());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BCrypt.Result result = BCrypt.verifyer().verify(authData.getPassword().toCharArray(), rs.getString(2));
                if(result.verified){
                    return true;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changePassword(ChangePasswordRequest changePasswordRequest){
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT login FROM users_data WHERE login = ?;");
            ps.setString(1, changePasswordRequest.getLogin());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PreparedStatement ps1 = connection.prepareStatement("UPDATE users_data SET password = ? WHERE login = ?;");
                ps1.setString(1, BCrypt.withDefaults().hashToString(12, changePasswordRequest.getPassword().toCharArray()));
                ps1.setString(2, changePasswordRequest.getLogin());
                ps1.executeUpdate();
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getLogin(AuthRequest authRequest) {
        return authRequest.getLogin();
    }

//    public String getPath(AuthRequest authRequest) throws SQLException {
//        ResultSet rs = null;
//        try {
//            PreparedStatement ps3 = connection.prepareStatement("SELECT path FROM users_data WHERE login = ? AND password = ?;");
//            ps3.setString(1, authRequest.getLogin());
//            ps3.setString(2, authRequest.getPassword());
//            rs = ps3.executeQuery();
//            if (rs.next()) {
//                rs.getString("path");
//        }
//        }catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return rs.getString("path");
//    }

}
