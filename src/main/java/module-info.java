module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Para iText7
    requires kernel;
    requires layout;
    requires io;

    opens org.example.demo to javafx.fxml;
    opens Controller to javafx.fxml;
    opens model to javafx.base;

    exports org.example.demo;
    exports Controller;
    exports util;
}