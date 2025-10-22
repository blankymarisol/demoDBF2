package Controller;

import dao.TipoProductoDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.TipoProducto;

import java.net.URL;
import java.util.ResourceBundle;

public class TipoProductoController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TableView<TipoProducto> tblTipos;
    @FXML private TableColumn<TipoProducto, Integer> colId;
    @FXML private TableColumn<TipoProducto, String> colNombre;
    @FXML private Label lblMensaje;

    private TipoProductoDAO dao = new TipoProductoDAO();
    private TipoProducto seleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("idTipoProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreTipoProducto"));
        cargar();

        tblTipos.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) -> {
            if (nuevo != null) {
                seleccionado = nuevo;
                txtNombre.setText(nuevo.getNombreTipoProducto());
            }
        });
    }

    @FXML private void cargar() {
        tblTipos.setItems(FXCollections.observableArrayList(dao.obtenerTodos()));
        lblMensaje.setText("Tipos cargados: " + tblTipos.getItems().size());
    }

    @FXML private void nuevo() {
        limpiarCampos();
        lblMensaje.setText("Nuevo tipo - complete el campo");
    }

    @FXML private void guardar() {
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

    @FXML private void actualizar() {
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

    @FXML private void eliminar() {
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

    @FXML private void limpiarCampos() {
        txtNombre.clear();
        seleccionado = null;
        tblTipos.getSelectionModel().clearSelection();
        lblMensaje.setText("");
    }
}