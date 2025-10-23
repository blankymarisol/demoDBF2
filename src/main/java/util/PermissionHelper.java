package util;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import model.LoginSession;

/**
 * Clase utilitaria para gestionar permisos en los controladores
 */
public class PermissionHelper {

    private static final LoginSession session = LoginSession.getInstance();

    /**
     * Configura la visibilidad y estado de botones de acción según permisos
     */
    public static void configurarBotonesProductos(Button btnGuardar, Button btnActualizar, Button btnEliminar) {
        btnGuardar.setDisable(!session.puedeAgregarProductos());
        btnActualizar.setDisable(!session.puedeEditarProductos());
        btnEliminar.setDisable(!session.puedeEliminarProductos());
    }

    public static void configurarBotonesInventarios(Button btnGuardar, Button btnActualizar, Button btnEliminar) {
        boolean puedeGestionar = session.puedeGestionarInventarios();
        btnGuardar.setDisable(!puedeGestionar);
        btnActualizar.setDisable(!puedeGestionar);
        btnEliminar.setDisable(!puedeGestionar);
    }

    public static void configurarBotonesCompras(Button btnGuardar, Button btnEliminar) {
        boolean puedeGestionar = session.puedeGestionarCompras();
        btnGuardar.setDisable(!puedeGestionar);
        if (btnEliminar != null) {
            btnEliminar.setDisable(!puedeGestionar);
        }
    }

    public static void configurarBotonesProveedores(Button btnGuardar, Button btnActualizar, Button btnEliminar) {
        boolean puedeGestionar = session.puedeGestionarProveedores();
        btnGuardar.setDisable(!puedeGestionar);
        btnActualizar.setDisable(!puedeGestionar);
        btnEliminar.setDisable(!puedeGestionar);
    }

    public static void configurarBotonesUsuarios(Button btnGuardar, Button btnActualizar, Button btnEliminar) {
        boolean puedeGestionar = session.puedeGestionarUsuarios();
        btnGuardar.setDisable(!puedeGestionar);
        btnActualizar.setDisable(!puedeGestionar);
        btnEliminar.setDisable(!puedeGestionar);
    }

    public static void configurarBotonesBodegas(Button btnGuardar, Button btnActualizar, Button btnEliminar) {
        boolean puedeGestionar = session.puedeGestionarBodegas();
        btnGuardar.setDisable(!puedeGestionar);
        btnActualizar.setDisable(!puedeGestionar);
        btnEliminar.setDisable(!puedeGestionar);
    }

    public static void configurarBotonesCategorias(Button btnGuardar, Button btnActualizar, Button btnEliminar) {
        boolean puedeGestionar = session.puedeGestionarCategorias();
        btnGuardar.setDisable(!puedeGestionar);
        btnActualizar.setDisable(!puedeGestionar);
        btnEliminar.setDisable(!puedeGestionar);
    }

    /**
     * Muestra alerta de permiso denegado
     */
    public static void mostrarErrorPermiso(String accion) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Permiso Denegado");
        alert.setHeaderText("No tiene permisos suficientes");
        alert.setContentText("Su rol (" + session.getRolActual() + ") no tiene permiso para " + accion + ".");
        alert.showAndWait();
    }

    /**
     * Valida si el usuario puede ejecutar una acción
     */
    public static boolean validarPermiso(String tipoAccion, String modulo) {
        boolean tienePermiso = false;

        switch (modulo.toUpperCase()) {
            case "PRODUCTOS":
                tienePermiso = validarPermisoProductos(tipoAccion);
                break;
            case "INVENTARIOS":
                tienePermiso = validarPermisoInventarios(tipoAccion);
                break;
            case "COMPRAS":
                tienePermiso = validarPermisoCompras(tipoAccion);
                break;
            case "PROVEEDORES":
                tienePermiso = validarPermisoProveedores(tipoAccion);
                break;
            case "USUARIOS":
                tienePermiso = validarPermisoUsuarios(tipoAccion);
                break;
            case "BODEGAS":
                tienePermiso = validarPermisoBodegas(tipoAccion);
                break;
            case "CATEGORIAS":
                tienePermiso = validarPermisoCategorias(tipoAccion);
                break;
        }

        if (!tienePermiso) {
            mostrarErrorPermiso(tipoAccion + " en " + modulo);
        }

        return tienePermiso;
    }

    private static boolean validarPermisoProductos(String accion) {
        return switch (accion.toUpperCase()) {
            case "VER", "CONSULTAR" -> session.puedeVerProductos();
            case "AGREGAR", "CREAR" -> session.puedeAgregarProductos();
            case "EDITAR", "ACTUALIZAR" -> session.puedeEditarProductos();
            case "ELIMINAR" -> session.puedeEliminarProductos();
            default -> false;
        };
    }

    private static boolean validarPermisoInventarios(String accion) {
        return switch (accion.toUpperCase()) {
            case "VER", "CONSULTAR" -> session.puedeVerInventarios();
            case "AGREGAR", "CREAR", "EDITAR", "ACTUALIZAR", "ELIMINAR" -> session.puedeGestionarInventarios();
            default -> false;
        };
    }

    private static boolean validarPermisoCompras(String accion) {
        return switch (accion.toUpperCase()) {
            case "VER", "CONSULTAR" -> session.puedeVerCompras();
            case "AGREGAR", "CREAR", "EDITAR", "ACTUALIZAR" -> session.puedeGestionarCompras();
            default -> false;
        };
    }

    private static boolean validarPermisoProveedores(String accion) {
        return switch (accion.toUpperCase()) {
            case "VER", "CONSULTAR" -> session.puedeVerProveedores();
            case "AGREGAR", "CREAR", "EDITAR", "ACTUALIZAR", "ELIMINAR" -> session.puedeGestionarProveedores();
            default -> false;
        };
    }

    private static boolean validarPermisoUsuarios(String accion) {
        return switch (accion.toUpperCase()) {
            case "VER", "CONSULTAR" -> session.puedeVerUsuarios();
            case "AGREGAR", "CREAR", "EDITAR", "ACTUALIZAR", "ELIMINAR" -> session.puedeGestionarUsuarios();
            default -> false;
        };
    }

    private static boolean validarPermisoBodegas(String accion) {
        return switch (accion.toUpperCase()) {
            case "VER", "CONSULTAR" -> session.puedeVerBodegas();
            case "AGREGAR", "CREAR", "EDITAR", "ACTUALIZAR", "ELIMINAR" -> session.puedeGestionarBodegas();
            default -> false;
        };
    }

    private static boolean validarPermisoCategorias(String accion) {
        return switch (accion.toUpperCase()) {
            case "VER", "CONSULTAR" -> session.puedeVerCategorias();
            case "AGREGAR", "CREAR", "EDITAR", "ACTUALIZAR", "ELIMINAR" -> session.puedeGestionarCategorias();
            default -> false;
        };
    }

    /**
     * Obtiene el rol actual del usuario
     */
    public static String getRolActual() {
        return session.getRolActual();
    }

    /**
     * Verifica si hay una sesión activa
     */
    public static boolean haySesionActiva() {
        return session.isLoggedIn();
    }
}