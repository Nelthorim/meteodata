package eu.nelthorim.meteodata.db;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Usuario {

    public final String usuario;
    public final Nivel nivel;

    public enum Nivel { USUARIO, ADMINISTRADOR }

    private Usuario(String usuario, Nivel nivel) {
        this.usuario = usuario;
        this.nivel = nivel;
    }

    public static Usuario login(Connection db, String usuario, String contrasena) throws SQLException {
        PreparedStatement sentencia = db.prepareStatement("select * from Usuario where Nombre = ?");
        sentencia.setString(1, usuario);
        ResultSet resultados = sentencia.executeQuery();

        if (!resultados.next()) {
            return null;
        }

        String hash = resultados.getString("Hash");
        StrongPasswordEncryptor encripta = new StrongPasswordEncryptor();

        if (encripta.checkPassword(contrasena, hash)) {
            Nivel nivel = resultados.getInt("Nivel") == 1? Nivel.ADMINISTRADOR : Nivel.USUARIO;
            return new Usuario(usuario, nivel);
        } else {
            return null;
        }
    }

    public static List<Usuario> lista(Connection db) throws SQLException {
        PreparedStatement sentencia = db.prepareStatement("select * from Usuario");
        ResultSet resultados = sentencia.executeQuery();

        List<Usuario> usuarios = new ArrayList<>();
        while (resultados.next()) {
            String usuario = resultados.getString("Nombre");
            Nivel nivel = resultados.getInt("Nivel") == 1? Nivel.ADMINISTRADOR : Nivel.USUARIO;
            usuarios.add(new Usuario(usuario, nivel));
        }

        return usuarios;
    }

    public static boolean crearUsuario(Connection db, String usuario, String contrasena) throws SQLException {
        StrongPasswordEncryptor encripta = new StrongPasswordEncryptor();
        String hash = encripta.encryptPassword(contrasena);

        PreparedStatement sentencia = db.prepareStatement("insert into Usuario (Nombre, Hash) values (?, ?);");
        sentencia.setString(1, usuario);
        sentencia.setString(2, hash);

        return sentencia.execute();
    }

    public static boolean setNivel(Connection db, Usuario usuario, Nivel nivel) throws SQLException {
        PreparedStatement sentencia = db.prepareStatement("update Usuario set Nivel = ? where Nombre = ?");
        sentencia.setInt(1, nivel.ordinal());
        sentencia.setString(2, usuario.usuario);

        return sentencia.execute();
    }

    public static boolean cambiarContrasena(Connection db, Usuario usuario, String pass) throws SQLException {
        StrongPasswordEncryptor encripta = new StrongPasswordEncryptor();

        PreparedStatement sentencia = db.prepareStatement("update Usuario set Hash = ? where Nombre = ?");
        sentencia.setString(2, usuario.usuario);
        sentencia.setString(1, encripta.encryptPassword(pass));

        return sentencia.execute();
    }

    public static boolean borrar(Connection db, Usuario usuario) throws SQLException {
        PreparedStatement sentencia = db.prepareStatement("delete from Usuario where Nombre = ?");
        sentencia.setString(1, usuario.usuario);

        return sentencia.execute();
    }

    public boolean esAdmin()  {
        return nivel == Nivel.ADMINISTRADOR;
    }

    @Override
    public String toString() {
        return usuario + (nivel == Nivel.ADMINISTRADOR ? " [ADMIN]" : "");
    }

}
