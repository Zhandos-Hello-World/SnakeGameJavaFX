import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;

import java.util.Timer;
import java.util.TimerTask;


public class MyPlayer implements Player{
    private Map map;
    private Position pos;
    private int sizeH, sizeW;
    private boolean right, left, up, down;
    private SnakeBody snakeBody = new SnakeBody();
    private Color color = Color.GREEN;
    private boolean isAlive = true;
    private Timer timer;

    public void setSnake(int width, int height){
        this.sizeH = height;
        this.sizeW = width;


        int centerX = width / 2;
        int centerY = height / 2;
        if(width >= height){
            map.snakeDefaultValue(new int[]{centerX, centerX - 1, centerX - 2}, new int[]{centerY, centerY, centerY}, snakeBody.getHead(), color);
            defaultDirections();
            left = false;
        }
        else{
            map.snakeDefaultValue(new int[]{centerX, centerX, centerX}, new int[]{centerY, centerY - 1, centerY - 2}, snakeBody.getHead(), color);
            defaultDirections();
            down = false;
        }
        pos = new Position(centerX, centerY);
        starter();
    }

    private class Task extends TimerTask {
        public void run(){
            Platform.runLater(() -> {
                if(!right){
                    moveLeft();
                }
                else if(!left){
                    moveRight();
                }
                else if(!up){
                    moveDown();
                }
                else if(!down){
                    moveUp();
                }
                starter();
            });
        }
    }
    public void starter(){
        timer = new Timer();
        timer.schedule(new Task(), 5 * 25);
    }


    @Override
    public void setMap(Map map) {
        this.map = map;
    }



    @Override
    public void moveRight() {
        if(right && isAlive){
            defaultDirections();
            left = false;
            pos.setX(pos.getX() + 1);
            if(pos.getX() < sizeW) {
                map.snakeAction(pos, snakeBody.getHead(), color);
            }
            else{
                System.out.println("Game Over");
                isAlive = false;
                timer.cancel();
            }
        }
    }

    @Override
    public void moveLeft() {
        if(left && isAlive){
            defaultDirections();
            right = false;
            pos.setX(pos.getX() - 1);
            if(pos.getX() >= 0) {
                map.snakeAction(pos, snakeBody.getHead(), color);
            }
            else{
                System.out.println("Game Over");
                isAlive = false;
                timer.cancel();
            }
        }
    }

    @Override
    public void moveDown() {
        if(down && isAlive){
            defaultDirections();
            up = false;
            pos.setY(pos.getY() + 1);
            if(pos.getY() < sizeH) {
                map.snakeAction(pos, snakeBody.getHead(), color);
            }
            else{
                System.out.println("Game Over");
                isAlive = false;
                timer.cancel();
            }
        }
    }

    @Override
    public void moveUp() {
        if(up && isAlive){
            defaultDirections();
            down = false;

            pos.setY(pos.getY() - 1);
            if(pos.getY() >= 0) {
                map.snakeAction(pos, snakeBody.getHead(), color);
            }
            else{
                System.out.println("Game Over");
                isAlive = false;
                timer.cancel();
            }
        }
    }

    @Override
    public Position getPosition() {
        return pos;
    }

    public void defaultDirections(){
        right = true;
        left = true;
        up = true;
        down = true;
    }

    private class SnakeBody{
        public Pane getHead() {
            int size = Settings.sizePixel / 2;
            Arc arc = new Arc(size, size * 2, size, size, 0, 180);
            arc.setFill(color);
            Pane pane =  new Pane(arc, new Circle(size / 2.0, size + size / 3.0, size / 4.0, Color.WHITE), new Circle(size / 2.0 + size, size + size / 3.0, size / 4.0, Color.WHITE),
                    new Circle(size / 2.0, size + size / 3.0, size / 8.0, Color.BLACK), new Circle(size / 2.0 + size, size + size / 3.0, size / 8.0, Color.BLACK));
            if(!right){
                pane.setRotate(270);
            }
            else if(!left){
                pane.setRotate(90);
            }
            else if(!up){
                pane.setRotate(180);
            }
            else {
                pane.setRotate(0);
            }
            return pane;
        }

    }
}
