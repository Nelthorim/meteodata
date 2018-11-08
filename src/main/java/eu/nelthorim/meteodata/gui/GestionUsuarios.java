package eu.nelthorim.meteodata.gui;

import eu.nelthorim.meteodata.db.Usuario;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GestionUsuarios {

    private final Ventana padre;
    private final Usuario usuario;
    private final Connection db;

    private JPanel principal;
    private JList<Usuario> listaUsuarios;
    private JButton nuevo;
    private JButton cambiarContra;
    private JButton borrar;
    private JButton cambiarPermisos;
    private JButton atras;

    public GestionUsuarios(Ventana padre, Usuario usuario, Connection db) {
        this.padre = padre;
        this.usuario = usuario;
        this.db = db;

        atras.addActionListener(this::atras);
        nuevo.addActionListener(this::nuevo);
        borrar.addActionListener(this::borrar);
        cambiarContra.addActionListener(this::cambiarContra);
        cambiarPermisos.addActionListener(this::cambiarPermisos);
    }

    public void poblarLista() {
        try {
            List<Usuario> lista = Usuario.lista(db);
            DefaultListModel<Usuario> modelo = new DefaultListModel<>();
            for (Usuario usuario : lista) {
                modelo.addElement(usuario);
            }
            listaUsuarios.setModel(modelo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void atras(ActionEvent e) {
        padre.cambioPanel(PanelAdmin.getPanel(padre, usuario, db));
    }

    private void nuevo(ActionEvent e) {
        new NuevoUsuario(this, padre, db);
    }

    private void borrar(ActionEvent e) {
        Usuario user = listaUsuarios.getSelectedValue();
        try {
            if (user.usuario.equals(usuario.usuario)) {
                JOptionPane.showConfirmDialog(padre , "No puedes borrarte a ti mismo.",
                        "Error al borrar usuario", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else {
                Usuario.borrar(db, user);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        poblarLista();
    }

    private void cambiarContra(ActionEvent e) {
        String pass = JOptionPane.showInputDialog(padre, "Introduzca nueva contraseña", "Nueva contraseña", JOptionPane.QUESTION_MESSAGE);
        Usuario user = listaUsuarios.getSelectedValue();
        try {
            Usuario.cambiarContrasena(db, user, pass);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void cambiarPermisos(ActionEvent e) {
        Usuario usuario = listaUsuarios.getSelectedValue();
        if (usuario.usuario.equals(this.usuario.usuario)) {
            JOptionPane.showConfirmDialog(padre , "No puedes modificar tus propios permisos.",
                    "Error al modificar usuario", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Usuario.setNivel(db, usuario, usuario.nivel == Usuario.Nivel.ADMINISTRADOR ? Usuario.Nivel.USUARIO : Usuario.Nivel.ADMINISTRADOR);
            poblarLista();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public static JPanel getPanel(Ventana padre, Usuario usuario, Connection db) {
        GestionUsuarios gu = new GestionUsuarios(padre, usuario, db);
        return gu.principal;
    }

    private void createUIComponents() {
        listaUsuarios = new JList<>(new Usuario[]{});
        poblarLista();
    }
}
