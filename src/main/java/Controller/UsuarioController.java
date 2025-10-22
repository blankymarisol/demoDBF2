package Controller;

import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Usuario;
import util.PDFGenerator;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UsuarioController implements Initializable {

    @FXML private TextField txtNombreUsuario, txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private ComboBox<String> cmbRol, cmbFiltroRol;
    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNombreUsuario, colRol, colCorreo;
    @FXML private Label lblMensaje;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Usuario usuarioSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarComboBoxes();
        cargarUsuarios();

        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                usuarioSeleccionado = newSelection;
                llenarCampos(newSelection);
            }
        });
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rolUsuario"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correoUsuario"));
    }

    private void cargarComboBoxes() {
        ObservableList<String> roles = FXCollections.observableArrayList(
                "ADMINISTRADOR", "SUPERVISOR", "EMPLEADO", "VENDEDOR"
        );
        cmbRol.setItems(roles);
        cmbFiltroRol.setItems(roles);
    }

    @FXML
    private void cargarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        ObservableList<Usuario> data = FXCollections.observableArrayList(usuarios);
        tblUsuarios.setItems(data);
        lblMensaje.setText("Usuarios cargados: " + usuarios.size());
    }

    @FXML
    private void filtrarPorRol() {
        String rol = cmbFiltroRol.getValue();
        if (rol == null || rol.isEmpty()) {
            cargarUsuarios();
        } else {
            List<Usuario> usuarios = usuarioDAO.obtenerPorRol(rol);
            ObservableList<Usuario> data = FXCollections.observableArrayList(usuarios);
            tblUsuarios.setItems(data);
            lblMensaje.setText("Usuarios encontrados: " + usuarios.size());
        }
    }

    @FXML
    private void nuevoUsuario() {
        limpiarCampos();
        usuarioSeleccionado = null;
        lblMensaje.setText("Nuevo usuario - complete los campos");
    }

    @FXML
    private void guardarUsuario() {
        if (!validarCampos()) return;

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(txtNombreUsuario.getText().trim());
        usuario.setRolUsuario(cmbRol.getValue());
        usuario.setCorreoUsuario(txtCorreo.getText().trim());
        usuario.setContrasena(txtContrasena.getText().trim());

        if (usuarioDAO.insertar(usuario)) {
            lblMensaje.setText("✓ Usuario guardado exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargarUsuarios();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al guardar el usuario");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void actualizarUsuario() {
        if (usuarioSeleccionado == null) {
            lblMensaje.setText("✗ Seleccione un usuario de la tabla");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!validarCampos()) return;

        usuarioSeleccionado.setNombreUsuario(txtNombreUsuario.getText().trim());
        usuarioSeleccionado.setRolUsuario(cmbRol.getValue());
        usuarioSeleccionado.setCorreoUsuario(txtCorreo.getText().trim());
        usuarioSeleccionado.setContrasena(txtContrasena.getText().trim());

        if (usuarioDAO.actualizar(usuarioSeleccionado)) {
            lblMensaje.setText("✓ Usuario actualizado exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargarUsuarios();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al actualizar el usuario");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void eliminarUsuario() {
        if (usuarioSeleccionado == null) {
            lblMensaje.setText("✗ Seleccione un usuario de la tabla");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este usuario?");
        alert.setContentText(usuarioSeleccionado.getNombreUsuario());

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (usuarioDAO.eliminar(usuarioSeleccionado.getIdUsuario())) {
                lblMensaje.setText("✓ Usuario eliminado");
                lblMensaje.setStyle("-fx-text-fill: green;");
                cargarUsuarios();
                limpiarCampos();
            } else {
                lblMensaje.setText("✗ Error al eliminar el usuario");
                lblMensaje.setStyle("-fx-text-fill: red;");
            }
        }
    }

    @FXML
    private void exportarPDF() {
        if (tblUsuarios.getItems().isEmpty()) {
            lblMensaje.setText("✗ No hay datos para exportar");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        String nombreArchivo = PDFGenerator.generarNombreArchivo("Reporte_Usuarios");
        File pdf = PDFGenerator.generarPDF(tblUsuarios, "Reporte de Usuarios", nombreArchivo);

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
        txtNombreUsuario.clear();
        txtCorreo.clear();
        txtContrasena.clear();
        cmbRol.setValue(null);
        usuarioSeleccionado = null;
        tblUsuarios.getSelectionModel().clearSelection();
        lblMensaje.setText("");
    }

    private void llenarCampos(Usuario usuario) {
        txtNombreUsuario.setText(usuario.getNombreUsuario());
        txtCorreo.setText(usuario.getCorreoUsuario());
        txtContrasena.setText(usuario.getContrasena());
        cmbRol.setValue(usuario.getRolUsuario());
    }

    private boolean validarCampos() {
        if (txtNombreUsuario.getText().trim().isEmpty() || txtCorreo.getText().trim().isEmpty() ||
                txtContrasena.getText().trim().isEmpty() || cmbRol.getValue() == null) {

            lblMensaje.setText("✗ Todos los campos son obligatorios");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return false;
        }
        return true;
    }
}