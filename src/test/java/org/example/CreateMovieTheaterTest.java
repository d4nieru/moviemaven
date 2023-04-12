package org.example;

import org.junit.After;
import org.junit.Test;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CreateMovieTheaterTest {

    @Test
    public void testCreateMovieTheater() throws SQLException {

        // Création d'une instance de la classe Main
        Main main = new Main("MovieMaven TestMode");

        // Création d'une instance de la classe CreateMovieTheater
        CreateMovieTheater createMovieTheater = new CreateMovieTheater(main);

        // Vérification que l'instance a été créée avec succès
        assertNotNull(createMovieTheater);

        // Création d'un nom de salle de cinéma fictif
        String movieTheaterName = "Salle de cinéma XYZ";

        // Remplacement du champ de texte par le nom de la salle de cinéma fictif
        JTextField movieTheaterField = (JTextField) createMovieTheater.getComponent(2);
        movieTheaterField.setText(movieTheaterName);

        // Obtention du bouton "Créer la salle de cinéma"
        JButton createMovieTheaterButton = (JButton) createMovieTheater.getComponent(3);

        // Simulation du clic sur le bouton "Créer la salle de cinéma"
        createMovieTheaterButton.doClick();

        // Vérification que la salle de cinéma a bien été créée dans la base de données
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/moviemaven?user=root&password=");
        String selectQuery = "SELECT * FROM movie_theaters WHERE movie_theater_name = ?";
        PreparedStatement statement = conn.prepareStatement(selectQuery);
        statement.setString(1, movieTheaterName);
        ResultSet resultSet = statement.executeQuery();
        assertEquals(true, resultSet.next());

        // Suppression de la salle de cinéma créée
        String deleteQuery = "DELETE FROM movie_theaters WHERE movie_theater_name = ?";
        PreparedStatement deleteStatement = conn.prepareStatement(deleteQuery);
        deleteStatement.setString(1, movieTheaterName);
        deleteStatement.executeUpdate();

        // Vérification que la salle de cinéma a bien été supprimée de la base de données
        PreparedStatement checkStatement = conn.prepareStatement(selectQuery);
        checkStatement.setString(1, movieTheaterName);
        ResultSet checkResultSet = checkStatement.executeQuery();
        assertFalse(checkResultSet.next());

        // Fermeture de la connexion à la base de données
        conn.close();
    }
}