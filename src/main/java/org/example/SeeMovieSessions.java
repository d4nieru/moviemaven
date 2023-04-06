package org.example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SeeMovieSessions extends JPanel {

    private Main main;
    private int movieTheaterId;

    private JLabel titleLabel;
    private JTable movieTable;

    private JButton backButton;
    private JButton deleteButton;

    public SeeMovieSessions(Main main, int movieTheaterId) {
        super();
        this.main = main;
        this.movieTheaterId = movieTheaterId;


        setLayout(new BorderLayout());

        titleLabel = new JLabel("Movies at Theater " + movieTheaterId);
        add(titleLabel, BorderLayout.NORTH);

        // Create table model with columns "Movie Name", "Release Date", "Session Date", "Session Time"
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Movie Name", "Release Date", "Session Date", "Session Time"}, 0) {
            // Override isCellEditable() to disable cell editing
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Connect to database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviemaven", "root", "")) {

            // Query database for movies at specified theater
            String query = "SELECT movies.movie_name, movies.movie_release_date, movie_theaters_movies.session_date, movie_theaters_movies.session_time "
                    + "FROM movies "
                    + "JOIN movie_theaters_movies ON movies.id = movie_theaters_movies.movie_id "
                    + "WHERE movie_theaters_movies.movie_theater_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, movieTheaterId);
            ResultSet resultSet = statement.executeQuery();

            // Add data to table model
            while (resultSet.next()) {
                String movieName = resultSet.getString("movie_name");
                String releaseDate = resultSet.getString("movie_release_date");
                String sessionDate = resultSet.getString("session_date");
                String sessionTime = resultSet.getString("session_time");
                tableModel.addRow(new Object[]{movieName, releaseDate, sessionDate, sessionTime});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Create movie table with table model
        movieTable = new JTable(tableModel);

        // Add movie table to scroll pane and add scroll pane to panel
        JScrollPane scrollPane = new JScrollPane(movieTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create button panel with back button and delete button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        backButton = new JButton("Revenir au menu principal");
        deleteButton = new JButton("Supprimer la séance");
        buttonPanel.add(backButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.NORTH);

        // Add action listeners to buttons
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.switchPanel("main");
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = movieTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Veuillez sélectionner la séance à supprimer.");
                    return;
                } else if (selectedRow >= 0) {
                    String movieName = (String) tableModel.getValueAt(selectedRow, 0);
                    String releaseDate = (String) tableModel.getValueAt(selectedRow, 1);
                    String sessionDate = (String) tableModel.getValueAt(selectedRow, 2);
                    String sessionTime = (String) tableModel.getValueAt(selectedRow, 3);
                    int confirm = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer cette séance ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviemaven", "root", "")) {
                            // Delete movie session from database
                            String query = "DELETE FROM movie_theaters_movies "
                                    + "WHERE movie_theater_id = ? AND movie_id = (SELECT id FROM movies WHERE movie_name = ? AND movie_release_date = ?) "
                                    + "AND session_date = ? AND session_time = ?";
                            PreparedStatement statement = connection.prepareStatement(query);
                            statement.setInt(1, movieTheaterId);
                            statement.setString(2, movieName);
                            statement.setString(3, releaseDate);
                            statement.setString(4, sessionDate);
                            statement.setString(5, sessionTime);
                            int rowsAffected = statement.executeUpdate();
                            if (rowsAffected > 0) {
                                // Remove row from table model
                                tableModel.removeRow(selectedRow);
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    return;
                }
            }
        });
    }
}