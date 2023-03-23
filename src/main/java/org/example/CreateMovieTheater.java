package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class CreateMovieTheater extends JPanel {

    private Main main;

    public CreateMovieTheater(Main main) {
        super();
        this.main = main;

        JLabel movie_theater_name_label = new JLabel("Nom de la salle de cinéma : ");
        JTextField movie_theater = new JTextField();
        movie_theater.setPreferredSize(new Dimension(120,20));

        JButton createMovieTheaterButton = new JButton("Créer la salle de cinéma");

        this.add(movie_theater_name_label);
        this.add(movie_theater);
        this.add(createMovieTheaterButton);

        createMovieTheaterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == createMovieTheaterButton) {
                    String movie_theater_name = movie_theater.getText();

                    if (movie_theater_name.isEmpty()) {
                        MissingData();
                    } else {

                        try {
                            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/moviemaven?user=root&password=");
                            String insertQuery = "INSERT INTO movie_theaters (movie_theater_name, created_at) VALUES (?, ?)";
                            PreparedStatement statement = conn.prepareStatement(insertQuery);
                            Date date = new Date();
                            statement.setString(1, movie_theater_name); // Remplacez valeur1, valeur2, valeur3 par les valeurs que vous voulez insérer dans vos colonnes
                            statement.setDate(2, new java.sql.Date(date.getTime()));
                            statement.executeUpdate();
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(null, "Votre salle de cinéma a été crée avec succès !", "Information", 1);
                        main.switchPanel("main");
                    }
                }
            }

            private void MissingData() {
                JOptionPane.showMessageDialog(null,"Vous n'avez pas rempli le champ !","Erreur",2);
                //System.out.print("Vous n'avez pas rempli tous les champs !");
            }
        });
    }
}
