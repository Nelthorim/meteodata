package eu.nelthorim.meteodata.gui;

import eu.nelthorim.meteodata.db.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;

public class NuevoUsuario extends JDialog {

    private final Connection db;
    private final Ventana padre;
    private final GestionUsuarios panelUsuarios;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPasswordField campoPass;
    private JTextField campoUsuario;

    public NuevoUsuario(GestionUsuarios panelUsuarios, Ventana padre, Connection db) {
        this.panelUsuarios = panelUsuarios;
        this.padre = padre;
        this.db = db;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();
        setLocationRelativeTo(padre);
        setTitle("Nuevo usuario");
        setVisible(true);
    }

    private void onOK() {
        try {
            Usuario.crearUsuario(db, campoUsuario.getText(), new String(campoPass.getPassword()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dispose();
        panelUsuarios.poblarLista();
    }

    private void onCancel() {
        dispose();
    }

}
