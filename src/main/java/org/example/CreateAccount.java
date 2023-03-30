package org.example;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class CreateAccount {

    private String name;
    private String lastname;
    private String email;
    private String password;



    public CreateAccount(String name, String lastname, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void save() throws SQLException, NoSuchAlgorithmException {
        // Hash the password
        String hashedPassword = hashPassword(password);

        // Insert the user into the database
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/moviemaven?user=root&password=");

            // Check if the email is already taken
            String checkQuery = "SELECT count(*) FROM users WHERE email = ?";
            stmt = conn.prepareStatement(checkQuery);
            stmt.setString(1, email);
            rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count > 0) {
                throw new SQLException("Email address already taken");
            }

            // Insert the new user
            String insertQuery = "INSERT INTO users (name, lastname, email, password) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(insertQuery);
            stmt.setString(1, name);
            stmt.setString(2, lastname);
            stmt.setString(3, email);
            stmt.setString(4, hashedPassword);
            stmt.executeUpdate();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        byte[] bytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String name = "Daniel";
        String lastname = "F";
        String email = "a@a.com";
        String password = "123";

        CreateAccount account = new CreateAccount(name, lastname, email, password);
        try {
            account.save();
            System.out.println("Le compte utilisateur a été créé avec succès!");
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
