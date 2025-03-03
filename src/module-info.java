module com.example.bankingapp3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    exports com.example.bankingapp3;
    opens com.example.bankingapp3 to javafx.fxml;
    exports com.example.bankingapp3.Controllers;
    opens com.example.bankingapp3.Controllers to javafx.fxml;
}