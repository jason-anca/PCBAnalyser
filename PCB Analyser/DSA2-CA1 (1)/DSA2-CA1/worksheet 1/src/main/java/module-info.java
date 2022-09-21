module com.home.worksheet_1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires javafx.graphics;
    requires javafx.media;
    requires java.desktop;

    opens com.home.worksheet_1 to javafx.fxml;
    exports com.home.worksheet_1;
}