package com.dlsc.layoutfx.pane;

import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * Layout features:<br/>
 * 1: Child elements are located in the middle, and arranged from left to right, from top to bottom; <br/>
 * 2: The last row will still be arranged from left to right ;
 *
 * TODO: [V] 1. A boolean that turns animations on and off
 * TODO:     2. maximum width and height crm tile pane
 * TODO:     3. supportPadding
 */
public abstract class TilePaneBase extends Pane {

    protected static final double LAYOUT_ANIME_SPEED = 180;
    protected static final int DEFAULT_PREF_TILE_WIDTH = 100;
    protected static final int DEFAULT_PREF_TILE_HEIGHT = 100;
    protected Timeline layoutAnim;
    /**
     * The width of each child element
     */
    private SimpleDoubleProperty prefTileWidth;
    /**
     * the height of each child element
     */
    private SimpleDoubleProperty prefTileHeight;

    private  SimpleBooleanProperty enableAnimation;
    protected static final boolean DEFAULT_ENABLE_ANIMATION = true;


    private SimpleDoubleProperty hgap;
    private SimpleDoubleProperty vgap;

    public TilePaneBase() {
        getChildren().addListener((ListChangeListener<Node>) c -> customLayout());
        ChangeListener<Number> numberChangeListener = (ob, ov, nv) -> customLayout();

        widthProperty().addListener(numberChangeListener);
        heightProperty().addListener(numberChangeListener);

        prefWidthProperty().addListener(numberChangeListener);
        prefWidthProperty().addListener(numberChangeListener);

        maxWidthProperty().addListener(numberChangeListener);
        maxHeightProperty().addListener(numberChangeListener);

        minWidthProperty().addListener(numberChangeListener);
        minHeightProperty().addListener(numberChangeListener);

        prefTileHeightProperty().addListener(numberChangeListener);
        prefTileWidthProperty().addListener(numberChangeListener);

        enableAnimationProperty().addListener(it -> {
            customLayout();
        });

        hgapProperty().addListener(numberChangeListener);
        vgapProperty().addListener(numberChangeListener);
        sceneProperty().addListener(it -> {
            if (getScene() != null) {
                customLayout();
            }
        });
    }

    public final void setEnableAnimation(boolean enableAnimation) {
        enableAnimationProperty().set(enableAnimation);
    }

    public final boolean isEnableAnimation() {
        return enableAnimation == null ? DEFAULT_ENABLE_ANIMATION : enableAnimationProperty().get();
    }

    public final SimpleBooleanProperty enableAnimationProperty() {
        if (enableAnimation == null) {
            enableAnimation = new SimpleBooleanProperty(DEFAULT_ENABLE_ANIMATION);
        }
        return enableAnimation;
    }

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

    protected abstract void customLayout() ;

    protected void setBaseHeight(double h) {
        setMinHeight(h);
        setPrefHeight(h);
        setMaxHeight(h);
    }
}
