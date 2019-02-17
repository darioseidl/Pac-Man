package pacman;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    Application.launch(Main.class, args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Pac-Man by Henry Zhang www.javafxgame.com and Patrick Webster");
    primaryStage.setWidth(MazeData.calcGridX(MazeData.GRID_SIZE_X + 2));
    primaryStage.setHeight(MazeData.calcGridY(MazeData.GRID_SIZE_Y + 4));

    final Group root = new Group();
    final Scene scene = new Scene(root);
    root.getChildren().add(new Maze());
    primaryStage.setScene(scene);
    primaryStage.show();
  }

}
