package Controller;

import config.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Label lblEstado;
    @FXML private Label lblConexion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        verificarConexion();
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

    @FXML
    private void abrirProductos() {
        cargarVista("/org/example/demo/ProductoView.fxml", "Gestión de Productos");
    }

    @FXML
    private void abrirProveedores() {
        cargarVista("/org/example/demo/ProveedorView.fxml", "Gestión de Proveedores");
    }

    @FXML
    private void abrirUsuarios() {
        cargarVista("/org/example/demo/UsuarioView.fxml", "Gestión de Usuarios");
    }

    @FXML
    private void abrirInventarios() {
        cargarVista("/org/example/demo/InventarioView.fxml", "Gestión de Inventarios");
    }

    @FXML
    private void abrirCompras() {
        cargarVista("/org/example/demo/CompraView.fxml", "Gestión de Compras");
    }

    @FXML
    private void abrirBodegas() {
        cargarVista("/org/example/demo/BodegaView.fxml", "Gestión de Bodegas");
    }

    @FXML
    private void abrirCategorias() {
        cargarVista("/org/example/demo/CategoriaView.fxml", "Gestión de Categorías");
    }

    @FXML
    private void abrirTipoProductos() {
        cargarVista("/org/example/demo/TipoProductoView.fxml", "Gestión de Tipos de Producto");
    }

    @FXML
    private void abrirUnidadMedidas() {
        cargarVista("/org/example/demo/UnidadMedidaView.fxml", "Gestión de Unidades de Medida");
    }

    private void cargarVista(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent vista = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(vista);
            lblEstado.setText(titulo);
        } catch (IOException e) {
            lblEstado.setText("Error al cargar la vista: " + e.getMessage());
            e.printStackTrace();
        }
    }
}