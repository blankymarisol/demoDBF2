package Controller;

import dao.BodegaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Bodega;
import model.LoginSession;
import util.PDFGenerator;
import util.PermissionHelper;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BodegaController implements Initializable {

    @FXML private TextField txtNombre, txtUbicacion, txtDescripcion, txtTelefono, txtCapacidad, txtMunicipioId;
    @FXML private TableView<Bodega> tblBodegas;
    @FXML private TableColumn<Bodega, Integer> colId, colCapacidad, colMunicipio;
    @FXML private TableColumn<Bodega, String> colNombre, colUbicacion, colDescripcion, colTelefono;
    @FXML private Label lblMensaje;

    // Botones
    @FXML private Button btnNuevo, btnGuardar, btnActualizar, btnEliminar, btnLimpiar;

    private BodegaDAO bodegaDAO = new BodegaDAO();
    private Bodega bodegaSeleccionada;
    private LoginSession session = LoginSession.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarBodegas();

        // ========== APLICAR PERMISOS ==========
        aplicarPermisos();

        tblBodegas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                bodegaSeleccionada = newSelection;
                llenarCampos(newSelection);
            }
        });
    }

    /**
     * MÉTODO CRÍTICO: Aplica los permisos según el rol del usuario
     */
    private void aplicarPermisos() {
        System.out.println("\n[BODEGAS] Aplicando permisos para: " + session.getRolActual());

        // Botones de acción
        btnGuardar.setDisable(!session.puedeGestionarBodegas());
        btnActualizar.setDisable(!session.puedeGestionarBodegas());
        btnEliminar.setDisable(!session.puedeGestionarBodegas());

        // Campos de formulario (solo lectura si no puede editar)
        boolean puedeEditar = session.puedeGestionarBodegas();
        txtNombre.setEditable(puedeEditar);
        txtUbicacion.setEditable(puedeEditar);
        txtDescripcion.setEditable(puedeEditar);
        txtTelefono.setEditable(puedeEditar);
        txtCapacidad.setEditable(puedeEditar);
        txtMunicipioId.setEditable(puedeEditar);

        System.out.println("  - Guardar: " + (btnGuardar.isDisabled() ? "BLOQUEADO" : "PERMITIDO"));
        System.out.println("  - Actualizar: " + (btnActualizar.isDisabled() ? "BLOQUEADO" : "PERMITIDO"));
        System.out.println("  - Eliminar: " + (btnEliminar.isDisabled() ? "BLOQUEADO" : "PERMITIDO"));
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idBodega"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreBodega"));
        colUbicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacionBodega"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionBodega"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefonoBodega"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidadBodega"));
        colMunicipio.setCellValueFactory(new PropertyValueFactory<>("mpioId"));
    }

    @FXML
    private void cargarBodegas() {
        List<Bodega> bodegas = bodegaDAO.obtenerTodas();
        ObservableList<Bodega> data = FXCollections.observableArrayList(bodegas);
        tblBodegas.setItems(data);
        lblMensaje.setText("Bodegas cargadas: " + bodegas.size());
    }

    @FXML
    private void nuevaBodega() {
        limpiarCampos();
        bodegaSeleccionada = null;
        lblMensaje.setText("Nueva bodega - complete los campos");
    }

    @FXML
    private void guardarBodega() {
        // ========== VALIDAR PERMISO ==========
        if (!session.puedeGestionarBodegas()) {
            PermissionHelper.mostrarErrorPermiso("agregar bodegas");
            return;
        }

        if (!validarCampos()) return;

        Bodega bodega = new Bodega();
        bodega.setNombreBodega(txtNombre.getText().trim());
        bodega.setUbicacionBodega(txtUbicacion.getText().trim());
        bodega.setDescripcionBodega(txtDescripcion.getText().trim());
        bodega.setTelefonoBodega(txtTelefono.getText().trim());
        bodega.setCapacidadBodega(Integer.parseInt(txtCapacidad.getText().trim()));
        bodega.setMpioId(Integer.parseInt(txtMunicipioId.getText().trim()));

        if (bodegaDAO.insertar(bodega)) {
            lblMensaje.setText("✓ Bodega guardada exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargarBodegas();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al guardar la bodega");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void actualizarBodega() {
        // ========== VALIDAR PERMISO ==========
        if (!session.puedeGestionarBodegas()) {
            PermissionHelper.mostrarErrorPermiso("actualizar bodegas");
            return;
        }

        if (bodegaSeleccionada == null) {
            lblMensaje.setText("✗ Seleccione una bodega de la tabla");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!validarCampos()) return;

        bodegaSeleccionada.setNombreBodega(txtNombre.getText().trim());
        bodegaSeleccionada.setUbicacionBodega(txtUbicacion.getText().trim());
        bodegaSeleccionada.setDescripcionBodega(txtDescripcion.getText().trim());
        bodegaSeleccionada.setTelefonoBodega(txtTelefono.getText().trim());
        bodegaSeleccionada.setCapacidadBodega(Integer.parseInt(txtCapacidad.getText().trim()));
        bodegaSeleccionada.setMpioId(Integer.parseInt(txtMunicipioId.getText().trim()));

        if (bodegaDAO.actualizar(bodegaSeleccionada)) {
            lblMensaje.setText("✓ Bodega actualizada exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargarBodegas();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al actualizar la bodega");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void eliminarBodega() {
        // ========== VALIDAR PERMISO ==========
        if (!session.puedeGestionarBodegas()) {
            PermissionHelper.mostrarErrorPermiso("eliminar bodegas");
            return;
        }

        if (bodegaSeleccionada == null) {
            lblMensaje.setText("✗ Seleccione una bodega de la tabla");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar esta bodega?");
        alert.setContentText(bodegaSeleccionada.getNombreBodega());

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (bodegaDAO.eliminar(bodegaSeleccionada.getIdBodega())) {
                lblMensaje.setText("✓ Bodega eliminada");
                lblMensaje.setStyle("-fx-text-fill: green;");
                cargarBodegas();
                limpiarCampos();
            } else {
                lblMensaje.setText("✗ Error al eliminar la bodega");
                lblMensaje.setStyle("-fx-text-fill: red;");
            }
        }
    }

    @FXML
    private void exportarPDF() {
        if (tblBodegas.getItems().isEmpty()) {
            lblMensaje.setText("✗ No hay datos para exportar");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        String nombreArchivo = PDFGenerator.generarNombreArchivo("Reporte_Bodegas");
        File pdf = PDFGenerator.generarPDF(tblBodegas, "Reporte de Bodegas", nombreArchivo);

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
        txtUbicacion.clear();
        txtDescripcion.clear();
        txtTelefono.clear();
        txtCapacidad.clear();
        txtMunicipioId.clear();
        bodegaSeleccionada = null;
        tblBodegas.getSelectionModel().clearSelection();
        lblMensaje.setText("");
    }

    private void llenarCampos(Bodega bodega) {
        txtNombre.setText(bodega.getNombreBodega());
        txtUbicacion.setText(bodega.getUbicacionBodega());
        txtDescripcion.setText(bodega.getDescripcionBodega());
        txtTelefono.setText(bodega.getTelefonoBodega());
        txtCapacidad.setText(String.valueOf(bodega.getCapacidadBodega()));
        txtMunicipioId.setText(String.valueOf(bodega.getMpioId()));
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() || txtUbicacion.getText().trim().isEmpty() ||
                txtTelefono.getText().trim().isEmpty() || txtCapacidad.getText().trim().isEmpty() ||
                txtMunicipioId.getText().trim().isEmpty()) {

            lblMensaje.setText("✗ Todos los campos son obligatorios");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return false;
        }

        try {
            Integer.parseInt(txtCapacidad.getText().trim());
            Integer.parseInt(txtMunicipioId.getText().trim());
        } catch (NumberFormatException e) {
            lblMensaje.setText("✗ Verifique los valores numéricos");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return false;
        }

        return true;
    }
}