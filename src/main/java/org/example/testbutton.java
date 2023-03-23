package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class testbutton extends JButton {

    testbutton(String name) {
        super(name);
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomB();
            }
        });
    }

    private void CustomB() {
        System.out.print("helloooo");
    }
}
