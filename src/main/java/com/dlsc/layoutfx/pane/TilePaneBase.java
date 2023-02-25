package com.dlsc.layoutfx.pane;

import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.css.*;
import javafx.css.converter.*;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.*;

/**
 * Layout features:<br/>
 * 1: Child elements are located in the middle, and arranged from left to right, from top to bottom; <br/>
 * 2: The last row will still be arranged from left to right ;
 * <p>
 * TODO: [V] 1. A boolean that turns animations on and off
 * TODO:     2. maximum width and height crm tile pane
 * TODO: [V] 3. supportPadding
 */
public abstract class TilePaneBase extends Pane {

    private static final Duration DEFAULT_ANIMATION_DURATION = Duration.millis(180);
    private StyleableObjectProperty<Duration> animationDuration;

    protected Timeline layoutAnim;

    private StyleableBooleanProperty animated;
    protected static final boolean DEFAULT_ANIMATED = true;
    private DoubleProperty vgap;
    private DoubleProperty hgap;
    protected boolean oneColumn;

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

        animatedProperty().addListener(it -> customLayout());

        hgapProperty().addListener(numberChangeListener);
        vgapProperty().addListener(numberChangeListener);
        sceneProperty().addListener(it -> {
            if (getScene() != null) {
                customLayout();
            }
        });
    }

    // Calculate the maximum height of the child node as the height of the tile
    protected double computeTileHeight() {
        int size = getManagedChildren().size();
        double maxHeight = 0;
        for (int i = 0; i < size; i++) {
            Region node = (Region) getManagedChildren().get(i);
            if (node.getHeight() > maxHeight) {
                maxHeight = node.getHeight();
            }
        }
        return maxHeight;
    }

    // Calculate the maximum width of the child node as the width of the tile
    protected double computeTileWidth() {
        int size = getManagedChildren().size();
        double maxWidth = 0;
        for (int i = 0; i < size; i++) {
            Node node = getManagedChildren().get(i);
            double width = node.getLayoutBounds().getWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }

    public final void setAnimated(boolean animated) {
        animatedProperty().set(animated);
    }

    public final boolean getAnimated() {
        return animated == null ? DEFAULT_ANIMATED : animatedProperty().get();
    }

    public final StyleableBooleanProperty animatedProperty() {
        if (animated == null) {
            animated = new SimpleStyleableBooleanProperty(
                    StyleableProperties.ENABLE_ANIM, this, "animated", DEFAULT_ANIMATED);
        }
        return animated;
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

    public final StyleableObjectProperty<Duration> animationDurationProperty() {
        if (animationDuration == null) {
            animationDuration = new SimpleStyleableObjectProperty<>(StyleableProperties.ANIMATION_DURATION, this,
                    "animationDuration", DEFAULT_ANIMATION_DURATION);
        }
        return this.animationDuration;
    }

    public final Duration getAnimationDuration() {
        return animationDuration == null ? DEFAULT_ANIMATION_DURATION : animationDuration.get();
    }

    public final void setAnimationDuration(final Duration animationDuration) {
        this.animationDurationProperty().set(animationDuration);
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
                "-fx-animated", BooleanConverter.getInstance(), DEFAULT_ANIMATED) {

            @Override
            public StyleableProperty<Boolean> getStyleableProperty(TilePaneBase control) {
                return control.animatedProperty();
            }

            @Override
            public boolean isSettable(TilePaneBase control) {
                return control.animated == null || !control.animated.isBound();
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
        private static final CssMetaData<TilePaneBase, Duration> ANIMATION_DURATION = new CssMetaData<TilePaneBase, Duration>(
                "-fx-animation-duration", DurationConverter.getInstance(), DEFAULT_ANIMATION_DURATION) {

            @Override
            public boolean isSettable(TilePaneBase control) {
                return control.animationDuration == null || !control.animationDuration.isBound();
            }

            @Override
            public StyleableProperty<Duration> getStyleableProperty(TilePaneBase control) {
                return control.animationDurationProperty();
            }
        };
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Pane.getClassCssMetaData());
            Collections.addAll(styleables, ENABLE_ANIM, HGAP, VGAP, ANIMATION_DURATION);
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

    protected abstract void customLayout();

    protected void setBaseHeight(double h) {
        setMinHeight(h);
        setPrefHeight(h);
        setMaxHeight(h);
    }

    /**
     * computes the padding of the specified side
     */
    protected double getInset(Side side) {
        Insets padding = (getPadding() == null ? Insets.EMPTY : getPadding());
        if (Side.TOP == side) {
            return padding.getTop();
        } else if (Side.RIGHT == side) {
            return padding.getRight();
        } else if (Side.BOTTOM == side) {
            return padding.getBottom();
        } else {
            return padding.getLeft();
        }
    }

    /**
     * Calculate horizontal padding
     */
    protected double getHorInset() {
        Insets padding = (getPadding() == null ? Insets.EMPTY : getPadding());
        return padding.getRight() + padding.getLeft();
    }

    /**
     * calculate vertical padding
     */
    protected double getVerInset() {
        Insets padding = (getPadding() == null ? Insets.EMPTY : getPadding());
        return padding.getTop() + padding.getBottom();
    }

}
