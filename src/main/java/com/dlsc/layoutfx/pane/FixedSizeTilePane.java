package com.dlsc.layoutfx.pane;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Duration;

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
        int totalLen = nodes.size();
        if (totalLen <= 0) {
            return;
        }
        double rowWidth = this.getWidth();
        int eachRowLen = (int) (rowWidth / (getPrefTileWidth() + getHgap()));
        if (eachRowLen == 0) {
            return;
        }
        if (eachRowLen > totalLen) {
            multiLineLayout(totalLen, nodes, eachRowLen);
        } else {
            multiLineLayout(totalLen, nodes, eachRowLen);
        }
    }

    private void multiLineLayout(int totalLen, ObservableList<Node> nodes, int eachRowLen) {
        double newHgap = (this.getWidth() - getPrefTileWidth() * eachRowLen) / (eachRowLen + 1);
        double rowSize = Math.ceil(getChildren().size() * 1.0 / eachRowLen);
        double cellHeight = getPrefTileHeight() * rowSize;
        setBaseHeight(cellHeight + (rowSize - 1) * getVgap());
        playLayoutAnim(totalLen, nodes, eachRowLen, newHgap);
    }

    private void playLayoutAnim(int totalLen, ObservableList<Node> nodes, int rowLen, double newHgap) {
        if (layoutAnim != null && layoutAnim.getStatus() == Animation.Status.RUNNING) {
            layoutAnim.stop();
        }
        if (isEnableAnimation()) {
            if (layoutAnim == null) {
                layoutAnim = new Timeline();
            }
            KeyValue[] keyValues = new KeyValue[totalLen * 2];
            for (int i = 0, j = 0; i < totalLen; i++, j += 2) {
                Region pane = (Region) nodes.get(i);
                keyValues[j] = new KeyValue(pane.translateXProperty(), (i % rowLen) * getPrefTileWidth() + (i % rowLen + 1) * newHgap);
                keyValues[j + 1] = new KeyValue(pane.translateYProperty(), (i / rowLen) * getPrefTileHeight() + (i / rowLen) * getVgap());
            }
            KeyFrame keyFrame = new KeyFrame(Duration.millis(LAYOUT_ANIME_SPEED), keyValues);
            layoutAnim.getKeyFrames().setAll(keyFrame);
            layoutAnim.play();
        } else {
            for (int i = 0, j = 0; i < totalLen; i++, j += 2) {
                Region pane = (Region) nodes.get(i);
                pane.setTranslateX((i % rowLen) * getPrefTileWidth() + (i % rowLen + 1) * newHgap);
                pane.setTranslateY((i / rowLen) * getPrefTileHeight() + (i / rowLen) * getVgap());
            }
        }

    }
}
