package com.dlsc.layoutfx.pane;

import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.css.*;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.SizeConverter;
import javafx.scene.Node;
import javafx.scene.layout.*;

import java.util.*;

/**
 * Layout features:<br/>
 * 1: Child elements are located in the middle, and arranged from left to right, from top to bottom; <br/>
 * 2: The last row will still be arranged from left to right ;
 * <p>
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
    /**
     * whether to enable layout animation;
     * true->enable
     */
    private StyleableBooleanProperty enableAnim;
    protected static final boolean DEFAULT_ENABLE_ANIM = true;

    private DoubleProperty vgap;
    private DoubleProperty hgap;

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

        enableAnimProperty().addListener(it -> customLayout());

        hgapProperty().addListener(numberChangeListener);
        vgapProperty().addListener(numberChangeListener);
        sceneProperty().addListener(it -> {
            if (getScene() != null) {
                customLayout();
            }
        });
    }

    public final void setEnableAnim(boolean enableAnim) {
        enableAnimProperty().set(enableAnim);
    }

    public final boolean getEnableAnim() {
        return enableAnim == null ? DEFAULT_ENABLE_ANIM : enableAnimProperty().get();
    }

    public final StyleableBooleanProperty enableAnimProperty() {
        if (enableAnim == null) {
            enableAnim = new SimpleStyleableBooleanProperty(
                    StyleableProperties.ENABLE_ANIM, this, "enableAnim", DEFAULT_ENABLE_ANIM);
        }
        return enableAnim;
    }

    public final DoubleProperty hgapProperty() {
        if (hgap == null) {
            hgap = new StyleableDoubleProperty() {
                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public CssMetaData<TilePaneBase, Number> getCssMetaData() {
                    return TilePaneBase.StyleableProperties.HGAP;
                }

                @Override
                public Object getBean() {
                    return TilePaneBase.this;
                }

                @Override
                public String getName() {
                    return "hgap";
                }
            };
        }
        return hgap;
    }

    public final void setHgap(double value) {
        hgapProperty().set(value);
    }

    public final double getHgap() {
        return hgap == null ? 0 : hgap.get();
    }

    public final DoubleProperty vgapProperty() {
        if (vgap == null) {
            vgap = new StyleableDoubleProperty() {
                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public CssMetaData<TilePaneBase, Number> getCssMetaData() {
                    return TilePaneBase.StyleableProperties.VGAP;
                }

                @Override
                public Object getBean() {
                    return TilePaneBase.this;
                }

                @Override
                public String getName() {
                    return "vgap";
                }
            };
        }
        return vgap;
    }

    public final void setVgap(double value) {
        vgapProperty().set(value);
    }

    public final double getVgap() {
        return vgap == null ? 0 : vgap.get();
    }

    private static class StyleableProperties {
        private static final CssMetaData<TilePaneBase, Number> HGAP =
                new CssMetaData<TilePaneBase, Number>("-fx-hgap",
                        SizeConverter.getInstance(), 0.0) {

                    @Override
                    public boolean isSettable(TilePaneBase node) {
                        return node.hgap == null ||
                                !node.hgap.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(TilePaneBase node) {
                        return (StyleableProperty<Number>) node.hgapProperty();
                    }
                };

        private static final CssMetaData<TilePaneBase, Boolean> ENABLE_ANIM = new CssMetaData<TilePaneBase, Boolean>(
                "-fx-enable-anim", BooleanConverter.getInstance(), true) {

            @Override
            public StyleableProperty<Boolean> getStyleableProperty(TilePaneBase control) {
                return control.enableAnimProperty();
            }

            @Override
            public boolean isSettable(TilePaneBase control) {
                return control.enableAnim == null || !control.enableAnim.isBound();
            }
        };

        private static final CssMetaData<TilePaneBase, Number> VGAP =
                new CssMetaData<TilePaneBase, Number>("-fx-vgap",
                        SizeConverter.getInstance(), 0.0) {

                    @Override
                    public boolean isSettable(TilePaneBase node) {
                        return node.vgap == null ||
                                !node.vgap.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(TilePaneBase node) {
                        return (StyleableProperty<Number>) node.vgapProperty();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Pane.getClassCssMetaData());
            Collections.addAll(styleables, ENABLE_ANIM, HGAP, VGAP);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
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

    protected abstract void customLayout();

    protected void setBaseHeight(double h) {
        setMinHeight(h);
        setPrefHeight(h);
        setMaxHeight(h);
    }
}
