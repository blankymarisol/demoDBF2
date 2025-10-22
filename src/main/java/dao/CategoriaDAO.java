package dao;

import config.DatabaseConnection;
import model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    // CREATE
    public boolean insertar(Categoria categoria) {
        String sql = "INSERT INTO Item.Tb_Categorias (Nombre_Categoria, Descripcion_Categoria) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, categoria.getNombreCategoria());
            pstmt.setString(2, categoria.getDescripcionCategoria());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    categoria.setIdCategoria(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar categoría: " + e.getMessage());
        }
        return false;
    }

    // READ - Todas
    public List<Categoria> obtenerTodas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM Item.Tb_Categorias ORDER BY Nombre_Categoria";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categorias.add(mapearCategoria(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
        }

        return categorias;
    }

    // READ - Por ID
    public Categoria obtenerPorId(int id) {
        String sql = "SELECT * FROM Item.Tb_Categorias WHERE Id_Categoria = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearCategoria(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener categoría: " + e.getMessage());
        }

        return null;
    }

    // UPDATE
    public boolean actualizar(Categoria categoria) {
        String sql = "UPDATE Item.Tb_Categorias SET Nombre_Categoria = ?, Descripcion_Categoria = ? WHERE Id_Categoria = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categoria.getNombreCategoria());
            pstmt.setString(2, categoria.getDescripcionCategoria());
            pstmt.setInt(3, categoria.getIdCategoria());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar categoría: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminar(int id) {
        String sql = "DELETE FROM Item.Tb_Categorias WHERE Id_Categoria = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }

    // Mapear ResultSet a Categoria
    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setIdCategoria(rs.getInt("Id_Categoria"));
        c.setNombreCategoria(rs.getString("Nombre_Categoria"));
        c.setDescripcionCategoria(rs.getString("Descripcion_Categoria"));
        return c;
    }
}