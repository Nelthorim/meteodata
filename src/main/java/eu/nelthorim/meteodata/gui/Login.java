package eu.nelthorim.meteodata.gui;

import eu.nelthorim.meteodata.db.Usuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class Login implements ActionListener {

    private final Ventana padre;
    private JTextField txtUsuario;
    private JPasswordField txtPass;
    private JLabel error;
    private JPanel principal;
    private JButton login;

    private Connection db;

    public Login(Ventana padre, Connection db) {
        this.padre = padre;
        this.db = db;
        login.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        error.setText("");
        try {
            Usuario user = Usuario.login(db, txtUsuario.getText(), new String(txtPass.getPassword()));
            if (user == null) {
                error.setText("Usuario incorrecto");
            } else {
                padre.cambioPanel(Principal.getPanel(padre, user, db));
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            error.setText("No se pudo conectar a la base de datos.");
        }
    }

    public static JPanel getPanel(Ventana padre, Connection db) {
        Login login = new Login(padre, db);
        return login.principal;
    }

}
