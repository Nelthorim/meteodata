package eu.nelthorim.meteodata.db;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Temperatura {

    private final LocalDateTime fecha;
    private final double maxima;
    private final double minima;

    private Temperatura(LocalDateTime fecha, double maxima, double minima) {
        this.fecha = fecha;
        this.maxima = maxima;
        this.minima = minima;
    }

    private static List<Temperatura> obtener(Connection db, LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        PreparedStatement sentencia = db.prepareStatement("select Fecha, Minima, Maxima from Temperatura where Fecha between ? and ?");
        sentencia.setTimestamp(1, java.sql.Timestamp.valueOf(desde));
        sentencia.setTimestamp(2, java.sql.Timestamp.valueOf(hasta));

        ResultSet resultados = sentencia.executeQuery();
        List<Temperatura> listaTemperatura = new ArrayList<>();

        while (resultados.next()) {
            LocalDateTime fecha = resultados.getTimestamp("Fecha").toLocalDateTime();
            double maxima = resultados.getDouble("Maxima");
            double minima = resultados.getDouble("Minima");
            listaTemperatura.add(new Temperatura(fecha, maxima, minima));
        }
        return listaTemperatura;
    }

    public static TimeSeriesCollection crearDatos(Connection db, LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        List<Temperatura> datos = Temperatura.obtener(db, desde, hasta);

        final TimeSeriesCollection series = new TimeSeriesCollection();
        final TimeSeries maximas = new TimeSeries("Máximas (ºC)");
        final TimeSeries minimas = new TimeSeries("Mínimas (ºC)");

        for (Temperatura dato : datos) {
            Millisecond fecha = new Millisecond(Date.from(dato.fecha.atZone(ZoneId.systemDefault()).toInstant()));
            maximas.add(fecha, dato.maxima);
            minimas.add(fecha, dato.minima);
        }

        series.addSeries(maximas);
        series.addSeries(minimas);

        return series;
    }

    public static Temperatura add(Connection db, double minima, double maxima, LocalDateTime fecha) throws SQLException {
        PreparedStatement sentencia = db.prepareStatement("insert into Temperatura (Fecha, Maxima, Minima) values (?, ?, ?)");

        if (fecha == null) {
            fecha = LocalDateTime.now();
        }

        sentencia.setTimestamp(1, Timestamp.valueOf(fecha));
        sentencia.setDouble(2, maxima);
        sentencia.setDouble(3, minima);

        sentencia.execute();
        return new Temperatura(fecha, maxima, minima);
    }

    @Override
    public String toString() {
        return fecha + ": [" + minima + "ºC - " + maxima + "ºC]";
    }


}
