// ============================================
// MODELOS
// ============================================

// UnidadMedida.java
package model;

public class UnidadMedida {
    private int idUnidadMedida;
    private String nombreUnidadMedida;
    private String abreviaturaUnidadMedida;

    public UnidadMedida() {}

    public UnidadMedida(int idUnidadMedida, String nombreUnidadMedida, String abreviaturaUnidadMedida) {
        this.idUnidadMedida = idUnidadMedida;
        this.nombreUnidadMedida = nombreUnidadMedida;
        this.abreviaturaUnidadMedida = abreviaturaUnidadMedida;
    }

    public int getIdUnidadMedida() { return idUnidadMedida; }
    public void setIdUnidadMedida(int idUnidadMedida) { this.idUnidadMedida = idUnidadMedida; }

    public String getNombreUnidadMedida() { return nombreUnidadMedida; }
    public void setNombreUnidadMedida(String nombreUnidadMedida) { this.nombreUnidadMedida = nombreUnidadMedida; }

    public String getAbreviaturaUnidadMedida() { return abreviaturaUnidadMedida; }
    public void setAbreviaturaUnidadMedida(String abreviaturaUnidadMedida) {
        this.abreviaturaUnidadMedida = abreviaturaUnidadMedida;
    }

    @Override
    public String toString() {
        return "UnidadMedida{" +
                "idUnidadMedida=" + idUnidadMedida +
                ", nombreUnidadMedida='" + nombreUnidadMedida + '\'' +
                ", abreviaturaUnidadMedida='" + abreviaturaUnidadMedida + '\'' +
                '}';
    }
}