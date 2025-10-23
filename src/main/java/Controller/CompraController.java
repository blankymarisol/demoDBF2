package Controller;

import dao.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import model.*;
import util.PDFGenerator;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CompraController implements Initializable {

    // Campos del formulario principal
    @FXML private DatePicker dpFechaCompra;
    @FXML private ComboBox<Proveedor> cmbProveedor;
    @FXML private ComboBox<Usuario> cmbUsuario;
    @FXML private ComboBox<Bodega> cmbBodega;
    @FXML private ComboBox<String> cmbEstado, cmbFiltroEstado;
    @FXML private TextField txtTotal, txtObservacion, txtReferencia;
    @FXML private Label lblMensaje;

    // Tabla principal de compras
    @FXML private TableView<Compra> tblCompras;
    @FXML private TableColumn<Compra, Integer> colId;
    @FXML private TableColumn<Compra, Date> colFecha;
    @FXML private TableColumn<Compra, BigDecimal> colTotal;
    @FXML private TableColumn<Compra, String> colEstado;
    @FXML private TableColumn<Compra, Integer> colProveedor;
    @FXML private TableColumn<Compra, String> colNombreProveedor;

    // Campos para agregar productos a la compra
    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private TextField txtCantidad, txtPrecioUnitario;

    // Tabla de detalles de compra (productos agregados)
    @FXML private TableView<CompraDetalle> tblDetalles;
    @FXML private TableColumn<CompraDetalle, String> colProducto;
    @FXML private TableColumn<CompraDetalle, Integer> colCantidadDetalle;
    @FXML private TableColumn<CompraDetalle, BigDecimal> colPrecioDetalle, colSubtotal;

    private CompraDAO compraDAO = new CompraDAO();
    private ProveedorDAO proveedorDAO = new ProveedorDAO();
    private ProductoDAO productoDAO = new ProductoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private BodegaDAO bodegaDAO = new BodegaDAO();
    private InventarioDAO inventarioDAO = new InventarioDAO();
    private MovimientoInventarioDAO movimientoDAO = new MovimientoInventarioDAO();

    private Compra compraSeleccionada;
    private ObservableList<CompraDetalle> detallesCompraActual = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTablas();
        cargarComboBoxes();
        cargarCompras();

        // Listener para selección de compra
        tblCompras.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                compraSeleccionada = newSelection;
            }
        });

        // Configurar tabla de detalles
        tblDetalles.setItems(detallesCompraActual);
    }

    private void configurarTablas() {
        // Configurar tabla de compras
        colId.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaCompra"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalCompra"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoCompra"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));

        // Columna personalizada para mostrar nombre del proveedor
        colNombreProveedor.setCellValueFactory(cellData -> {
            int idProveedor = cellData.getValue().getIdProveedor();
            Proveedor proveedor = proveedorDAO.obtenerPorId(idProveedor);
            return new SimpleStringProperty(proveedor != null ? proveedor.getNombreProveedor() : "N/A");
        });

        // Configurar tabla de detalles
        colProducto.setCellValueFactory(cellData -> {
            int idProducto = cellData.getValue().getIdProducto();
            Producto producto = productoDAO.obtenerPorId(idProducto);
            return new SimpleStringProperty(producto != null ? producto.getNombreProducto() : "N/A");
        });
        colCantidadDetalle.setCellValueFactory(new PropertyValueFactory<>("cantidadDetalle"));
        colPrecioDetalle.setCellValueFactory(new PropertyValueFactory<>("precioUnitarioDetalle"));

        // Columna calculada para subtotal
        colSubtotal.setCellValueFactory(cellData -> {
            CompraDetalle detalle = cellData.getValue();
            BigDecimal subtotal = detalle.getPrecioUnitarioDetalle()
                    .multiply(new BigDecimal(detalle.getCantidadDetalle()));
            return new javafx.beans.property.SimpleObjectProperty<>(subtotal);
        });
    }

    private void cargarComboBoxes() {
        // Cargar proveedores
        List<Proveedor> proveedores = proveedorDAO.obtenerTodos();
        cmbProveedor.setItems(FXCollections.observableArrayList(proveedores));
        cmbProveedor.setConverter(new javafx.util.StringConverter<Proveedor>() {
            @Override
            public String toString(Proveedor p) {
                return p != null ? p.getNombreProveedor() + " - " + p.getNitProveedor() : "";
            }
            @Override
            public Proveedor fromString(String string) {
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

        // Cargar bodegas
        List<Bodega> bodegas = bodegaDAO.obtenerTodas();
        cmbBodega.setItems(FXCollections.observableArrayList(bodegas));
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

        // Cargar estados
        ObservableList<String> estados = FXCollections.observableArrayList(
                "PENDIENTE", "COMPLETADA", "CANCELADA", "EN_PROCESO"
        );
        cmbEstado.setItems(estados);
        cmbEstado.setValue("PENDIENTE");
        cmbFiltroEstado.setItems(estados);
    }

    @FXML
    private void cargarCompras() {
        List<Compra> compras = compraDAO.obtenerTodas();
        ObservableList<Compra> data = FXCollections.observableArrayList(compras);
        tblCompras.setItems(data);
        lblMensaje.setText("Compras cargadas: " + compras.size());
    }

    @FXML
    private void filtrarPorEstado() {
        String estado = cmbFiltroEstado.getValue();
        if (estado == null || estado.isEmpty()) {
            cargarCompras();
        } else {
            List<Compra> compras = compraDAO.obtenerPorEstado(estado);
            ObservableList<Compra> data = FXCollections.observableArrayList(compras);
            tblCompras.setItems(data);
            lblMensaje.setText("Compras encontradas: " + compras.size());
        }
    }

    @FXML
    private void filtrarPorProveedor() {
        Proveedor proveedor = cmbProveedor.getValue();
        if (proveedor == null) {
            mostrarMensaje("Seleccione un proveedor para filtrar", "red");
            return;
        }

        List<Compra> compras = compraDAO.obtenerPorProveedor(proveedor.getIdProveedor());
        ObservableList<Compra> data = FXCollections.observableArrayList(compras);
        tblCompras.setItems(data);
        lblMensaje.setText("Compras del proveedor: " + compras.size());
    }

    @FXML
    private void filtrarPorFechas() {
        // Crear diálogo para seleccionar rango de fechas
        Dialog<LocalDate[]> dialog = new Dialog<>();
        dialog.setTitle("Filtrar por Fechas");
        dialog.setHeaderText("Seleccione el rango de fechas");

        ButtonType btnFiltrar = new ButtonType("Filtrar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnFiltrar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        DatePicker dpInicio = new DatePicker(LocalDate.now().minusMonths(1));
        DatePicker dpFin = new DatePicker(LocalDate.now());

        grid.add(new Label("Fecha Inicio:"), 0, 0);
        grid.add(dpInicio, 1, 0);
        grid.add(new Label("Fecha Fin:"), 0, 1);
        grid.add(dpFin, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnFiltrar) {
                return new LocalDate[]{dpInicio.getValue(), dpFin.getValue()};
            }
            return null;
        });

        Optional<LocalDate[]> result = dialog.showAndWait();
        result.ifPresent(fechas -> {
            Date fechaInicio = Date.valueOf(fechas[0]);
            Date fechaFin = Date.valueOf(fechas[1]);

            List<Compra> compras = compraDAO.obtenerPorFechas(fechaInicio, fechaFin);
            ObservableList<Compra> data = FXCollections.observableArrayList(compras);
            tblCompras.setItems(data);
            lblMensaje.setText("Compras encontradas: " + compras.size());
        });
    }

    @FXML
    private void nuevaCompra() {
        limpiarCampos();
        compraSeleccionada = null;
        detallesCompraActual.clear();
        dpFechaCompra.setValue(LocalDate.now());
        cmbEstado.setValue("PENDIENTE");
        lblMensaje.setText("Nueva compra - agregue productos y complete los campos");
    }

    @FXML
    private void agregarProducto() {
        if (!validarCamposProducto()) return;

        Producto producto = cmbProducto.getValue();
        int cantidad = Integer.parseInt(txtCantidad.getText().trim());
        BigDecimal precioUnitario = new BigDecimal(txtPrecioUnitario.getText().trim());

        // Verificar si el producto ya está en la lista
        Optional<CompraDetalle> detalleExistente = detallesCompraActual.stream()
                .filter(d -> d.getIdProducto() == producto.getIdProducto())
                .findFirst();

        if (detalleExistente.isPresent()) {
            // Actualizar cantidad si ya existe
            CompraDetalle detalle = detalleExistente.get();
            detalle.setCantidadDetalle(detalle.getCantidadDetalle() + cantidad);
            tblDetalles.refresh();
        } else {
            // Agregar nuevo detalle
            CompraDetalle detalle = new CompraDetalle();
            detalle.setIdProducto(producto.getIdProducto());
            detalle.setCantidadDetalle(cantidad);
            detalle.setPrecioUnitarioDetalle(precioUnitario);
            detallesCompraActual.add(detalle);
        }

        calcularTotal();
        limpiarCamposProducto();
        mostrarMensaje("✓ Producto agregado a la compra", "green");
    }

    @FXML
    private void quitarProducto() {
        CompraDetalle seleccionado = tblDetalles.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("✗ Seleccione un producto de la lista", "red");
            return;
        }

        detallesCompraActual.remove(seleccionado);
        calcularTotal();
        mostrarMensaje("✓ Producto eliminado de la compra", "green");
    }

    private void calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CompraDetalle detalle : detallesCompraActual) {
            BigDecimal subtotal = detalle.getPrecioUnitarioDetalle()
                    .multiply(new BigDecimal(detalle.getCantidadDetalle()));
            total = total.add(subtotal);
        }
        txtTotal.setText(total.toString());
    }

    @FXML
    private void guardarCompra() {
        if (!validarCamposCompra()) return;

        if (detallesCompraActual.isEmpty()) {
            mostrarMensaje("✗ Debe agregar al menos un producto a la compra", "red");
            return;
        }

        if (cmbBodega.getValue() == null) {
            mostrarMensaje("✗ Debe seleccionar una bodega", "red");
            return;
        }

        if (cmbUsuario.getValue() == null) {
            mostrarMensaje("✗ Debe seleccionar un usuario", "red");
            return;
        }

        try {
            Compra compra = new Compra();
            compra.setFechaCompra(Date.valueOf(dpFechaCompra.getValue()));
            compra.setTotalCompra(new BigDecimal(txtTotal.getText().trim()));
            compra.setEstadoCompra(cmbEstado.getValue());
            compra.setIdProveedor(cmbProveedor.getValue().getIdProveedor());

            // Guardar compra con detalles (transacción)
            List<CompraDetalle> detalles = new ArrayList<>(detallesCompraActual);

            if (compraDAO.insertarCompraCompleta(compra, detalles)) {
                // Procesar cada producto: descontar del inventario y registrar movimiento
                int idBodega = cmbBodega.getValue().getIdBodega();
                int idUsuario = cmbUsuario.getValue().getIdUsuario();
                String observacion = txtObservacion.getText().trim();
                String referencia = txtReferencia.getText().trim();

                boolean todosMovimientosExitosos = true;

                for (CompraDetalle detalle : detalles) {
                    // Buscar inventario del producto en la bodega seleccionada
                    List<Inventario> inventarios = inventarioDAO.obtenerPorBodega(idBodega);
                    Inventario inventarioProducto = null;

                    for (Inventario inv : inventarios) {
                        if (inv.getIdProducto() == detalle.getIdProducto()) {
                            inventarioProducto = inv;
                            break;
                        }
                    }

                    if (inventarioProducto != null) {
                        // Descontar del inventario
                        int cantidadActual = inventarioProducto.getCantidadInventario();
                        int nuevaCantidad = cantidadActual - detalle.getCantidadDetalle();

                        if (nuevaCantidad < 0) {
                            mostrarMensaje("⚠ Advertencia: Stock insuficiente para " +
                                            productoDAO.obtenerPorId(detalle.getIdProducto()).getNombreProducto(),
                                    "orange");
                            nuevaCantidad = 0; // No permitir negativos
                        }

                        inventarioProducto.setCantidadInventario(nuevaCantidad);
                        inventarioDAO.actualizar(inventarioProducto);

                        // Registrar movimiento de SALIDA
                        MovimientoInventario movimiento = new MovimientoInventario();
                        movimiento.setTipoMovimiento("SALIDA");
                        movimiento.setCantidadMovimiento(detalle.getCantidadDetalle());
                        movimiento.setObservacionMovimiento(observacion.isEmpty() ?
                                "Compra #" + compra.getIdCompra() : observacion);
                        movimiento.setReferenciaMovimiento(referencia.isEmpty() ?
                                "COMPRA-" + compra.getIdCompra() : referencia);
                        movimiento.setIdInventario(inventarioProducto.getIdInventario());
                        movimiento.setIdUsuario(idUsuario);

                        if (!movimientoDAO.insertar(movimiento)) {
                            todosMovimientosExitosos = false;
                            System.err.println("Error al registrar movimiento para producto ID: " +
                                    detalle.getIdProducto());
                        }
                    } else {
                        mostrarMensaje("⚠ Advertencia: Producto no encontrado en inventario de la bodega", "orange");
                        todosMovimientosExitosos = false;
                    }
                }

                if (todosMovimientosExitosos) {
                    mostrarMensaje("✓ Compra guardada, inventario actualizado y movimientos registrados - ID: " +
                            compra.getIdCompra(), "green");
                } else {
                    mostrarMensaje("⚠ Compra guardada pero hubo errores en algunos movimientos", "orange");
                }

                cargarCompras();
                limpiarCampos();
                detallesCompraActual.clear();
            } else {
                mostrarMensaje("✗ Error al guardar la compra", "red");
            }

        } catch (Exception e) {
            mostrarMensaje("✗ Error: " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }

    @FXML
    private void actualizarEstado() {
        if (compraSeleccionada == null) {
            mostrarMensaje("✗ Seleccione una compra de la tabla", "red");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(compraSeleccionada.getEstadoCompra(),
                "PENDIENTE", "COMPLETADA", "CANCELADA", "EN_PROCESO");
        dialog.setTitle("Actualizar Estado");
        dialog.setHeaderText("Cambiar estado de la compra");
        dialog.setContentText("Nuevo estado:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nuevoEstado -> {
            if (compraDAO.actualizarEstado(compraSeleccionada.getIdCompra(), nuevoEstado)) {
                mostrarMensaje("✓ Estado actualizado a: " + nuevoEstado, "green");
                cargarCompras();
            } else {
                mostrarMensaje("✗ Error al actualizar el estado", "red");
            }
        });
    }

    @FXML
    private void exportarPDF() {
        if (tblCompras.getItems().isEmpty()) {
            mostrarMensaje("✗ No hay datos para exportar", "red");
            return;
        }

        String nombreArchivo = PDFGenerator.generarNombreArchivo("Reporte_Compras");
        File pdf = PDFGenerator.generarPDF(tblCompras, "Reporte de Compras", nombreArchivo);

        if (pdf != null) {
            mostrarMensaje("✓ PDF generado exitosamente: " + pdf.getName(), "green");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("PDF Generado");
            alert.setHeaderText("Reporte exportado exitosamente");
            alert.setContentText("Archivo: " + pdf.getAbsolutePath());
            alert.showAndWait();
        } else {
            mostrarMensaje("✗ Error al generar el PDF", "red");
        }
    }

    @FXML
    private void limpiarCampos() {
        dpFechaCompra.setValue(LocalDate.now());
        cmbProveedor.setValue(null);
        cmbUsuario.setValue(null);
        cmbBodega.setValue(null);
        cmbEstado.setValue("PENDIENTE");
        txtTotal.setText("0.00");
        txtObservacion.clear();
        txtReferencia.clear();
        limpiarCamposProducto();
        compraSeleccionada = null;
        tblCompras.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        lblMensaje.setStyle("");
    }

    private void limpiarCamposProducto() {
        cmbProducto.setValue(null);
        txtCantidad.clear();
        txtPrecioUnitario.clear();
    }

    private boolean validarCamposCompra() {
        if (dpFechaCompra.getValue() == null || cmbProveedor.getValue() == null ||
                cmbEstado.getValue() == null || cmbBodega.getValue() == null ||
                cmbUsuario.getValue() == null) {
            mostrarMensaje("✗ Complete todos los campos obligatorios de la compra", "red");
            return false;
        }
        return true;
    }

    private boolean validarCamposProducto() {
        if (cmbProducto.getValue() == null || txtCantidad.getText().trim().isEmpty() ||
                txtPrecioUnitario.getText().trim().isEmpty()) {
            mostrarMensaje("✗ Complete todos los campos del producto", "red");
            return false;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                mostrarMensaje("✗ La cantidad debe ser mayor a 0", "red");
                return false;
            }

            BigDecimal precio = new BigDecimal(txtPrecioUnitario.getText().trim());
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarMensaje("✗ El precio debe ser mayor a 0", "red");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("✗ Verifique los valores numéricos", "red");
            return false;
        }

        return true;
    }

    private void mostrarMensaje(String mensaje, String color) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: " + color + ";");
    }
}