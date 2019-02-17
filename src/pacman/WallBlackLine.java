package pacman;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class WallBlackLine extends Line {


    public WallBlackLine(float x1, float y1, float x2, float y2) {
        init(x1, y1, x2, y2);

        setStrokeWidth(MazeData.GRID_STROKE + 1);
        setStroke(Color.BLACK);
    }

    public WallBlackLine(Color lineColor, float x1, float y1, float x2, float y2) {
        init(x1, y1, x2, y2);

        setStrokeWidth(MazeData.GRID_STROKE);
        setStroke(lineColor);
    }

    private void init(float x1, float y1, float x2, float y2) {
        setCache(true);

        if (x1 == x2) {
            setStartX(MazeData.calcGridXFloat(x1));
            setStartY(MazeData.calcGridYFloat(y1) + MazeData.GRID_STROKE);
            setEndX(MazeData.calcGridXFloat(x2));
            setEndY(MazeData.calcGridYFloat(y2) - MazeData.GRID_STROKE);
        } else {
            setStartX(MazeData.calcGridXFloat(x1) + MazeData.GRID_STROKE);
            setStartY(MazeData.calcGridYFloat(y1));
            setEndX(MazeData.calcGridXFloat(x2) - MazeData.GRID_STROKE);
            setEndY(MazeData.calcGridYFloat(y2));
        }
    }

}
