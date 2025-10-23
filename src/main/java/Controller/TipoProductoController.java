package Controller;

import dao.TipoProductoDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.LoginSession;
import model.TipoProducto;
import util.PDFGenerator;
import util.PermissionHelper;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class TipoProductoController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TableView<TipoProducto> tblTipos;
    @FXML private TableColumn<TipoProducto, Integer> colId;
    @FXML private TableColumn<TipoProducto, String> colNombre;
    @FXML private Label lblMensaje;

    // Botones
    @FXML private Button btnNuevo, btnGuardar, btnActualizar, btnEliminar, btnLimpiar;

    private TipoProductoDAO dao = new TipoProductoDAO();
    private TipoProducto seleccionado;
    private LoginSession session = LoginSession.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("idTipoProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreTipoProducto"));
        cargar();

        // ========== APLICAR PERMISOS ==========
        aplicarPermisos();

        tblTipos.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) -> {
            if (nuevo != null) {
                seleccionado = nuevo;
                txtNombre.setText(nuevo.getNombreTipoProducto());
            }
        });
    }

    /**
     * MÉTODO CRÍTICO: Aplica los permisos según el rol del usuario
     */
    private void aplicarPermisos() {
        System.out.println("\n[TIPOS PRODUCTO] Aplicando permisos para: " + session.getRolActual());

        // Botones de acción
        btnGuardar.setDisable(!session.puedeGestionarCategorias());
        btnActualizar.setDisable(!session.puedeGestionarCategorias());
        btnEliminar.setDisable(!session.puedeGestionarCategorias());

        // Campos de formulario (solo lectura si no puede editar)
        boolean puedeEditar = session.puedeGestionarCategorias();
        txtNombre.setEditable(puedeEditar);

        System.out.println("  - Guardar: " + (btnGuardar.isDisabled() ? "BLOQUEADO" : "PERMITIDO"));
        System.out.println("  - Actualizar: " + (btnActualizar.isDisabled() ? "BLOQUEADO" : "PERMITIDO"));
        System.out.println("  - Eliminar: " + (btnEliminar.isDisabled() ? "BLOQUEADO" : "PERMITIDO"));
    }

    @FXML
    private void cargar() {
        tblTipos.setItems(FXCollections.observableArrayList(dao.obtenerTodos()));
        lblMensaje.setText("Tipos cargados: " + tblTipos.getItems().size());
    }

    @FXML
    private void nuevo() {
        limpiarCampos();
        lblMensaje.setText("Nuevo tipo - complete el campo");
    }

    @FXML
    private void guardar() {
        // ========== VALIDAR PERMISO ==========
        if (!session.puedeGestionarCategorias()) {
            PermissionHelper.mostrarErrorPermiso("agregar tipos de producto");
            return;
        }

        if (txtNombre.getText().trim().isEmpty()) {
            lblMensaje.setText("✗ El nombre es obligatorio");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        TipoProducto tipo = new TipoProducto();
        tipo.setNombreTipoProducto(txtNombre.getText().trim());

        if (dao.insertar(tipo)) {
            lblMensaje.setText("✓ Guardado exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargar();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al guardar");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void actualizar() {
        // ========== VALIDAR PERMISO ==========
        if (!session.puedeGestionarCategorias()) {
            PermissionHelper.mostrarErrorPermiso("actualizar tipos de producto");
            return;
        }

        if (seleccionado == null) {
            lblMensaje.setText("✗ Seleccione un tipo");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        seleccionado.setNombreTipoProducto(txtNombre.getText().trim());

        if (dao.actualizar(seleccionado)) {
            lblMensaje.setText("✓ Actualizado exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargar();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al actualizar");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void eliminar() {
        // ========== VALIDAR PERMISO ==========
        if (!session.puedeGestionarCategorias()) {
            PermissionHelper.mostrarErrorPermiso("eliminar tipos de producto");
            return;
        }

        if (seleccionado == null) {
            lblMensaje.setText("✗ Seleccione un tipo");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Eliminar " + seleccionado.getNombreTipoProducto() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (dao.eliminar(seleccionado.getIdTipoProducto())) {
                lblMensaje.setText("✓ Eliminado");
                lblMensaje.setStyle("-fx-text-fill: green;");
                cargar();
                limpiarCampos();
            }
        }
    }

    @FXML
    private void exportarPDF() {
        if (tblTipos.getItems().isEmpty()) {
            lblMensaje.setText("✗ No hay datos para exportar");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        String nombreArchivo = PDFGenerator.generarNombreArchivo("Reporte_TipoProductos");
        File pdf = PDFGenerator.generarPDF(tblTipos, "Reporte de Tipos de Producto", nombreArchivo);

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
        txtNombre.clear();
        seleccionado = null;
        tblTipos.getSelectionModel().clearSelection();
        lblMensaje.setText("");
    }
}