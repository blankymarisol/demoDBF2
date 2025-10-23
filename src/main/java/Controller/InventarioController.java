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
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

public class InventarioController implements Initializable {

    @FXML private TextField txtCantidad, txtPrecioVenta, txtObservacion, txtReferencia;
    @FXML private ComboBox<Bodega> cmbBodega, cmbFiltroBodega;
    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private ComboBox<Usuario> cmbUsuario;
    @FXML private TableView<Inventario> tblInventarios;
    @FXML private TableColumn<Inventario, Integer> colId, colCantidad, colBodega, colProducto;
    @FXML private TableColumn<Inventario, BigDecimal> colPrecioVenta;
    @FXML private TableColumn<Inventario, Timestamp> colFecha;
    @FXML private Label lblMensaje;

    // Botones
    @FXML private Button btnNuevo, btnGuardar, btnActualizar, btnEliminar, btnLimpiar;

    private InventarioDAO inventarioDAO = new InventarioDAO();
    private BodegaDAO bodegaDAO = new BodegaDAO();
    private ProductoDAO productoDAO = new ProductoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private MovimientoInventarioDAO movimientoDAO = new MovimientoInventarioDAO();
    private Inventario inventarioSeleccionado;
    private LoginSession session = LoginSession.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarComboBoxes();
        cargarInventarios();

        // ========== APLICAR PERMISOS ==========
        aplicarPermisos();

        tblInventarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                inventarioSeleccionado = newSelection;
                llenarCampos(newSelection);
            }
        });

        // Listener para cargar automáticamente el precio cuando se selecciona un producto
        cmbProducto.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                // Cargar el precio de venta basado en el costo del producto
                txtPrecioVenta.setText(newValue.getCostoUnitarioProducto().toString());
            }
        });
    }

    /**
     * MÉTODO CRÍTICO: Aplica los permisos según el rol del usuario
     */
    private void aplicarPermisos() {
        System.out.println("\n[INVENTARIOS] Aplicando permisos para: " + session.getRolActual());

        // Botones de acción
        btnGuardar.setDisable(!session.puedeGestionarInventarios());
        btnActualizar.setDisable(!session.puedeGestionarInventarios());
        btnEliminar.setDisable(!session.puedeGestionarInventarios());

        // Campos de formulario (solo lectura si no puede editar)
        boolean puedeEditar = session.puedeGestionarInventarios();
        txtCantidad.setEditable(puedeEditar);
        txtPrecioVenta.setEditable(false); // Siempre readonly porque se carga automáticamente
        txtObservacion.setEditable(puedeEditar);
        txtReferencia.setEditable(puedeEditar);
        cmbBodega.setDisable(!puedeEditar);
        cmbProducto.setDisable(!puedeEditar);
        cmbUsuario.setDisable(!puedeEditar);

        System.out.println("  - Guardar: " + (btnGuardar.isDisabled() ? "BLOQUEADO" : "PERMITIDO"));
        System.out.println("  - Actualizar: " + (btnActualizar.isDisabled() ? "BLOQUEADO" : "PERMITIDO"));
        System.out.println("  - Eliminar: " + (btnEliminar.isDisabled() ? "BLOQUEADO" : "PERMITIDO"));
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
        // Cargar bodegas
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

        // Cargar productos
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

        // Cargar usuarios
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        cmbUsuario.setItems(FXCollections.observableArrayList(usuarios));
        cmbUsuario.setConverter(new javafx.util.StringConverter<Usuario>() {
            @Override
            public String toString(Usuario u) {
                return u != null ? u.getNombreUsuario() + " (" + u.getRolUsuario() + ")" : "";
            }
            @Override
            public Usuario fromString(String string) {
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
        lblMensaje.setText("Nueva entrada de inventario - complete los campos");
    }

    @FXML
    private void guardarInventario() {
        // ========== VALIDAR PERMISO ==========
        if (!session.puedeGestionarInventarios()) {
            PermissionHelper.mostrarErrorPermiso("agregar inventarios");
            return;
        }

        if (!validarCampos()) return;

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            BigDecimal precioVenta = new BigDecimal(txtPrecioVenta.getText().trim());
            int idBodega = cmbBodega.getValue().getIdBodega();
            int idProducto = cmbProducto.getValue().getIdProducto();
            int idUsuario = cmbUsuario.getValue().getIdUsuario();

            // Verificar si ya existe inventario para este producto en esta bodega
            List<Inventario> inventariosExistentes = inventarioDAO.obtenerPorBodega(idBodega);
            Inventario inventarioExistente = null;

            for (Inventario inv : inventariosExistentes) {
                if (inv.getIdProducto() == idProducto) {
                    inventarioExistente = inv;
                    break;
                }
            }

            // Actualizar el stock en la tabla de productos
            Producto producto = productoDAO.obtenerPorId(idProducto);
            if (producto != null) {
                int nuevoStockProducto = producto.getStockProducto() + cantidad;
                productoDAO.actualizarStock(idProducto, nuevoStockProducto);
                System.out.println("✓ Stock del producto actualizado: " + producto.getNombreProducto() +
                        " - Nuevo stock: " + nuevoStockProducto);
            }

            if (inventarioExistente != null) {
                // Actualizar inventario existente
                int nuevaCantidad = inventarioExistente.getCantidadInventario() + cantidad;
                inventarioExistente.setCantidadInventario(nuevaCantidad);
                inventarioExistente.setPrecioVentaInventario(precioVenta);

                if (inventarioDAO.actualizar(inventarioExistente)) {
                    // Registrar movimiento de ENTRADA
                    registrarMovimiento("ENTRADA", cantidad, inventarioExistente.getIdInventario(),
                            idUsuario, txtObservacion.getText().trim(), txtReferencia.getText().trim());

                    mostrarMensaje("✓ Inventario y stock del producto actualizados - Movimiento registrado", "green");
                    cargarInventarios();
                    limpiarCampos();
                } else {
                    mostrarMensaje("✗ Error al actualizar el inventario", "red");
                }
            } else {
                // Crear nuevo inventario
                Inventario inventario = new Inventario();
                inventario.setCantidadInventario(cantidad);
                inventario.setPrecioVentaInventario(precioVenta);
                inventario.setIdBodega(idBodega);
                inventario.setIdProducto(idProducto);

                if (inventarioDAO.insertar(inventario)) {
                    // Registrar movimiento de ENTRADA
                    registrarMovimiento("ENTRADA", cantidad, inventario.getIdInventario(),
                            idUsuario, txtObservacion.getText().trim(), txtReferencia.getText().trim());

                    mostrarMensaje("✓ Inventario creado, stock actualizado y movimiento registrado", "green");
                    cargarInventarios();
                    limpiarCampos();
                } else {
                    mostrarMensaje("✗ Error al crear el inventario", "red");
                }
            }

        } catch (Exception e) {
            mostrarMensaje("✗ Error: " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }

    @FXML
    private void actualizarInventario() {
        // ========== VALIDAR PERMISO ==========
        if (!session.puedeGestionarInventarios()) {
            PermissionHelper.mostrarErrorPermiso("actualizar inventarios");
            return;
        }

        if (inventarioSeleccionado == null) {
            lblMensaje.setText("✗ Seleccione un inventario de la tabla");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!validarCampos()) return;

        int cantidadAnterior = inventarioSeleccionado.getCantidadInventario();
        int cantidadNueva = Integer.parseInt(txtCantidad.getText().trim());
        int diferencia = cantidadNueva - cantidadAnterior;

        inventarioSeleccionado.setCantidadInventario(cantidadNueva);
        inventarioSeleccionado.setPrecioVentaInventario(new BigDecimal(txtPrecioVenta.getText().trim()));
        inventarioSeleccionado.setIdBodega(cmbBodega.getValue().getIdBodega());
        inventarioSeleccionado.setIdProducto(cmbProducto.getValue().getIdProducto());

        if (inventarioDAO.actualizar(inventarioSeleccionado)) {
            // Registrar movimiento si hubo cambio en cantidad
            if (diferencia != 0) {
                String tipoMovimiento = diferencia > 0 ? "ENTRADA" : "AJUSTE";
                registrarMovimiento(tipoMovimiento, Math.abs(diferencia),
                        inventarioSeleccionado.getIdInventario(),
                        cmbUsuario.getValue().getIdUsuario(),
                        txtObservacion.getText().trim(),
                        txtReferencia.getText().trim());
            }

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
        // ========== VALIDAR PERMISO ==========
        if (!session.puedeGestionarInventarios()) {
            PermissionHelper.mostrarErrorPermiso("eliminar inventarios");
            return;
        }

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

    /**
     * Registra un movimiento de inventario
     */
    private void registrarMovimiento(String tipo, int cantidad, int idInventario,
                                     int idUsuario, String observacion, String referencia) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidadMovimiento(cantidad);
        movimiento.setObservacionMovimiento(observacion.isEmpty() ? null : observacion);
        movimiento.setReferenciaMovimiento(referencia.isEmpty() ? null : referencia);
        movimiento.setIdInventario(idInventario);
        movimiento.setIdUsuario(idUsuario);

        if (movimientoDAO.insertar(movimiento)) {
            System.out.println("✓ Movimiento de " + tipo + " registrado correctamente");
        } else {
            System.err.println("✗ Error al registrar movimiento");
        }
    }

    @FXML
    private void exportarPDF() {
        if (tblInventarios.getItems().isEmpty()) {
            lblMensaje.setText("✗ No hay datos para exportar");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        String nombreArchivo = PDFGenerator.generarNombreArchivo("Reporte_Inventarios");
        File pdf = PDFGenerator.generarPDF(tblInventarios, "Reporte de Inventarios", nombreArchivo);

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
        txtCantidad.clear();
        txtPrecioVenta.clear();
        txtObservacion.clear();
        txtReferencia.clear();
        cmbBodega.setValue(null);
        cmbProducto.setValue(null);
        cmbUsuario.setValue(null);
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
                cmbBodega.getValue() == null || cmbProducto.getValue() == null || cmbUsuario.getValue() == null) {

            lblMensaje.setText("✗ Todos los campos son obligatorios (excepto observación y referencia)");
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

    private void mostrarMensaje(String mensaje, String color) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: " + color + ";");
    }
}