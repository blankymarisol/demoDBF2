package dao;

import config.DatabaseConnection;
import model.Bodega;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BodegaDAO {

    // CREATE - CORREGIDO: Sin Depto_Id
    public boolean insertar(Bodega bodega) {
        String sql = "INSERT INTO Place.Tb_Bodegas (Nombre_Bodega, Ubicacion_Bodega, " +
                "Descripcion_Bodega, Telefono_Bodega, Capacidad_Bodega, Mpio_Id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, bodega.getNombreBodega());
            pstmt.setString(2, bodega.getUbicacionBodega());
            pstmt.setString(3, bodega.getDescripcionBodega());
            pstmt.setString(4, bodega.getTelefonoBodega());
            pstmt.setInt(5, bodega.getCapacidadBodega()); // CORREGIDO: Ahora es setInt
            pstmt.setInt(6, bodega.getMpioId());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    bodega.setIdBodega(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar bodega: " + e.getMessage());
        }
        return false;
    }

    // READ - Todas
    public List<Bodega> obtenerTodas() {
        List<Bodega> bodegas = new ArrayList<>();
        String sql = "SELECT * FROM Place.Tb_Bodegas ORDER BY Nombre_Bodega";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bodegas.add(mapearBodega(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener bodegas: " + e.getMessage());
        }

        return bodegas;
    }

    // READ - Por ID
    public Bodega obtenerPorId(int id) {
        String sql = "SELECT * FROM Place.Tb_Bodegas WHERE Id_Bodega = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapearBodega(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener bodega: " + e.getMessage());
        }

        return null;
    }

    // READ - Por municipio
    public List<Bodega> obtenerPorMunicipio(int mpioId) {
        List<Bodega> bodegas = new ArrayList<>();
        String sql = "SELECT * FROM Place.Tb_Bodegas WHERE Mpio_Id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mpioId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                bodegas.add(mapearBodega(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener bodegas por municipio: " + e.getMessage());
        }

        return bodegas;
    }

    // READ - Por departamento (CORREGIDO: Ahora usa JOIN con municipios)
    public List<Bodega> obtenerPorDepartamento(int deptoId) {
        List<Bodega> bodegas = new ArrayList<>();
        String sql = "SELECT b.* FROM Place.Tb_Bodegas b " +
                "INNER JOIN Place.Tb_Municipios m ON b.Mpio_Id = m.Mpio_Id " +
                "WHERE m.Depto_Id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deptoId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                bodegas.add(mapearBodega(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener bodegas por departamento: " + e.getMessage());
        }

        return bodegas;
    }

    // UPDATE - CORREGIDO: Sin Depto_Id
    public boolean actualizar(Bodega bodega) {
        String sql = "UPDATE Place.Tb_Bodegas SET Nombre_Bodega = ?, Ubicacion_Bodega = ?, " +
                "Descripcion_Bodega = ?, Telefono_Bodega = ?, Capacidad_Bodega = ?, " +
                "Mpio_Id = ? WHERE Id_Bodega = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bodega.getNombreBodega());
            pstmt.setString(2, bodega.getUbicacionBodega());
            pstmt.setString(3, bodega.getDescripcionBodega());
            pstmt.setString(4, bodega.getTelefonoBodega());
            pstmt.setInt(5, bodega.getCapacidadBodega()); // CORREGIDO: Ahora es setInt
            pstmt.setInt(6, bodega.getMpioId());
            pstmt.setInt(7, bodega.getIdBodega());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar bodega: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminar(int id) {
        String sql = "DELETE FROM Place.Tb_Bodegas WHERE Id_Bodega = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar bodega: " + e.getMessage());
            return false;
        }
    }

    // Mapear ResultSet a Bodega - CORREGIDO
    private Bodega mapearBodega(ResultSet rs) throws SQLException {
        Bodega b = new Bodega();
        b.setIdBodega(rs.getInt("Id_Bodega"));
        b.setNombreBodega(rs.getString("Nombre_Bodega"));
        b.setUbicacionBodega(rs.getString("Ubicacion_Bodega"));
        b.setDescripcionBodega(rs.getString("Descripcion_Bodega"));
        b.setTelefonoBodega(rs.getString("Telefono_Bodega"));
        b.setCapacidadBodega(rs.getInt("Capacidad_Bodega")); // CORREGIDO: Ahora es getInt
        b.setMpioId(rs.getInt("Mpio_Id"));
        // ELIMINADO: b.setDeptoId(rs.getInt("Depto_Id"));
        return b;
    }
}