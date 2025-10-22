package Controller;

import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

public class InventarioController implements Initializable {

    @FXML private TextField txtCantidad, txtPrecioVenta;
    @FXML private ComboBox<Bodega> cmbBodega, cmbFiltroBodega;
    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private TableView<Inventario> tblInventarios;
    @FXML private TableColumn<Inventario, Integer> colId, colCantidad, colBodega, colProducto;
    @FXML private TableColumn<Inventario, BigDecimal> colPrecioVenta;
    @FXML private TableColumn<Inventario, Timestamp> colFecha;
    @FXML private Label lblMensaje;

    private InventarioDAO inventarioDAO = new InventarioDAO();
    private BodegaDAO bodegaDAO = new BodegaDAO();
    private ProductoDAO productoDAO = new ProductoDAO();
    private Inventario inventarioSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarComboBoxes();
        cargarInventarios();

        tblInventarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                inventarioSeleccionado = newSelection;
                llenarCampos(newSelection);
            }
        });
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idInventario"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadInventario"));
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVentaInventario"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaActualizacionInventario"));
        colBodega.setCellValueFactory(new PropertyValueFactory<>("idBodega"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
    }

    private void cargarComboBoxes() {
        // Bodegas
        List<Bodega> bodegas = bodegaDAO.obtenerTodas();
        cmbBodega.setItems(FXCollections.observableArrayList(bodegas));
        cmbFiltroBodega.setItems(FXCollections.observableArrayList(bodegas));

        cmbBodega.setConverter(new javafx.util.StringConverter<Bodega>() {
            @Override
            public String toString(Bodega b) {
                return b != null ? b.getNombreBodega() : "";
            }
            @Override
            public Bodega fromString(String string) {
                return null;
            }
        });

        cmbFiltroBodega.setConverter(new javafx.util.StringConverter<Bodega>() {
            @Override
            public String toString(Bodega b) {
                return b != null ? b.getNombreBodega() : "";
            }
            @Override
            public Bodega fromString(String string) {
                return null;
            }
        });

        // Productos
        List<Producto> productos = productoDAO.obtenerTodos();
        cmbProducto.setItems(FXCollections.observableArrayList(productos));
        cmbProducto.setConverter(new javafx.util.StringConverter<Producto>() {
            @Override
            public String toString(Producto p) {
                return p != null ? p.getNombreProducto() + " (" + p.getSkuProducto() + ")" : "";
            }
            @Override
            public Producto fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void cargarInventarios() {
        List<Inventario> inventarios = inventarioDAO.obtenerTodos();
        ObservableList<Inventario> data = FXCollections.observableArrayList(inventarios);
        tblInventarios.setItems(data);
        lblMensaje.setText("Inventarios cargados: " + inventarios.size());
    }

    @FXML
    private void filtrarPorBodega() {
        Bodega bodega = cmbFiltroBodega.getValue();
        if (bodega == null) {
            cargarInventarios();
        } else {
            List<Inventario> inventarios = inventarioDAO.obtenerPorBodega(bodega.getIdBodega());
            ObservableList<Inventario> data = FXCollections.observableArrayList(inventarios);
            tblInventarios.setItems(data);
            lblMensaje.setText("Inventarios encontrados: " + inventarios.size());
        }
    }

    @FXML
    private void mostrarStockBajo() {
        TextInputDialog dialog = new TextInputDialog("20");
        dialog.setTitle("Stock Bajo");
        dialog.setHeaderText("Filtrar productos con stock bajo");
        dialog.setContentText("Cantidad mínima:");

        dialog.showAndWait().ifPresent(cantidad -> {
            try {
                int min = Integer.parseInt(cantidad);
                List<Inventario> inventarios = inventarioDAO.obtenerStockBajo(min);
                ObservableList<Inventario> data = FXCollections.observableArrayList(inventarios);
                tblInventarios.setItems(data);
                lblMensaje.setText("Productos con stock menor a " + min + ": " + inventarios.size());
            } catch (NumberFormatException e) {
                lblMensaje.setText("✗ Ingrese un número válido");
                lblMensaje.setStyle("-fx-text-fill: red;");
            }
        });
    }

    @FXML
    private void nuevoInventario() {
        limpiarCampos();
        inventarioSeleccionado = null;
        lblMensaje.setText("Nuevo inventario - complete los campos");
    }

    @FXML
    private void guardarInventario() {
        if (!validarCampos()) return;

        Inventario inventario = new Inventario();
        inventario.setCantidadInventario(Integer.parseInt(txtCantidad.getText().trim()));
        inventario.setPrecioVentaInventario(new BigDecimal(txtPrecioVenta.getText().trim()));
        inventario.setIdBodega(cmbBodega.getValue().getIdBodega());
        inventario.setIdProducto(cmbProducto.getValue().getIdProducto());

        if (inventarioDAO.insertar(inventario)) {
            lblMensaje.setText("✓ Inventario guardado exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargarInventarios();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al guardar el inventario");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void actualizarInventario() {
        if (inventarioSeleccionado == null) {
            lblMensaje.setText("✗ Seleccione un inventario de la tabla");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!validarCampos()) return;

        inventarioSeleccionado.setCantidadInventario(Integer.parseInt(txtCantidad.getText().trim()));
        inventarioSeleccionado.setPrecioVentaInventario(new BigDecimal(txtPrecioVenta.getText().trim()));
        inventarioSeleccionado.setIdBodega(cmbBodega.getValue().getIdBodega());
        inventarioSeleccionado.setIdProducto(cmbProducto.getValue().getIdProducto());

        if (inventarioDAO.actualizar(inventarioSeleccionado)) {
            lblMensaje.setText("✓ Inventario actualizado exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargarInventarios();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al actualizar el inventario");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void eliminarInventario() {
        if (inventarioSeleccionado == null) {
            lblMensaje.setText("✗ Seleccione un inventario de la tabla");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este inventario?");
        alert.setContentText("ID: " + inventarioSeleccionado.getIdInventario());

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (inventarioDAO.eliminar(inventarioSeleccionado.getIdInventario())) {
                lblMensaje.setText("✓ Inventario eliminado");
                lblMensaje.setStyle("-fx-text-fill: green;");
                cargarInventarios();
                limpiarCampos();
            } else {
                lblMensaje.setText("✗ Error al eliminar el inventario");
                lblMensaje.setStyle("-fx-text-fill: red;");
            }
        }
    }

    @FXML
    private void limpiarCampos() {
        txtCantidad.clear();
        txtPrecioVenta.clear();
        cmbBodega.setValue(null);
        cmbProducto.setValue(null);
        inventarioSeleccionado = null;
        tblInventarios.getSelectionModel().clearSelection();
        lblMensaje.setText("");
    }

    private void llenarCampos(Inventario inventario) {
        txtCantidad.setText(String.valueOf(inventario.getCantidadInventario()));
        txtPrecioVenta.setText(inventario.getPrecioVentaInventario().toString());

        for (Bodega b : cmbBodega.getItems()) {
            if (b.getIdBodega() == inventario.getIdBodega()) {
                cmbBodega.setValue(b);
                break;
            }
        }

        for (Producto p : cmbProducto.getItems()) {
            if (p.getIdProducto() == inventario.getIdProducto()) {
                cmbProducto.setValue(p);
                break;
            }
        }
    }

    private boolean validarCampos() {
        if (txtCantidad.getText().trim().isEmpty() || txtPrecioVenta.getText().trim().isEmpty() ||
                cmbBodega.getValue() == null || cmbProducto.getValue() == null) {

            lblMensaje.setText("✗ Todos los campos son obligatorios");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return false;
        }

        try {
            Integer.parseInt(txtCantidad.getText().trim());
            new BigDecimal(txtPrecioVenta.getText().trim());
        } catch (NumberFormatException e) {
            lblMensaje.setText("✗ Verifique los valores numéricos");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return false;
        }

        return true;
    }
}