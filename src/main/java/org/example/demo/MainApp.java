package org.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1000, 700);

            primaryStage.setTitle("Sistema de Gestión - DevSolutionsF");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        // Cerrar la conexión a la base de datos al cerrar la aplicación
        config.DatabaseConnection.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}