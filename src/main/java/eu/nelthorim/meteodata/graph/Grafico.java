package eu.nelthorim.meteodata.graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.text.SimpleDateFormat;

public class Grafico extends JDialog {

    public Grafico(String titulo, TimeSeriesCollection temperaturas) {
        JFreeChart plot = ChartFactory.createTimeSeriesChart(titulo, "Fecha-hora", titulo, temperaturas);

        XYPlot plotxy = plot.getXYPlot();
        DateAxis axis = (DateAxis) plotxy.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("dd/MM/yyyy hh:mm"));
        axis.setTickMarkPosition(DateTickMarkPosition.START);
        ChartPanel panel = new ChartPanel(plot);

        this.add(panel);
        this.setSize(panel.getPreferredSize());
        this.setVisible(true);
    }


}
