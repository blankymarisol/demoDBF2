// Clase modelo Proveedor
package model;

public class Proveedor {
    private int idProveedor;
    private String nombreProveedor;
    private String telefonoProveedor;
    private String correoProveedor;
    private String direccionProveedor;
    private String nitProveedor;

    public Proveedor() {}

    public Proveedor(int idProveedor, String nombreProveedor, String telefonoProveedor,
                     String correoProveedor, String direccionProveedor, String nitProveedor) {
        this.idProveedor = idProveedor;
        this.nombreProveedor = nombreProveedor;
        this.telefonoProveedor = telefonoProveedor;
        this.correoProveedor = correoProveedor;
        this.direccionProveedor = direccionProveedor;
        this.nitProveedor = nitProveedor;
    }

    // Getters y Setters
    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    public String getTelefonoProveedor() { return telefonoProveedor; }
    public void setTelefonoProveedor(String telefonoProveedor) { this.telefonoProveedor = telefonoProveedor; }

    public String getCorreoProveedor() { return correoProveedor; }
    public void setCorreoProveedor(String correoProveedor) { this.correoProveedor = correoProveedor; }

    public String getDireccionProveedor() { return direccionProveedor; }
    public void setDireccionProveedor(String direccionProveedor) { this.direccionProveedor = direccionProveedor; }

    public String getNitProveedor() { return nitProveedor; }
    public void setNitProveedor(String nitProveedor) { this.nitProveedor = nitProveedor; }

    @Override
    public String toString() {
        return "Proveedor{" +
                "idProveedor=" + idProveedor +
                ", nombreProveedor='" + nombreProveedor + '\'' +
                ", telefonoProveedor='" + telefonoProveedor + '\'' +
                ", correoProveedor='" + correoProveedor + '\'' +
                ", nitProveedor='" + nitProveedor + '\'' +
                '}';
    }
}