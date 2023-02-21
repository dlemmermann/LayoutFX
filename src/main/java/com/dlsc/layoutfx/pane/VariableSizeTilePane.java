package com.dlsc.layoutfx.pane;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Layout features:<br/>
 * 1: Child elements are located in the middle, and arranged from left to right, from top to bottom; <br/>
 * 2: The last row will still be arranged from left to right ; <br/>
 * 3: If the width and height of the container change, the width and height of the child elements will also change Responsive change; <br/>
 * 4: The spacing will not change;
 * 5: Child elements have the same width and height
 */
public class DynamicSizeTilePane extends TilePaneBase {

    protected void customLayout() {
        ObservableList<Node> nodes = getChildren();
        int totalLen = nodes.size();
        if (totalLen == 0) {
            return;
        }
        double rowWidth = this.getWidth();
        double blankWidth = (rowWidth % (getPrefTileWidth() + getHgap())) - getHgap();
        int eachRowLen = (int) (rowWidth / (getPrefTileWidth() + getHgap()));
        if (eachRowLen == 0) {
            return;
        }
        if (eachRowLen > totalLen) {
            singleLineLayout(totalLen, nodes, rowWidth, eachRowLen);
        } else {
            multiLineLayout(totalLen, nodes, blankWidth, eachRowLen);
        }
    }

    private void multiLineLayout(int totalLen, ObservableList<Node> nodes, double blankWidth, int eachRowLen) {
        double big = blankWidth / eachRowLen;
        double cellWidth = getPrefTileWidth() + big;
        double cellHeight = getPrefTileHeight() * (cellWidth / getPrefTileWidth());
        setBaseHeight(((totalLen - 1) / eachRowLen + 1) * (cellHeight + getVgap()) - getVgap());
        playLayoutAnim(totalLen, nodes, eachRowLen, cellWidth, cellHeight);
    }

    private void singleLineLayout(int totalLen, ObservableList<Node> nodes, double rowWidth, int eachRowLen) {
        layoutAnim.stop();
        double cellWidth = (rowWidth - (getHgap() * (totalLen - 1))) / totalLen;
        double cellHeight = getPrefTileHeight() * (cellWidth / getPrefTileWidth());
        setBaseHeight(cellHeight);
        playLayoutAnim(totalLen, nodes, eachRowLen, cellWidth, cellHeight);
    }

    private void playLayoutAnim(int totalLen, ObservableList<Node> nodes, int rowLen, double cellWidth, double cellHeight) {
        KeyValue[] keyValues = new KeyValue[totalLen * 2];
        for (int i = 0, j = 0; i < totalLen; i++, j += 2) {
            Region pane = (Region) nodes.get(i);
            pane.setPrefSize(cellWidth, cellHeight);
            keyValues[j] = new KeyValue(pane.translateXProperty(), (i % rowLen) * cellWidth + (i % rowLen) * getHgap());
            keyValues[j + 1] = new KeyValue(pane.translateYProperty(), (i / rowLen) * cellHeight + (i / rowLen) * getVgap());
        }
        if (layoutAnim.getStatus() == Animation.Status.RUNNING) {
            layoutAnim.stop();
        }
        KeyFrame keyFrame = new KeyFrame(Duration.millis(LAYOUT_ANIME_SPEED), keyValues);
        layoutAnim.getKeyFrames().setAll(keyFrame);
        layoutAnim.play();
    }


}
