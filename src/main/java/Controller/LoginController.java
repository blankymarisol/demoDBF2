package Controller;

import dao.UsuarioDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.LoginSession;
import model.Usuario;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblMensaje;
    @FXML private Button btnLogin;
    @FXML private CheckBox chkMostrarPassword;
    @FXML private TextField txtPasswordVisible;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar el checkbox para mostrar/ocultar contraseña
        txtPasswordVisible.setVisible(false);
        txtPasswordVisible.setManaged(false);

        chkMostrarPassword.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                txtPasswordVisible.setText(txtContrasena.getText());
                txtPasswordVisible.setVisible(true);
                txtPasswordVisible.setManaged(true);
                txtContrasena.setVisible(false);
                txtContrasena.setManaged(false);
            } else {
                txtContrasena.setText(txtPasswordVisible.getText());
                txtContrasena.setVisible(true);
                txtContrasena.setManaged(true);
                txtPasswordVisible.setVisible(false);
                txtPasswordVisible.setManaged(false);
            }
        });

        // Sincronizar los campos de contraseña
        txtPasswordVisible.textProperty().addListener((observable, oldValue, newValue) -> {
            if (chkMostrarPassword.isSelected()) {
                txtContrasena.setText(newValue);
            }
        });

        txtContrasena.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!chkMostrarPassword.isSelected()) {
                txtPasswordVisible.setText(newValue);
            }
        });

        // Permitir login con Enter
        txtContrasena.setOnAction(event -> iniciarSesion());
        txtPasswordVisible.setOnAction(event -> iniciarSesion());
    }

    @FXML
    private void iniciarSesion() {
        String correo = txtUsuario.getText().trim();
        String contrasena = chkMostrarPassword.isSelected() ?
                txtPasswordVisible.getText().trim() :
                txtContrasena.getText().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Por favor complete todos los campos");
            return;
        }

        try {
            // Intentar autenticar
            Usuario usuario = usuarioDAO.login(correo, contrasena);

            if (usuario != null) {
                // Iniciar sesión
                LoginSession.getInstance().iniciarSesion(usuario);

                mostrarExito("¡Bienvenido " + usuario.getNombreUsuario() + "!");

                // Pequeña pausa para que el usuario vea el mensaje
                new Thread(() -> {
                    try {
                        Thread.sleep(800);
                        javafx.application.Platform.runLater(this::abrirVentanaPrincipal);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            } else {
                mostrarError("Credenciales incorrectas");
                limpiarCampos();
            }

        } catch (Exception e) {
            mostrarError("Error al iniciar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirVentanaPrincipal() {
        try {
            // Cargar la ventana principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/MainView.fxml"));
            Parent root = loader.load();

            // Obtener el stage actual y cerrarlo
            Stage loginStage = (Stage) btnLogin.getScene().getWindow();

            // Crear y mostrar la ventana principal
            Stage mainStage = new Stage();
            mainStage.setTitle("Sistema de Gestión - DevSolutionsF");
            mainStage.setScene(new Scene(root, 1200, 750));
            mainStage.setMaximized(true);

            // Manejar el cierre de la ventana
            mainStage.setOnCloseRequest(event -> {
                LoginSession.getInstance().cerrarSesion();
                config.DatabaseConnection.closeConnection();
            });

            mainStage.show();
            loginStage.close();

        } catch (Exception e) {
            mostrarError("Error al abrir ventana principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void limpiarCampos() {
        txtUsuario.clear();
        txtContrasena.clear();
        txtPasswordVisible.clear();
        lblMensaje.setText("");
        txtUsuario.requestFocus();
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setText("✗ " + mensaje);
        lblMensaje.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.setText("✓ " + mensaje);
        lblMensaje.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
    }
}