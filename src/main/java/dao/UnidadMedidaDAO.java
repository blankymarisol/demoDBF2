// ============================================
// UnidadMedidaDAO.java
// ============================================
package dao;

import config.DatabaseConnection;
import model.UnidadMedida;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnidadMedidaDAO {

    public boolean insertar(UnidadMedida unidad) {
        String sql = "INSERT INTO Item.Tb_UnidadMedidas (Nombre_UnidadMedida, Abreviatura_UnidadMedida) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, unidad.getNombreUnidadMedida());
            pstmt.setString(2, unidad.getAbreviaturaUnidadMedida());

            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    unidad.setIdUnidadMedida(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar unidad de medida: " + e.getMessage());
        }
        return false;
    }

    public List<UnidadMedida> obtenerTodas() {
        List<UnidadMedida> unidades = new ArrayList<>();
        String sql = "SELECT * FROM Item.Tb_UnidadMedidas ORDER BY Nombre_UnidadMedida";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UnidadMedida u = new UnidadMedida();
                u.setIdUnidadMedida(rs.getInt("Id_UnidadMedida"));
                u.setNombreUnidadMedida(rs.getString("Nombre_UnidadMedida"));
                u.setAbreviaturaUnidadMedida(rs.getString("Abreviatura_UnidadMedida"));
                unidades.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener unidades: " + e.getMessage());
        }
        return unidades;
    }

    public UnidadMedida obtenerPorId(int id) {
        String sql = "SELECT * FROM Item.Tb_UnidadMedidas WHERE Id_UnidadMedida = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                UnidadMedida u = new UnidadMedida();
                u.setIdUnidadMedida(rs.getInt("Id_UnidadMedida"));
                u.setNombreUnidadMedida(rs.getString("Nombre_UnidadMedida"));
                u.setAbreviaturaUnidadMedida(rs.getString("Abreviatura_UnidadMedida"));
                return u;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener unidad: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizar(UnidadMedida unidad) {
        String sql = "UPDATE Item.Tb_UnidadMedidas SET Nombre_UnidadMedida = ?, Abreviatura_UnidadMedida = ? WHERE Id_UnidadMedida = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, unidad.getNombreUnidadMedida());
            pstmt.setString(2, unidad.getAbreviaturaUnidadMedida());
            pstmt.setInt(3, unidad.getIdUnidadMedida());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar unidad: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM Item.Tb_UnidadMedidas WHERE Id_UnidadMedida = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar unidad: " + e.getMessage());
            return false;
        }
    }
}

