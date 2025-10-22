package Controller;

import dao.UnidadMedidaDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.UnidadMedida;
import util.PDFGenerator;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class UnidadMedidaController implements Initializable {

    @FXML private TextField txtNombre, txtAbreviatura;
    @FXML private TableView<UnidadMedida> tblUnidades;
    @FXML private TableColumn<UnidadMedida, Integer> colId;
    @FXML private TableColumn<UnidadMedida, String> colNombre, colAbreviatura;
    @FXML private Label lblMensaje;

    private UnidadMedidaDAO dao = new UnidadMedidaDAO();
    private UnidadMedida seleccionada;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("idUnidadMedida"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreUnidadMedida"));
        colAbreviatura.setCellValueFactory(new PropertyValueFactory<>("abreviaturaUnidadMedida"));
        cargar();

        tblUnidades.getSelectionModel().selectedItemProperty().addListener((obs, old, nueva) -> {
            if (nueva != null) {
                seleccionada = nueva;
                txtNombre.setText(nueva.getNombreUnidadMedida());
                txtAbreviatura.setText(nueva.getAbreviaturaUnidadMedida());
            }
        });
    }

    @FXML private void cargar() {
        tblUnidades.setItems(FXCollections.observableArrayList(dao.obtenerTodas()));
        lblMensaje.setText("Unidades cargadas: " + tblUnidades.getItems().size());
    }

    @FXML private void nuevo() {
        limpiarCampos();
        seleccionada = null;
        lblMensaje.setText("Nueva unidad de medida - complete los campos");
    }

    @FXML private void guardar() {
        if (!validarCampos()) return;

        UnidadMedida unidad = new UnidadMedida();
        unidad.setNombreUnidadMedida(txtNombre.getText().trim());
        unidad.setAbreviaturaUnidadMedida(txtAbreviatura.getText().trim());

        if (dao.insertar(unidad)) {
            lblMensaje.setText("✓ Unidad de medida guardada exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargar();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al guardar la unidad de medida");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML private void actualizar() {
        if (seleccionada == null) {
            lblMensaje.setText("✗ Seleccione una unidad de medida de la tabla");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!validarCampos()) return;

        seleccionada.setNombreUnidadMedida(txtNombre.getText().trim());
        seleccionada.setAbreviaturaUnidadMedida(txtAbreviatura.getText().trim());

        if (dao.actualizar(seleccionada)) {
            lblMensaje.setText("✓ Unidad de medida actualizada exitosamente");
            lblMensaje.setStyle("-fx-text-fill: green;");
            cargar();
            limpiarCampos();
        } else {
            lblMensaje.setText("✗ Error al actualizar la unidad de medida");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML private void eliminar() {
        if (seleccionada == null) {
            lblMensaje.setText("✗ Seleccione una unidad de medida de la tabla");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar esta unidad de medida?");
        alert.setContentText("Eliminar: " + seleccionada.getNombreUnidadMedida() + " (" + seleccionada.getAbreviaturaUnidadMedida() + ")");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (dao.eliminar(seleccionada.getIdUnidadMedida())) {
                lblMensaje.setText("✓ Unidad de medida eliminada exitosamente");
                lblMensaje.setStyle("-fx-text-fill: green;");
                cargar();
                limpiarCampos();
            } else {
                lblMensaje.setText("✗ Error al eliminar la unidad de medida");
                lblMensaje.setStyle("-fx-text-fill: red;");
            }
        }
    }

    @FXML
    private void exportarPDF() {
        if (tblUnidades.getItems().isEmpty()) {
            lblMensaje.setText("✗ No hay datos para exportar");
            lblMensaje.setStyle("-fx-text-fill: red;");
            return;
        }

        String nombreArchivo = PDFGenerator.generarNombreArchivo("Reporte_UnidadesMedida");
        File pdf = PDFGenerator.generarPDF(tblUnidades, "Reporte de Unidades de Medida", nombreArchivo);

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

    @FXML private void limpiarCampos() {
        txtNombre.clear();
        txtAbreviatura.clear();
        seleccionada = null;
        tblUnidades.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        lblMensaje.setStyle("");
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            lblMensaje.setText("✗ El nombre es obligatorio");
            lblMensaje.setStyle("-fx-text-fill: red;");
            txtNombre.requestFocus();
            return false;
        }

        if (txtAbreviatura.getText().trim().isEmpty()) {
            lblMensaje.setText("✗ La abreviatura es obligatoria");
            lblMensaje.setStyle("-fx-text-fill: red;");
            txtAbreviatura.requestFocus();
            return false;
        }

        if (txtAbreviatura.getText().trim().length() > 10) {
            lblMensaje.setText("✗ La abreviatura no puede tener más de 10 caracteres");
            lblMensaje.setStyle("-fx-text-fill: red;");
            txtAbreviatura.requestFocus();
            return false;
        }

        return true;
    }
}