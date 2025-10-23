package org.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Clase principal que inicia la aplicación con la pantalla de login
 */
public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Cargar la vista de login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/LoginView.fxml"));
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Login - DevSolutionsF");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();

        System.out.println("=================================================");
        System.out.println("  Sistema de Gestión DevSolutionsF");
        System.out.println("  Pantalla de Login Iniciada");
        System.out.println("=================================================");
    }

    @Override
    public void stop() {
        // Cerrar la conexión a la base de datos al cerrar la aplicación
        config.DatabaseConnection.closeConnection();
        System.out.println("\n✓ Aplicación cerrada correctamente");
    }

    public static void main(String[] args) {
        launch(args);
    }
}