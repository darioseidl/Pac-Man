package pacman;

public class MoveDecision {

  public int x;
  public int y;

  public int score;

  public void evaluate(PacMan pacMan, boolean isHollow) {
    if (x < 1 || y < 1 || (y >= MazeData.GRID_SIZE_Y) || (x >= MazeData.GRID_SIZE_X)) {
      score = -1;
      return;
    }

    int status = MazeData.getData(x, y);
    if (status == MazeData.BLOCK) {
      score = -1;
      return;
    }

    int distance = Math.abs(x - pacMan.x) + Math.abs(y - pacMan.y);

    if (isHollow) {
      score = 500 + distance;
    }
    else {
      score = 500 - distance;
    }

  }

}
