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

        JButton movieSearchButton = new JButton("Ajouter un film dans le catalogue");

        JButton seeMovieCatalogButton = new JButton("Voir le catalogue de films");

        JButton bookSessionButton = new JButton("Réserver une séance");


        this.add(button);
        this.add(createMovieTheaterButton);
        this.add(refreshTableButton);
        this.add(editMovieTheaterButton);
        this.add(deleteMovieTheaterButton);
        this.add(movieSearchButton);
        this.add(seeMovieCatalogButton);
        this.add(bookSessionButton);

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

            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) { // Vérifiez si le double clic est détecté
                        JTable target = (JTable) e.getSource();
                        int row = target.getSelectedRow(); // Récupérez la ligne sélectionnée
                        Object value = target.getValueAt(row, 0);
                        if (value == null) {
                            return; // Si la valeur est nulle, sortez de la méthode sans rien faire
                        }
                        int movieTheaterId = (int) target.getValueAt(row, 0); // Récupérez l'ID de la salle de cinéma sélectionnée
                        main.switchPanelId("seemoviesessions", movieTheaterId); // Rediriger vers le panel de sessions qui sont reliés à la salle de cinéma
                    }
                }
            });

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
                     if (tableModel.getValueAt(selectedRow, 0) != null) {
                         // Récupérer l'ID de la salle de cinéma sélectionnée
                         int movieTheaterId = (int) tableModel.getValueAt(selectedRow, 0);

                         // Rediriger vers le panel de modification de la salle de cinéma
                         main.switchPanelId("editmovietheater", movieTheaterId);
                     } else {
                         JOptionPane.showMessageDialog(MainSection.this, "Veuillez sélectionner une salle de cinéma à modifier.");
                         return;
                     }
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

                    if (tableModel.getValueAt(selectedRow, 0) != null) {

                        int dialogResult = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment effectuer cette action ?", "Confirmation", JOptionPane.YES_NO_OPTION);

                        if (dialogResult == JOptionPane.YES_OPTION) {
                            // Récupérer l'ID de la salle de cinéma sélectionnée
                            int movieTheaterId = (int) tableModel.getValueAt(selectedRow, 0);

                            // Supprimer la salle de cinéma de la base de données
                            try {
                                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/moviemaven?user=root&password=");
                                Statement stmt = conn.createStatement();
                                stmt.executeUpdate("DELETE FROM movie_theaters WHERE id = " + movieTheaterId);
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        } else {
                            return;
                        }

                        // Supprimer la ligne correspondante dans le tableau
                        tableModel.removeRow(selectedRow);
                    } else {
                        JOptionPane.showMessageDialog(MainSection.this, "Veuillez sélectionner une salle de cinéma à supprimer.");
                        return;
                    }
                }
            }
        });

        movieSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.switchPanel("moviesearch");
            }
        });

        seeMovieCatalogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.switchPanel("moviecatalog");
            }
        });

        bookSessionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.switchPanel("booksession");
            }
        });
    }
}
