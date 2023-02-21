module com.dlsc.layoutfx.layoutfx {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.dlsc.layoutfx.layoutfx to javafx.fxml;
    exports com.dlsc.layoutfx.layoutfx;
}