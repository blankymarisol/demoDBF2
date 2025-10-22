// ============================================
// TipoProductoDAO.java
// ============================================
package dao;

import config.DatabaseConnection;
import model.TipoProducto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoProductoDAO {

    public boolean insertar(TipoProducto tipo) {
        String sql = "INSERT INTO Item.Tb_TipoProductos (Nombre_TipoProducto) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, tipo.getNombreTipoProducto());

            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    tipo.setIdTipoProducto(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar tipo de producto: " + e.getMessage());
        }
        return false;
    }

    public List<TipoProducto> obtenerTodos() {
        List<TipoProducto> tipos = new ArrayList<>();
        String sql = "SELECT * FROM Item.Tb_TipoProductos ORDER BY Nombre_TipoProducto";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TipoProducto t = new TipoProducto();
                t.setIdTipoProducto(rs.getInt("Id_TipoProducto"));
                t.setNombreTipoProducto(rs.getString("Nombre_TipoProducto"));
                tipos.add(t);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener tipos: " + e.getMessage());
        }
        return tipos;
    }

    public TipoProducto obtenerPorId(int id) {
        String sql = "SELECT * FROM Item.Tb_TipoProductos WHERE Id_TipoProducto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                TipoProducto t = new TipoProducto();
                t.setIdTipoProducto(rs.getInt("Id_TipoProducto"));
                t.setNombreTipoProducto(rs.getString("Nombre_TipoProducto"));
                return t;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener tipo: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizar(TipoProducto tipo) {
        String sql = "UPDATE Item.Tb_TipoProductos SET Nombre_TipoProducto = ? WHERE Id_TipoProducto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tipo.getNombreTipoProducto());
            pstmt.setInt(2, tipo.getIdTipoProducto());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar tipo: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM Item.Tb_TipoProductos WHERE Id_TipoProducto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar tipo: " + e.getMessage());
            return false;
        }
    }
}
