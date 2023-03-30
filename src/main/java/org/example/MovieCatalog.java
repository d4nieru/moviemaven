package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import org.example.Movie;

public class MovieCatalog extends JPanel implements ActionListener {

    private Main main;
    private JButton[] deleteButtons;
    private JPanel panel;

    public MovieCatalog(Main main) {
        super();
        this.main = main;
        setLayout(new BorderLayout());

        // Création d'un JPanel pour le bouton "Revenir au menu principal"
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton goBackButton = new JButton("Revenir au menu principal");
        goBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.switchPanel("main");
            }
        });
        buttonPanel.add(goBackButton);
        add(buttonPanel, BorderLayout.NORTH);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        try {
            // Connexion à la base de données
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviemaven", "root", "");

            // Exécution de la requête
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM movies");

            // Parcours des résultats
            int count = 0;
            while (rs.next()) {
                // Récupération du nom du film
                String name = rs.getString("movie_name");

                // Création d'un nouveau JPanel pour chaque film
                JPanel moviePanel = new JPanel();
                moviePanel.setLayout(new BoxLayout(moviePanel, BoxLayout.LINE_AXIS));
                JLabel nameLabel = new JLabel(name);
                moviePanel.add(nameLabel);

                // Ajout du bouton "Supprimer"
                JButton deleteButton = new JButton("Supprimer");
                deleteButton.setActionCommand(String.valueOf(count));
                deleteButton.addActionListener(this);
                moviePanel.add(deleteButton);
                count++;

                // Ajout du JPanel du film au panel principal
                panel.add(moviePanel);
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        add(scrollPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        // Récupération de l'indice du bouton "Supprimer" cliqué
        int index = Integer.parseInt(e.getActionCommand());

        try {
            // Connexion à la base de données
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/movies", "root", "");

            // Suppression du film correspondant à l'indice
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM movies WHERE id = ?");
            stmt.setInt(1, index + 1);
            stmt.executeUpdate();

            // Fermeture de la connexion
            conn.close();

            // Suppression du JPanel correspondant au film
            panel.remove(index);
            panel.revalidate();
            panel.repaint();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
