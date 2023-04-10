package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {

    private static long serialVersionUID = 1L;

    private CardLayout cardLayout;
    private JPanel contentPane;
    private LoginSection loginPanel;
    private MainSection mainPanel;

    private CreateMovieSession createMovieSessionPanel;

    private CreateMovieTheater createMovieTheaterPanel;

    private MovieSearch movieSearchPanel;

    private MovieCatalog movieCatalogPanel;

    private BookSession bookSessionPanel;

    JMenuBar menuBar;
    JMenu fileMenu, helpMenu;
    JMenuItem newMenuItem, aboutMenuItem;


    public Main(String title) {

        // passer le title à la JFrame
        super(title);
        // permet de libérer de la mémoire
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(800,600);
        this.setLocationRelativeTo(null);

        // Créer la barre de menu
        menuBar = new JMenuBar();

        // Créer le menu "Fichier" avec des éléments de menu "Nouveau"
        fileMenu = new JMenu("Fichier");
        newMenuItem = new JMenuItem("...");

        helpMenu = new JMenu("Aide");
        aboutMenuItem = new JMenuItem("A Propos");

        fileMenu.add(newMenuItem);
        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        // Ajouter les événements à chaque élément de menu
        MenuListener menuListener = new MenuListener(this);
        helpMenu.addActionListener(menuListener);
        aboutMenuItem.addActionListener(menuListener);
        fileMenu.addActionListener(menuListener);
        newMenuItem.addActionListener(menuListener);

        this.setJMenuBar(menuBar);

        // Utilisation d'un CardLayout pour afficher les différents panneaux
        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);
        loginPanel = new LoginSection(this);
        mainPanel = new MainSection(this);
        createMovieSessionPanel = new CreateMovieSession(this);
        createMovieTheaterPanel = new CreateMovieTheater(this);
        movieSearchPanel = new MovieSearch(this);
        movieCatalogPanel = new MovieCatalog(this);
        bookSessionPanel = new BookSession(this);


        // Ajout des panneaux au container
        contentPane.add(loginPanel, "login");
        contentPane.add(mainPanel, "main");
        contentPane.add(createMovieSessionPanel, "createmoviesession");
        contentPane.add(createMovieTheaterPanel, "createmovietheater");
        contentPane.add(movieSearchPanel, "moviesearch");
        contentPane.add(movieCatalogPanel, "moviecatalog");
        contentPane.add(bookSessionPanel, "booksession");

        // Ajout du container à la fenêtre
        setContentPane(contentPane);

        // Affichage du panneau de connexion par défaut
        cardLayout.show(contentPane, "login");

        this.setVisible(true);
    }

    public JMenuItem getNewMenuItem() {
        return newMenuItem;
    }

    public JMenuItem getAboutMenuItem() {
        return aboutMenuItem;
    }

    public void switchPanel(String panelName) {
        // Change le panneau affiché en fonction du nom passé en paramètre
        cardLayout.show(contentPane, panelName);
        contentPane.revalidate();
        contentPane.repaint();
    }

    public void switchPanelId(String panelName, int movieTheaterId) {
        // vérifier si le nom du panneau est "editmovietheater"
        if (panelName.equals("editmovietheater")) {
            // créer un nouveau panneau de modification de la salle de cinéma avec l'ID sélectionné
            EditMovieTheater editMovieTheaterPanel = new EditMovieTheater(this, movieTheaterId);
            // ajouter le panneau à l'interface graphique

            contentPane.add(editMovieTheaterPanel, panelName);
            cardLayout.show(contentPane, panelName);
            contentPane.revalidate();
            contentPane.repaint();

        } else if(panelName.equals("seemoviesessions")) {
            // créer un nouveau panneau de modification de la salle de cinéma avec l'ID sélectionné
            SeeMovieSessions seeMovieSessionsPanel = new SeeMovieSessions(this, movieTheaterId);
            // ajouter le panneau à l'interface graphique

            contentPane.add(seeMovieSessionsPanel, panelName);
            cardLayout.show(contentPane, panelName);
            contentPane.revalidate();
            contentPane.repaint();
        } else {

        }
        // ajouter d'autres conditions pour les autres panneaux
        // ...
    }
    public static void main(String[] args) {
        new Main("Movie Maven");
    }
}