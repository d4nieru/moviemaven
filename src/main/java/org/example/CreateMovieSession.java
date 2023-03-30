package org.example;

import org.jdatepicker.JDatePicker;

import javax.swing.*;
import org.jdatepicker.JDateComponentFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        JTextField movie_name = new JTextField();
        JLabel movie_description_label = new JLabel("Description du film : ");
        JTextField movie_description = new JTextField();
        JLabel movie_genre_label = new JLabel("Genre du film : ");
        JTextField movie_genre = new JTextField();
        JLabel movie_duration_label = new JLabel("Durée du film : ");
        JTextField movie_duration = new JTextField();
        JLabel movie_actors_label = new JLabel("Acteurs : ");
        JTextField movie_actors = new JTextField();

        JButton createMovieSessionButton = new JButton("Créer la session");

        movie_name.setPreferredSize(new Dimension(120,20));
        movie_description.setPreferredSize(new Dimension(120,90));
        movie_genre.setPreferredSize(new Dimension(120,20));
        movie_duration.setPreferredSize(new Dimension(120,20));
        movie_actors.setPreferredSize(new Dimension(120,20));
        createMovieSessionButton.setBounds(100,140,100,40);

        JDateComponentFactory dateComponentFactory = new JDateComponentFactory();
        datePicker = dateComponentFactory.createJDatePicker();

        this.add(movie_name_label);
        this.add(movie_name);
        this.add(movie_description_label);
        this.add(movie_description);
        this.add(movie_genre_label);
        this.add(movie_genre);
        this.add(movie_duration_label);
        this.add(movie_duration);
        this.add(movie_actors_label);
        this.add(movie_actors);
        this.add(new JLabel("Date de la séance : "));
        this.add((JComponent) datePicker);
        this.add(createMovieSessionButton);

        createMovieSessionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() == createMovieSessionButton) {
                    System.out.print(datePicker.getModel().getValue());
                }
            }
        });
    }
}
