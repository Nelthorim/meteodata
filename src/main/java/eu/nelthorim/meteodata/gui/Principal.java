package eu.nelthorim.meteodata.gui;

import eu.nelthorim.meteodata.db.*;
import eu.nelthorim.meteodata.graph.Grafico;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Principal {

    private final Ventana padre;
    private final Connection db;
    private final Usuario usuario;
    private JPanel principal;
    private JSpinner fechaInicio;
    private JSpinner fechaFinal;
    private JButton buscar;
    private JComboBox<TipoDato> comboTipo;
    private JButton admin;
    private JButton logout;



    private Principal(Ventana padre, Usuario usuario, Connection db) {
        this.padre = padre;
        this.usuario = usuario;
        this.db = db;
        buscar.addActionListener(this::buscarDatos);
        logout.addActionListener(this::logout);
    }

    public static JPanel getPanel(Ventana padre, Usuario usuario, Connection db) {
        Principal principal = new Principal(padre, usuario, db);
        return principal.principal;
    }

    private void createUIComponents() {
        fechaInicio = new JSpinner();
        fechaInicio.setModel(new SpinnerDateModel());
        fechaFinal = new JSpinner();
        fechaFinal.setModel(new SpinnerDateModel());

        comboTipo = new JComboBox<>(TipoDato.values());

        admin = new JButton("Añadir datos");
        admin.addActionListener(this::admin);
        admin.setVisible(usuario.esAdmin());
    }

    public void buscarDatos(ActionEvent e) {
        LocalDateTime desde = LocalDateTime.ofInstant(((Date) fechaInicio.getValue()).toInstant(), ZoneId.systemDefault());
        LocalDateTime hasta = LocalDateTime.ofInstant(((Date) fechaFinal.getValue()).toInstant(), ZoneId.systemDefault());
        TipoDato tipo = (TipoDato) comboTipo.getSelectedItem();

        try {
            TimeSeriesCollection datos = null;
            switch (tipo) {
                case Temperatura: datos = Temperatura.crearDatos(db, desde, hasta); break;
                case Presion: datos = Presion.crearDatos(db, desde, hasta); break;
                case Lluvia: datos = Lluvia.crearDatos(db, desde, hasta); break;
                default: datos = new TimeSeriesCollection(); break;
            }
            Grafico graph = new Grafico(tipo.toString(), datos);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public void admin(ActionEvent e) {
        padre.cambioPanel(PanelAdmin.getPanel(padre, usuario, db));
    }

    private void logout(ActionEvent e) {
        padre.cambioPanel(Login.getPanel(padre, db));
    }

}
