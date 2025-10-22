// TipoProducto.java
package model;

public class TipoProducto {
    private int idTipoProducto;
    private String nombreTipoProducto;

    public TipoProducto() {}

    public TipoProducto(int idTipoProducto, String nombreTipoProducto) {
        this.idTipoProducto = idTipoProducto;
        this.nombreTipoProducto = nombreTipoProducto;
    }

    public int getIdTipoProducto() { return idTipoProducto; }
    public void setIdTipoProducto(int idTipoProducto) { this.idTipoProducto = idTipoProducto; }

    public String getNombreTipoProducto() { return nombreTipoProducto; }
    public void setNombreTipoProducto(String nombreTipoProducto) { this.nombreTipoProducto = nombreTipoProducto; }

    @Override
    public String toString() {
        return "TipoProducto{" +
                "idTipoProducto=" + idTipoProducto +
                ", nombreTipoProducto='" + nombreTipoProducto + '\'' +
                '}';
    }
}