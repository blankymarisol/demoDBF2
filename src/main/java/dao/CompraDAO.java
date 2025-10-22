package dao;

import config.DatabaseConnection;
import model.Compra;
import model.CompraDetalle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO {

    // ========================================
    // CREATE - Insertar compra con detalles (TRANSACCIÓN)
    // ========================================
    public boolean insertarCompraCompleta(Compra compra, List<CompraDetalle> detalles) {
        Connection conn = null;
        PreparedStatement pstmtCompra = null;
        PreparedStatement pstmtDetalle = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Insertar la compra
            String sqlCompra = "INSERT INTO ProFin.Tb_Compras (Fecha_Compra, Total_Compra, Estado_Compra, Id_Proveedor) " +
                    "VALUES (?, ?, ?, ?)";

            pstmtCompra = conn.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS);
            pstmtCompra.setDate(1, compra.getFechaCompra());
            pstmtCompra.setBigDecimal(2, compra.getTotalCompra());
            pstmtCompra.setString(3, compra.getEstadoCompra());
            pstmtCompra.setInt(4, compra.getIdProveedor());

            int filasCompra = pstmtCompra.executeUpdate();

            if (filasCompra > 0) {
                // Obtener el ID generado de la compra
                ResultSet rs = pstmtCompra.getGeneratedKeys();
                if (rs.next()) {
                    int idCompra = rs.getInt(1);
                    compra.setIdCompra(idCompra);

                    // 2. Insertar los detalles de la compra
                    String sqlDetalle = "INSERT INTO ProFin.Tb_CompraDetalle (Cantidad_Detalle, PrecioUnitario_Detalle, Id_Compra, Id_Producto) " +
                            "VALUES (?, ?, ?, ?)";

                    pstmtDetalle = conn.prepareStatement(sqlDetalle);

                    for (CompraDetalle detalle : detalles) {
                        pstmtDetalle.setInt(1, detalle.getCantidadDetalle()); // CORREGIDO: setInt en lugar de setString
                        pstmtDetalle.setBigDecimal(2, detalle.getPrecioUnitarioDetalle());
                        pstmtDetalle.setInt(3, idCompra);
                        pstmtDetalle.setInt(4, detalle.getIdProducto());
                        pstmtDetalle.addBatch();
                    }

                    pstmtDetalle.executeBatch();
                    conn.commit(); // Confirmar transacción
                    System.out.println("✓ Compra insertada correctamente con ID: " + idCompra);
                    return true;
                }
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            System.err.println("Error al insertar compra: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    System.err.println("Transacción revertida");
                }
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            return false;

        } finally {
            try {
                if (pstmtDetalle != null) pstmtDetalle.close();
                if (pstmtCompra != null) pstmtCompra.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }

    // ========================================
    // CREATE - Insertar solo la compra (sin detalles)
    // ========================================
    public boolean insertar(Compra compra) {
        String sql = "INSERT INTO ProFin.Tb_Compras (Fecha_Compra, Total_Compra, Estado_Compra, Id_Proveedor) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDate(1, compra.getFechaCompra());
            pstmt.setBigDecimal(2, compra.getTotalCompra());
            pstmt.setString(3, compra.getEstadoCompra());
            pstmt.setInt(4, compra.getIdProveedor());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    compra.setIdCompra(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar compra: " + e.getMessage());
        }
        return false;
    }

    // ========================================
    // READ - Obtener todas las compras
    // ========================================
    public List<Compra> obtenerTodas() {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM ProFin.Tb_Compras ORDER BY Fecha_Compra DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                compras.add(mapearCompra(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener compras: " + e.getMessage());
        }

        return compras;
    }

    // ========================================
    // READ - Obtener compra por ID
    // ========================================
    public Compra obtenerPorId(int id) {
        String sql = "SELECT * FROM ProFin.Tb_Compras WHERE Id_Compra = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearCompra(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener compra por ID: " + e.getMessage());
        }

        return null;
    }

    // ========================================
    // READ - Obtener compras por proveedor
    // ========================================
    public List<Compra> obtenerPorProveedor(int idProveedor) {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM ProFin.Tb_Compras WHERE Id_Proveedor = ? ORDER BY Fecha_Compra DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProveedor);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                compras.add(mapearCompra(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener compras por proveedor: " + e.getMessage());
        }

        return compras;
    }

    // ========================================
    // READ - Obtener compras por rango de fechas
    // ========================================
    public List<Compra> obtenerPorFechas(Date fechaInicio, Date fechaFin) {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM ProFin.Tb_Compras WHERE Fecha_Compra BETWEEN ? AND ? ORDER BY Fecha_Compra DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, fechaInicio);
            pstmt.setDate(2, fechaFin);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                compras.add(mapearCompra(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener compras por fechas: " + e.getMessage());
        }

        return compras;
    }

    // ========================================
    // READ - Obtener compras por estado
    // ========================================
    public List<Compra> obtenerPorEstado(String estado) {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM ProFin.Tb_Compras WHERE Estado_Compra = ? ORDER BY Fecha_Compra DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estado);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                compras.add(mapearCompra(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener compras por estado: " + e.getMessage());
        }

        return compras;
    }

    // ========================================
    // READ - Obtener detalles de una compra
    // ========================================
    public List<CompraDetalle> obtenerDetalles(int idCompra) {
        List<CompraDetalle> detalles = new ArrayList<>();
        String sql = "SELECT * FROM ProFin.Tb_CompraDetalle WHERE Id_Compra = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCompra);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                detalles.add(mapearDetalle(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener detalles de compra: " + e.getMessage());
        }

        return detalles;
    }

    // ========================================
    // UPDATE - Actualizar compra
    // ========================================
    public boolean actualizar(Compra compra) {
        String sql = "UPDATE ProFin.Tb_Compras SET Fecha_Compra = ?, Total_Compra = ?, " +
                "Estado_Compra = ?, Id_Proveedor = ? WHERE Id_Compra = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, compra.getFechaCompra());
            pstmt.setBigDecimal(2, compra.getTotalCompra());
            pstmt.setString(3, compra.getEstadoCompra());
            pstmt.setInt(4, compra.getIdProveedor());
            pstmt.setInt(5, compra.getIdCompra());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar compra: " + e.getMessage());
            return false;
        }
    }

    // ========================================
    // UPDATE - Actualizar solo el estado de una compra
    // ========================================
    public boolean actualizarEstado(int idCompra, String nuevoEstado) {
        String sql = "UPDATE ProFin.Tb_Compras SET Estado_Compra = ? WHERE Id_Compra = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idCompra);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    // ========================================
    // DELETE - Eliminar compra con sus detalles (TRANSACCIÓN)
    // ========================================
    public boolean eliminar(int id) {
        Connection conn = null;
        PreparedStatement pstmtDetalle = null;
        PreparedStatement pstmtCompra = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Primero eliminar los detalles
            String sqlDetalle = "DELETE FROM ProFin.Tb_CompraDetalle WHERE Id_Compra = ?";
            pstmtDetalle = conn.prepareStatement(sqlDetalle);
            pstmtDetalle.setInt(1, id);
            pstmtDetalle.executeUpdate();

            // 2. Luego eliminar la compra
            String sqlCompra = "DELETE FROM ProFin.Tb_Compras WHERE Id_Compra = ?";
            pstmtCompra = conn.prepareStatement(sqlCompra);
            pstmtCompra.setInt(1, id);
            int filasAfectadas = pstmtCompra.executeUpdate();

            conn.commit();
            System.out.println("✓ Compra eliminada correctamente");
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar compra: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    System.err.println("Transacción revertida");
                }
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            return false;

        } finally {
            try {
                if (pstmtDetalle != null) pstmtDetalle.close();
                if (pstmtCompra != null) pstmtCompra.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }

    // ========================================
    // MÉTODO AUXILIAR - Mapear ResultSet a Compra
    // ========================================
    private Compra mapearCompra(ResultSet rs) throws SQLException {
        Compra compra = new Compra();
        compra.setIdCompra(rs.getInt("Id_Compra"));
        compra.setFechaCompra(rs.getDate("Fecha_Compra"));
        compra.setTotalCompra(rs.getBigDecimal("Total_Compra"));
        compra.setEstadoCompra(rs.getString("Estado_Compra"));
        compra.setIdProveedor(rs.getInt("Id_Proveedor"));
        return compra;
    }

    // ========================================
    // MÉTODO AUXILIAR - Mapear ResultSet a CompraDetalle - CORREGIDO
    // ========================================
    private CompraDetalle mapearDetalle(ResultSet rs) throws SQLException {
        CompraDetalle detalle = new CompraDetalle();
        detalle.setIdDetalle(rs.getInt("Id_Detalle"));
        detalle.setCantidadDetalle(rs.getInt("Cantidad_Detalle")); // CORREGIDO: getInt en lugar de getString + parseInt
        detalle.setPrecioUnitarioDetalle(rs.getBigDecimal("PrecioUnitario_Detalle"));
        detalle.setIdCompra(rs.getInt("Id_Compra"));
        detalle.setIdProducto(rs.getInt("Id_Producto"));
        return detalle;
    }
}