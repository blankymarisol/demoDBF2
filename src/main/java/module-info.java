module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // 👈 agrega esta línea

    opens org.example.demo to javafx.fxml;
    opens Controller to javafx.fxml; // 👈 si tienes tus controladores en ese paquete
    exports org.example.demo;
    exports Controller; // 👈 opcional, si lo necesitas fuera del módulo
}
