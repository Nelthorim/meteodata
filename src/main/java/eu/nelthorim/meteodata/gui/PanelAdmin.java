package eu.nelthorim.meteodata.gui;

import eu.nelthorim.meteodata.db.Usuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;

public class PanelAdmin {
    
    private final Ventana padre;
    private final Connection db;
    private final Usuario usuario;
    private JButton atras;
    private JButton anadir;
    private JButton usuarios;
    private JPanel principal;

    public PanelAdmin(Ventana padre, Usuario usuario, Connection db) {
        this.padre = padre;
        this.usuario = usuario;
        this.db = db;
        atras.addActionListener(this::atrasAccion);
        anadir.addActionListener(this::anadirAccion);
        usuarios.addActionListener(this::usuariosAccion);
    }

    private void atrasAccion(ActionEvent e) {
        padre.cambioPanel(Principal.getPanel(padre, usuario, db));
    }

    private void anadirAccion(ActionEvent e) {
        padre.cambioPanel(InsertDatos.getPanel(padre, usuario, db));
    }

    private void usuariosAccion(ActionEvent actionEvent) {
        padre.cambioPanel(GestionUsuarios.getPanel(padre, usuario, db));
    }

    public static JPanel getPanel(Ventana padre, Usuario usuario, Connection db) {
        PanelAdmin admin = new PanelAdmin(padre, usuario, db);
        return admin.principal;
    }


}
