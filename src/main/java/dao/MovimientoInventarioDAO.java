package dao;

import config.DatabaseConnection;
import model.MovimientoInventario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimientoInventarioDAO {

    // CREATE - Insertar un nuevo movimiento
    public boolean insertar(MovimientoInventario movimiento) {
        String sql = "INSERT INTO List.Tb_MovimientoInventario (Tipo_Movimiento, Cantidad_Movimiento, " +
                "Fecha_Movimiento, Observacion_Movimiento, Referencia_Movimiento, Id_Inventario, Id_Usuario) " +
                "VALUES (?, ?, GETDATE(), ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, movimiento.getTipoMovimiento());
            pstmt.setInt(2, movimiento.getCantidadMovimiento());
            pstmt.setString(3, movimiento.getObservacionMovimiento());
            pstmt.setString(4, movimiento.getReferenciaMovimiento());
            pstmt.setInt(5, movimiento.getIdInventario());
            pstmt.setInt(6, movimiento.getIdUsuario());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    movimiento.setIdMovimiento(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar movimiento: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // READ - Obtener todos los movimientos
    public List<MovimientoInventario> obtenerTodos() {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM List.Tb_MovimientoInventario ORDER BY Fecha_Movimiento DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                movimientos.add(mapearMovimiento(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener movimientos: " + e.getMessage());
        }

        return movimientos;
    }

    // READ - Obtener movimientos por inventario
    public List<MovimientoInventario> obtenerPorInventario(int idInventario) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM List.Tb_MovimientoInventario WHERE Id_Inventario = ? ORDER BY Fecha_Movimiento DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idInventario);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                movimientos.add(mapearMovimiento(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener movimientos por inventario: " + e.getMessage());
        }

        return movimientos;
    }

    // READ - Obtener movimientos por tipo
    public List<MovimientoInventario> obtenerPorTipo(String tipoMovimiento) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM List.Tb_MovimientoInventario WHERE Tipo_Movimiento = ? ORDER BY Fecha_Movimiento DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tipoMovimiento);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                movimientos.add(mapearMovimiento(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener movimientos por tipo: " + e.getMessage());
        }

        return movimientos;
    }

    // READ - Obtener movimientos por usuario
    public List<MovimientoInventario> obtenerPorUsuario(int idUsuario) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM List.Tb_MovimientoInventario WHERE Id_Usuario = ? ORDER BY Fecha_Movimiento DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                movimientos.add(mapearMovimiento(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener movimientos por usuario: " + e.getMessage());
        }

        return movimientos;
    }

    // READ - Obtener movimientos por rango de fechas
    public List<MovimientoInventario> obtenerPorFechas(Timestamp fechaInicio, Timestamp fechaFin) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM List.Tb_MovimientoInventario WHERE Fecha_Movimiento BETWEEN ? AND ? ORDER BY Fecha_Movimiento DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, fechaInicio);
            pstmt.setTimestamp(2, fechaFin);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                movimientos.add(mapearMovimiento(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener movimientos por fechas: " + e.getMessage());
        }

        return movimientos;
    }

    // Mapear ResultSet a MovimientoInventario
    private MovimientoInventario mapearMovimiento(ResultSet rs) throws SQLException {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdMovimiento(rs.getInt("Id_Movimiento"));
        mov.setTipoMovimiento(rs.getString("Tipo_Movimiento"));
        mov.setCantidadMovimiento(rs.getInt("Cantidad_Movimiento"));
        mov.setFechaMovimiento(rs.getTimestamp("Fecha_Movimiento"));
        mov.setObservacionMovimiento(rs.getString("Observacion_Movimiento"));
        mov.setReferenciaMovimiento(rs.getString("Referencia_Movimiento"));
        mov.setIdInventario(rs.getInt("Id_Inventario"));
        mov.setIdUsuario(rs.getInt("Id_Usuario"));
        return mov;
    }
}