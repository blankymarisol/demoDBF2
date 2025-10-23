package Controller;

import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;
import util.PDFGenerator;
import util.PermissionHelper;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProductoController implements Initializable {

    @FXML private TextField txtSku, txtNombre, txtDescripcion, txtCosto, txtDescuento;
    @FXML private TextField txtImagenURL, txtStock, txtBuscar;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private ComboBox<TipoProducto> cmbTipoProducto;
    @FXML private ComboBox<UnidadMedida> cmbUnidadMedida;
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colSku, colNombre, colDescripcion, colEstado;
    @FXML private TableColumn<Producto, BigDecimal> colCosto, colDescuento;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private Label lblMensaje;

    // Botones
    @FXML private Button btnNuevo, btnGuardar, btnActualizar, btnEliminar, btnLimpiar;

    private ProductoDAO productoDAO = new ProductoDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();
    private TipoProductoDAO tipoProductoDAO = new TipoProductoDAO();
    private UnidadMedidaDAO unidadMedidaDAO = new UnidadMedidaDAO();
    private Producto productoSeleccionado;
    private LoginSession session = LoginSession.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarProductos();
        cargarComboBoxes();

        // ========== APLICAR PERMISOS ==========
        aplicarPermisos();

        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                productoSeleccionado = newSelection;
                llenarCampos(newSelection);
            }
        });
    }

    /**
     * MÉTODO CRÍTICO: Aplica los permisos según el rol del usuario
     */
    private void aplicarPermisos() {
        System.out.println("\n[PRODUCTOS] Aplicando permisos para: " + session.getRolActual());

        // Botones de acción
        btnGuardar.setDisable(!session.puedeAgregarProductos());
        btnActualizar.setDisable(!session.puedeEditarProductos());
        btnEliminar.setDisable(!session.puedeEliminarProductos());

        // Campos de formulario (solo lectura si no puede editar)
        boolean puedeEditar = session.puedeEditarProductos() || session.puedeAgregarProductos();
        txtSku.setEditable(puedeEditar);
        txtNombre.setEditable(puedeEditar);
        txtDescripcion.setEditable(puedeEditar);
        txtCosto.setEditable(puedeEditar);
        txtDescuento.setEditable(puedeEditar);
        txtImagenURL.setEditable(puedeEditar);
        txtStock.setEditable(puedeEditar);
        cmbEstado.setDisable(!puedeEditar);
        cmbCategoria.setDisable(!puedeEditar);
        cmbTipoProducto.setDisable(!puedeEditar);
        cmbUnidadMedida.setDisable(!puedeEditar);

        System.out.println("  - Guardar: " + (btnGuardar.isDisabled() ? "BLOQUEADO" : "PERMITIDO"));
        System.out.println("  - Actualizar: " + (btnActualizar.isDisabled() ? "BLOQUEADO" : "PERMITIDO"));
        System.out.println("  - Eliminar: " + (btnEliminar.isDisabled() ? "BLOQUEADO" : "PERMITIDO"));
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colSku.setCellValueFactory(new PropertyValueFactory<>("skuProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionProducto"));
        colCosto.setCellValueFactory(new PropertyValueFactory<>("costoUnitarioProducto"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuentoProducto"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoProducto"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockProducto"));
    }

    private void cargarComboBoxes() {
        cmbEstado.setItems(FXCollections.observableArrayList("ACTIVO", "INACTIVO"));
        cmbEstado.setValue("ACTIVO");

        List<Categoria> categorias = categoriaDAO.obtenerTodas();
        cmbCategoria.setItems(FXCollections.observableArrayList(categorias));
        cmbCategoria.setConverter(new javafx.util.StringConverter<Categoria>() {
            @Override
            public String toString(Categoria c) {
                return c != null ? c.getNombreCategoria() : "";
            }
            @Override
            public Categoria fromString(String string) {
                return null;
            }
        });

        List<TipoProducto> tipos = tipoProductoDAO.obtenerTodos();
        cmbTipoProducto.setItems(FXCollections.observableArrayList(tipos));
        cmbTipoProducto.setConverter(new javafx.util.StringConverter<TipoProducto>() {
            @Override
            public String toString(TipoProducto t) {
                return t != null ? t.getNombreTipoProducto() : "";
            }
            @Override
            public TipoProducto fromString(String string) {
                return null;
            }
        });

        List<UnidadMedida> unidades = unidadMedidaDAO.obtenerTodas();
        cmbUnidadMedida.setItems(FXCollections.observableArrayList(unidades));
        cmbUnidadMedida.setConverter(new javafx.util.StringConverter<UnidadMedida>() {
            @Override
            public String toString(UnidadMedida u) {
                return u != null ? u.getNombreUnidadMedida() + " (" + u.getAbreviaturaUnidadMedida() + ")" : "";
            }
            @Override
            public UnidadMedida fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void cargarProductos() {
        List<Producto> productos = productoDAO.obtenerTodos();
        ObservableList<Producto> data = FXCollections.observableArrayList(productos);
        tblProductos.setItems(data);
        lblMensaje.setText("Productos cargados: " + productos.size());
    }

    @FXML
    private void buscarProductos() {
        String nombre = txtBuscar.getText().trim();
        if (nombre.isEmpty()) {
            cargarProductos();
        } else {
            List<Producto> productos = productoDAO.buscarPorNombre(nombre);
            ObservableList<Producto> data = FXCollections.observableArrayList(productos);
            tblProductos.setItems(data);
            lblMensaje.setText("Productos encontrados: " + productos.size());
        }
    }

    @FXML
    private void nuevoProducto() {
        limpiarCampos();
        productoSeleccionado = null;
        lblMensaje.setText("Nuevo producto - complete los campos");
    }

    @FXML
    private void guardarProducto() {
        // ========== VALIDAR PERMISO ==========
        if (!session.puedeAgregarProductos()) {
            PermissionHelper.mostrarErrorPermiso("agregar productos");
            return;
        }

        if (!validarCampos()) return;

        Producto producto = new Producto();
        producto.setSkuProducto(txtSku.getText().trim());
        producto.setNombreProducto(txtNombre.getText().trim());
        producto.setDescripcionProducto(txtDescripcion.getText().trim());
        producto.setCostoUnitarioProducto(new BigDecimal(txtCosto.getText().trim()));
        producto.setDescuentoProducto(new BigDecimal(txtDescuento.getText().trim()));
        producto.setImagenURLProducto(txtImagenURL.getText().trim());
        producto.setEstadoProducto(cmbEstado.getValue());
        producto.setStockProducto(Integer.parseInt(txtStock.getText().trim()));
        producto.setIdCategoria(cmbCategoria.getValue().getIdCategoria());
        producto.setIdTipoProducto(cmbTipoProducto.getValue().getIdTipoProducto());
        producto.setIdUnidadMedida(cmbUnidadMedida.getValue().getIdUnidadMedida());

        if (productoDAO.insertar(producto)) {
            lblMensaje.setText("✓ Producto guardado exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargarProductos();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al guardar el producto");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void actualizarProducto() {
        // ========== VALIDAR PERMISO ==========
        if (!session.puedeEditarProductos()) {
            PermissionHelper.mostrarErrorPermiso("actualizar productos");
            return;
        }

        if (productoSeleccionado == null) {
            lblMensaje.setText("✗ Seleccione un producto de la tabla");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!validarCampos()) return;

        productoSeleccionado.setSkuProducto(txtSku.getText().trim());
        productoSeleccionado.setNombreProducto(txtNombre.getText().trim());
        productoSeleccionado.setDescripcionProducto(txtDescripcion.getText().trim());
        productoSeleccionado.setCostoUnitarioProducto(new BigDecimal(txtCosto.getText().trim()));
        productoSeleccionado.setDescuentoProducto(new BigDecimal(txtDescuento.getText().trim()));
        productoSeleccionado.setImagenURLProducto(txtImagenURL.getText().trim());
        productoSeleccionado.setEstadoProducto(cmbEstado.getValue());
        productoSeleccionado.setStockProducto(Integer.parseInt(txtStock.getText().trim()));
        productoSeleccionado.setIdCategoria(cmbCategoria.getValue().getIdCategoria());
        productoSeleccionado.setIdTipoProducto(cmbTipoProducto.getValue().getIdTipoProducto());
        productoSeleccionado.setIdUnidadMedida(cmbUnidadMedida.getValue().getIdUnidadMedida());

        if (productoDAO.actualizar(productoSeleccionado)) {
            lblMensaje.setText("✓ Producto actualizado exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargarProductos();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al actualizar el producto");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void eliminarProducto() {
        // ========== VALIDAR PERMISO ==========
        if (!session.puedeEliminarProductos()) {
            PermissionHelper.mostrarErrorPermiso("eliminar productos");
            return;
        }

        if (productoSeleccionado == null) {
            lblMensaje.setText("✗ Seleccione un producto de la tabla");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este producto?");
        alert.setContentText(productoSeleccionado.getNombreProducto());

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (productoDAO.eliminarLogico(productoSeleccionado.getIdProducto())) {
                lblMensaje.setText("✓ Producto eliminado (desactivado)");
                lblMensaje.setStyle("-fx-text-fill: green;");
                cargarProductos();
                limpiarCampos();
            } else {
                lblMensaje.setText("✗ Error al eliminar el producto");
                lblMensaje.setStyle("-fx-text-fill: red;");
            }
        }
    }

    @FXML
    private void exportarPDF() {
        if (tblProductos.getItems().isEmpty()) {
            lblMensaje.setText("✗ No hay datos para exportar");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        String nombreArchivo = PDFGenerator.generarNombreArchivo("Reporte_Productos");
        File pdf = PDFGenerator.generarPDF(tblProductos, "Reporte de Productos", nombreArchivo);

        if (pdf != null) {
            lblMensaje.setText("✓ PDF generado exitosamente: " + pdf.getName());
            lblMensaje.setStyle("-fx-text-fill: green;");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("PDF Generado");
            alert.setHeaderText("Reporte exportado exitosamente");
            alert.setContentText("Archivo: " + pdf.getAbsolutePath());
            alert.showAndWait();
        } else {
            lblMensaje.setText("✗ Error al generar el PDF");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void limpiarCampos() {
        txtSku.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtCosto.clear();
        txtDescuento.clear();
        txtImagenURL.clear();
        txtStock.clear();
        cmbEstado.setValue("ACTIVO");
        cmbCategoria.setValue(null);
        cmbTipoProducto.setValue(null);
        cmbUnidadMedida.setValue(null);
        productoSeleccionado = null;
        tblProductos.getSelectionModel().clearSelection();
        lblMensaje.setText("");
    }

    private void llenarCampos(Producto producto) {
        txtSku.setText(producto.getSkuProducto());
        txtNombre.setText(producto.getNombreProducto());
        txtDescripcion.setText(producto.getDescripcionProducto());
        txtCosto.setText(producto.getCostoUnitarioProducto().toString());
        txtDescuento.setText(producto.getDescuentoProducto().toString());
        txtImagenURL.setText(producto.getImagenURLProducto());
        txtStock.setText(String.valueOf(producto.getStockProducto()));
        cmbEstado.setValue(producto.getEstadoProducto());

        for (Categoria c : cmbCategoria.getItems()) {
            if (c.getIdCategoria() == producto.getIdCategoria()) {
                cmbCategoria.setValue(c);
                break;
            }
        }
        for (TipoProducto t : cmbTipoProducto.getItems()) {
            if (t.getIdTipoProducto() == producto.getIdTipoProducto()) {
                cmbTipoProducto.setValue(t);
                break;
            }
        }
        for (UnidadMedida u : cmbUnidadMedida.getItems()) {
            if (u.getIdUnidadMedida() == producto.getIdUnidadMedida()) {
                cmbUnidadMedida.setValue(u);
                break;
            }
        }
    }

    private boolean validarCampos() {
        if (txtSku.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty() ||
                txtCosto.getText().trim().isEmpty() || txtDescuento.getText().trim().isEmpty() ||
                txtStock.getText().trim().isEmpty() || cmbEstado.getValue() == null ||
                cmbCategoria.getValue() == null || cmbTipoProducto.getValue() == null ||
                cmbUnidadMedida.getValue() == null) {

            lblMensaje.setText("✗ Todos los campos son obligatorios");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return false;
        }

        try {
            new BigDecimal(txtCosto.getText().trim());
            new BigDecimal(txtDescuento.getText().trim());
            Integer.parseInt(txtStock.getText().trim());
        } catch (NumberFormatException e) {
            lblMensaje.setText("✗ Verifique los valores numéricos");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return false;
        }

        return true;
    }
}