package pacman;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Ghost extends MovingObject {

  private static final int TRAPPED = 10;

  private final PacMan pacMan;

  private static final Image HOLLOW_IMAGE1 = new Image(Ghost.class.getResourceAsStream("images/ghosthollow2.png"));

  private static final Image HOLLOW_IMAGE2 = new Image(Ghost.class.getResourceAsStream("images/ghosthollow3.png"));

  private static final Image HOLLOW_IMAGE3 = new Image(Ghost.class.getResourceAsStream("images/ghosthollow1.png"));
  private static final Image[] HOLLOW_IMG = new Image[] {
    HOLLOW_IMAGE1,
    HOLLOW_IMAGE2,
    HOLLOW_IMAGE1,
    HOLLOW_IMAGE2
  };

  private static final Image[] FLASH_HOLLOW_IMG = new Image[] {
    HOLLOW_IMAGE1,
    HOLLOW_IMAGE3,
    HOLLOW_IMAGE1,
    HOLLOW_IMAGE3
  };

  private static final int HOLLOW_MAX_TIME = 80;

  private int hollowCounter;

  private final Image[] defaultImg;

  private final int initialLocationX;
  private final int initialLocationY;
  private final int initialDirectionX;
  private final int initialDirectionY;

  private final int trapTime;
  public int trapCounter;

  private static final double CHANGE_FACTOR = 0.75;
  private static final double CHASE_FACTOR = 0.5;
  private int chaseCount;

  public boolean isHollow;


  public Ghost(Image defaultImage1,
          Image defaultImage2,
          Maze maze,
          PacMan pacMan,
          int x,
          int y,
          int xDirection,
          int yDirection,
          int trapTime) {

    this.maze = maze;
    this.pacMan = pacMan;
    this.x = x;
    this.y = y;
    this.xDirection = xDirection;
    this.yDirection = yDirection;
    this.trapTime = trapTime;

    defaultImg = new Image[] {
      defaultImage1,
      defaultImage2,
      defaultImage1,
      defaultImage2
    };
    images = defaultImg;
    chaseCount = 0;
    isHollow = false;

    trapCounter = 0;

    initialLocationX = x;
    initialLocationY = y;
    initialDirectionX = xDirection;
    initialDirectionY = yDirection;
    imageX = new SimpleIntegerProperty(MazeData.calcGridX(x));
    imageY = new SimpleIntegerProperty(MazeData.calcGridY(y));

    ImageView ghostNode = new ImageView(defaultImage1);
    ghostNode.xProperty().bind(imageX.add(-13));
    ghostNode.yProperty().bind(imageY.add(-13));
    ghostNode.imageProperty().bind(imageBinding);
    ghostNode.setCache(true);

    getChildren().add(ghostNode);
  }

  public void resetStatus() {
    x = initialLocationX;
    y = initialLocationY;

    xDirection = initialDirectionX;
    yDirection = initialDirectionY;

    isHollow = false;

    moveCounter = 0;
    trapCounter = 0;
    currentImage.set(0);

    imageX.set(MazeData.calcGridX(x));
    imageY.set(MazeData.calcGridY(y));

    images = defaultImg;
    state = TRAPPED;

    timeline.setRate(1.0);

    setVisible(true);
    start();
  }

  public void changeToHollowGhost() {
    hollowCounter = 0;
    isHollow = true;

    images = HOLLOW_IMG;

    timeline.stop();
    timeline.setRate(0.35);
    timeline.play();
  }

  private void changeDirectionXtoY(boolean mustChange) {
    if ( !mustChange && (Math.random() > CHANGE_FACTOR) ) {
      return;
    }

    MoveDecision goUp = new MoveDecision();
    goUp.x = this.x;
    goUp.y = this.y - 1;

    MoveDecision goDown = new MoveDecision();
    goDown.x = this.x;
    goDown.y = this.y + 1;

    goUp.evaluate(pacMan, isHollow);
    goDown.evaluate(pacMan, isHollow);

    if ( goUp.score < 0 && goDown.score < 0 ) {
      return;
    }

    if ( Math.random() < CHASE_FACTOR && chaseCount == 0 ) {
      chaseCount += (int) (Math.random() * 10 + 3);
    }

    MoveDecision continueGo = new MoveDecision();
    continueGo.x = this.x + xDirection;
    continueGo.y = this.y;
    continueGo.evaluate(pacMan, isHollow);

    if ( (continueGo.score > 0) && (continueGo.score > goUp.score)
         && (continueGo.score > goDown.score) && (chaseCount > 0) ) {
      chaseCount--;
      return;
    }

    int decision = -1;
    if (goUp.score  < 0) {
      decision = 1;
    }
    else {
      if (goDown.score > 0) {
        if (chaseCount > 0) {
          if (goDown.score > goUp.score) {
            decision = 1;
            chaseCount--;
          }
        }
        else {
          if (Math.random() > 0.5) {
            decision = 1;
          }
        }
      }
    }

    yDirection = decision;
    xDirection = 0;
  }

  private void changeDirectionYtoX(boolean mustChange) {

    if ( !mustChange && (Math.random() > CHANGE_FACTOR) ) {
    }

    MoveDecision goLeft = new MoveDecision();
    goLeft.x = this.x - 1;
    goLeft.y = this.y;

    MoveDecision goRight = new MoveDecision();
    goRight.x = this.x + 1;
    goRight.y = this.y;

    goLeft.evaluate(pacMan, isHollow);
    goRight.evaluate(pacMan, isHollow);

    if ( (goLeft.score < 0) && (goRight.score < 0) ) {
      return;
    }

    if ( (Math.random() < CHASE_FACTOR) && (chaseCount == 0) ) {
      chaseCount += (int) (Math.random() * 10 + 3);
    }

    MoveDecision continueGo = new MoveDecision();
    continueGo.x = this.x;
    continueGo.y = this.y + yDirection;
    continueGo.evaluate(pacMan, isHollow);

    if ( (continueGo.score > 0) && (continueGo.score > goLeft.score)
         && (continueGo.score > goRight.score) && (chaseCount > 0) ) {
      chaseCount--;
      return;
    }

    int decision = -1;
    if (goLeft.score  < 0) {
      decision = 1;
    }
    else {
      if (goRight.score > 0) {
        if (chaseCount > 0) {
          if (goRight.score > goLeft.score) {
            decision = 1;
            chaseCount--;
          }
        }
        else {
          if (Math.random() > 0.5) {
            decision = 1;
          }
        }
      }
    }

    xDirection = decision;
    yDirection = 0;
  }

  private void moveHorizontally() {

    moveCounter++;

    if (moveCounter > ANIMATION_STEP - 1) {
      moveCounter = 0;
      x += xDirection;
      imageX.set(MazeData.calcGridX(x));

      int nextX = xDirection + x;

      if ( y == 14 && ( nextX <= 1 || nextX >= 28) ) {
        if ( nextX < -1 && xDirection < 0 ) {
          x = MazeData.GRID_SIZE_X;
          imageX.set(MazeData.calcGridX(x));
        }
        else if (nextX > 30 && xDirection > 0) {
          x = 0;
          imageX.set(MazeData.calcGridX(x));
        }
      }
      else if (nextX < 0 || nextX > MazeData.GRID_SIZE_X) {
        changeDirectionXtoY(true);
      }
      else if (MazeData.getData(nextX, y) == MazeData.BLOCK) {
        changeDirectionXtoY(true);
      }
      else {
        changeDirectionXtoY(false);
      }

    }
    else {
      imageX.set(imageX.get() + (xDirection * MOVE_SPEED));
    }
  }

  private void moveVertically() {

    moveCounter++;

    if (moveCounter > ANIMATION_STEP - 1) {
      moveCounter = 0;
      y += yDirection;
      imageY.set(MazeData.calcGridX(y));

      int nextY = yDirection + y;
      if (nextY < 0 || nextY > MazeData.GRID_SIZE_Y) {
        changeDirectionYtoX(true);
      }
      else {
        if (MazeData.getData(x, nextY) == MazeData.BLOCK) {
          changeDirectionYtoX(true);
        }
        else {
          changeDirectionYtoX(false);
        }
      }
    }
    else {
      imageY.set(imageY.get() + (yDirection * MOVE_SPEED));
    }
  }

  private void moveHorizontallyInCage() {

    moveCounter++;

    if (moveCounter > ANIMATION_STEP - 1) {

      moveCounter = 0;
      x += xDirection;
      imageX.set(MazeData.calcGridX(x));

      int nextX = xDirection + x;

      if (nextX < 12) {
        xDirection = 0;
        yDirection = 1;
      }
      else if (nextX > 17) {
        xDirection = 0;
        yDirection = -1;
      }
    }
    else {
      imageX.set(imageX.get() + (xDirection * MOVE_SPEED));
    }
  }

  private void moveVerticallyInCage() {

    moveCounter++;

    if (moveCounter > ANIMATION_STEP - 1) {
      moveCounter = 0;
      y += yDirection;
      imageY.set(MazeData.calcGridX(y) + 8);

      int nextY = yDirection + y;

      if (nextY < 13) {
        yDirection = 0;
        xDirection = -1;
      }
      else if (nextY > 15) {
        yDirection = 0;
        xDirection = 1;
      }
    }
    else {
      imageY.set(imageY.get() + (yDirection * MOVE_SPEED));
    }
  }

  public void hide() {
    setVisible(false);
    timeline.stop();
  }

  @Override
  public void moveOneStep() {
    if (maze.gamePaused.get()) {
      if (!isPaused()) {
        timeline.pause();
      }
      return;
    }

    if ( state == MOVING || state == TRAPPED ) {
      if (xDirection != 0) {
        if (state == MOVING) {
          moveHorizontally();
        }
        else {
          moveHorizontallyInCage();
        }
      }
      else {
        if (yDirection != 0) {
          if (state == MOVING) {
            moveVertically();
          }
          else {
            moveVerticallyInCage();
          }
        }
      }

      if ( currentImage.get() < (ANIMATION_STEP - 1) ) {
        currentImage.set(currentImage.get() + 1);
      }
      else {
        currentImage.set(0);
        if (state == TRAPPED) {
          trapCounter++;

          if (trapCounter > trapTime && x == 14 && y == 13) {
            y = 12;

            xDirection = 0;
            yDirection = -1;
            state = MOVING;
          }
        }
      }
    }

    if (isHollow) {

      hollowCounter++;

      if (hollowCounter == HOLLOW_MAX_TIME - 30) {
        images = FLASH_HOLLOW_IMG;
      }
      else if (hollowCounter > HOLLOW_MAX_TIME) {
        isHollow = false;
        images = defaultImg;

        timeline.stop();
        timeline.setRate(1.0);
        timeline.play();
      }
    }

  }

}
