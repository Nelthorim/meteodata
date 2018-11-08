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

public class Presion {

    private final LocalDateTime fecha;
    private final int presion;

    private Presion(LocalDateTime fecha, int presion) {
        this.fecha = fecha;
        this.presion = presion;
    }

    private static List<Presion> obtener(Connection db, LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        PreparedStatement sentencia = db.prepareStatement("select Fecha, Presion from Presion where Fecha between ? and ?");
        sentencia.setTimestamp(1, java.sql.Timestamp.valueOf(desde));
        sentencia.setTimestamp(2, java.sql.Timestamp.valueOf(hasta));

        ResultSet resultados = sentencia.executeQuery();
        List<Presion> listaPresion = new ArrayList<>();

        while (resultados.next()) {
            LocalDateTime fecha = resultados.getTimestamp("Fecha").toLocalDateTime();
            int presion = resultados.getInt("Presion");
            listaPresion.add(new Presion(fecha, presion));
        }
        return listaPresion;
    }

    public static TimeSeriesCollection crearDatos(Connection db, LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        List<Presion> datos = Presion.obtener(db, desde, hasta);

        final TimeSeriesCollection series = new TimeSeriesCollection();
        final TimeSeries presiones = new TimeSeries("Presión (mb)");

        for (Presion dato : datos) {
            Millisecond fecha = new Millisecond(Date.from(dato.fecha.atZone(ZoneId.systemDefault()).toInstant()));
            presiones.add(fecha, dato.presion);
        }

        series.addSeries(presiones);
        return series;
    }


    public static Presion add(Connection db, int presion, LocalDateTime fecha) throws SQLException {
        PreparedStatement sentencia = db.prepareStatement("insert into Presion (Fecha, Presion) values (?, ?)");

        if (fecha == null) {
            fecha = LocalDateTime.now();
        }

        sentencia.setTimestamp(1, Timestamp.valueOf(fecha));
        sentencia.setInt(2, presion);

        sentencia.execute();
        return new Presion(fecha, presion);
    }


    @Override
    public String toString() {
        return fecha + ": " + presion + "mb";
    }

}
