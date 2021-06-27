import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {
    //path of the theme. Actually they is just images 0_0
    private String[]theme = {"\\Themes\\BlueScene.jpg", "\\Themes\\DarkScene.jpg", "\\Themes\\MainScene.jpg"};
    //choice for chooses of the theme
    private int choice = 0;

    //I declared mainStage in the data fields because we will be need close mainStage in another function
    Stage mainStage;
    //root contains buttons and sometimes gridPanes. declared because of access and manage with root in another functions
    VBox root;
    StackPane mainPane;

    @Override
    public void start(Stage primaryStage) {
        //For run with current settings and themes which installed previous time. Yes I save new configuration of the settings in .dat files -_0
        try(DataInputStream dis = new DataInputStream(new FileInputStream("settingConfig.dat"));
            DataInputStream theme = new DataInputStream(new FileInputStream("themeConfig.dat"))){
            choice = theme.readInt();
            Settings.screenResolution[0] = dis.readInt();
            Settings.screenResolution[1] = dis.readInt();
        }
        catch (IOException ex){
            Settings.screenResolution = new int[]{1280, 720};
        }
        //for avoid IndexOfBoundException in the array
        choice = (choice >= 0 && choice < 3) ? choice:2;

        mainStage = primaryStage;

        root = new VBox();
        //install new buttons and tittle in the root
        getPrimaryScene();

        //First of All we need to set images(theme) then set root in the mainStage
        ImageView imageView = new ImageView(theme[choice]);
        imageView.setFitWidth(Settings.screenResolution[0]);
        imageView.setFitHeight(Settings.screenResolution[1]);

        //installing
        mainPane = new StackPane(imageView, root);
        mainStage.setScene(new Scene(mainPane, Settings.screenResolution[0], Settings.screenResolution[1]));
        //run 0*0
        mainStage.show();

    }
    public void getPrimaryScene(){
        root.getChildren().clear();
        Label tittle = new Label("Snake");
        tittle.setFont(Font.font("Calibri Light", FontPosture.ITALIC, 80));
        tittle.setTextFill(Color.WHITE);

        Button play = new Button("Play");
        Button settings = new Button("Settings");
        Button close = new Button("Close");

        play.setMinSize(Settings.screenResolution[0] / 4.0, Settings.screenResolution[1] / 5.5);
        settings.setMinSize(Settings.screenResolution[0] / 4.0, Settings.screenResolution[1] / 5.5);
        close.setMinSize(Settings.screenResolution[0] / 4.0, Settings.screenResolution[1] / 5.5);
        play.setStyle("-fx-background-color: white");
        settings.setStyle("-fx-background-color: white");
        close.setStyle("-fx-background-color: white");

        root.getChildren().addAll(tittle, play, settings, close);
        root.setSpacing(30);
        root.setAlignment(Pos.TOP_CENTER);

        play.setOnAction(event -> {
            root.getChildren().removeAll(settings, close, play);
            root.getChildren().add(getSettingMap());
        });
        settings.setOnAction(event -> {
            root.getChildren().removeAll(settings, close, play);
            settings();
        });
        close.setOnAction(e -> System.exit(1));
    }
    public GridPane getSettingMap(){
        TextField x = new TextField("30");
        x.setMinSize(Settings.screenResolution[0] / 3.5, Settings.screenResolution[1] / 8.5);
        TextField y = new TextField("30");
        y.setMinSize(Settings.screenResolution[0] / 3.5, Settings.screenResolution[1] / 8.5);

        Label width = new Label("Width: ");
        width.setFont(Font.font("Arial", FontWeight.LIGHT, FontPosture.REGULAR, 40));
        width.setTextFill(Color.WHITE);
        Label height = new Label("Height: ");
        height.setTextFill(Color.WHITE);
        height.setFont(Font.font("Arial", FontWeight.LIGHT, FontPosture.REGULAR, 40));

        Button start = new Button("Start");
        start.setMinSize(Settings.screenResolution[0] / 4.5, Settings.screenResolution[1] / 5.5);


        Button close = new Button("close");
        close.setMinSize(Settings.screenResolution[0] / 4.5, Settings.screenResolution[1] / 5.5);
        close.setOnAction(e -> getPrimaryScene());


        GridPane group = new GridPane();
        group.setAlignment(Pos.CENTER);
        group.setVgap(30);
        group.setHgap(30);

        Label messageX = new Label();
        Label messageY = new Label();

        group.add(width, 0, 0);
        group.add(x, 0, 1);
        group.add(height, 1, 0);
        group.add(y, 1,1);
        group.add(close, 0, 2);
        group.add(start, 1, 2);
        group.add(messageX, 0, 3);
        group.add(messageY, 1, 3);

        start.setOnAction(event -> {
            int widthInt = Integer.parseInt(x.getText());
            int heightInt = Integer.parseInt(y.getText());

            Settings.sizePixel = Settings.screenResolution[1] / heightInt;
            if(widthInt >= 10 && widthInt <= 30 && heightInt >= 10 && heightInt <= 30){
                startGame(widthInt, heightInt);
            }
            else{
                messageX.setText("BlockX must be (10, 30)");
                messageY.setText("BlockY must be (10, 30)");
                messageX.setTextFill(new Color(1, 0, 0, 1));
                messageY.setTextFill(new Color(1, 0, 0, 1));
            }
        });
        return group;
    }

    //main function for run
    public void startGame(int width, int height){
        //install new number of block in the setting
        Settings.blockHeight = height;
        //creating object of the map class and installing blocks by rows and column
        Map map = new Map(width, height);
        //installing new map in the game
        Game game = new Game(map);
        //create object of the MyPlayer which has methods like moveUp(), moveDown(), moveRight(), moveLeft() et al.
        Player myPlayer = new MyPlayer();
        game.addPlayer(myPlayer);
        myPlayer.setSnake(width, height);

        //closing main stage for change stage
        mainStage.close();

        //scene image
        ImageView imageView = new ImageView(theme[choice]);
        imageView.setFitWidth(Settings.screenResolution[0]);
        imageView.setFitHeight(Settings.screenResolution[1]);
        //installing current theme and map of the game -_0
        mainPane = new StackPane(imageView, map.getMapUI());

        //running the game -_-
        Scene gameScene = new Scene(mainPane);
        Stage stage = new Stage();
        stage.setScene(gameScene);
        stage.show();

        //Control for user using gameScene we set new command in the MyPlayer class*.*
        gameScene.setOnKeyPressed(E ->{
            switch (E.getCode()){
                case RIGHT:myPlayer.moveRight();break;
                case LEFT:myPlayer.moveLeft();break;
                case DOWN:myPlayer.moveDown();break;
                case UP:myPlayer.moveUp();break;
                default:break;
            }
        });
    }


    public void settings(){
        GridPane root = new GridPane();

        root.setAlignment(Pos.CENTER);
        root.setVgap(40);
        root.setHgap(40);

        Label resolutionMessage = new Label("Screen resolution: ");
        resolutionMessage.setTextFill(Color.WHITE);
        resolutionMessage.setFont(Font.font("Arial", FontWeight.LIGHT, FontPosture.REGULAR, 40));
        MenuItem[]itemsResolution = {new MenuItem("1920x1080"), new MenuItem("1366x768"),
                        new MenuItem("1360x720"), new MenuItem("1280x720"),
                        new MenuItem("640x400")};
        MenuButton buttonResolution = new MenuButton(Settings.screenResolution[0] + "x" + Settings.screenResolution[1]);
        buttonResolution.setMinSize(Settings.screenResolution[0] / 6.0, Settings.screenResolution[1] / 7.0);
        buttonResolution.getItems().addAll(itemsResolution);
        for(int i = 0; i < itemsResolution.length; i++){
            int finalI = i;
            itemsResolution[i].setOnAction(e -> buttonResolution.setText(itemsResolution[finalI].getText()));
        }


        Label themeMessage = new Label("Theme: ");
        themeMessage.setTextFill(Color.WHITE);
        themeMessage.setFont(Font.font("Arial", FontWeight.LIGHT, FontPosture.REGULAR, 40));
        MenuItem[]itemsTheme = {new MenuItem("Blue Style"), new MenuItem("Dark Style"),
                new MenuItem("Green Style")};
        MenuButton buttonTheme = new MenuButton(itemsTheme[choice].getText());
        buttonTheme.setMinSize(Settings.screenResolution[0] / 6.0, Settings.screenResolution[1] / 7.0);
        buttonTheme.getItems().addAll(itemsTheme);
        for(int i = 0; i < itemsTheme.length; i++){
            int finalI = i;
            itemsTheme[i].setOnAction(e -> {
                buttonTheme.setText(itemsTheme[finalI].getText());
                choice = finalI;
            });
        }

        Button save = new Button("Save");
        save.setMinSize(Settings.screenResolution[0] / 6.0, Settings.screenResolution[1] / 7.0);
        save.setOnAction(e -> {
            try(DataOutputStream dis = new DataOutputStream(new FileOutputStream("settingConfig.dat"));
                DataOutputStream theme = new DataOutputStream(new FileOutputStream("themeConfig.dat"))){
                theme.writeInt(choice);
                //recording integer in the config
                dis.writeInt(Integer.parseInt(buttonResolution.getText().substring(0, buttonResolution.getText().indexOf("x"))));
                dis.writeInt(Integer.parseInt(buttonResolution.getText().substring(buttonResolution.getText().indexOf("x") + 1)));
                mainStage.close();
                //for reload and run with new Settings
                Main main = new Main();
                main.start(new Stage());
            }
            catch (IOException ex){
                System.out.print(ex.getMessage());
            }
        });
        Button close = new Button("close");
        close.setMinSize(Settings.screenResolution[0] / 6.0, Settings.screenResolution[1] / 7.0);
        root.add(resolutionMessage, 0, 0);
        root.add(buttonResolution, 1, 0);
        root.add(themeMessage, 0, 1);
        root.add(buttonTheme, 1, 1);
        root.add(close, 0, 2);
        root.add(save, 1, 2);
        this.root.getChildren().add(root);

        close.setOnAction(e -> {
            this.root.getChildren().remove(root);
            getPrimaryScene();});
    }


    public static void main(String[] args) {
        launch(args);
    }
}
