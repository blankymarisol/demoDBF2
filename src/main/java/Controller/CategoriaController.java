package Controller;

import dao.CategoriaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Categoria;
import model.LoginSession;
import util.PDFGenerator;
import util.PermissionHelper;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CategoriaController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre, colDescripcion;
    @FXML private Label lblMensaje;

    // Botones
    @FXML private Button btnNuevo, btnGuardar, btnActualizar, btnEliminar, btnLimpiar;

    private CategoriaDAO categoriaDAO = new CategoriaDAO();
    private Categoria categoriaSeleccionada;
    private LoginSession session = LoginSession.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarCategorias();

        // ========== APLICAR PERMISOS ==========
        aplicarPermisos();

        tblCategorias.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                categoriaSeleccionada = newSelection;
                llenarCampos(newSelection);
            }
        });
    }

    /**
     * MÉTODO CRÍTICO: Aplica los permisos según el rol del usuario
     */
    private void aplicarPermisos() {
        System.out.println("\n[CATEGORIAS] Aplicando permisos para: " + session.getRolActual());

        // Botones de acción
        btnGuardar.setDisable(!session.puedeGestionarCategorias());
        btnActualizar.setDisable(!session.puedeGestionarCategorias());
        btnEliminar.setDisable(!session.puedeGestionarCategorias());

        // Campos de formulario (solo lectura si no puede editar)
        boolean puedeEditar = session.puedeGestionarCategorias();
        txtNombre.setEditable(puedeEditar);
        txtDescripcion.setEditable(puedeEditar);

        System.out.println("  - Guardar: " + (btnGuardar.isDisabled() ? "BLOQUEADO" : "PERMITIDO"));
        System.out.println("  - Actualizar: " + (btnActualizar.isDisabled() ? "BLOQUEADO" : "PERMITIDO"));
        System.out.println("  - Eliminar: " + (btnEliminar.isDisabled() ? "BLOQUEADO" : "PERMITIDO"));
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCategoria"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionCategoria"));
    }

    @FXML
    private void cargarCategorias() {
        List<Categoria> categorias = categoriaDAO.obtenerTodas();
        ObservableList<Categoria> data = FXCollections.observableArrayList(categorias);
        tblCategorias.setItems(data);
        lblMensaje.setText("Categorías cargadas: " + categorias.size());
    }

    @FXML
    private void nuevaCategoria() {
        limpiarCampos();
        categoriaSeleccionada = null;
        lblMensaje.setText("Nueva categoría - complete los campos");
    }

    @FXML
    private void guardarCategoria() {
        // ========== VALIDAR PERMISO ==========
        if (!session.puedeGestionarCategorias()) {
            PermissionHelper.mostrarErrorPermiso("agregar categorías");
            return;
        }

        if (!validarCampos()) return;

        Categoria categoria = new Categoria();
        categoria.setNombreCategoria(txtNombre.getText().trim());
        categoria.setDescripcionCategoria(txtDescripcion.getText().trim());

        if (categoriaDAO.insertar(categoria)) {
            lblMensaje.setText("✓ Categoría guardada exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargarCategorias();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al guardar la categoría");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void actualizarCategoria() {
        // ========== VALIDAR PERMISO ==========
        if (!session.puedeGestionarCategorias()) {
            PermissionHelper.mostrarErrorPermiso("actualizar categorías");
            return;
        }

        if (categoriaSeleccionada == null) {
            lblMensaje.setText("✗ Seleccione una categoría de la tabla");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!validarCampos()) return;

        categoriaSeleccionada.setNombreCategoria(txtNombre.getText().trim());
        categoriaSeleccionada.setDescripcionCategoria(txtDescripcion.getText().trim());

        if (categoriaDAO.actualizar(categoriaSeleccionada)) {
            lblMensaje.setText("✓ Categoría actualizada exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargarCategorias();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al actualizar la categoría");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void eliminarCategoria() {
        // ========== VALIDAR PERMISO ==========
        if (!session.puedeGestionarCategorias()) {
            PermissionHelper.mostrarErrorPermiso("eliminar categorías");
            return;
        }

        if (categoriaSeleccionada == null) {
            lblMensaje.setText("✗ Seleccione una categoría de la tabla");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar esta categoría?");
        alert.setContentText(categoriaSeleccionada.getNombreCategoria());

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (categoriaDAO.eliminar(categoriaSeleccionada.getIdCategoria())) {
                lblMensaje.setText("✓ Categoría eliminada");
                lblMensaje.setStyle("-fx-text-fill: green;");
                cargarCategorias();
                limpiarCampos();
            } else {
                lblMensaje.setText("✗ Error al eliminar la categoría");
                lblMensaje.setStyle("-fx-text-fill: red;");
            }
        }
    }

    @FXML
    private void exportarPDF() {
        if (tblCategorias.getItems().isEmpty()) {
            lblMensaje.setText("✗ No hay datos para exportar");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        String nombreArchivo = PDFGenerator.generarNombreArchivo("Reporte_Categorias");
        File pdf = PDFGenerator.generarPDF(tblCategorias, "Reporte de Categorías", nombreArchivo);

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
        txtDescripcion.clear();
        categoriaSeleccionada = null;
        tblCategorias.getSelectionModel().clearSelection();
        lblMensaje.setText("");
    }

    private void llenarCampos(Categoria categoria) {
        txtNombre.setText(categoria.getNombreCategoria());
        txtDescripcion.setText(categoria.getDescripcionCategoria());
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            lblMensaje.setText("✗ El nombre es obligatorio");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return false;
        }
        return true;
    }
}