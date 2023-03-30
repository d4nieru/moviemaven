package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class LoginSection extends JPanel {

    private static final long serialVersionUID = 1L;
    private Main main;

    public LoginSection(Main main) {
        super();
        this.main = main;

        JButton loginButton = new JButton("Se connecter");

        JLabel email_label = new JLabel("Email : ");
        JTextField email = new JTextField();
        JLabel pass_label = new JLabel("Mot de Passe : ");
        JPasswordField password = new JPasswordField();
        password.setEchoChar('●');


        email.setPreferredSize(new Dimension(120,20));
        password.setPreferredSize(new Dimension(120,20));
        loginButton.setBounds(100,140,100,40);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String mail = email.getText();
                String pass = password.getText();
                if(e.getSource() == loginButton) {
                    if(mail.isEmpty() || pass.isEmpty()) {
                        MissingData();
                    } else {

                        Connection conn = null;
                        PreparedStatement stmt = null;

                        try {
                            conn = DriverManager.getConnection("jdbc:mysql://localhost/moviemaven?user=root&password=");
                            //stmt = conn.createStatement();

                            String request = "SELECT password FROM users WHERE email = ?";
                            stmt = conn.prepareStatement(request);
                            stmt.setString(1, mail);
                            ResultSet rs = stmt.executeQuery();

                            if (rs.next()) {
                                String hash = rs.getString("password");
                                String providedHash = hashPassword(pass); // function to hash the provided password
                                if (hash.equals(providedHash)) {
                                    SuccessfulConnection();
                                } else {
                                    InvalidCredentials();
                                }
                            } else {
                                InvalidCredentials();
                            }

                        } catch (SQLException ex) {
                            System.out.println("SQLException: " + ex.getMessage());
                            System.out.println("SQLState: " + ex.getSQLState());
                            System.out.println("VendorError: " + ex.getErrorCode());
                            FailedDBConnection();

                        } finally {
                            if (stmt != null) {
                                try {
                                    stmt.close();
                                } catch (SQLException ex) {
                                    System.out.println("SQLException: " + ex.getMessage());
                                    System.out.println("SQLState: " + ex.getSQLState());
                                    System.out.println("VendorError: " + ex.getErrorCode());
                                    FailedDBConnection();
                                }
                            }

                            if (conn != null) {
                                try {
                                    conn.close();
                                } catch (SQLException ex) {
                                    System.out.println("SQLException: " + ex.getMessage());
                                    System.out.println("SQLState: " + ex.getSQLState());
                                    System.out.println("VendorError: " + ex.getErrorCode());
                                    FailedDBConnection();
                                }
                            }
                        }
                    }
                }
            }
        });

        //JButton secondButton = new testbutton("bitch");

        this.add(email_label);
        this.add(email);
        this.add(pass_label);
        this.add(password);
        this.add(loginButton);

        //this.add(secondButton);
    }

    private void FailedDBConnection() {
        JOptionPane.showMessageDialog(null, "La connection vers la base de données a échoué :/", "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public String hashPassword(String password) {
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

    private void MissingData() {
        JOptionPane.showMessageDialog(null,"Vous n'avez pas rempli tous les champs !","Erreur",2);
        //System.out.print("Vous n'avez pas rempli tous les champs !");
    }

    private void SuccessfulConnection() {
        JOptionPane.showMessageDialog(null,"Vous avez été authentifié avec succès !","Information",1);
        main.switchPanel("main");

    }

    private void InvalidCredentials() {
        JOptionPane.showMessageDialog(null,"Votre identifiant / mot de passe est incorrect !","Information",2);
    }
}
