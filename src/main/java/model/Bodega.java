// Clase modelo Bodega - CORREGIDO
package model;

public class Bodega {
    private int idBodega;
    private String nombreBodega;
    private String ubicacionBodega;
    private String descripcionBodega;
    private String telefonoBodega;
    private int capacidadBodega; // CORREGIDO: Ahora es int en lugar de String
    private int mpioId;
    // ELIMINADO: private int deptoId; (Violaba 3FN - se obtiene a través de Mpio_Id)

    public Bodega() {}

    public Bodega(int idBodega, String nombreBodega, String ubicacionBodega,
                  String descripcionBodega, String telefonoBodega, int capacidadBodega,
                  int mpioId) {
        this.idBodega = idBodega;
        this.nombreBodega = nombreBodega;
        this.ubicacionBodega = ubicacionBodega;
        this.descripcionBodega = descripcionBodega;
        this.telefonoBodega = telefonoBodega;
        this.capacidadBodega = capacidadBodega;
        this.mpioId = mpioId;
    }

    // Getters y Setters
    public int getIdBodega() { return idBodega; }
    public void setIdBodega(int idBodega) { this.idBodega = idBodega; }

    public String getNombreBodega() { return nombreBodega; }
    public void setNombreBodega(String nombreBodega) { this.nombreBodega = nombreBodega; }

    public String getUbicacionBodega() { return ubicacionBodega; }
    public void setUbicacionBodega(String ubicacionBodega) { this.ubicacionBodega = ubicacionBodega; }

    public String getDescripcionBodega() { return descripcionBodega; }
    public void setDescripcionBodega(String descripcionBodega) { this.descripcionBodega = descripcionBodega; }

    public String getTelefonoBodega() { return telefonoBodega; }
    public void setTelefonoBodega(String telefonoBodega) { this.telefonoBodega = telefonoBodega; }

    public int getCapacidadBodega() { return capacidadBodega; }
    public void setCapacidadBodega(int capacidadBodega) { this.capacidadBodega = capacidadBodega; }

    public int getMpioId() { return mpioId; }
    public void setMpioId(int mpioId) { this.mpioId = mpioId; }

    @Override
    public String toString() {
        return "Bodega{" +
                "idBodega=" + idBodega +
                ", nombreBodega='" + nombreBodega + '\'' +
                ", ubicacionBodega='" + ubicacionBodega + '\'' +
                ", descripcionBodega='" + descripcionBodega + '\'' +
                ", capacidadBodega=" + capacidadBodega +
                ", mpioId=" + mpioId +
                '}';
    }
}