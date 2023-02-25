package com.dlsc.layoutfx;

import com.dlsc.layoutfx.pane.FixedSizeTilePane;
import com.dlsc.layoutfx.pane.VariableSizeTilePane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;

import java.net.URL;
import java.util.ResourceBundle;

public class SamplerController implements Initializable  {

    @FXML
    private FixedSizeTilePane fixedSizeTilePane;

    @FXML
    private CheckBox animateFixedSizeTilePane;

    @FXML
    private VariableSizeTilePane variableSizeTilePane;

    @FXML
    private CheckBox animateVariableSizeTilePane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //fixedSizeTilePane.animatedProperty().bind(animateFixedSizeTilePane.selectedProperty());
        //variableSizeTilePane.animatedProperty().bind(animateVariableSizeTilePane.selectedProperty());
    }
}