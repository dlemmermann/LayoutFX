package uk.co.senapt.desktop.shell.panes;

import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * Layout features:<br/>
 * 1: Child elements are located in the middle, and arranged from left to right, from top to bottom; <br/>
 * 2: The last row will still be arranged from left to right ;
 */
public abstract class CrmTilePaneBase extends Pane {
    
    protected static final double LAYOUT_ANIME_SPEED = 180;
    protected static final int DEFAULT_PREF_TILE_WIDTH = 100;
    protected static final int DEFAULT_PREF_TILE_HEIGHT = 100;

    protected final Timeline layoutAnim = new Timeline();

    /**
     * The width of each child element
     */
    private SimpleDoubleProperty prefTileWidth;
    /**
     * the height of each child element
     */
    private SimpleDoubleProperty prefTileHeight;


    private SimpleDoubleProperty hgap;
    private SimpleDoubleProperty vgap;

    public final SimpleDoubleProperty hgapProperty() {
        if (hgap == null) {
            hgap = new SimpleDoubleProperty(0);
        }
        return this.hgap;
    }

    public final double getHgap() {
        return hgap == null ? 0 : hgap.get();
    }

    public final void setHgap(final double hgap) {
        this.hgapProperty().set(hgap);
    }

    public final SimpleDoubleProperty vgapProperty() {
        if (vgap == null) {
            vgap = new SimpleDoubleProperty(0);
        }
        return this.vgap;
    }

    public final double getVgap() {
        return vgap == null ? 0 : vgap.get();
    }

    public final void setVgap(final double vgap) {
        this.vgapProperty().set(vgap);
    }

    public final SimpleDoubleProperty prefTileWidthProperty() {
        if (prefTileWidth == null) {
            prefTileWidth = new SimpleDoubleProperty(DEFAULT_PREF_TILE_WIDTH);
        }
        return this.prefTileWidth;
    }

    public final double getPrefTileWidth() {
        return prefTileWidth == null ? 0 : prefTileWidth.get();
    }

    public final void setPrefTileWidth(final double prefTileWidth) {
        this.prefTileWidthProperty().set(prefTileWidth);
    }

    public final SimpleDoubleProperty prefTileHeightProperty() {
        if (prefTileHeight == null) {
            prefTileHeight = new SimpleDoubleProperty(DEFAULT_PREF_TILE_HEIGHT);
        }
        return this.prefTileHeight;
    }

    public final double getPrefTileHeight() {
        return prefTileHeight == null ? 0 : prefTileHeight.get();
    }

    public final void setPrefTileHeight(final double prefTileHeight) {
        this.prefTileHeightProperty().set(prefTileHeight);
    }

    public CrmTilePaneBase() {
        getChildren().addListener((ListChangeListener<Node>) c -> customLayout());
        ChangeListener<Number> numberChangeListener = (ob, ov, nv) -> customLayout();
        widthProperty().addListener(numberChangeListener);
        prefWidthProperty().addListener(numberChangeListener);
        maxWidthProperty().addListener(numberChangeListener);
        minWidthProperty().addListener(numberChangeListener);
        prefTileHeightProperty().addListener(numberChangeListener);
        prefTileWidthProperty().addListener(numberChangeListener);
        hgapProperty().addListener(numberChangeListener);
        vgapProperty().addListener(numberChangeListener);
    }

    protected abstract void customLayout() ;


    protected void setBaseHeight(double h) {
        setMinHeight(h);
        setPrefHeight(h);
        setMaxHeight(h);
    }
}
