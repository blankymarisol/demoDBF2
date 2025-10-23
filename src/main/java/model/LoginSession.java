package model;

/**
 * Clase Singleton para gestionar la sesión del usuario autenticado
 */
public class LoginSession {
    private static LoginSession instance;
    private Usuario usuarioActual;

    private LoginSession() {}

    public static LoginSession getInstance() {
        if (instance == null) {
            instance = new LoginSession();
        }
        return instance;
    }

    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
        System.out.println("✓ Sesión iniciada para: " + usuario.getNombreUsuario() +
                " (" + usuario.getRolUsuario() + ")");
    }

    public void cerrarSesion() {
        System.out.println("✓ Sesión cerrada para: " +
                (usuarioActual != null ? usuarioActual.getNombreUsuario() : "N/A"));
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean isLoggedIn() {
        return usuarioActual != null;
    }

    public String getRolActual() {
        return usuarioActual != null ? usuarioActual.getRolUsuario() : null;
    }

    // Métodos de verificación de permisos por rol
    public boolean esPatron() {
        return "PATRON".equalsIgnoreCase(getRolActual());
    }

    public boolean esGerente() {
        return "GERENTE".equalsIgnoreCase(getRolActual());
    }

    public boolean esBodeguero() {
        return "BODEGUERO".equalsIgnoreCase(getRolActual());
    }

    public boolean esEmpleado() {
        return "EMPLEADO".equalsIgnoreCase(getRolActual());
    }

    public boolean esAdministrador() {
        return "ADMINISTRADOR".equalsIgnoreCase(getRolActual());
    }

    public boolean esSupervisor() {
        return "SUPERVISOR".equalsIgnoreCase(getRolActual());
    }

    public boolean esVendedor() {
        return "VENDEDOR".equalsIgnoreCase(getRolActual());
    }

    // Permisos específicos basados en roles
    public boolean puedeVerProductos() {
        return true; // Todos pueden ver
    }

    public boolean puedeAgregarProductos() {
        return esPatron() || esGerente() || esAdministrador();
    }

    public boolean puedeEditarProductos() {
        return esPatron() || esGerente() || esAdministrador();
    }

    public boolean puedeEliminarProductos() {
        return esPatron() || esGerente() || esAdministrador();
    }

    public boolean puedeVerInventarios() {
        return true; // Todos pueden ver
    }

    public boolean puedeGestionarInventarios() {
        return esPatron() || esGerente() || esBodeguero() || esAdministrador() || esSupervisor();
    }

    public boolean puedeVerCompras() {
        return true; // Todos pueden ver
    }

    public boolean puedeGestionarCompras() {
        return esPatron() || esGerente() || esBodeguero() || esAdministrador() || esSupervisor();
    }

    public boolean puedeVerProveedores() {
        return true; // Todos pueden ver
    }

    public boolean puedeGestionarProveedores() {
        return esPatron() || esGerente() || esAdministrador();
    }

    public boolean puedeVerUsuarios() {
        return esPatron() || esGerente() || esAdministrador();
    }

    public boolean puedeGestionarUsuarios() {
        return esPatron() || esAdministrador(); // Solo PATRON y ADMINISTRADOR
    }

    public boolean puedeVerBodegas() {
        return true; // Todos pueden ver
    }

    public boolean puedeGestionarBodegas() {
        return esPatron() || esGerente() || esAdministrador();
    }

    public boolean puedeVerCategorias() {
        return true; // Todos pueden ver
    }

    public boolean puedeGestionarCategorias() {
        return esPatron() || esGerente() || esAdministrador();
    }

    public boolean puedeExportarPDF() {
        return true; // Todos pueden exportar reportes
    }

    public boolean puedeVerEstadisticas() {
        return true; // Todos pueden ver estadísticas
    }
}