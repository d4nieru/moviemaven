package org.example;

import org.jdatepicker.JDatePicker;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdatepicker.JDateComponentFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Date;

public class CreateMovieSession extends JPanel {

    private static final long serialVersionUID = 1L;
    private Main main;

    private JDatePicker datePicker;

    private JPanel panel;
    public CreateMovieSession(Main main) {
        super();
        this.main = main;

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

        JLabel movie_name_label = new JLabel("Nom du film : ");
        JComboBox<String> movie_name = new JComboBox<String>();
        // Code pour remplir le menu déroulant avec les noms des films stockés en base de données
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviemaven", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT movie_name FROM movies");
            while (rs.next()) {
                movie_name.addItem(rs.getString("movie_name"));
            }
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        // Code pour ajouter une barre de recherche pour le menu déroulant des noms des films
        JTextField movie_name_search = new JTextField();
        movie_name_search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateList();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateList();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateList();
            }
            private void updateList() {
                String searchString = movie_name_search.getText();
                for (int i = 0; i < movie_name.getItemCount(); i++) {
                    String item = movie_name.getItemAt(i);
                    if (item.toLowerCase().contains(searchString.toLowerCase())) {
                        movie_name.setSelectedIndex(i);
                        return;
                    }
                }
            }
        });

        JLabel movie_theater_label = new JLabel("Salle de cinéma : ");
        JComboBox<String> movie_theater = new JComboBox<String>();
        // Code pour remplir le menu déroulant avec les noms des salles de cinéma stockées en base de données
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviemaven", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT movie_theater_name FROM movie_theaters");
            while (rs.next()) {
                movie_theater.addItem(rs.getString("movie_theater_name"));
            }
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Code pour ajouter une barre de recherche pour le menu déroulant des noms des salles de cinéma
        JTextField movie_theater_search = new JTextField();
        movie_theater_search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateList();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateList();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateList();
            }
            private void updateList() {
                String searchString = movie_theater_search.getText();
                for (int i = 0; i < movie_theater.getItemCount(); i++) {
                    String item = movie_theater.getItemAt(i);
                    if (item.toLowerCase().contains(searchString.toLowerCase())) {
                        movie_theater.setSelectedIndex(i);
                        return;
                    }
                }
            }
        });

        JLabel date_label = new JLabel("Date : ");

        JDateComponentFactory dateComponentFactory = new JDateComponentFactory();
        datePicker = dateComponentFactory.createJDatePicker();

        // Code pour ajouter une liste de cases à cocher pour les heures de la séance
        JLabel time_label = new JLabel("Heure : ");
        JPanel time_panel = new JPanel();
        time_panel.setLayout(new BoxLayout(time_panel, BoxLayout.PAGE_AXIS));
        JCheckBox time_10 = new JCheckBox("10:00");
        JCheckBox time_13 = new JCheckBox("13:00");
        JCheckBox time_16 = new JCheckBox("16:00");
        JCheckBox time_19 = new JCheckBox("19:00");
        JCheckBox time_22 = new JCheckBox("22:00");
        time_panel.add(time_10);
        time_panel.add(time_13);
        time_panel.add(time_16);
        time_panel.add(time_19);
        time_panel.add(time_22);

        // Code pour récupérer les informations sur la session de cinéma et les stocker dans la base de données
        JButton createSessionButton = new JButton("Créer la session");
        createSessionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Récupération des informations sur la session de cinéma
                String movieName = (String) movie_name.getSelectedItem();
                String movieTheaterName = (String) movie_theater.getSelectedItem();
                GregorianCalendar calendar = (GregorianCalendar) datePicker.getModel().getValue();
                Date date = calendar.getTime();
                List<String> selectedTimes = new ArrayList<>();
                if (time_10.isSelected()) {
                    selectedTimes.add("10:00");
                }
                if (time_13.isSelected()) {
                    selectedTimes.add("13:00");
                }
                if (time_16.isSelected()) {
                    selectedTimes.add("16:00");
                }
                if (time_19.isSelected()) {
                    selectedTimes.add("19:00");
                }
                if (time_22.isSelected()) {
                    selectedTimes.add("22:00");
                }

                // Vérification si aucune heure n'est cochée
                if (selectedTimes.size() == 0) {
                    JOptionPane.showMessageDialog(CreateMovieSession.this, "Veuillez sélectionner au moins une heure de séance.", "Avertissement", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Récupération de l'ID du film
                int movieId = -1;
                try {
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviemaven", "root", "");
                    String query = "SELECT id FROM movies WHERE movie_name = ?";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setString(1, movieName);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        movieId = rs.getInt("id");
                    }
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                // Récupération de l'ID de la salle de cinéma
                int movieTheaterId = -1;
                try {
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviemaven", "root", "");
                    String query = "SELECT id FROM movie_theaters WHERE movie_theater_name = ?";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setString(1, movieTheaterName);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        movieTheaterId = rs.getInt("id");
                    }
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                // Insertion de la session de cinéma dans la table intermédiaire
                try {
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviemaven", "root", "");
                    for (String time : selectedTimes) {
                        String query = "INSERT INTO movie_sessions (movie_theater_id, movie_id, session_date, session_time) VALUES (?, ?, ?, ?)";
                        PreparedStatement stmt = con.prepareStatement(query);
                        stmt.setInt(1, movieTheaterId);
                        stmt.setInt(2, movieId);
                        stmt.setDate(3, new java.sql.Date(date.getTime()));
                        stmt.setString(4, time);
                        stmt.executeUpdate();
                    }
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                Successful();
            }
        });

        // Ajout de tous les composants au JPanel
        panel.add(movie_name_label);
        panel.add(movie_name);
        panel.add(movie_name_search);
        panel.add(movie_theater_label);
        panel.add(movie_theater);
        panel.add(movie_theater_search);
        panel.add(date_label);
        panel.add((JComponent) datePicker);
        panel.add(time_label);
        //panel.add(time);
        panel.add(time_panel);
        panel.add(createSessionButton);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void Failed() {
        JOptionPane.showMessageDialog(null, "Oula il y a des soucis avec la base de données :/", "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void Successful() {
        JOptionPane.showMessageDialog(null,"Vous avez crée une session de film avec succès !","Information",1);
        main.switchPanel("main");
    }
}
