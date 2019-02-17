package pacman;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Dot extends Parent {

    public BooleanProperty shouldStopAnimation;
    public int dotType;

    private IntegerProperty radius;

    private int animationRadius;
    private int delta;
    private Timeline timeline;


    public Dot(int x, int y, int dotType) {

        this.shouldStopAnimation = new SimpleBooleanProperty(false);
        this.dotType = dotType;

        if (dotType == MazeData.MAGIC_DOT) {
            this.radius = new SimpleIntegerProperty(5);
        } else {
            this.radius = new SimpleIntegerProperty(1);
        }

        delta = -1;
        animationRadius = 3;

        Circle circle = new Circle(x, y, this.radius.intValue(), Color.YELLOW);
        circle.radiusProperty().bind(this.radius);

        getChildren().add(circle);
    }


    private void createTimeline() {
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame kf = new KeyFrame(Duration.seconds(0.25), event -> doOneTick());

        timeline.getKeyFrames().add(kf);
    }

    public void playTimeline() {
        if (timeline == null) {
            createTimeline();
        }

        timeline.play();
    }

    public final void doOneTick() {

        if (!isVisible() || shouldStopAnimation.get()) {
            return;
        }

        animationRadius += delta;
        int x1 = Math.abs(animationRadius) + 3;

        if (x1 > 5) {
            delta = -delta;
        }

        this.radius.set(x1);
    }

}
