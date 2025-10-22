// Clase modelo Usuario
package model;

public class Usuario {
    private int idUsuario;
    private String nombreUsuario;
    private String rolUsuario;
    private String correoUsuario;
    private String contrasena;

    public Usuario() {}

    public Usuario(int idUsuario, String nombreUsuario, String rolUsuario,
                   String correoUsuario, String contrasena) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.rolUsuario = rolUsuario;
        this.correoUsuario = correoUsuario;
        this.contrasena = contrasena;
    }

    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getRolUsuario() { return rolUsuario; }
    public void setRolUsuario(String rolUsuario) { this.rolUsuario = rolUsuario; }

    public String getCorreoUsuario() { return correoUsuario; }
    public void setCorreoUsuario(String correoUsuario) { this.correoUsuario = correoUsuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", rolUsuario='" + rolUsuario + '\'' +
                ", correoUsuario='" + correoUsuario + '\'' +
                '}';
    }
}