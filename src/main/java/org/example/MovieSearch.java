package org.example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieSearch extends JPanel implements ActionListener {
    private Main main;

    private static final long serialVersionUID = 1L;
    private JTextField searchField;

    private JButton searchButton;
    private JComboBox<Movie> resultsBox;
    private DefaultComboBoxModel<Movie> resultsModel;
    private JLabel imageLabel;
    private JLabel titleLabel;
    private JLabel yearLabel;

    private JLabel descriptionLabel;
    private JLabel genreLabel;
    private JLabel durationLabel;
    private JLabel actorsLabel;

    private JPanel panel;

    public MovieSearch(Main main) {
        super(new BorderLayout());
        this.main = main;

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel searchPanel = new JPanel(new FlowLayout());



        searchField = new JTextField(20);
        searchField.addActionListener(this);
        searchPanel.add(searchField);

        resultsModel = new DefaultComboBoxModel<>();
        resultsBox = new JComboBox<>(resultsModel);
        resultsBox.addActionListener(this);
        searchPanel.add(resultsBox);

        add(searchPanel, BorderLayout.NORTH);

        JPanel moviePanel = new JPanel(new FlowLayout());

        imageLabel = new JLabel();
        moviePanel.add(imageLabel);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel();
        textPanel.add(titleLabel);

        yearLabel = new JLabel();
        textPanel.add(yearLabel);

        descriptionLabel = new JLabel();
        textPanel.add(descriptionLabel);

        genreLabel = new JLabel();
        textPanel.add(genreLabel);

        durationLabel = new JLabel();
        textPanel.add(durationLabel);

        actorsLabel = new JLabel();
        textPanel.add(actorsLabel);

        moviePanel.add(textPanel);

        add(moviePanel, BorderLayout.CENTER);

        JButton goBackButton = new JButton("Revenir au menu principal");
        goBackButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                main.switchPanel("main");
            }
        });

        JButton addButton = new JButton("Ajouter le film dans le catalogue");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Movie selectedMovie = (Movie) resultsBox.getSelectedItem();
                addMovieToDatabase(selectedMovie);
            }
        });
        moviePanel.add(goBackButton);
        moviePanel.add(addButton);

        searchButton = new JButton("Effectuer la recherche");
        searchButton.addActionListener(this);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchField || e.getSource() == resultsBox || e.getSource() == searchButton) {
            // Vérifier si une recherche a déjà été effectuée
            if (resultsModel.getSize() == 0) {
                JOptionPane.showMessageDialog(this, "Veuillez effectuer une recherche pour afficher les résultats.", "Aucun résultat", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String searchTerm = searchField.getText();
            resultsModel.removeAllElements();
            if (!searchTerm.trim().isEmpty()) {
                String OMDb_API_LINK = "https://www.omdbapi.com/?apikey=";
                String API_KEY = "ce43c39f";
                String requestURL = OMDb_API_LINK + API_KEY + "&s=" + URLEncoder.encode(searchTerm.trim());
                try {
                    JSONObject response = readJsonFromUrl(requestURL);
                    if (response.getBoolean("Response")) {
                        JSONArray results = response.getJSONArray("Search");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject movieJson = results.getJSONObject(i);
                            System.out.println(results);
                            String title = movieJson.getString("Title");
                            String year = movieJson.getString("Year");
                            String imdbID = movieJson.getString("imdbID");
                            String posterUrl = movieJson.getString("Poster");

                            // Get additional information from the OMDb API
                            String detailsURL = OMDb_API_LINK + API_KEY + "&i=" + imdbID;
                            JSONObject detailsResponse = readJsonFromUrl(detailsURL);

                            Movie movie = new Movie(title, year, imdbID, posterUrl/*, description*/);
                            resultsModel.addElement(movie);
                        }
                    }
                } catch (IOException | JSONException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (e.getSource() == resultsBox) {
            Movie selectedMovie = (Movie) resultsBox.getSelectedItem();
            titleLabel.setText(selectedMovie.getTitle());
            yearLabel.setText(selectedMovie.getYear());
            try {
                URL imageUrl = new URL(selectedMovie.getPosterUrl());
                Image image = ImageIO.read(imageUrl);
                imageLabel.setIcon(new ImageIcon(image));
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    private void addMovieToDatabase(Movie movie) {

        if (resultsModel.getSize() == 0) {
            JOptionPane.showMessageDialog(this, "Veuillez effectuer une recherche et sélectionner un film avant de l'ajouter à la base de données.", "Aucun film sélectionné", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = movie.getTitle();
        String release_date = movie.getYear();
        String imdb_id = movie.getImdbID();

        // Insertion des données dans la base de données
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviemaven", "root", "");
            Statement stmt = conn.createStatement();
            String query = String.format("INSERT INTO movies (movie_name, movie_release_date, imdb_id) VALUES ('%s', '%s', '%s')",
                    name, release_date, imdb_id);
            stmt.executeUpdate(query);
            stmt.close();
            conn.close();
            JOptionPane.showMessageDialog(null,"Le film a été ajouté avec succès dans le catalogue !","Information",1);
            main.switchPanel("main");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"Une erreur est survenue lors de l'ajout du film dans le catalogue !","Erreur",2);
        }
    }


    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        // Open the URL and create a BufferedReader to read its contents
        URL apiURL = new URL(url);
        BufferedReader reader = new BufferedReader(new InputStreamReader(apiURL.openStream()));

        // Read the response into a StringBuilder
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }

        // Close the reader
        reader.close();

        // Convert the response to a JSON object and return it
        String responseString = responseBuilder.toString();
        JSONObject response = new JSONObject(responseString);
        return response;
    }
}
