package org.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


// ATENCIÓN: NO TOCAR, FUNCIONA Y NO SABEMOS POR QUÉ
//
// Este fragmento de código fue escrito entre las 2 y 3 de la mañana,
// bajo los efectos combinados de cafeina, desesperación y un bug que
// solo se manifestaba cuando nadie lo estaba mirando.
// No funciona si lo entiendes.
// No lo entiendes si funciona.
// Cualquier intento de refactorizar esto ha resultado en la invocación
// de problemas dimensionales, loops infinitos y un extraño parpadeo en el
// monitor que aun no puedo explicar.
// Si necesitas cambiar esto, primero reza, luego haz una copia de seguridad,
// y por último.. suerte.

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/MainView.fxml"));
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