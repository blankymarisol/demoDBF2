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

        tblCompras.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                compraSeleccionada = newSelection;
            }
        });

        cmbProducto.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                txtPrecioUnitario.setText(newValue.getCostoUnitarioProducto().toString());
            }
        });

        tblDetalles.setItems(detallesCompraActual);
    }

    private void configurarTablas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaCompra"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalCompra"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoCompra"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));

        colNombreProveedor.setCellValueFactory(cellData -> {
            int idProveedor = cellData.getValue().getIdProveedor();
            Proveedor proveedor = proveedorDAO.obtenerPorId(idProveedor);
            return new SimpleStringProperty(proveedor != null ? proveedor.getNombreProveedor() : "N/A");
        });

        colProducto.setCellValueFactory(cellData -> {
            int idProducto = cellData.getValue().getIdProducto();
            Producto producto = productoDAO.obtenerPorId(idProducto);
            return new SimpleStringProperty(producto != null ? producto.getNombreProducto() : "N/A");
        });
        colCantidadDetalle.setCellValueFactory(new PropertyValueFactory<>("cantidadDetalle"));
        colPrecioDetalle.setCellValueFactory(new PropertyValueFactory<>("precioUnitarioDetalle"));

        colSubtotal.setCellValueFactory(cellData -> {
            CompraDetalle detalle = cellData.getValue();
            BigDecimal subtotal = detalle.getPrecioUnitarioDetalle()
                    .multiply(new BigDecimal(detalle.getCantidadDetalle()));
            return new javafx.beans.property.SimpleObjectProperty<>(subtotal);
        });
    }

    private void cargarComboBoxes() {
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

        ObservableList<String> estados = FXCollections.observableArrayList(
                "PENDIENTE", "COMPLETADA", "CANCELADA", "EN_PROCESO"
        );
        cmbEstado.setItems(estados);
        cmbEstado.setValue("PENDIENTE");
        cmbFiltroEstado.setItems(estados);
    }

    @FXML
    private void cargarCompras() {
        try {
            List<Compra> compras = compraDAO.obtenerTodas();
            ObservableList<Compra> data = FXCollections.observableArrayList(compras);
            tblCompras.setItems(data);
            mostrarMensaje("Compras cargadas: " + compras.size(), "blue");
        } catch (Exception e) {
            mostrarMensaje("✗ Error al cargar compras: " + e.getMessage(), "red");
            e.printStackTrace();
        }
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
            mostrarMensaje("Compras encontradas: " + compras.size(), "blue");
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
        mostrarMensaje("Compras del proveedor: " + compras.size(), "blue");
    }

    @FXML
    private void filtrarPorFechas() {
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
            mostrarMensaje("Compras encontradas: " + compras.size(), "blue");
        });
    }

    @FXML
    private void nuevaCompra() {
        limpiarCampos();
        compraSeleccionada = null;
        detallesCompraActual.clear();
        dpFechaCompra.setValue(LocalDate.now());
        cmbEstado.setValue("PENDIENTE");
        mostrarMensaje("Nueva compra - agregue productos y complete los campos", "blue");
    }

    @FXML
    private void agregarProducto() {
        if (!validarCamposProducto()) return;

        Producto producto = cmbProducto.getValue();
        int cantidad = Integer.parseInt(txtCantidad.getText().trim());
        BigDecimal precioUnitario = new BigDecimal(txtPrecioUnitario.getText().trim());

        Optional<CompraDetalle> detalleExistente = detallesCompraActual.stream()
                .filter(d -> d.getIdProducto() == producto.getIdProducto())
                .findFirst();

        if (detalleExistente.isPresent()) {
            CompraDetalle detalle = detalleExistente.get();
            detalle.setCantidadDetalle(detalle.getCantidadDetalle() + cantidad);
            tblDetalles.refresh();
        } else {
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
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("    INICIANDO PROCESO DE GUARDADO DE COMPRA (SALIDA)");
        System.out.println("═══════════════════════════════════════════════════════\n");

        // ===== VALIDACIONES INICIALES =====
        if (!validarCamposCompra()) {
            System.out.println("❌ Validación de campos falló");
            return;
        }

        if (detallesCompraActual.isEmpty()) {
            mostrarMensaje("✗ Debe agregar al menos un producto a la compra", "red");
            System.out.println("❌ No hay productos en la compra");
            return;
        }

        if (cmbBodega.getValue() == null) {
            mostrarMensaje("✗ Debe seleccionar una bodega", "red");
            System.out.println("❌ No hay bodega seleccionada");
            return;
        }

        if (cmbUsuario.getValue() == null) {
            mostrarMensaje("✗ Debe seleccionar un usuario", "red");
            System.out.println("❌ No hay usuario seleccionado");
            return;
        }

        try {
            int idBodega = cmbBodega.getValue().getIdBodega();
            int idUsuario = cmbUsuario.getValue().getIdUsuario();

            System.out.println("📋 Datos principales:");
            System.out.println("   • Bodega ID: " + idBodega);
            System.out.println("   • Usuario ID: " + idUsuario);
            System.out.println("   • Productos a procesar: " + detallesCompraActual.size());

            // ===== VERIFICACIÓN DE STOCK =====
            System.out.println("\n🔍 PASO 1: Verificando stock disponible...");
            List<String> productosInsuficientes = new ArrayList<>();

            for (CompraDetalle detalle : detallesCompraActual) {
                Producto producto = productoDAO.obtenerPorId(detalle.getIdProducto());
                List<Inventario> inventarios = inventarioDAO.obtenerPorBodega(idBodega);

                Inventario inventarioProducto = inventarios.stream()
                        .filter(inv -> inv.getIdProducto() == detalle.getIdProducto())
                        .findFirst()
                        .orElse(null);

                System.out.println("\n   📦 Producto: " + producto.getNombreProducto());
                System.out.println("      - Cantidad solicitada: " + detalle.getCantidadDetalle());

                if (inventarioProducto == null) {
                    String error = producto.getNombreProducto() + " (no existe en inventario de esta bodega)";
                    productosInsuficientes.add(error);
                    System.out.println("      ❌ ERROR: No existe en inventario");
                } else {
                    int stockInventario = inventarioProducto.getCantidadInventario();
                    int stockProducto = producto.getStockProducto();

                    System.out.println("      - Stock en inventario: " + stockInventario);
                    System.out.println("      - Stock del producto: " + stockProducto);

                    if (stockInventario < detalle.getCantidadDetalle()) {
                        String error = producto.getNombreProducto() +
                                " (disponible: " + stockInventario +
                                ", solicitado: " + detalle.getCantidadDetalle() + ")";
                        productosInsuficientes.add(error);
                        System.out.println("      ❌ ERROR: Stock insuficiente");
                    } else {
                        System.out.println("      ✅ Stock suficiente");
                    }
                }
            }

            // Si hay problemas de stock, mostrar y cancelar
            if (!productosInsuficientes.isEmpty()) {
                System.out.println("\n❌ PROCESO CANCELADO: Stock insuficiente");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Stock Insuficiente");
                alert.setHeaderText("Los siguientes productos no tienen stock suficiente:");
                alert.setContentText(String.join("\n", productosInsuficientes));
                alert.showAndWait();
                return;
            }

            System.out.println("\n✅ Verificación de stock completada exitosamente");

            // ===== CREAR Y GUARDAR COMPRA =====
            System.out.println("\n💾 PASO 2: Guardando compra en base de datos...");

            Compra compra = new Compra();
            compra.setFechaCompra(Date.valueOf(dpFechaCompra.getValue()));
            compra.setTotalCompra(new BigDecimal(txtTotal.getText().trim()));
            compra.setEstadoCompra(cmbEstado.getValue());
            compra.setIdProveedor(cmbProveedor.getValue().getIdProveedor());

            List<CompraDetalle> detalles = new ArrayList<>(detallesCompraActual);

            if (!compraDAO.insertarCompraCompleta(compra, detalles)) {
                mostrarMensaje("✗ Error al guardar la compra en la base de datos", "red");
                System.out.println("❌ Error al insertar compra en BD");
                return;
            }

            System.out.println("✅ Compra guardada con ID: " + compra.getIdCompra());

            // ===== PROCESAR SALIDAS DE INVENTARIO =====
            System.out.println("\n📤 PASO 3: Procesando salidas de inventario...");

            String observacion = txtObservacion.getText().trim();
            String referencia = txtReferencia.getText().trim();
            boolean todosMovimientosExitosos = true;

            for (CompraDetalle detalle : detalles) {
                Producto producto = productoDAO.obtenerPorId(detalle.getIdProducto());
                System.out.println("\n   ━━━ Procesando: " + producto.getNombreProducto() + " ━━━");

                // Buscar inventario del producto en la bodega
                List<Inventario> inventarios = inventarioDAO.obtenerPorBodega(idBodega);
                Inventario inventarioProducto = inventarios.stream()
                        .filter(inv -> inv.getIdProducto() == detalle.getIdProducto())
                        .findFirst()
                        .orElse(null);

                if (inventarioProducto != null) {
                    // DESCONTAR del inventario
                    int cantidadAntes = inventarioProducto.getCantidadInventario();
                    int cantidadDespues = cantidadAntes - detalle.getCantidadDetalle();

                    System.out.println("   📊 INVENTARIO:");
                    System.out.println("      Antes:   " + cantidadAntes);
                    System.out.println("      Salida:  -" + detalle.getCantidadDetalle());
                    System.out.println("      Después: " + cantidadDespues);

                    inventarioProducto.setCantidadInventario(cantidadDespues);

                    if (inventarioDAO.actualizar(inventarioProducto)) {
                        System.out.println("      ✅ Inventario actualizado");
                    } else {
                        System.out.println("      ❌ Error al actualizar inventario");
                        todosMovimientosExitosos = false;
                    }

                    // DESCONTAR del stock del producto
                    int stockAntes = producto.getStockProducto();
                    int stockDespues = stockAntes - detalle.getCantidadDetalle();

                    System.out.println("   📦 STOCK PRODUCTO:");
                    System.out.println("      Antes:   " + stockAntes);
                    System.out.println("      Salida:  -" + detalle.getCantidadDetalle());
                    System.out.println("      Después: " + stockDespues);

                    if (productoDAO.actualizarStock(detalle.getIdProducto(), stockDespues)) {
                        System.out.println("      ✅ Stock del producto actualizado");
                    } else {
                        System.out.println("      ❌ Error al actualizar stock del producto");
                        todosMovimientosExitosos = false;
                    }

                    // REGISTRAR MOVIMIENTO DE SALIDA
                    System.out.println("   📝 MOVIMIENTO:");

                    MovimientoInventario movimiento = new MovimientoInventario();
                    movimiento.setTipoMovimiento("SALIDA");
                    movimiento.setCantidadMovimiento(detalle.getCantidadDetalle());
                    movimiento.setObservacionMovimiento(observacion.isEmpty() ?
                            "Compra #" + compra.getIdCompra() : observacion);
                    movimiento.setReferenciaMovimiento(referencia.isEmpty() ?
                            "COMPRA-" + compra.getIdCompra() : referencia);
                    movimiento.setIdInventario(inventarioProducto.getIdInventario());
                    movimiento.setIdUsuario(idUsuario);

                    if (movimientoDAO.insertar(movimiento)) {
                        System.out.println("      ✅ Movimiento de SALIDA registrado");
                    } else {
                        todosMovimientosExitosos = false;
                        System.out.println("      ❌ Error al registrar movimiento");
                    }
                } else {
                    mostrarMensaje("⚠ Error: Producto no encontrado en inventario", "red");
                    System.out.println("   ❌ Producto no encontrado en inventario");
                    todosMovimientosExitosos = false;
                }
            }

            // ===== RESULTADO FINAL =====
            System.out.println("\n═══════════════════════════════════════════════════════");
            if (todosMovimientosExitosos) {
                System.out.println("✅ ¡PROCESO COMPLETADO EXITOSAMENTE!");
                System.out.println("   • Compra ID: " + compra.getIdCompra());
                System.out.println("   • Inventario actualizado (salidas)");
                System.out.println("   • Stock de productos actualizado");
                System.out.println("   • Movimientos registrados");
                mostrarMensaje("✓ Compra guardada exitosamente - Inventario y stock actualizados - ID: " +
                        compra.getIdCompra(), "green");
            } else {
                System.out.println("⚠️ PROCESO COMPLETADO CON ERRORES");
                mostrarMensaje("⚠ Compra guardada pero hubo errores en algunos movimientos", "orange");
            }
            System.out.println("═══════════════════════════════════════════════════════\n");

            cargarCompras();
            limpiarCampos();
            detallesCompraActual.clear();

        } catch (Exception e) {
            System.out.println("\n❌ EXCEPCIÓN DURANTE EL PROCESO:");
            System.out.println("   " + e.getMessage());
            e.printStackTrace();
            mostrarMensaje("✗ Error: " + e.getMessage(), "red");
        }
    }

    @FXML
    private void actualizarEstado() {
        if (compraSeleccionada == null) {
            mostrarMensaje("✗ Seleccione una compra de la tabla", "red");
            return;
        }

        try {
            ChoiceDialog<String> dialog = new ChoiceDialog<>(compraSeleccionada.getEstadoCompra(),
                    "PENDIENTE", "COMPLETADA", "CANCELADA", "EN_PROCESO");
            dialog.setTitle("Actualizar Estado");
            dialog.setHeaderText("Cambiar estado de la compra #" + compraSeleccionada.getIdCompra());
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
        } catch (Exception e) {
            mostrarMensaje("✗ Error: " + e.getMessage(), "red");
            e.printStackTrace();
        }
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