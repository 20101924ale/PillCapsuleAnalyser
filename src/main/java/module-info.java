module com.example.pillanalyser {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires org.junit.jupiter.api;


    exports com.example.pillanalyser;
    opens com.example.pillanalyser to javafx.fxml;
    exports com.example.pillanalyser.benchmark;
    opens com.example.pillanalyser.benchmark to javafx.fxml;
}