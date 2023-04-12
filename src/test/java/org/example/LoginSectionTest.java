package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoginSectionTest {

    private Connection conn;

    @Before
    public void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://localhost/moviemaven?user=root&password=");
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name, lastname, email, password) VALUES (?, ?, ?, ?)");
        stmt.setString(1, "D");
        stmt.setString(2, "F");
        stmt.setString(3, "test@example.com");
        stmt.setString(4, hashPassword("testpassword"));
        stmt.executeUpdate();
        stmt.close();
    }

    @After
    public void tearDown() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE email = ?");
        stmt.setString(1, "test@example.com");
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    @Test
    public void testSuccessfulConnection() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT password FROM users WHERE email = ?");
        stmt.setString(1, "test@example.com");
        ResultSet rs = stmt.executeQuery();
        rs.next();
        String hash = rs.getString("password");
        String providedHash = hashPassword("testpassword");
        assertEquals(hash, providedHash);
        rs.close();
        stmt.close();
    }

    @Test
    public void testInvalidCredentials() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT password FROM users WHERE email = ?");
        stmt.setString(1, "test@example.com");
        ResultSet rs = stmt.executeQuery();
        rs.next();
        String hash = rs.getString("password");
        String providedHash = hashPassword("wrongpassword");
        assertEquals(false, hash.equals(providedHash));
        rs.close();
        stmt.close();
    }


    private String hashPassword(String password) {
        // implémentation de la fonction hashPassword à partir de LoginSection
        String hashedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            hashedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPassword;
    }
}