package eu.nelthorim.meteodata.gui;

import sun.awt.X11.Screen;

import javax.management.monitor.Monitor;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Ventana extends JFrame {

    public Ventana() {
        config();
    }

    private void config() {
        Connection db = null;
        try {
            db = DriverManager.getConnection("jdbc:mysql://127.0.0.1:9906/Lluvias", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.setContentPane(Login.getPanel(this, db));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();

        this.setTitle("MeteoData v1.0");
        this.setLocation((int) (pantalla.getWidth() * 0.5), (int) (pantalla.getHeight() * 0.5));
        this.pack();
        this.setVisible(true);
    }

    public void cambioPanel(JPanel panel) {
        this.setContentPane(panel);
        this.revalidate();
        this.repaint();
        this.pack();
    }

}
