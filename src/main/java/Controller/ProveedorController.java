package Controller;

import dao.ProveedorDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Proveedor;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProveedorController implements Initializable {

    @FXML private TextField txtNombre, txtTelefono, txtCorreo, txtDireccion, txtNIT, txtBuscar;
    @FXML private TableView<Proveedor> tblProveedores;
    @FXML private TableColumn<Proveedor, Integer> colId;
    @FXML private TableColumn<Proveedor, String> colNombre, colTelefono, colCorreo, colDireccion, colNIT;
    @FXML private Label lblMensaje;

    private ProveedorDAO proveedorDAO = new ProveedorDAO();
    private Proveedor proveedorSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarProveedores();

        tblProveedores.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                proveedorSeleccionado = newSelection;
                llenarCampos(newSelection);
            }
        });
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefonoProveedor"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correoProveedor"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccionProveedor"));
        colNIT.setCellValueFactory(new PropertyValueFactory<>("nitProveedor"));
    }

    @FXML
    private void cargarProveedores() {
        List<Proveedor> proveedores = proveedorDAO.obtenerTodos();
        ObservableList<Proveedor> data = FXCollections.observableArrayList(proveedores);
        tblProveedores.setItems(data);
        lblMensaje.setText("Proveedores cargados: " + proveedores.size());
    }

    @FXML
    private void buscarProveedores() {
        String nombre = txtBuscar.getText().trim();
        if (nombre.isEmpty()) {
            cargarProveedores();
        } else {
            List<Proveedor> proveedores = proveedorDAO.buscarPorNombre(nombre);
            ObservableList<Proveedor> data = FXCollections.observableArrayList(proveedores);
            tblProveedores.setItems(data);
            lblMensaje.setText("Proveedores encontrados: " + proveedores.size());
        }
    }

    @FXML
    private void nuevoProveedor() {
        limpiarCampos();
        proveedorSeleccionado = null;
        lblMensaje.setText("Nuevo proveedor - complete los campos");
    }

    @FXML
    private void guardarProveedor() {
        if (!validarCampos()) return;

        Proveedor proveedor = new Proveedor();
        proveedor.setNombreProveedor(txtNombre.getText().trim());
        proveedor.setTelefonoProveedor(txtTelefono.getText().trim());
        proveedor.setCorreoProveedor(txtCorreo.getText().trim());
        proveedor.setDireccionProveedor(txtDireccion.getText().trim());
        proveedor.setNitProveedor(txtNIT.getText().trim());

        if (proveedorDAO.insertar(proveedor)) {
            lblMensaje.setText("✓ Proveedor guardado exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargarProveedores();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al guardar el proveedor");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void actualizarProveedor() {
        if (proveedorSeleccionado == null) {
            lblMensaje.setText("✗ Seleccione un proveedor de la tabla");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!validarCampos()) return;

        proveedorSeleccionado.setNombreProveedor(txtNombre.getText().trim());
        proveedorSeleccionado.setTelefonoProveedor(txtTelefono.getText().trim());
        proveedorSeleccionado.setCorreoProveedor(txtCorreo.getText().trim());
        proveedorSeleccionado.setDireccionProveedor(txtDireccion.getText().trim());
        proveedorSeleccionado.setNitProveedor(txtNIT.getText().trim());

        if (proveedorDAO.actualizar(proveedorSeleccionado)) {
            lblMensaje.setText("✓ Proveedor actualizado exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargarProveedores();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al actualizar el proveedor");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void eliminarProveedor() {
        if (proveedorSeleccionado == null) {
            lblMensaje.setText("✗ Seleccione un proveedor de la tabla");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este proveedor?");
        alert.setContentText(proveedorSeleccionado.getNombreProveedor());

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (proveedorDAO.eliminar(proveedorSeleccionado.getIdProveedor())) {
                lblMensaje.setText("✓ Proveedor eliminado");
                lblMensaje.setStyle("-fx-text-fill: green;");
                cargarProveedores();
                limpiarCampos();
            } else {
                lblMensaje.setText("✗ Error al eliminar el proveedor");
                lblMensaje.setStyle("-fx-text-fill: red;");
            }
        }
    }

    @FXML
    private void limpiarCampos() {
        txtNombre.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtDireccion.clear();
        txtNIT.clear();
        proveedorSeleccionado = null;
        tblProveedores.getSelectionModel().clearSelection();
        lblMensaje.setText("");
    }

    private void llenarCampos(Proveedor proveedor) {
        txtNombre.setText(proveedor.getNombreProveedor());
        txtTelefono.setText(proveedor.getTelefonoProveedor());
        txtCorreo.setText(proveedor.getCorreoProveedor());
        txtDireccion.setText(proveedor.getDireccionProveedor());
        txtNIT.setText(proveedor.getNitProveedor());
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() || txtTelefono.getText().trim().isEmpty() ||
                txtCorreo.getText().trim().isEmpty() || txtDireccion.getText().trim().isEmpty() ||
                txtNIT.getText().trim().isEmpty()) {

            lblMensaje.setText("✗ Todos los campos son obligatorios");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return false;
        }
        return true;
    }
}