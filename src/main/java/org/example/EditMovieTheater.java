package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditMovieTheater extends JPanel {
    private Main main;
    private int movieTheaterId;

    public EditMovieTheater(Main main, int movieTheaterId) {
        super();
        this.main = main;
        this.movieTheaterId = movieTheaterId;

        JLabel movie_theater_label = new JLabel("Nouveau nom de salle de cinéma : ");
        JTextField movie_theater_name = new JTextField();

        JButton save_movie_theater = new JButton("Enregistrer le changement");

        movie_theater_name.setPreferredSize(new Dimension(120,20));
        //save_movie_theater.setBounds(100,140,100,40);

        this.add(movie_theater_label);
        this.add(movie_theater_name);
        this.add(save_movie_theater);

        save_movie_theater.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() == save_movie_theater) {
                    String new_movie_theater_name = movie_theater_name.getText();
                    if (new_movie_theater_name.isEmpty()) {
                        MissingData();
                    } else {
                        // récupérer les données de la salle de cinéma à partir de la base de données
                        Connection conn = null;
                        Statement stmt = null;

                        try {
                            conn = DriverManager.getConnection("jdbc:mysql://localhost/moviemaven?user=root&password=");
                            String request = "UPDATE movie_theaters SET movie_theater_name = ? WHERE id = ?";
                            PreparedStatement preparedStatement = conn.prepareStatement(request);
                            preparedStatement.setString(1, new_movie_theater_name);
                            preparedStatement.setInt(2, movieTheaterId);
                            preparedStatement.executeUpdate();
                            SuccessfulEdit();

                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            }
            private void MissingData() {
                JOptionPane.showMessageDialog(null,"Vous n'avez pas rempli le champ !","Erreur",2);
                //System.out.print("Vous n'avez pas rempli tous les champs !");
            }

            private void SuccessfulEdit() {
                JOptionPane.showMessageDialog(null,"Vous avez modifié avec succès la salle de cinéma !","Information",1);
                main.switchPanel("main");
                main.repaint();
                main.revalidate();
            }

        });
    }
}
