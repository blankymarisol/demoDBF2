package dao;

import config.DatabaseConnection;
import model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // CREATE
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO Person.Tb_Usuarios (Nombre_Usuario, Rol_Usuario, " +
                "Correo_Usuario, Contrasena) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getRolUsuario());
            pstmt.setString(3, usuario.getCorreoUsuario());
            pstmt.setString(4, usuario.getContrasena());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    usuario.setIdUsuario(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
        }
        return false;
    }

    // READ - Todos
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM Person.Tb_Usuarios ORDER BY Nombre_Usuario";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }

        return usuarios;
    }

    // READ - Por ID
    public Usuario obtenerPorId(int id) {
        String sql = "SELECT * FROM Person.Tb_Usuarios WHERE Id_Usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
        }

        return null;
    }

    // READ - Login (autenticación)
    public Usuario login(String correo, String contrasena) {
        String sql = "SELECT * FROM Person.Tb_Usuarios WHERE Correo_Usuario = ? AND Contrasena = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, correo);
            pstmt.setString(2, contrasena);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error en login: " + e.getMessage());
        }

        return null;
    }

    // READ - Por rol
    public List<Usuario> obtenerPorRol(String rol) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM Person.Tb_Usuarios WHERE Rol_Usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rol);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios por rol: " + e.getMessage());
        }

        return usuarios;
    }

    // UPDATE
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE Person.Tb_Usuarios SET Nombre_Usuario = ?, Rol_Usuario = ?, " +
                "Correo_Usuario = ?, Contrasena = ? WHERE Id_Usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getRolUsuario());
            pstmt.setString(3, usuario.getCorreoUsuario());
            pstmt.setString(4, usuario.getContrasena());
            pstmt.setInt(5, usuario.getIdUsuario());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // UPDATE - Cambiar contraseña
    public boolean cambiarContrasena(int idUsuario, String nuevaContrasena) {
        String sql = "UPDATE Person.Tb_Usuarios SET Contrasena = ? WHERE Id_Usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nuevaContrasena);
            pstmt.setInt(2, idUsuario);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al cambiar contraseña: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminar(int id) {
        String sql = "DELETE FROM Person.Tb_Usuarios WHERE Id_Usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    // Verificar si existe correo
    public boolean existeCorreo(String correo) {
        String sql = "SELECT COUNT(*) FROM Person.Tb_Usuarios WHERE Correo_Usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, correo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar correo: " + e.getMessage());
        }

        return false;
    }

    // Mapear ResultSet a Usuario
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("Id_Usuario"));
        u.setNombreUsuario(rs.getString("Nombre_Usuario"));
        u.setRolUsuario(rs.getString("Rol_Usuario"));
        u.setCorreoUsuario(rs.getString("Correo_Usuario"));
        u.setContrasena(rs.getString("Contrasena"));
        return u;
    }
}

