package dao;

import config.DatabaseConnection;
import model.Inventario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    // CREATE - CORREGIDO
    // CREATE - CORREGIDO
    public boolean insertar(Inventario inventario) {
        String sql = "INSERT INTO List.Tb_Inventarios (Cantidad_Inventario, PrecioVenta_Inventario, " +
                "FechaActualizacion_Inventario, Id_Bodega, Id_Producto) VALUES (?, ?, GETDATE(), ?, ?)";
        //                                                                              ↑ Usa GETDATE() directamente

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, inventario.getCantidadInventario());
            pstmt.setBigDecimal(2, inventario.getPrecioVentaInventario());
            // ELIMINADA: pstmt.setTimestamp(3, inventario.getFechaActualizacionInventario());
            pstmt.setInt(3, inventario.getIdBodega()); // Índice ajustado
            pstmt.setInt(4, inventario.getIdProducto()); // Índice ajustado

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    inventario.setIdInventario(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar inventario: " + e.getMessage());
        }
        return false;
    }

    // READ - Todos
    public List<Inventario> obtenerTodos() {
        List<Inventario> inventarios = new ArrayList<>();
        String sql = "SELECT * FROM List.Tb_Inventarios ORDER BY FechaActualizacion_Inventario DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                inventarios.add(mapearInventario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener inventarios: " + e.getMessage());
        }

        return inventarios;
    }

    // READ - Por ID
    public Inventario obtenerPorId(int id) {
        String sql = "SELECT * FROM List.Tb_Inventarios WHERE Id_Inventario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearInventario(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener inventario: " + e.getMessage());
        }

        return null;
    }

    // READ - Por Bodega
    public List<Inventario> obtenerPorBodega(int idBodega) {
        List<Inventario> inventarios = new ArrayList<>();
        String sql = "SELECT * FROM List.Tb_Inventarios WHERE Id_Bodega = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idBodega);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                inventarios.add(mapearInventario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener inventarios por bodega: " + e.getMessage());
        }

        return inventarios;
    }

    // READ - Por Producto
    public List<Inventario> obtenerPorProducto(int idProducto) {
        List<Inventario> inventarios = new ArrayList<>();
        String sql = "SELECT * FROM List.Tb_Inventarios WHERE Id_Producto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProducto);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                inventarios.add(mapearInventario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener inventarios por producto: " + e.getMessage());
        }

        return inventarios;
    }

    // READ - Stock bajo (menor a una cantidad específica) - CORREGIDO
    public List<Inventario> obtenerStockBajo(int cantidadMinima) {
        List<Inventario> inventarios = new ArrayList<>();
        String sql = "SELECT * FROM List.Tb_Inventarios WHERE Cantidad_Inventario < ?"; // CORREGIDO: Sin CAST

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cantidadMinima);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                inventarios.add(mapearInventario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener stock bajo: " + e.getMessage());
        }

        return inventarios;
    }

    // UPDATE - CORREGIDO
    public boolean actualizar(Inventario inventario) {
        String sql = "UPDATE List.Tb_Inventarios SET Cantidad_Inventario = ?, " +
                "PrecioVenta_Inventario = ?, FechaActualizacion_Inventario = GETDATE(), " +
                "Id_Bodega = ?, Id_Producto = ? WHERE Id_Inventario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, inventario.getCantidadInventario()); // CORREGIDO: setInt en lugar de setString
            pstmt.setBigDecimal(2, inventario.getPrecioVentaInventario());
            pstmt.setInt(3, inventario.getIdBodega());
            pstmt.setInt(4, inventario.getIdProducto());
            pstmt.setInt(5, inventario.getIdInventario());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar inventario: " + e.getMessage());
            return false;
        }
    }

    // UPDATE - Solo cantidad - CORREGIDO
    public boolean actualizarCantidad(int idInventario, int nuevaCantidad) {
        String sql = "UPDATE List.Tb_Inventarios SET Cantidad_Inventario = ?, " +
                "FechaActualizacion_Inventario = GETDATE() WHERE Id_Inventario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nuevaCantidad); // CORREGIDO: setInt directamente
            pstmt.setInt(2, idInventario);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar cantidad: " + e.getMessage());
            return false;
        }
    }

    // UPDATE - Solo precio
    public boolean actualizarPrecio(int idInventario, java.math.BigDecimal nuevoPrecio) {
        String sql = "UPDATE List.Tb_Inventarios SET PrecioVenta_Inventario = ?, " +
                "FechaActualizacion_Inventario = GETDATE() WHERE Id_Inventario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, nuevoPrecio);
            pstmt.setInt(2, idInventario);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar precio: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminar(int id) {
        String sql = "DELETE FROM List.Tb_Inventarios WHERE Id_Inventario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar inventario: " + e.getMessage());
            return false;
        }
    }

    // Verificar existencia de inventario por bodega y producto
    public boolean existeInventario(int idBodega, int idProducto) {
        String sql = "SELECT COUNT(*) FROM List.Tb_Inventarios WHERE Id_Bodega = ? AND Id_Producto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idBodega);
            pstmt.setInt(2, idProducto);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar inventario: " + e.getMessage());
        }

        return false;
    }

    // Mapear ResultSet a Inventario - CORREGIDO
    private Inventario mapearInventario(ResultSet rs) throws SQLException {
        Inventario inv = new Inventario();
        inv.setIdInventario(rs.getInt("Id_Inventario"));
        inv.setCantidadInventario(rs.getInt("Cantidad_Inventario")); // CORREGIDO: getInt directamente
        inv.setPrecioVentaInventario(rs.getBigDecimal("PrecioVenta_Inventario"));
        inv.setFechaActualizacionInventario(rs.getTimestamp("FechaActualizacion_Inventario"));
        inv.setIdBodega(rs.getInt("Id_Bodega"));
        inv.setIdProducto(rs.getInt("Id_Producto"));
        return inv;
    }
}