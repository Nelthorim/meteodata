package eu.nelthorim.meteodata.gui;

import eu.nelthorim.meteodata.db.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class InsertDatos {

    private final Ventana padre;
    private final Usuario usuario;
    private final Connection db;

    private JPanel principal;
    private JTextArea listaDatos;
    private JComboBox<TipoDato> comboTipo;
    private JCheckBox usarFechaHoraActualCheckBox;
    private JSpinner spinnerFecha;
    private JButton insertar;
    private JButton atras;
    private JPanel panelDatos;

    private JFormattedTextField lluvia;
    private JFormattedTextField presion;
    private JFormattedTextField tempMax;
    private JFormattedTextField tempMin;

    private NumberFormat formato;

    public InsertDatos(Ventana padre, Usuario usuario, Connection db) {
        this.padre = padre;
        this.usuario = usuario;
        this.db = db;

        listaDatos.setRows(30);
        usarFechaHoraActualCheckBox.addItemListener(this::activarFecha);
        comboTipo.addItemListener(this::cambiarTipo);
        spinnerFecha.setEnabled(false);

        insertar.addActionListener(this::insertar);
        atras.addActionListener(this::atras);
    }

    private void createUIComponents() {
        comboTipo = new JComboBox<>(TipoDato.values());
        SpinnerDateModel modelo = new SpinnerDateModel();
        modelo.setCalendarField(Calendar.SECOND);
        spinnerFecha = new JSpinner(modelo);

        panelDatos = new JPanel();
        panelDatos.setLayout(new CardLayout());

        formato = NumberFormat.getNumberInstance();
        formato.setMaximumFractionDigits(2);

        JPanel panelTemperaturas = new JPanel();
        tempMax = new JFormattedTextField(formato);
        tempMax.setColumns(4);
        tempMin = new JFormattedTextField(formato);
        tempMin.setColumns(4);
        panelTemperaturas.add(new JLabel("Máxima (ºC): "));
        panelTemperaturas.add(tempMax);
        panelTemperaturas.add(new JLabel("Mínima (ºC): "));
        panelTemperaturas.add(tempMin);

        JPanel panelLluvia = new JPanel();
        lluvia = new JFormattedTextField(formato);
        lluvia.setColumns(4);
        panelLluvia.add(new JLabel("Lluvia (ml/m²): "));
        panelLluvia.add(lluvia);

        JPanel panelPresion = new JPanel();
        presion = new JFormattedTextField(formato);
        presion.setColumns(4);
        panelPresion.add(new JLabel("Presión (mb): "));
        panelPresion.add(presion);

        panelDatos.add(panelTemperaturas, TipoDato.Temperatura.toString());
        panelDatos.add(panelLluvia, TipoDato.Lluvia.toString());
        panelDatos.add(panelPresion, TipoDato.Presion.toString());

    }

    private void cambiarTipo(ItemEvent e) {
        CardLayout cl = (CardLayout) panelDatos.getLayout();
        cl.show(panelDatos, e.getItem().toString());
    }

    private void activarFecha(ItemEvent e) {
        spinnerFecha.setEnabled(e.getStateChange() == ItemEvent.DESELECTED);
    }

    private void insertar(ActionEvent e) {
        TipoDato tipo = (TipoDato) comboTipo.getSelectedItem();
        LocalDateTime fecha = null;
        if (spinnerFecha.isEnabled())
            fecha = LocalDateTime.ofInstant(((Date) spinnerFecha.getValue()).toInstant(), ZoneId.systemDefault());

        Object dato = null;

        try {
            switch (tipo) {
                case Temperatura:
                    double max = formato.parse(tempMax.getText()).doubleValue();
                    double min = formato.parse(tempMin.getText()).doubleValue();
                    dato = Temperatura.add(db, min, max, fecha);
                    break;
                case Lluvia:
                    double cantLluvia = formato.parse(lluvia.getText()).doubleValue();
                    dato = Lluvia.add(db, cantLluvia, fecha);
                    break;
                case Presion:
                    int cantPresion = formato.parse(presion.getText()).intValue();
                    dato = Presion.add(db, cantPresion, fecha);
                    break;
                default:
                    break;
            }
            Thread.sleep(1000);
            listaDatos.setText(listaDatos.getText() + tipo.toString() + ":  " + dato.toString() + "\n");
            spinnerFecha.getModel().setValue(spinnerFecha.getModel().getNextValue());
            padre.pack();
        } catch (ParseException | SQLException | InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    private void atras(ActionEvent e) {
        padre.cambioPanel(PanelAdmin.getPanel(padre, usuario, db));
    }

    public static JPanel getPanel(Ventana padre, Usuario usuario, Connection db) {
        InsertDatos id = new InsertDatos(padre, usuario, db);
        return id.principal;
    }

}
