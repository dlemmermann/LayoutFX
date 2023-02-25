package com.dlsc.layoutfx.pane;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.util.*;

/**
 * Layout features:<br/>
 * 1: Child elements are located in the middle, and arranged from left to right, from top to bottom; <br/>
 * 2: The last row will still be arranged from left to right ; <br/>
 * 3: If the width and height of the container change, the width and height of the child elements will also change Responsive change; <br/>
 * 4: The spacing will not change;
 * 5: Child elements have the same width and height
 */
public class VariableSizeTilePane extends TilePaneBase {
    private static final int DEFAULT_MIN_TILE_WIDTH = 100;
    private static final int DEFAULT_MIN_TILE_HEIGHT = 100;
    protected DoubleProperty minTileWidth;
    protected DoubleProperty minTileHeight;
    private static final int DEFAULT_MAX_TILE_WIDTH = 180;
    private static final int DEFAULT_MAX_TILE_HEIGHT = 180;
    protected DoubleProperty maxTileWidth;
    protected DoubleProperty maxTileHeight;

    protected void customLayout() {
        ObservableList<Node> nodes = getChildren();
        int nodeSize = nodes.size();
        if (nodeSize == 0) {
            return;
        }
        double containerWidth = this.getWidth() - getHorInset();
        double minTileW = getMinTileWidth();
        double maxTileW = getMaxTileWidth();
        //we set is the minimum gap
        double minHgap = getHgap();
        int rowTileCount = (int) Math.floor(containerWidth / (minTileW + minHgap));
        //Maybe it will be 0, if it is 0, it will be assigned a value of 1
        rowTileCount = Math.max(1, rowTileCount);
        double newW;
        double hgap;
        double offsetX = 0;
        //Case only one column
        if (rowTileCount <= 1) {
            hgap = 0;
            oneColumn = true;
            if (containerWidth >= maxTileW) {
                newW = maxTileW;
                offsetX = Math.max(0, (containerWidth - newW) / 2.0);
            } else {
                newW = containerWidth;
                offsetX = 0;
            }
        } else {
            double extraSpace = containerWidth - rowTileCount * minTileW - (rowTileCount - 1) * minHgap;
            double increase = Math.min(extraSpace / rowTileCount, maxTileW - minTileW);
            newW = minTileW + increase;
            hgap = Math.max(minHgap, minHgap + (containerWidth - rowTileCount * newW - (rowTileCount - 1) * minHgap) / (rowTileCount - 1));
            if (rowTileCount >= nodeSize) {
                offsetX = (containerWidth - (newW * nodeSize + hgap * (nodeSize - 1))) / 2.0;
            }
        }
        double rowCount = Math.ceil(nodeSize * 1.0 / rowTileCount);
        double newH = Math.min((newW / minTileW) * getMinTileHeight(), getMaxTileHeight());
        //TODO: 1. When the animation is turned off at startup, the height of the Pane will be 0, and only padding will appear.
        //TODO: 2. If the animation is turned off after startup, and the width of the Pane is quickly adjusted multiple times, the Node will not be visible on the interface, or the position of the Pane will move down.
        setBaseHeight(newH * rowCount + (rowCount - 1) * getVgap() + getVerInset());
        playLayoutAnim(rowTileCount, newW, newH, offsetX, hgap);
    }

    private void playLayoutAnim(int rowLen, double tileWidth, double tileHeight, double offsetX, double gap) {
        ObservableList<Node> nodes = getChildren();
        int nodeSize = nodes.size();
        if (layoutAnim != null && layoutAnim.getStatus() == Animation.Status.RUNNING) {
            layoutAnim.stop();
        }
        if (getAnimated()) {
            if (layoutAnim == null) {
                layoutAnim = new Timeline();
            }
            KeyValue[] keyValues = new KeyValue[nodeSize * 2];
            for (int i = 0, j = 0; i < nodeSize; i++, j += 2) {
                Node node = nodes.get(i);
                if (node instanceof Region) {
                    Region pane = (Region) node;
                    pane.setPrefSize(tileWidth, tileHeight);
                    pane.setMinSize(tileWidth, tileHeight);
                    pane.setMaxSize(tileWidth, tileHeight);
                }
                keyValues[j] = new KeyValue(node.layoutXProperty(), getInset(Side.LEFT) + offsetX + (i % rowLen) * tileWidth + (i % rowLen) * gap);
                keyValues[j + 1] = new KeyValue(node.layoutYProperty(), getInset(Side.TOP) + (i / rowLen) * tileHeight + (i / rowLen) * getVgap());
            }
            KeyFrame keyFrame = new KeyFrame(getAnimationDuration(), keyValues);
            layoutAnim.getKeyFrames().setAll(keyFrame);
            layoutAnim.play();
        } else {
            for (int i = 0, j = 0; i < nodeSize; i++, j += 2) {
                Node node = nodes.get(i);
                if (node instanceof Region) {
                    Region pane = (Region) node;
                    pane.setPrefSize(tileWidth, tileHeight);
                    pane.setMaxSize(tileWidth, tileHeight);
                }
                node.setLayoutX(getInset(Side.LEFT) + offsetX + (i % rowLen) * tileWidth + (i % rowLen) * gap);
                node.setLayoutY(getInset(Side.TOP) + (i / rowLen) * tileHeight + (i / rowLen) * getVgap());
            }
        }
    }

    public final DoubleProperty minTileWidthProperty() {
        if (minTileWidth == null) {
            minTileWidth = new StyleableDoubleProperty(DEFAULT_MIN_TILE_WIDTH) {
                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public CssMetaData<VariableSizeTilePane, Number> getCssMetaData() {
                    return VariableSizeTilePane.StyleableProperties.MIN_TILE_WIDTH;
                }

                @Override
                public Object getBean() {
                    return VariableSizeTilePane.this;
                }

                @Override
                public String getName() {
                    return "minTileWidth";
                }
            };
        }
        return minTileWidth;
    }

    public final void setMinTileWidth(double value) {
        minTileWidthProperty().set(value);
    }

    public final double getMinTileWidth() {
        return minTileWidth == null ? DEFAULT_MIN_TILE_WIDTH : minTileWidth.get();
    }

    public final DoubleProperty minTileHeightProperty() {
        if (minTileHeight == null) {
            minTileHeight = new StyleableDoubleProperty(DEFAULT_MIN_TILE_HEIGHT) {
                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public CssMetaData<VariableSizeTilePane, Number> getCssMetaData() {
                    return VariableSizeTilePane.StyleableProperties.MIN_TILE_HEIGHT;
                }

                @Override
                public Object getBean() {
                    return VariableSizeTilePane.this;
                }

                @Override
                public String getName() {
                    return "minTileHeight";
                }
            };
        }
        return minTileHeight;
    }

    public final void setMinTileHeight(double value) {
        minTileHeightProperty().set(value);
    }

    public final double getMinTileHeight() {
        return minTileHeight == null ? DEFAULT_MIN_TILE_HEIGHT : minTileHeight.get();
    }

    public final DoubleProperty maxTileWidthProperty() {
        if (maxTileWidth == null) {
            maxTileWidth = new StyleableDoubleProperty(DEFAULT_MAX_TILE_WIDTH) {
                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public CssMetaData<VariableSizeTilePane, Number> getCssMetaData() {
                    return VariableSizeTilePane.StyleableProperties.MAX_TILE_WIDTH;
                }

                @Override
                public Object getBean() {
                    return VariableSizeTilePane.this;
                }

                @Override
                public String getName() {
                    return "maxTileWidth";
                }
            };
        }
        return maxTileWidth;
    }

    public final void setMaxTileWidth(double value) {
        maxTileWidthProperty().set(value);
    }

    public final double getMaxTileWidth() {
        return maxTileWidth == null ? DEFAULT_MAX_TILE_WIDTH : maxTileWidth.get();
    }

    public final DoubleProperty maxTileHeightProperty() {
        if (maxTileHeight == null) {
            maxTileHeight = new StyleableDoubleProperty(DEFAULT_MIN_TILE_HEIGHT) {
                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public CssMetaData<VariableSizeTilePane, Number> getCssMetaData() {
                    return VariableSizeTilePane.StyleableProperties.MAX_TILE_HEIGHT;
                }

                @Override
                public Object getBean() {
                    return VariableSizeTilePane.this;
                }

                @Override
                public String getName() {
                    return "maxTileHeight";
                }
            };
        }
        return maxTileHeight;
    }

    public final void setMaxTileHeight(double value) {
        maxTileHeightProperty().set(value);
    }

    public final double getMaxTileHeight() {
        return maxTileHeight == null ? DEFAULT_MAX_TILE_HEIGHT : maxTileHeight.get();
    }

    private static class StyleableProperties {
        private static final CssMetaData<VariableSizeTilePane, Number> MIN_TILE_WIDTH =
                new CssMetaData<VariableSizeTilePane, Number>("-fx-min-tile-width",
                        SizeConverter.getInstance(), DEFAULT_MIN_TILE_WIDTH) {

                    @Override
                    public boolean isSettable(VariableSizeTilePane node) {
                        return node.minTileWidth == null ||
                                !node.minTileWidth.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(VariableSizeTilePane node) {
                        return (StyleableProperty<Number>) node.minTileWidthProperty();
                    }
                };
        private static final CssMetaData<VariableSizeTilePane, Number> MIN_TILE_HEIGHT =
                new CssMetaData<VariableSizeTilePane, Number>("-fx-min-tile-height",
                        SizeConverter.getInstance(), DEFAULT_MIN_TILE_HEIGHT) {

                    @Override
                    public boolean isSettable(VariableSizeTilePane node) {
                        return node.minTileHeight == null ||
                                !node.minTileHeight.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(VariableSizeTilePane node) {
                        return (StyleableProperty<Number>) node.minTileHeightProperty();
                    }
                };

        private static final CssMetaData<VariableSizeTilePane, Number> MAX_TILE_WIDTH =
                new CssMetaData<VariableSizeTilePane, Number>("-fx-max-tile-width",
                        SizeConverter.getInstance(), DEFAULT_MAX_TILE_WIDTH) {

                    @Override
                    public boolean isSettable(VariableSizeTilePane node) {
                        return node.maxTileWidth == null ||
                                !node.maxTileWidth.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(VariableSizeTilePane node) {
                        return (StyleableProperty<Number>) node.maxTileWidthProperty();
                    }
                };
        private static final CssMetaData<VariableSizeTilePane, Number> MAX_TILE_HEIGHT =
                new CssMetaData<VariableSizeTilePane, Number>("-fx-max-tile-height",
                        SizeConverter.getInstance(), DEFAULT_MAX_TILE_HEIGHT) {

                    @Override
                    public boolean isSettable(VariableSizeTilePane node) {
                        return node.maxTileHeight == null ||
                                !node.maxTileHeight.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(VariableSizeTilePane node) {
                        return (StyleableProperty<Number>) node.maxTileHeightProperty();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(TilePaneBase.getClassCssMetaData());
            Collections.addAll(styleables, MIN_TILE_WIDTH, MIN_TILE_HEIGHT, MAX_TILE_WIDTH, MAX_TILE_HEIGHT);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return VariableSizeTilePane.StyleableProperties.STYLEABLES;
    }

    @Override
    protected double computeMinWidth(double height) {
        if (oneColumn) {
            return getMinTileWidth() + getHorInset();
        }
        return super.computeMinWidth(height);
    }

}
