import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

import java.io.*;
import java.util.LinkedList;

public class Map {
    //GridPane for map
    private static GridPane mapUI = new GridPane();
    //array for logic of the map; if array contains 'S' then there Snake and '0' for empty space and 'F' for fruit(apple)
    private static char[][]mapLogic;
    //VBox is necessary for connect with scores and map
    private VBox mainGame;
    //GridPane for score
    private GridPane score;

    //partBodyX and partBodyY are points from body of the snake.
    private static LinkedList<Integer> partBodyX = new LinkedList<>();
    private static LinkedList<Integer> partBodyY = new LinkedList<>();

    //apple is score and Fruit class which put apple randomly in the map
    private int countCurrent = 0;
    private int record;
    private Label displayCurrentCount = new Label(String.valueOf(countCurrent));
    private Label displayRecord;
    private Fruit fruit = new Fruit();


    //get counts of the block by x and block by y
    Map(int width, int height){
        mapUI.setAlignment(Pos.CENTER);
        //create char array
        mapLogic = new char[height][width];
        //creating empty space
        for(int i = 0; i < mapLogic.length; i++){
            for(int j = 0; j < mapLogic[i].length; j++){
                mapUI.add(new Rectangle(Settings.sizePixel, Settings.sizePixel), j, i);
                mapLogic[i][j] = '0';
            }
        }

        try(DataInputStream dis = new DataInputStream(new FileInputStream("Progress.dat"))){
            record = dis.readInt();
            displayRecord = new Label(String.valueOf(record));
        }
        catch (IOException ex){
            //File is not found(((
            displayRecord = new Label("0");
        }
        displayCurrentCount.setFont(Font.font("Arial", FontPosture.REGULAR, 20));
        displayRecord.setFont(Font.font("Arial", FontPosture.REGULAR, 20));

    }
    //send mapUI in Main class for install in the StackPane
    public HBox getMapUI(){
        ImageView imageView = new ImageView("medal.jpg");
        imageView.setFitWidth(Settings.sizePixel * 3);
        imageView.setFitHeight(Settings.sizePixel * 3);

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);

        displayRecord.setFont(Font.font("Arial", FontPosture.REGULAR, Settings.sizePixel * 3));
        displayRecord.setTextFill(Color.WHITE);
        displayCurrentCount.setFont(Font.font("Arial", FontPosture.REGULAR, Settings.sizePixel * 3));
        displayCurrentCount.setTextFill(Color.WHITE);

        gp.add(imageView, 0, 0);
        gp.add(displayRecord, 1, 0);
        gp.add(new Circle(Settings.sizePixel * 3 / 2.0, Color.RED), 0, 1);
        gp.add(displayCurrentCount, 1, 1);
        HBox vBox = new HBox(mapUI, gp);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    //spawn body of the snake
    public void snakeDefaultValue(int[]x, int[]y, Pane head, Color color){

        for(int i = 0; i < x.length; i++){
            //saving all points(x, y) length of snake
            partBodyX.addFirst(x[i]);
            partBodyY.addFirst(y[i]);

            //put body of the snake in the GridPane
            mapUI.add((i == 0 ? head:new Rectangle(Settings.sizePixel, Settings.sizePixel, new Color(color.getRed(), color.getGreen(), color.getBlue(), 1))), x[i], y[i]);
            //saving body of the snake in the mapLogic
            mapLogic[y[i]][x[i]] = 'S';
        }
        //create apple randomly places in the map
        fruit.randomApple();
    }
    //this method is used only by Player class
    public void snakeAction(Position pos, Pane head, Color color){
        //if snake is eating apple F - apple; S - Snake;
        if(mapLogic[pos.getY()][pos.getX()] == 'F'){
            //replace head of the snake on the body
            mapUI.add(new Rectangle(Settings.sizePixel, Settings.sizePixel, new Color(color.getRed(), color.getGreen(), color.getBlue(), 1)), partBodyX.getLast(), partBodyY.getLast());

            //replacing F in S. Because snake was eaten.
            mapLogic[pos.getY()][pos.getX()] = 'S';
            //current point for Snake
            partBodyY.addLast(pos.getY());
            partBodyX.addLast(pos.getX());
            //install head of the Snake in the map
            mapUI.add(head, pos.getX(), pos.getY());
            //creation new apple because of those apple is eaten
            fruit.randomApple();
            //for apple
            countScore();
        }
        //if snake knock itself over
        else if(mapLogic[pos.getY()][pos.getX()] == 'S'){
            System.out.println("Game over");
            System.exit(1);
        }
        //if in front of the snake is space
        else if(mapLogic[pos.getY()][pos.getX()] == '0'){
            //replace head of the snake on the body
            mapUI.add(new Rectangle(Settings.sizePixel, Settings.sizePixel, new Color(color.getRed(), color.getGreen(), color.getBlue(), 1)), partBodyX.getLast(), partBodyY.getLast());
            mapLogic[pos.getY()][pos.getX()] = 'S';
            mapLogic[partBodyY.getFirst()][partBodyX.getFirst()] = '0';

            partBodyY.addLast(pos.getY());
            partBodyX.addLast(pos.getX());

            mapUI.add(head, pos.getX(), pos.getY());
            mapUI.add(new Rectangle(Settings.sizePixel, Settings.sizePixel), partBodyX.getFirst(), partBodyY.getFirst());

            //remove last tail of the snake because snake is moving
            partBodyY.removeFirst();
            partBodyX.removeFirst();
        }
    }

    public void countScore(){
        countCurrent++;
        if(countCurrent <= record){
            displayCurrentCount.setText(String.valueOf(countCurrent));
        }
        else{
            displayRecord.setText(String.valueOf(countCurrent));
            displayCurrentCount.setText(String.valueOf(countCurrent));
            try(DataOutputStream dos = new DataOutputStream(new FileOutputStream("Progress.dat"))){
                dos.writeInt(countCurrent);
            }
            catch (IOException ex){
                //
            }
        }

    }

    private static class Fruit{
        // вкусняшки которого нужно хавать 0_0
        public void randomApple(){
            //putting safely new apple in randomly place
            int x = (int)(Math.random() * mapLogic[0].length + 0);
            int y = (int)(Math.random() * mapLogic.length + 0);
            while(mapLogic[y][x] != '0'){
                x = (int)(Math.random() * mapLogic[0].length + 0);
                y = (int)(Math.random() * mapLogic.length + 0);
            }
            //after finding a safe place we put 'F'(apple) in the point and mapUI(GridPane)
            mapLogic[y][x] = 'F';
            mapUI.add(getAppleUI(), x, y);
        }
        public Circle getAppleUI(){
            return new Circle(Settings.sizePixel / 2.0, Color.RED);
        }
    }
}
