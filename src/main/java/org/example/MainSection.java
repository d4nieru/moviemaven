package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class MainSection extends JPanel {

    private static final long serialVersionUID = 1L;
    private Main main;

    private JTable table;
    private DefaultTableModel tableModel;
    public MainSection(Main main) {
        super();
        this.main = main;

        JButton button = new JButton("Créer une séance de cinéma");

        JButton createMovieTheaterButton = new JButton("Créer une salle de cinéma");

        JButton editMovieTheaterButton = new JButton("Modifier la salle de cinéma sélectionné");

        JButton deleteMovieTheaterButton = new JButton("Supprimer la salle de cinéma");

        JButton refreshTableButton = new JButton("Actualiser la liste des salles de cinéma");

        this.add(button);
        this.add(createMovieTheaterButton);
        this.add(refreshTableButton);
        this.add(editMovieTheaterButton);
        this.add(deleteMovieTheaterButton);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/moviemaven?user=root&password=");

            // Créer une requête pour récupérer les données de la table
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM movie_theaters");

            // Créer un tableau de données à partir des résultats de la requête
            Object[][] data = new Object[100][4];
            int i = 0;
            while (rs.next()) {
                data[i][0] = rs.getInt("id");
                data[i][1] = rs.getString("movie_theater_name");
                data[i][2] = rs.getString("created_at");
                i++;
            }

            // Créer un modèle de tableau à partir des données
            String[] columnNames = {"ID", "Nom de la salle de cinéma", "Date de Création"};
            tableModel = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            // Créer une table à partir du modèle
            table = new JTable(tableModel);

            // Ajouter la table à l'interface graphique
            JScrollPane scrollPane = new JScrollPane(table);
            this.add(scrollPane);

        } catch (Exception e) {
            e.printStackTrace();
        }

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.switchPanel("createmoviesession");
            }
        });

        createMovieTheaterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.switchPanel("createmovietheater");
            }
        });

        refreshTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/moviemaven?user=root&password=");

                    // Créer une requête pour récupérer les données de la table
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM movie_theaters");

                    // Créer un tableau de données à partir des résultats de la requête
                    Object[][] data = new Object[100][4];
                    int i = 0;
                    while (rs.next()) {
                        data[i][0] = rs.getInt("id");
                        data[i][1] = rs.getString("movie_theater_name");
                        data[i][2] = rs.getString("created_at");
                        i++;
                    }

                    // Mettre à jour le modèle de tableau avec les nouvelles données
                    String[] columnNames = {"ID", "Nom de la salle de cinéma", "Date de Création"};
                    tableModel.setDataVector(data, columnNames);

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        // Définir un ActionListener pour le bouton de redirection
        editMovieTheaterButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 // Vérifier si une salle de cinéma a été sélectionnée
                 int selectedRow = table.getSelectedRow();
                 if (selectedRow == -1) {
                     JOptionPane.showMessageDialog(MainSection.this, "Veuillez sélectionner une salle de cinéma à modifier.");
                     return;
                 } else {
                     // Récupérer l'ID de la salle de cinéma sélectionnée
                     int movieTheaterId = (int) tableModel.getValueAt(selectedRow, 0);

                     // Rediriger vers le panel de modification de la salle de cinéma
                     main.switchPanelId("editmovietheater", movieTheaterId);
                 }
             }
        });

        // Définir un ActionListener pour le bouton de suppression
        deleteMovieTheaterButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               // Vérifier si une salle de cinéma a été sélectionnée
               int selectedRow = table.getSelectedRow();
               if (selectedRow == -1) {
                   JOptionPane.showMessageDialog(MainSection.this, "Veuillez sélectionner une salle de cinéma à supprimer.");
                   return;
               } else {
                   // Récupérer l'ID de la salle de cinéma sélectionnée
                   int movieTheaterId = (int) tableModel.getValueAt(selectedRow, 0);

                   // Demander confirmation à l'utilisateur
                   int confirmation = JOptionPane.showConfirmDialog(MainSection.this, "Êtes-vous sûr de vouloir supprimer cette salle de cinéma ?");

                   if (confirmation == JOptionPane.YES_OPTION) {
                       // Supprimer la salle de cinéma de la base de données
                       try {
                           Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/moviemaven?user=root&password=");
                           PreparedStatement stmt = conn.prepareStatement("DELETE FROM movie_theaters WHERE id = ?");
                           stmt.setInt(1, movieTheaterId);
                           stmt.executeUpdate();

                           // Actualiser la table
                           refreshTableButton.doClick();

                       } catch (SQLException exception) {
                           exception.printStackTrace();
                       }
                   }
               }
           }
        });
    }
}
