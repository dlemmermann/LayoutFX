package com.dlsc.layoutfx.pane;

import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;

/**
 * Layout features:<br/>
 * 1: Child elements are located in the middle, and arranged from left to right, from top to bottom;<br/>
 * 2: The last row will still be arranged from left to right ;<br/>
 * 3: If the width and height of the container change, The width and height of the child elements remain the same;<br/>
 * 4: The spacing will change with the width and height;
 * 5: Child elements have the same width and height
 */
public class FixedSizeTilePane extends TilePaneBase {

    protected void customLayout() {

        ObservableList<Node> nodes = getChildren();
        int nodeSize = nodes.size();
        if (nodeSize == 0) {
            return;
        }
        double containerWidth = this.getWidth() - getHorInset();
        double tileWidth = computeTileWidth();
        //we set is the minimum gap
        double minHgap = getHgap();

        int rowTileCount = (int) Math.floor(containerWidth / (tileWidth + minHgap));
        //Maybe it will be 0, if it is 0, it will be assigned a value of 1
        rowTileCount = Math.max(1, rowTileCount);

        double hgap;
        double offsetX = 0;
        //Case only one column
        if (rowTileCount <= 1) {
            oneColumn = true;
            hgap = 0;
            offsetX = Math.max(0, (containerWidth - tileWidth) / 2.0);
        } else {
            //When Case multiple rows to compute hgap
            if (rowTileCount < nodeSize) {
                hgap = (containerWidth - rowTileCount * tileWidth) / (rowTileCount - 1);
                hgap = Math.max(minHgap, hgap);
            } else {
                //This is to calculate the gap between child nodes when there is only one row
                double totalLineWidth = tileWidth * nodeSize + minHgap * (nodeSize - 1);
                hgap = (containerWidth - totalLineWidth) / (nodeSize - 1) + minHgap;
            }
        }
        double rowCount = Math.ceil(nodeSize * 1.0 / rowTileCount);
        double height = computeTileHeight() * rowCount + (rowCount - 1) * getVgap() + getVerInset();
        //TODO: If the animation is turned off after startup, and the width of the Pane is quickly adjusted multiple times, the Node will not be visible on the interface, or the position of the Pane will move down.
        setBaseHeight(height);
        playLayoutAnim(rowTileCount, hgap, offsetX);

    }

    private void playLayoutAnim(int rowTileCount, double hgap, double offsetX) {
        ObservableList<Node> nodes = getChildren();
        int nodeSize = nodes.size();
        if (layoutAnim != null && layoutAnim.getStatus() == Animation.Status.RUNNING) {
            layoutAnim.stop();
        }
        if (getAnimated()) {
            if (layoutAnim == null) {
                System.out.println("创建了");
                layoutAnim = new Timeline();
            }
            KeyValue[] keyValues = new KeyValue[nodeSize * 2];
            for (int i = 0, j = 0; i < nodeSize; i++, j += 2) {
                Node node = nodes.get(i);
                keyValues[j] = new KeyValue(node.layoutXProperty(), getInset(Side.LEFT) + offsetX + (i % rowTileCount) * computeTileWidth() + (i % rowTileCount) * hgap);
                keyValues[j + 1] = new KeyValue(node.layoutYProperty(), getInset(Side.TOP) + (i / rowTileCount) * computeTileHeight() + (i / rowTileCount) * getVgap());
            }
            KeyFrame keyFrame = new KeyFrame(getAnimationDuration(), keyValues);
            layoutAnim.getKeyFrames().setAll(keyFrame);
            layoutAnim.play();
        } else {
            for (int i = 0, j = 0; i < nodeSize; i++, j += 2) {
                Node node = nodes.get(i);
                node.setLayoutX(getInset(Side.LEFT) + offsetX + (i % rowTileCount) * computeTileWidth() + (i % rowTileCount) * hgap);
                node.setLayoutY(getInset(Side.TOP) + (i / rowTileCount) * computeTileHeight() + (i / rowTileCount) * getVgap());
            }
        }
    }

    @Override
    protected double computeMinWidth(double height) {
        if (oneColumn) {
            return computeTileWidth() + getHorInset();
        }
        return super.computeMinWidth(height);
    }

}
