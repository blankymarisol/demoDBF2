package dao;

import config.DatabaseConnection;
import model.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    // CREATE
    public boolean insertar(Proveedor proveedor) {
        String sql = "INSERT INTO Supplier.Tb_Proveedores (Nombre_Proveedor, Telefono_Proveedor, " +
                "Correo_Proveedor, Direccion_Proveedor, NIT_Proveedor) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, proveedor.getNombreProveedor());
            pstmt.setString(2, proveedor.getTelefonoProveedor());
            pstmt.setString(3, proveedor.getCorreoProveedor());
            pstmt.setString(4, proveedor.getDireccionProveedor());
            pstmt.setString(5, proveedor.getNitProveedor());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    proveedor.setIdProveedor(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar proveedor: " + e.getMessage());
        }
        return false;
    }

    // READ - Todos
    public List<Proveedor> obtenerTodos() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM Supplier.Tb_Proveedores ORDER BY Nombre_Proveedor";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                proveedores.add(mapearProveedor(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener proveedores: " + e.getMessage());
        }

        return proveedores;
    }

    // READ - Por ID
    public Proveedor obtenerPorId(int id) {
        String sql = "SELECT * FROM Supplier.Tb_Proveedores WHERE Id_Proveedor = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearProveedor(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener proveedor: " + e.getMessage());
        }

        return null;
    }

    // READ - Buscar por nombre
    public List<Proveedor> buscarPorNombre(String nombre) {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM Supplier.Tb_Proveedores WHERE Nombre_Proveedor LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nombre + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                proveedores.add(mapearProveedor(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar proveedores: " + e.getMessage());
        }

        return proveedores;
    }

    // UPDATE
    public boolean actualizar(Proveedor proveedor) {
        String sql = "UPDATE Supplier.Tb_Proveedores SET Nombre_Proveedor = ?, Telefono_Proveedor = ?, " +
                "Correo_Proveedor = ?, Direccion_Proveedor = ?, NIT_Proveedor = ? " +
                "WHERE Id_Proveedor = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, proveedor.getNombreProveedor());
            pstmt.setString(2, proveedor.getTelefonoProveedor());
            pstmt.setString(3, proveedor.getCorreoProveedor());
            pstmt.setString(4, proveedor.getDireccionProveedor());
            pstmt.setString(5, proveedor.getNitProveedor());
            pstmt.setInt(6, proveedor.getIdProveedor());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar proveedor: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminar(int id) {
        String sql = "DELETE FROM Supplier.Tb_Proveedores WHERE Id_Proveedor = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar proveedor: " + e.getMessage());
            return false;
        }
    }

    // Mapear ResultSet a Proveedor
    private Proveedor mapearProveedor(ResultSet rs) throws SQLException {
        Proveedor p = new Proveedor();
        p.setIdProveedor(rs.getInt("Id_Proveedor"));
        p.setNombreProveedor(rs.getString("Nombre_Proveedor"));
        p.setTelefonoProveedor(rs.getString("Telefono_Proveedor"));
        p.setCorreoProveedor(rs.getString("Correo_Proveedor"));
        p.setDireccionProveedor(rs.getString("Direccion_Proveedor"));
        p.setNitProveedor(rs.getString("NIT_Proveedor"));
        return p;
    }
}

