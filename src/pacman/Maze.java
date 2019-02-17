package pacman;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class Maze extends Parent {

    public static final boolean DEBUG = false;


    private int ghostEatenCount;

    public BooleanProperty gamePaused;

    private static final ScoreText[] SCORE_TEXT = {
            new ScoreText("200", false)
            ,
            new ScoreText("400", false)
            ,
            new ScoreText("800", false)

            ,
            new ScoreText("1600", false)
    };

    public PacMan pacMan;

    public final Ghost[] ghosts;

    private final DyingPacMan dyingPacMan;
    private static final Image PACMAN_IMAGE = new Image(Maze.class.getResourceAsStream("images/left1.png"));
    private final SimpleIntegerProperty level;
    private boolean addLifeFlag;
    private final SimpleIntegerProperty livesCount;
    public BooleanProperty waitForStart;

    private final Group messageBox;
    private final BooleanProperty lastGameResult;

    private final Text gameResultText;

    private int flashingCount;

    private final Timeline flashingTimeline;

    private final Group group;


    public Maze() {

        setFocused(true);

        gamePaused = new SimpleBooleanProperty(false);

        pacMan = new PacMan(this, 15, 24);

        final Ghost ghostBlinky = new Ghost(
                new Image(getClass().getResourceAsStream("images/ghostred1.png")),
                new Image(getClass().getResourceAsStream("images/ghostred2.png")),
                this,
                pacMan,
                15,
                14,
                0,
                -1,
                1);

        final Ghost ghostPinky = new Ghost(
                new Image(getClass().getResourceAsStream("images/ghostpink1.png")),
                new Image(getClass().getResourceAsStream("images/ghostpink2.png")),
                this,
                pacMan,
                14,
                15,
                1,
                0,
                5);

        final Ghost ghostInky = new Ghost(
                new Image(getClass().getResourceAsStream("images/ghostcyan1.png")),
                new Image(getClass().getResourceAsStream("images/ghostcyan2.png")),
                this,
                pacMan,
                12,
                15,
                1,
                0,
                20);

        final Ghost ghostClyde = new Ghost(
                new Image(getClass().getResourceAsStream("images/ghostorange1.png")),
                new Image(getClass().getResourceAsStream("images/ghostorange2.png")),
                this,
                pacMan,
                16,
                15,
                1,
                0,
                30);

        ghosts = new Ghost[]{ghostBlinky, ghostPinky, ghostInky, ghostClyde};

        dyingPacMan = new DyingPacMan(this);
        dyingPacMan.setCenterX(0);
        dyingPacMan.setCenterY(0);
        dyingPacMan.setRadiusX(13);
        dyingPacMan.setRadiusY(13);
        dyingPacMan.setStartAngle(90);
        dyingPacMan.setLength(360);
        dyingPacMan.setType(ArcType.ROUND);
        dyingPacMan.setFill(Color.YELLOW);
        dyingPacMan.setVisible(false);

        livesCount = new SimpleIntegerProperty(2);

        final ImageView livesImage1 = new ImageView(PACMAN_IMAGE);
        livesImage1.setX(MazeData.calcGridX(18));
        livesImage1.setY(MazeData.calcGridYFloat(MazeData.GRID_SIZE_Y + 0.8f));
        livesImage1.visibleProperty().bind(livesCount.greaterThan(0));
        livesImage1.setCache(true);
        final ImageView livesImage2 = new ImageView(PACMAN_IMAGE);
        livesImage2.setX(MazeData.calcGridX(16));
        livesImage2.setY(MazeData.calcGridYFloat(MazeData.GRID_SIZE_Y + 0.8f));
        livesImage2.visibleProperty().bind(livesCount.greaterThan(1));
        livesImage2.setCache(true);
        final ImageView livesImage3 = new ImageView(PACMAN_IMAGE);
        livesImage3.setX(MazeData.calcGridX(14));
        livesImage3.setY(MazeData.calcGridYFloat(MazeData.GRID_SIZE_Y + 0.8f));
        livesImage3.visibleProperty().bind(livesCount.greaterThan(2));
        livesImage3.setCache(true);
        final ImageView[] livesImage = new ImageView[]{livesImage1, livesImage2, livesImage3};

        level = new SimpleIntegerProperty(1);
        addLifeFlag = true;
        waitForStart = new SimpleBooleanProperty(true);

        messageBox = new Group();
        final Rectangle rectMessage = new Rectangle(MazeData.calcGridX(5),
                MazeData.calcGridYFloat(17.5f),
                MazeData.GRID_GAP * 19,
                MazeData.GRID_GAP * 5);
        rectMessage.setStroke(Color.RED);
        rectMessage.setStrokeWidth(5);
        rectMessage.setFill(Color.CYAN);
        rectMessage.setOpacity(0.75);
        rectMessage.setArcWidth(25);
        rectMessage.setArcHeight(25);

        final StringBinding messageBinding = new StringBinding() {

            {
                super.bind(gamePaused);
            }

            @Override
            protected String computeValue() {
                if (gamePaused.get()) {
                    return " PRESS 'P' BUTTON TO RESUME";
                } else {
                    return "   PRESS ANY KEY TO START!";
                }
            }
        };

        final Text textMessage = new Text(MazeData.calcGridX(6),
                MazeData.calcGridYFloat(20.5f),
                "   PRESS ANY KEY TO START!");
        textMessage.textProperty().bind(messageBinding);
        textMessage.setFont(new Font(18));
        textMessage.setFill(Color.RED);
        messageBox.getChildren().add(rectMessage);
        messageBox.getChildren().add(textMessage);

        lastGameResult = new SimpleBooleanProperty(false);

        final StringBinding lastGameResultBinding = new StringBinding() {

            {
                super.bind(lastGameResult);
            }

            @Override
            protected String computeValue() {
                if (lastGameResult.get()) {
                    return "  YOU WIN ";
                } else {
                    return "GAME OVER   ";
                }
            }
        };

        gameResultText = new Text(MazeData.calcGridX(11),
                MazeData.calcGridY(11) + 8,
                " YOU WIN ");
        gameResultText.textProperty().bind(lastGameResultBinding);
        gameResultText.setFont(new Font(20));
        gameResultText.setFill(Color.RED);
        gameResultText.setVisible(false);

        flashingCount = 0;

        flashingTimeline = new Timeline();
        flashingTimeline.setCycleCount(5);
        final KeyFrame kf = new KeyFrame(Duration.seconds(0.5), event -> {
            gameResultText.setVisible(!gameResultText.isVisible());
            if (++flashingCount == 5) {
                messageBox.setVisible(true);
                waitForStart.set(true);
            }
        });
        flashingTimeline.getKeyFrames().add(kf);

        group = new Group();

        final Rectangle blackBackground = new Rectangle(0, 0,
                MazeData.calcGridX(MazeData.GRID_SIZE_X + 2),
                MazeData.calcGridY(MazeData.GRID_SIZE_Y + 3));
        blackBackground.setFill(Color.BLACK);
        blackBackground.setCache(true);
        group.getChildren().add(blackBackground);

        group.getChildren().add(new WallRectangle(0, 0, MazeData.GRID_SIZE_X, MazeData.GRID_SIZE_Y));

        group.getChildren().add(new WallRectangle(14, -0.5f, 15, 4));
        group.getChildren().add(new WallBlackRectangle(13.8f, -1, 15.3f, 0));

        group.getChildren().add(new WallRectangle(2, 2, 5, 4));
        group.getChildren().add(new WallRectangle(7, 2, 12, 4));
        group.getChildren().add(new WallRectangle(17, 2, 22, 4));
        group.getChildren().add(new WallRectangle(24, 2, 27, 4));
        group.getChildren().add(new WallRectangle(2, 6, 5, 7));

        group.getChildren().add(new WallRectangle(14, 6, 15, 10));
        group.getChildren().add(new WallRectangle(10, 6, 19, 7));
        group.getChildren().add(new WallBlackLine(14.05f, 7, 14.95f, 7));

        group.getChildren().add(new WallRectangle(7.5f, 9, 12, 10));
        group.getChildren().add(new WallRectangle(7, 6, 8, 13));
        group.getChildren().add(new WallBlackLine(8, 9, 8, 10));

        group.getChildren().add(new WallRectangle(17, 9, 21.5f, 10));
        group.getChildren().add(new WallRectangle(21, 6, 22, 13));
        group.getChildren().add(new WallBlackLine(21, 9, 21, 10));

        group.getChildren().add(new WallRectangle(24, 6, 27, 7));

        group.getChildren().add(new WallRectangle(10, 12, 19, 17));
        group.getChildren().add(new WallRectangle(10.5f, 12.5f, 18.5f, 16.5f));
        final Rectangle cageRect = new Rectangle(MazeData.calcGridX(13),
                MazeData.calcGridY(12),
                MazeData.GRID_GAP * 3,
                MazeData.GRID_GAP / 2);
        cageRect.setStroke(Color.GREY);
        cageRect.setFill(Color.GREY);
        cageRect.setCache(true);
        group.getChildren().add(cageRect);

        group.getChildren().add(new WallRectangle(7, 15, 8, 20));

        group.getChildren().add(new WallRectangle(21, 15, 22, 20));

        group.getChildren().add(new WallRectangle(14, 19, 15, 23));
        group.getChildren().add(new WallRectangle(10, 19, 19, 20));
        group.getChildren().add(new WallBlackLine(14.05f, 20, 14.95f, 20));

        group.getChildren().add(new WallRectangle(4, 22, 5, 26));
        group.getChildren().add(new WallRectangle(2, 22, 5, 23));
        group.getChildren().add(new WallBlackRectangle(4, 22.05f, 5, 23.2f));

        group.getChildren().add(new WallRectangle(7, 22, 12, 23));
        group.getChildren().add(new WallRectangle(24, 22, 25, 26));
        group.getChildren().add(new WallRectangle(24, 22, 27, 23));
        group.getChildren().add(new WallBlackRectangle(24, 22.05f, 25, 23.2f));

        group.getChildren().add(new WallRectangle(17, 22, 22, 23));

        group.getChildren().add(new WallRectangle(-1, 25, 2, 26));
        group.getChildren().add(new WallRectangle(27, 25, MazeData.GRID_SIZE_X + 1, 26));

        group.getChildren().add(new WallRectangle(7, 25, 8, 29));
        group.getChildren().add(new WallRectangle(2, 28, 12, 29));
        group.getChildren().add(new WallBlackLine(7.05f, 28, 7.95f, 28));

        group.getChildren().add(new WallRectangle(14, 25, 15, 29));
        group.getChildren().add(new WallRectangle(10, 25, 19, 26));
        group.getChildren().add(new WallBlackLine(14.05f, 26, 14.95f, 26));

        group.getChildren().add(new WallRectangle(21, 25, 22, 29));
        group.getChildren().add(new WallRectangle(17, 28, 27, 29));
        group.getChildren().add(new WallBlackLine(21.05f, 28, 21.95f, 28));

        final Rectangle outerWall = new Rectangle(MazeData.calcGridXFloat(-0.5f),
                MazeData.calcGridYFloat(-0.5f),
                (MazeData.GRID_SIZE_X + 1) * MazeData.GRID_GAP,
                (MazeData.GRID_SIZE_Y + 1) * MazeData.GRID_GAP);
        outerWall.setStrokeWidth(MazeData.GRID_STROKE);
        outerWall.setStroke(Color.BLUE);
        outerWall.setFill(null);
        outerWall.setArcWidth(12);
        outerWall.setArcHeight(12);
        outerWall.setCache(true);
        group.getChildren().add(outerWall);

        group.getChildren().add(new WallRectangle(-1, 9, 5, 13));
        group.getChildren().add(new WallRectangle(-1, 9.5f, 4.5f, 12.5f));
        group.getChildren().add(new WallRectangle(-1, 15, 5, 20));
        group.getChildren().add(new WallRectangle(-1, 15.5f, 4.5f, 19.5f));

        group.getChildren().add(new WallRectangle(MazeData.GRID_SIZE_X - 5, 9, MazeData.GRID_SIZE_X + 1, 13));
        group.getChildren().add(new WallRectangle(MazeData.GRID_SIZE_X - 4.5f, 9.5f, MazeData.GRID_SIZE_X + 1, 12.5f));
        group.getChildren().add(new WallRectangle(MazeData.GRID_SIZE_X - 5, 15, MazeData.GRID_SIZE_X + 1, 20));
        group.getChildren().add(new WallRectangle(MazeData.GRID_SIZE_X - 4.5f, 15.5f, MazeData.GRID_SIZE_X + 1, 19.5f));

        group.getChildren().add(new WallBlackRectangle(-2, 8, -0.5f, MazeData.GRID_SIZE_Y));
        group.getChildren().add(new WallBlackRectangle(-0.5f, 8, 0, 9.5f));
        group.getChildren().add(new WallBlackRectangle(-0.5f, 19.5f, 0, MazeData.GRID_SIZE_Y));

        group.getChildren().add(new WallBlackRectangle(MazeData.GRID_SIZE_X + 0.5f, 8, MazeData.GRID_SIZE_X + 2, MazeData.GRID_SIZE_Y));
        group.getChildren().add(new WallBlackRectangle(MazeData.GRID_SIZE_X, 8, MazeData.GRID_SIZE_X + 0.5f, 9.5f));
        group.getChildren().add(new WallBlackRectangle(MazeData.GRID_SIZE_X, 19.5f, MazeData.GRID_SIZE_X + 0.5f, MazeData.GRID_SIZE_Y));

        group.getChildren().add(new WallBlackRectangle(-1, 13, 1, 15)); // left
        group.getChildren().add(new WallBlackRectangle(MazeData.GRID_SIZE_X - 1, 13, MazeData.GRID_SIZE_X + 1, 15));

        group.getChildren().add(new WallBlackLine(Color.BLUE, -0.5f, 9, -0.5f, 9.5f));
        group.getChildren().add(new WallBlackLine(Color.BLUE, -0.5f, 19.5f, -0.5f, 20));
        group.getChildren().add(new WallBlackLine(Color.BLUE, MazeData.GRID_SIZE_X + 0.5f, 9, MazeData.GRID_SIZE_X + 0.5f, 9.5f));
        group.getChildren().add(new WallBlackLine(Color.BLUE, MazeData.GRID_SIZE_X + 0.5f, 19.5f, MazeData.GRID_SIZE_X + 0.5f, 20));

        final Text textScore = new Text(MazeData.calcGridX(0),
                MazeData.calcGridY(MazeData.GRID_SIZE_Y + 2),
                "SCORE: " + pacMan.score);
        textScore.textProperty().bind(pacMan.score.asString("SCORE: %1d  "));
        textScore.setFont(new Font(20));
        textScore.setFill(Color.YELLOW);
        textScore.setCache(true);
        group.getChildren().add(textScore);

        group.getChildren().addAll(SCORE_TEXT);
        group.getChildren().add(dyingPacMan);
        group.getChildren().addAll(livesImage);
        group.getChildren().add(gameResultText);

        final Text textLevel = new Text(MazeData.calcGridX(22),
                MazeData.calcGridY(MazeData.GRID_SIZE_Y + 2),
                "LEVEL: " + level);
        textLevel.textProperty().bind(level.asString("LEVEL: %1d "));
        textLevel.setFont(new Font(20));
        textLevel.setFill(Color.YELLOW);
        textLevel.setCache(true);
        group.getChildren().add(textLevel);
        group.setFocusTraversable(true);
        group.setOnKeyPressed(ke -> onKeyPressed(ke));


        putDotHorizontally(2, 12, 1);
        putDotHorizontally(17, 27, 1);

        putDotHorizontally(2, 27, 5);

        putDotHorizontally(2, 5, 8);
        putDotHorizontally(24, 27, 8);

        putDotHorizontally(10, 13, 8);
        putDotHorizontally(16, 19, 8);

        putDotHorizontally(2, 12, 21);
        putDotHorizontally(17, 27, 21);

        putDotHorizontally(2, 2, 24);
        putDotHorizontally(27, 27, 24);

        putDotHorizontally(7, 12, 24);
        putDotHorizontally(17, 22, 24);

        putDotHorizontally(2, 5, 27);
        putDotHorizontally(24, 27, 27);

        putDotHorizontally(10, 12, 27);
        putDotHorizontally(17, 19, 27);

        putDotHorizontally(2, 27, 30);


        putDotVertically(1, 1, 8);
        putDotVertically(28, 1, 8);

        putDotVertically(1, 21, 24);
        putDotVertically(28, 21, 24);

        putDotVertically(1, 27, 30);
        putDotVertically(28, 27, 30);

        putDotVertically(3, 24, 27);
        putDotVertically(26, 24, 27);

        putDotVertically(6, 1, 27);
        putDotVertically(23, 1, 27);

        putDotVertically(9, 5, 8);
        putDotVertically(20, 5, 8);

        putDotVertically(9, 24, 27);
        putDotVertically(20, 24, 27);

        putDotVertically(13, 1, 4);
        putDotVertically(16, 1, 4);

        putDotVertically(13, 21, 24);
        putDotVertically(16, 21, 24);

        putDotVertically(13, 27, 30);
        putDotVertically(16, 27, 30);


        group.getChildren().add(pacMan);

        group.getChildren().addAll(ghosts);

        group.getChildren().add(new WallBlackRectangle(-2, 13, -0.5f, 15));
        group.getChildren().add(new WallBlackRectangle(29.5f, 13, 31, 15));

        group.getChildren().add(messageBox);


        getChildren().add(group);

        if (DEBUG) {
            MazeData.printData();
            MazeData.printDots();
        }
    }


    public void onKeyPressed(KeyEvent e) {

        if (waitForStart.get()) {
            waitForStart.set(false);
            startNewGame();
            return;
        }

        if (e.getCode() == KeyCode.P) {
            if (gamePaused.get()) {
                resumeGame();
            } else {
                pauseGame();
            }

            return;
        }

        if (gamePaused.get()) {
            return;
        }

        if (e.getCode() == KeyCode.DOWN) {
            pacMan.setKeyboardBuffer(MovingObject.MOVE_DOWN);
        } else if (e.getCode() == KeyCode.UP) {
            pacMan.setKeyboardBuffer(MovingObject.MOVE_UP);
        } else if (e.getCode() == KeyCode.RIGHT) {
            pacMan.setKeyboardBuffer(MovingObject.MOVE_RIGHT);
        } else if (e.getCode() == KeyCode.LEFT) {
            pacMan.setKeyboardBuffer(MovingObject.MOVE_LEFT);
        }

    }


    public final Dot createDot(int x1, int y1, int type) {
        Dot d = new Dot(MazeData.calcGridX(x1), MazeData.calcGridY(y1), type);

        if (type == MazeData.MAGIC_DOT) {
            d.playTimeline();

            d.shouldStopAnimation.bind(gamePaused.or(waitForStart));
        }
        MazeData.setData(x1, y1, type);

        MazeData.setDot(x1, y1, d);

        return d;
    }

    public final void putDotHorizontally(int x1, int x2, int y) {

        Dot dot;
        for (int x = x1; x <= x2; x++) {
            if (MazeData.getData(x, y) == MazeData.EMPTY) {
                int dotType;

                if ((x == 28 || x == 1) && (y == 3 || y == 24)) {
                    dotType = MazeData.MAGIC_DOT;
                } else {
                    dotType = MazeData.NORMAL_DOT;
                }

                dot = createDot(x, y, dotType);

                group.getChildren().add(dot);
            } else {
                if (DEBUG) {
                    System.out.println("!! WARNING: Trying to place horizontal dots at occupied position (" + x + ", " + y + ")");
                }
            }
        }
    }

    public final void putDotVertically(int x, int y1, int y2) {
        Dot dot;
        for (int y = y1; y <= y2; y++) {
            if (MazeData.getData(x, y) == MazeData.EMPTY) {
                int dotType;

                if ((x == 28 || x == 1) && (y == 3 || y == 24)) {
                    dotType = MazeData.MAGIC_DOT;
                } else {
                    dotType = MazeData.NORMAL_DOT;
                }

                dot = createDot(x, y, dotType);
                group.getChildren().add(dot);
            } else {
                if (DEBUG) {
                    System.out.println("!! WARNING: Trying to place vertical   dots at occupied position (" + x + ", " + y + ")");
                }
            }
        }
    }


    public void makeGhostsHollow() {

        ghostEatenCount = 0;

        for (Ghost g : ghosts) {
            g.changeToHollowGhost();
        }
    }


    public boolean hasMet(Ghost g) {

        int distanceThreshold = 22;

        int x1 = g.imageX.get();
        int x2 = pacMan.imageX.get();

        int diffX = Math.abs(x1 - x2);

        if (diffX >= distanceThreshold) {
            return false;
        }

        int y1 = g.imageY.get();
        int y2 = pacMan.imageY.get();
        int diffY = Math.abs(y1 - y2);

        if (diffY >= distanceThreshold) {
            return false;
        }

        if (diffY * diffY + diffX * diffX <= distanceThreshold * distanceThreshold) {
            return true;
        }

        return false;
    }

    public void pacManMeetsGhosts() {

        for (Ghost g : ghosts) {
            if (hasMet(g)) {
                if (g.isHollow) {
                    pacManEatsGhost(g);
                } else {
                    for (Ghost ghost : ghosts) {
                        ghost.stop();
                    }
                    pacMan.stop();

                    dyingPacMan.startAnimation(pacMan.imageX.get(), pacMan.imageY.get());
                    break;
                }
            }
        }
    }

    public void pacManEatsGhost(Ghost g) {

        ghostEatenCount++;

        int multiplier = 1;
        for (int i = 1; i <= ghostEatenCount; i++) {
            multiplier += multiplier;
        }

        pacMan.score.set(pacMan.score.get() + multiplier * 100);
        if (addLifeFlag && (pacMan.score.get() >= 10000)) {
            addLife();
        }

        ScoreText st = SCORE_TEXT[ghostEatenCount - 1];
        st.setX(g.imageX.get() - 10);
        st.setY(g.imageY.get());

        g.stop();
        g.resetStatus();
        g.trapCounter = -10;

        st.showText();

    }

    public void resumeGame() {

        if (!gamePaused.get()) {
            return;
        }

        messageBox.setVisible(false);

        for (Ghost g : ghosts) {
            if (g.isPaused()) {
                g.start();
            }
        }

        if (pacMan.isPaused()) {
            pacMan.start();
        }

        if (dyingPacMan.isPaused()) {
            dyingPacMan.start();
        }

        if (flashingTimeline.getStatus() == Animation.Status.PAUSED) {
            flashingTimeline.play();
        }

        gamePaused.set(false);

    }

    public void pauseGame() {

        if (waitForStart.get() || gamePaused.get()) {
            return;
        }

        messageBox.setVisible(true);

        for (Ghost g : ghosts) {
            if (g.isRunning()) {
                g.pause();
            }
        }

        if (pacMan.isRunning()) {
            pacMan.pause();
        }

        if (dyingPacMan.isRunning()) {
            dyingPacMan.pause();
        }

        if (flashingTimeline.getStatus() == Animation.Status.RUNNING) {
            flashingTimeline.pause();
        }
        gamePaused.set(true);
    }


    public void startNewGame() {

        messageBox.setVisible(false);
        pacMan.resetStatus();

        gameResultText.setVisible(false);

        if (!lastGameResult.get()) {
            level.set(1);
            addLifeFlag = true;
            pacMan.score.set(0);
            pacMan.dotEatenCount = 0;

            livesCount.set(2);
        } else {
            lastGameResult.set(false);
            level.set(level.get() + 1);
        }

        for (int x = 1; x <= MazeData.GRID_SIZE_X; x++) {
            for (int y = 1; y <= MazeData.GRID_SIZE_Y; y++) {
                Dot dot = (Dot) MazeData.getDot(x, y);

                if ((dot != null) && !dot.isVisible()) {
                    dot.setVisible(true);
                }
            }
        }
        for (Ghost g : ghosts) {
            g.resetStatus();
        }

    }

    public void startNewLevel() {

        lastGameResult.set(true);

        pacMan.hide();
        pacMan.dotEatenCount = 0;

        for (Ghost g : ghosts) {
            g.hide();
        }

        flashingCount = 0;
        flashingTimeline.playFromStart();
    }

    public void startNewLife() {

        if (livesCount.get() > 0) {
            livesCount.set(livesCount.get() - 1);
        } else {
            lastGameResult.set(false);
            flashingCount = 0;
            flashingTimeline.playFromStart();
            return;
        }

        pacMan.resetStatus();

        for (Ghost g : ghosts) {
            g.resetStatus();
        }
    }

    public void addLife() {

        if (addLifeFlag) {
            livesCount.set(livesCount.get() + 1);
            addLifeFlag = false;
        }
    }

}
