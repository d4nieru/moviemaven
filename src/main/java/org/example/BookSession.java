package org.example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.*;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.BarcodeQRCode;
import java.util.Date;


public class BookSession extends JPanel {

    private static final long serialVersionUID = 1L;
    private Main main;
    private JComboBox<String> sessionList;
    private ArrayList<String> sessionData;

    public BookSession(Main main) {
        super();
        this.main = main;
        sessionList = new JComboBox<String>();
        sessionData = new ArrayList<String>();

        JButton backButton = new JButton("Retour au menu principal");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                main.switchPanel("main");
            }
        });
        this.add(backButton);

        // Récupération des données de la base de données
        getData();

        // Ajout des séances dans le menu déroulant
        for (String session : sessionData) {
            sessionList.addItem(session);
        }

        // Ajout du menu déroulant à ce JPanel
        this.add(sessionList);

        JLabel session_price_label = new JLabel("Prix de la séance : ");
        JTextField session_price = new JTextField();
        session_price.setPreferredSize(new Dimension(120,20));
        JLabel currency_label = new JLabel("€");

        this.add(session_price_label);
        this.add(session_price);
        this.add(currency_label);

        // Ajout du bouton "Générer PDF"
        JButton pdfButton = new JButton("Générer le billet de réservation en PDF");
        pdfButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Génère un PDF avec les informations sélectionnées
                if(e.getSource() == pdfButton) {
                    String price = session_price.getText();
                    if (!price.isEmpty()) {
                        generatePDF(price);
                    } else {
                        JOptionPane.showMessageDialog(null, "Veuillez saisir le prix de la séance !");
                    }
                }
            }
        });
        this.add(pdfButton);
    }

    private void getData() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviemaven", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT movie_name, movie_release_date, session_date, session_time, movie_theater_name FROM movie_theaters_movies INNER JOIN movies ON movie_theaters_movies.movie_id = movies.id INNER JOIN movie_theaters ON movie_theaters_movies.movie_theater_id = movie_theaters.id");
            //ResultSet rs = stmt.executeQuery("SELECT movie_name, movie_release_date, session_date, session_time FROM movie_theaters_movies INNER JOIN movies ON movie_theaters_movies.movie_id = movies.id");
            while (rs.next()) {
                String movieName = rs.getString("movie_name");
                String releaseDate = rs.getString("movie_release_date");
                String sessionDate = rs.getString("session_date");
                String sessionTime = rs.getString("session_time");
                String theaterName = rs.getString("movie_theater_name");
                String session = movieName + " - " + releaseDate + " - " + sessionDate + " - " + sessionTime + " - " + theaterName;
                sessionData.add(session);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generatePDF(String price) {
        try {
            // Récupération de la séance sélectionnée
            String session = (String) sessionList.getSelectedItem();
            String[] sessionInfo = session.split(" - ");
            String movieName = sessionInfo[0];
            String releaseDate = sessionInfo[1];
            String sessionDate = sessionInfo[2];
            String sessionTime = sessionInfo[3];
            String theaterName = sessionInfo[4];

            // Création du document PDF avec un nom de fichier unique basé sur la date et l'heure actuelles
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fileName = "e-billet_" + sdf.format(new Date()) + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Ajout des informations sur la séance au document
            Paragraph title = new Paragraph("Informations sur la séance", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Film : " + movieName));
            document.add(new Paragraph("Date de sortie : " + releaseDate));
            document.add(new Paragraph("Date de la séance : " + sessionDate));
            document.add(new Paragraph("Heure de la séance : " + sessionTime));
            document.add(new Paragraph("Salle : " + theaterName));
            document.add(new Paragraph("Prix : " + price + "€"));

            // Ajout du QR code en bas des informations
            BarcodeQRCode qrCode = new BarcodeQRCode("https://github.com/d4nieru/moviemaven", 300, 300, null);
            Image qrCodeImage = qrCode.getImage();
            //qrCodeImage.setAbsolutePosition(220, 220);
            qrCodeImage.setAbsolutePosition((document.getPageSize().getWidth() - qrCodeImage.getWidth()) / 2, 50);
            document.add(qrCodeImage);

            document.close();

            // Ouverture du document PDF généré
            Desktop.getDesktop().open(new File(fileName));
            //JOptionPane.showMessageDialog(null, "Le fichier PDF a été généré avec succès !");

        } catch (Exception e) {

            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Une erreur est survenue lors de la génération du fichier PDF : " + e.getMessage());
        }
    }
}