package Controller;

import dao.CategoriaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Categoria;

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

    private CategoriaDAO categoriaDAO = new CategoriaDAO();
    private Categoria categoriaSeleccionada;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarCategorias();

        tblCategorias.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                categoriaSeleccionada = newSelection;
                llenarCampos(newSelection);
            }
        });
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