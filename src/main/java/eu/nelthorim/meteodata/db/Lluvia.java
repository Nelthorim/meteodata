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

public class Lluvia {

    private final LocalDateTime fecha;
    private final double precipitacion;

    private Lluvia(LocalDateTime fecha, double precipitacion) {
        this.fecha = fecha;
        this.precipitacion = precipitacion;
    }

    private static List<Lluvia> obtener(Connection db, LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        PreparedStatement sentencia = db.prepareStatement("select Fecha, Lluvia from Lluvia where Fecha between ? and ?");
        sentencia.setTimestamp(1, java.sql.Timestamp.valueOf(desde));
        sentencia.setTimestamp(2, java.sql.Timestamp.valueOf(hasta));

        ResultSet resultados = sentencia.executeQuery();
        List<Lluvia> listaLluvia = new ArrayList<>();

        while (resultados.next()) {
            LocalDateTime fecha = resultados.getTimestamp("Fecha").toLocalDateTime();
            double lluvia = resultados.getDouble("Precipitacion");
            listaLluvia.add(new Lluvia(fecha, lluvia));
        }
        return listaLluvia;
    }

    public static TimeSeriesCollection crearDatos(Connection db, LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        List<Lluvia> datos = Lluvia.obtener(db, desde, hasta);

        final TimeSeriesCollection series = new TimeSeriesCollection();
        final TimeSeries lluvias = new TimeSeries("Precipitaciones (ml/m³)");

        for (Lluvia dato : datos) {
            Millisecond fecha = new Millisecond(Date.from(dato.fecha.atZone(ZoneId.systemDefault()).toInstant()));
            lluvias.add(fecha, dato.precipitacion);
        }

        series.addSeries(lluvias);

        return series;
    }

    public static Lluvia add(Connection db, double precipitacion, LocalDateTime fecha) throws SQLException {
        PreparedStatement sentencia = db.prepareStatement("insert into Lluvia (Fecha, Lluvia) values (?, ?)");

        if (fecha == null) {
            fecha = LocalDateTime.now();
        }

        sentencia.setTimestamp(1, Timestamp.valueOf(fecha));
        sentencia.setDouble(2, precipitacion);

        sentencia.execute();
        return new Lluvia(fecha, precipitacion);
    }

    @Override
    public String toString() {
        return fecha + ": " + precipitacion + "mL/m^2";
    }
}
