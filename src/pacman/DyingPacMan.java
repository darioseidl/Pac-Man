package pacman;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.shape.Arc;
import javafx.util.Duration;

public class DyingPacMan extends Arc {

    private final Timeline timeline;

    private final SoundEffect dieEffect = new SoundEffect("sound/pac_die.wav");

    public DyingPacMan(final Maze maze) {

        timeline = new Timeline();
        timeline.setCycleCount(1);

        KeyFrame kf1 =
                new KeyFrame(Duration.millis(600),
                        event -> {
                            maze.pacMan.setVisible(false);

                            for (Ghost g : maze.ghosts) {
                                g.hide();
                            }

                            setVisible(true);
                        },
                        new KeyValue(startAngleProperty(), 90),
                        new KeyValue(lengthProperty(), 360)
                );

        KeyFrame kf2 =
                new KeyFrame(Duration.millis(1800),
                        event -> {
                            setVisible(false);
                            maze.startNewLife();
                        },
                        new KeyValue(startAngleProperty(), 270),
                        new KeyValue(lengthProperty(), 0)
                );


        timeline.getKeyFrames().addAll(kf1, kf2);
    }


    public void pause() {
        timeline.pause();
    }

    public void start() {
        timeline.play();
    }

    public boolean isRunning() {
        return timeline.getStatus() == Animation.Status.RUNNING;
    }

    public boolean isPaused() {
        return timeline.getStatus() == Animation.Status.PAUSED;
    }

    public void startAnimation(int x, int y) {

        dieEffect.playClip();

        setStartAngle(90);
        setCenterX(x);
        setCenterY(y);

        timeline.playFromStart();
    }

}
