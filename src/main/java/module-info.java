module com.dlsc.layoutfx.layoutfx {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.dlsc.layoutfx;
    exports com.dlsc.layoutfx.pane;

    opens com.dlsc.layoutfx to javafx.fxml;
}