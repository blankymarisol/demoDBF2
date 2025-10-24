package Controller;

import config.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.LoginSession;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Label lblEstado;
    @FXML private Label lblConexion;
    @FXML private Label lblUsuarioActual;
    @FXML private Label lblRolActual;
    @FXML private ImageView imgKirby;

    // Botones del menú
    @FXML private Button btnProductos;
    @FXML private Button btnProveedores;
    @FXML private Button btnUsuarios;
    @FXML private Button btnInventarios;
    @FXML private Button btnCompras;
    @FXML private Button btnBodegas;
    @FXML private Button btnCategorias;
    @FXML private Button btnTipoProductos;
    @FXML private Button btnUnidadMedidas;
    @FXML private Button btnCerrarSesion;

    private LoginSession session = LoginSession.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        verificarConexion();
        configurarSesion();
        configurarPermisos();
        cargarImagenKirby();
    }

    /**
     * Carga la imagen de Kirby desde los recursos
     */
    private void cargarImagenKirby() {
        try {
            // Ruta relativa desde src/main/resources
            String imagePath = "/org/example/demo/images/kirby.png";
            InputStream imageStream = getClass().getResourceAsStream(imagePath);

            if (imageStream != null) {
                Image image = new Image(imageStream);
                imgKirby.setImage(image);
                System.out.println("✓ Imagen de Kirby cargada exitosamente");
            } else {
                System.err.println("✗ No se encontró la imagen de Kirby en: " + imagePath);
                // Mostrar un mensaje en la interfaz si la imagen no se carga
                lblEstado.setText("⚠ Imagen no encontrada - Verifica que kirby.png esté en src/main/resources/org/example/demo/images/");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar imagen de Kirby: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void verificarConexion() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                lblConexion.setText("● Conectado");
                lblConexion.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                lblEstado.setText("Conexión a base de datos exitosa");
            } else {
                lblConexion.setText("● Desconectado");
                lblConexion.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                lblEstado.setText("Error de conexión a la base de datos");
            }
        } catch (Exception e) {
            lblConexion.setText("● Error");
            lblConexion.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            lblEstado.setText("Error: " + e.getMessage());
        }
    }

    private void configurarSesion() {
        if (session.isLoggedIn()) {
            lblUsuarioActual.setText("Usuario: " + session.getUsuarioActual().getNombreUsuario());
            lblRolActual.setText("Rol: " + session.getRolActual());

            // Aplicar color según el rol
            String colorRol = switch (session.getRolActual().toUpperCase()) {
                case "PATRON" -> "#e74c3c"; // Rojo
                case "GERENTE" -> "#3498db"; // Azul
                case "BODEGUERO" -> "#f39c12"; // Naranja
                case "EMPLEADO" -> "#95a5a6"; // Gris
                case "ADMINISTRADOR" -> "#9b59b6"; // Púrpura
                case "SUPERVISOR" -> "#16a085"; // Verde azulado
                case "VENDEDOR" -> "#27ae60"; // Verde
                default -> "#34495e";
            };

            lblRolActual.setStyle("-fx-text-fill: " + colorRol + "; -fx-font-weight: bold;");
        }
    }

    private void configurarPermisos() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("    CONFIGURANDO PERMISOS DE ACCESO");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Usuario: " + session.getUsuarioActual().getNombreUsuario());
        System.out.println("Rol: " + session.getRolActual());
        System.out.println("───────────────────────────────────────");

        // PRODUCTOS
        boolean puedeVerProductos = session.puedeVerProductos();
        btnProductos.setDisable(!puedeVerProductos);
        System.out.println("Productos: " + (puedeVerProductos ? "✓ ACCESO" : "✗ BLOQUEADO"));

        // PROVEEDORES
        boolean puedeVerProveedores = session.puedeVerProveedores();
        btnProveedores.setDisable(!puedeVerProveedores);
        System.out.println("Proveedores: " + (puedeVerProveedores ? "✓ ACCESO" : "✗ BLOQUEADO"));

        // USUARIOS - Solo PATRON y GERENTE/ADMINISTRADOR
        boolean puedeVerUsuarios = session.puedeVerUsuarios();
        btnUsuarios.setVisible(puedeVerUsuarios);
        btnUsuarios.setManaged(puedeVerUsuarios);
        System.out.println("Usuarios: " + (puedeVerUsuarios ? "✓ ACCESO" : "✗ BLOQUEADO"));

        // INVENTARIOS
        boolean puedeVerInventarios = session.puedeVerInventarios();
        btnInventarios.setDisable(!puedeVerInventarios);
        System.out.println("Inventarios: " + (puedeVerInventarios ? "✓ ACCESO" : "✗ BLOQUEADO"));

        // COMPRAS
        boolean puedeVerCompras = session.puedeVerCompras();
        btnCompras.setDisable(!puedeVerCompras);
        System.out.println("Compras: " + (puedeVerCompras ? "✓ ACCESO" : "✗ BLOQUEADO"));

        // BODEGAS
        boolean puedeVerBodegas = session.puedeVerBodegas();
        btnBodegas.setDisable(!puedeVerBodegas);
        System.out.println("Bodegas: " + (puedeVerBodegas ? "✓ ACCESO" : "✗ BLOQUEADO"));

        // CATEGORÍAS
        boolean puedeVerCategorias = session.puedeVerCategorias();
        btnCategorias.setDisable(!puedeVerCategorias);
        System.out.println("Categorías: " + (puedeVerCategorias ? "✓ ACCESO" : "✗ BLOQUEADO"));

        // TIPOS DE PRODUCTO
        btnTipoProductos.setDisable(!puedeVerCategorias);
        System.out.println("Tipos Producto: " + (puedeVerCategorias ? "✓ ACCESO" : "✗ BLOQUEADO"));

        // UNIDADES DE MEDIDA
        btnUnidadMedidas.setDisable(!puedeVerCategorias);
        System.out.println("Unidades Medida: " + (puedeVerCategorias ? "✓ ACCESO" : "✗ BLOQUEADO"));

        System.out.println("═══════════════════════════════════════\n");

        // Mostrar mensaje de bienvenida personalizado
        String mensajeBienvenida = obtenerMensajeBienvenida();
        lblEstado.setText(mensajeBienvenida);
    }

    private String obtenerMensajeBienvenida() {
        String rol = session.getRolActual().toUpperCase();
        return switch (rol) {
            case "PATRON" -> "Bienvenido Patrón - Tienes control total del sistema";
            case "GERENTE", "ADMINISTRADOR" -> "Bienvenido - Gestión operativa completa disponible";
            case "BODEGUERO", "SUPERVISOR" -> "Bienvenido - Operaciones de inventario disponibles";
            case "EMPLEADO", "VENDEDOR" -> "Bienvenido - Modo consulta de reportes";
            default -> "Bienvenido al sistema";
        };
    }

    @FXML
    private void abrirProductos() {
        if (!session.puedeVerProductos()) {
            mostrarErrorPermiso("ver productos");
            return;
        }
        cargarVista("/org/example/demo/ProductoView.fxml", "Gestión de Productos");
    }

    @FXML
    private void abrirProveedores() {
        if (!session.puedeVerProveedores()) {
            mostrarErrorPermiso("ver proveedores");
            return;
        }
        cargarVista("/org/example/demo/ProveedorView.fxml", "Gestión de Proveedores");
    }

    @FXML
    private void abrirUsuarios() {
        if (!session.puedeVerUsuarios()) {
            mostrarErrorPermiso("gestionar usuarios");
            return;
        }
        cargarVista("/org/example/demo/UsuarioView.fxml", "Gestión de Usuarios");
    }

    @FXML
    private void abrirInventarios() {
        if (!session.puedeVerInventarios()) {
            mostrarErrorPermiso("ver inventarios");
            return;
        }
        cargarVista("/org/example/demo/InventarioView.fxml", "Gestión de Inventarios");
    }

    @FXML
    private void abrirCompras() {
        if (!session.puedeVerCompras()) {
            mostrarErrorPermiso("ver compras");
            return;
        }
        cargarVista("/org/example/demo/CompraView.fxml", "Gestión de Compras");
    }

    @FXML
    private void abrirBodegas() {
        if (!session.puedeVerBodegas()) {
            mostrarErrorPermiso("ver bodegas");
            return;
        }
        cargarVista("/org/example/demo/BodegaView.fxml", "Gestión de Bodegas");
    }

    @FXML
    private void abrirCategorias() {
        if (!session.puedeVerCategorias()) {
            mostrarErrorPermiso("gestionar categorías");
            return;
        }
        cargarVista("/org/example/demo/CategoriaView.fxml", "Gestión de Categorías");
    }

    @FXML
    private void abrirTipoProductos() {
        if (!session.puedeVerCategorias()) {
            mostrarErrorPermiso("gestionar tipos de producto");
            return;
        }
        cargarVista("/org/example/demo/TipoProductoView.fxml", "Gestión de Tipos de Producto");
    }

    @FXML
    private void abrirUnidadMedidas() {
        if (!session.puedeVerCategorias()) {
            mostrarErrorPermiso("gestionar unidades de medida");
            return;
        }
        cargarVista("/org/example/demo/UnidadMedidaView.fxml", "Gestión de Unidades de Medida");
    }

    @FXML
    private void cerrarSesion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar Sesión");
        alert.setHeaderText("¿Está seguro que desea cerrar sesión?");
        alert.setContentText("Será redirigido a la pantalla de inicio de sesión");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                // Cerrar sesión
                session.cerrarSesion();

                // Cerrar conexión a BD
                DatabaseConnection.closeConnection();

                // Cargar ventana de login
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/LoginView.fxml"));
                Parent root = loader.load();

                Stage loginStage = new Stage();
                loginStage.setTitle("Login - DevSolutionsF");
                loginStage.setScene(new Scene(root, 500, 700));
                loginStage.setResizable(false);
                loginStage.show();

                // Cerrar ventana actual
                Stage currentStage = (Stage) btnCerrarSesion.getScene().getWindow();
                currentStage.close();

            } catch (IOException e) {
                mostrarError("Error al cerrar sesión: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void cargarVista(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent vista = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(vista);
            lblEstado.setText(titulo + " - " + session.getRolActual());
        } catch (IOException e) {
            lblEstado.setText("Error al cargar la vista: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarErrorPermiso(String accion) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Permiso Denegado");
        alert.setHeaderText("No tiene permisos suficientes");
        alert.setContentText("Su rol (" + session.getRolActual() + ") no tiene acceso para " + accion + ".");
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Ha ocurrido un error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}