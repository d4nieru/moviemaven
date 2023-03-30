package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuListener implements ActionListener {

    private Main main;

    public MenuListener(Main main) {
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == main.getNewMenuItem()) {
            // Code pour gérer l'événement "Revenir au menu principal"

        } else if (e.getSource() == main.getAboutMenuItem()) {
            // Code pour gérer l'événement "A Propos"
            JOptionPane.showMessageDialog(null, "Ceci est une boîte de dialogue d'information", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
