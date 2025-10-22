package dao;

import config.DatabaseConnection;
import model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // CREATE - Insertar un nuevo producto
    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO Item.Tb_Productos (Sku_Producto, Nombre_Producto, Descripcion_Producto, " +
                "CostoUnitario_Producto, Descuento_Producto, ImagenURL_Producto, Estado_Producto, " +
                "Stock_Producto, Id_Categoria, Id_TipoProducto, Id_UnidadMedida) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getSkuProducto());
            pstmt.setString(2, producto.getNombreProducto());
            pstmt.setString(3, producto.getDescripcionProducto());
            pstmt.setBigDecimal(4, producto.getCostoUnitarioProducto());
            pstmt.setBigDecimal(5, producto.getDescuentoProducto());
            pstmt.setString(6, producto.getImagenURLProducto());
            pstmt.setString(7, producto.getEstadoProducto());
            pstmt.setInt(8, producto.getStockProducto());
            pstmt.setInt(9, producto.getIdCategoria());
            pstmt.setInt(10, producto.getIdTipoProducto());
            pstmt.setInt(11, producto.getIdUnidadMedida());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    // READ - Obtener todos los productos
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Item.Tb_Productos ORDER BY Id_Producto DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }

        return productos;
    }

    // READ - Obtener producto por ID
    public Producto obtenerPorId(int id) {
        String sql = "SELECT * FROM Item.Tb_Productos WHERE Id_Producto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearProducto(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener producto por ID: " + e.getMessage());
        }

        return null;
    }

    // READ - Buscar productos por nombre
    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Item.Tb_Productos WHERE Nombre_Producto LIKE ? ORDER BY Nombre_Producto";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nombre + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
        }

        return productos;
    }

    // READ - Obtener productos por estado
    public List<Producto> obtenerPorEstado(String estado) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Item.Tb_Productos WHERE Estado_Producto = ? ORDER BY Nombre_Producto";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estado);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos por estado: " + e.getMessage());
        }

        return productos;
    }

    // UPDATE - Actualizar un producto
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE Item.Tb_Productos SET Sku_Producto = ?, Nombre_Producto = ?, " +
                "Descripcion_Producto = ?, CostoUnitario_Producto = ?, Descuento_Producto = ?, " +
                "ImagenURL_Producto = ?, Estado_Producto = ?, Stock_Producto = ?, " +
                "Id_Categoria = ?, Id_TipoProducto = ?, Id_UnidadMedida = ? " +
                "WHERE Id_Producto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getSkuProducto());
            pstmt.setString(2, producto.getNombreProducto());
            pstmt.setString(3, producto.getDescripcionProducto());
            pstmt.setBigDecimal(4, producto.getCostoUnitarioProducto());
            pstmt.setBigDecimal(5, producto.getDescuentoProducto());
            pstmt.setString(6, producto.getImagenURLProducto());
            pstmt.setString(7, producto.getEstadoProducto());
            pstmt.setInt(8, producto.getStockProducto());
            pstmt.setInt(9, producto.getIdCategoria());
            pstmt.setInt(10, producto.getIdTipoProducto());
            pstmt.setInt(11, producto.getIdUnidadMedida());
            pstmt.setInt(12, producto.getIdProducto());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    // UPDATE - Actualizar stock
    public boolean actualizarStock(int idProducto, int nuevoStock) {
        String sql = "UPDATE Item.Tb_Productos SET Stock_Producto = ? WHERE Id_Producto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nuevoStock);
            pstmt.setInt(2, idProducto);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }

    // DELETE - Eliminar un producto (físico)
    public boolean eliminar(int id) {
        String sql = "DELETE FROM Item.Tb_Productos WHERE Id_Producto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    // DELETE - Eliminar lógicamente (cambiar estado a INACTIVO)
    public boolean eliminarLogico(int id) {
        String sql = "UPDATE Item.Tb_Productos SET Estado_Producto = 'INACTIVO' WHERE Id_Producto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al desactivar producto: " + e.getMessage());
            return false;
        }
    }

    // Método auxiliar para mapear ResultSet a objeto Producto
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setIdProducto(rs.getInt("Id_Producto"));
        producto.setSkuProducto(rs.getString("Sku_Producto"));
        producto.setNombreProducto(rs.getString("Nombre_Producto"));
        producto.setDescripcionProducto(rs.getString("Descripcion_Producto"));
        producto.setCostoUnitarioProducto(rs.getBigDecimal("CostoUnitario_Producto"));
        producto.setDescuentoProducto(rs.getBigDecimal("Descuento_Producto"));
        producto.setImagenURLProducto(rs.getString("ImagenURL_Producto"));
        producto.setEstadoProducto(rs.getString("Estado_Producto"));
        producto.setStockProducto(rs.getInt("Stock_Producto"));
        producto.setIdCategoria(rs.getInt("Id_Categoria"));
        producto.setIdTipoProducto(rs.getInt("Id_TipoProducto"));
        producto.setIdUnidadMedida(rs.getInt("Id_UnidadMedida"));
        return producto;
    }

    // Método para verificar si existe un SKU
    public boolean existeSku(String sku) {
        String sql = "SELECT COUNT(*) FROM Item.Tb_Productos WHERE Sku_Producto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sku);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar SKU: " + e.getMessage());
        }

        return false;
    }
}