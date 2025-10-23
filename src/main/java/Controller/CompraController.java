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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    @FXML private ComboBox<String> cmbEstado, cmbFiltroEstado;
    @FXML private TextField txtTotal;
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
                cargarDetallesCompra(newSelection.getIdCompra());
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

        Compra compra = new Compra();
        compra.setFechaCompra(Date.valueOf(dpFechaCompra.getValue()));
        compra.setTotalCompra(new BigDecimal(txtTotal.getText().trim()));
        compra.setEstadoCompra(cmbEstado.getValue());
        compra.setIdProveedor(cmbProveedor.getValue().getIdProveedor());

        // Guardar compra con detalles (transacción)
        List<CompraDetalle> detalles = new ArrayList<>(detallesCompraActual);

        if (compraDAO.insertarCompraCompleta(compra, detalles)) {
            mostrarMensaje("✓ Compra guardada exitosamente con ID: " + compra.getIdCompra(), "green");
            cargarCompras();
            limpiarCampos();
            detallesCompraActual.clear();
        } else {
            mostrarMensaje("✗ Error al guardar la compra", "red");
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
    private void eliminarCompra() {
        if (compraSeleccionada == null) {
            mostrarMensaje("✗ Seleccione una compra de la tabla", "red");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar esta compra?");
        alert.setContentText("ID: " + compraSeleccionada.getIdCompra() +
                "\nTotal: Q" + compraSeleccionada.getTotalCompra() +
                "\nEsta acción no se puede deshacer.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (compraDAO.eliminar(compraSeleccionada.getIdCompra())) {
                mostrarMensaje("✓ Compra eliminada", "green");
                cargarCompras();
                limpiarCampos();
                detallesCompraActual.clear();
            } else {
                mostrarMensaje("✗ Error al eliminar la compra", "red");
            }
        }
    }

    @FXML
    private void verDetalles() {
        if (compraSeleccionada == null) {
            mostrarMensaje("✗ Seleccione una compra de la tabla", "red");
            return;
        }

        cargarDetallesCompra(compraSeleccionada.getIdCompra());

        Proveedor proveedor = proveedorDAO.obtenerPorId(compraSeleccionada.getIdProveedor());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles de la Compra");
        alert.setHeaderText("Compra ID: " + compraSeleccionada.getIdCompra());

        String contenido = "Fecha: " + compraSeleccionada.getFechaCompra() + "\n" +
                "Proveedor: " + (proveedor != null ? proveedor.getNombreProveedor() : "N/A") + "\n" +
                "Estado: " + compraSeleccionada.getEstadoCompra() + "\n" +
                "Total: Q" + compraSeleccionada.getTotalCompra() + "\n\n" +
                "Productos: " + detallesCompraActual.size();

        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void cargarDetallesCompra(int idCompra) {
        List<CompraDetalle> detalles = compraDAO.obtenerDetalles(idCompra);
        detallesCompraActual.clear();
        detallesCompraActual.addAll(detalles);
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
        cmbEstado.setValue("PENDIENTE");
        txtTotal.setText("0.00");
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
                cmbEstado.getValue() == null) {
            mostrarMensaje("✗ Complete todos los campos de la compra", "red");
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