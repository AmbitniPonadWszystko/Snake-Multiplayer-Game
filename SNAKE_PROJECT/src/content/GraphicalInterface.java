package content;

import com.esotericsoftware.minlog.Log;
import com.sun.javafx.geom.Rectangle;
//import content.network.ClientPrime;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;

import javafx.animation.AnimationTimer;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;

import java.awt.Point;
import content.Enums.*;
import static content.Snake.board;
import static content.Snake.mask;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class GraphicalInterface extends Application {

    //---------------------------
    //private
    //javaFX windows and scene
    private Stage window;                   //entire window handler
    private Scene mainScene;                //scene that holds whole content

    //layouts
    private BorderPane borderPane;          //holds boardGridPane and infoGridPane within
    private GridPane boardGridPane;         //layout to store our labels(board)
    private GridPane infoGridPane;              //layout for info bar at the top of the window

    //Important for FPS calculations
    private long previousFrameTime;         //time in nanosecond of the latest frame

    //important for game
    private Integer playerNumber = 4;
    private Integer tourNumber = 1;
    //private Snake[] snakes = new Snake[playerNumber];
    private List<Point> inne2 = new ArrayList<>();

    private static final Semaphore sem = new Semaphore(1);

    //images
    private Image bg;                       //background
    private Image brick;                    //peripheral wall
    private Image infoBarBg;                //template from paint
    private Image white;
    private final static int sizeWidth = 120;          //Width of our Label board
    private final static int sizeHeight = 60;         //Height our Label board
    //layout elements(childrens)

    //mask containing position of snakes, walls, etc
    private Label[] names = new Label[playerNumber];
    private Label[] scores = new Label[playerNumber];
    private Label tour;
    //-----------------------------
    //static
    private final static int infoBarHeight = 80;      //constant variable which determines InfoBar Height

    private static String windowName = "SNAKE - alpha compilation test";
    private static int windowWidth = sizeWidth * 20;
    private static int windowHeight = (sizeHeight * 20) + infoBarHeight; //how many round have to be done until the game ends

    private static int fps = 4;             //how many frames/moves are in one second

    //----------------------------
    //methods
    /*  initializing just our board             */
    private void initBoard(boolean refreshOnly) {

        for (int x = 0; x < sizeWidth; x++) {
            for (int y = 0; y < sizeHeight; y++) {

                if (!refreshOnly) {///important(if snake dies, we dont need to reallocate memory for labels
                    board[x][y] = new Label();
                    board[x][y].setScaleX(0.5);
                    board[x][y].setScaleY(0.5);
                }//calling constructor
                //board[x][y].setGraphic(new ImageView(bg));  //always fill board with background color
                mask[x][y] = BarrierType.EMPTY;             //updating mask
                GridPane.setConstraints(board[x][y], x, y);       //bind board tile to proper COLUMN and ROW in our grid
                boardGridPane.getChildren().add(board[x][y]);   //finally add each of them
            }
        }

    }


    /*  initializing multiple Image references to class variables   */
    private void initImages() {
        //basic textures, we can use Colors instead of Images
        bg = new Image(getClass().getResourceAsStream("resources/bg.png"));   //bg - background
        brick = new Image(getClass().getResourceAsStream("resources/brick.png"));
        infoBarBg = new Image(getClass().getResourceAsStream("resources/infoBar.png"));

    }

    /*set the attributes for label*/
    public void initMap() {
        for (int x = 0; x < sizeWidth; x++) {
            for (int y = 0; y < sizeHeight; y++) {
                if (mask[x][y] == BarrierType.WALL) {
                    board[x][y].setGraphic(new ImageView(brick)); // adding walls 
                } else {
                    board[x][y].setGraphic(new ImageView(bg));  //always fill board with background color
                }
            }
        }
    }

    @Override                                //override javaFX native method
    public void init() {
        //to change, need asking about names
//        snakes[0] = new Snake (new Point (1,1), "Ada", cp.client);
//        snakes[1] = new Snake (new Point (21,1), "Michal", cp.client);
//        snakes[2] = new Snake (new Point (5,1), "Mateusz", cp.client);
//        snakes[3] = new Snake (new Point (8,1), "Ania", cp.client);

        //moved here because Hbox requires InfoBg to be initialized
        initImages();                   //call Images initialization for further use

        //TOP
        infoGridPane = new GridPane();
        infoGridPane.setMinHeight(infoBarHeight);
        infoGridPane.getChildren().add(new ImageView(infoBarBg));

        //CENTER
        boardGridPane = new GridPane();
        boardGridPane.setPadding(new Insets(0, 0, 0, 0));    //0 pixel padding on each side
        boardGridPane.setVgap(-10);                         //vertical spacing between each label
        boardGridPane.setHgap(-10);                         //horizontal spacing

        //MAIN LAYOUT
        borderPane = new BorderPane();
        borderPane.setCenter(boardGridPane);              //board layout is in the center of main layout
        borderPane.setTop(infoGridPane);

        initBoard(false);               //call board initialization method
        initWalls(new PeripheralWall(sizeWidth, sizeHeight));
        initMap();
        //'false' means - force allocating memory for labels
        //initLabelToGridAssignment();    //bind board tiles to proper place in grid
//        initNames();
//        initScoreAndTour();
    }

    /* initializing walls*/
    public void initWalls(PeripheralWall peripheralWall) {
        for (Point w : peripheralWall.getWall()) {// 'w' means element
            // board[w.x][w.y].setGraphic(new ImageView(brick)); // adding walls 
            mask[w.x][w.y] = BarrierType.WALL; //upgrading mask
        }
    }

    /*initalizing new tour*/
    public void newTour() {
        System.out.println("Round " + tourNumber + " ended");
        tourNumber += 1;
        for (int i = 0; i < playerNumber; i++) {
            infoGridPane.getChildren().remove(scores[i]);
        }
        infoGridPane.getChildren().remove(tour);
        //initScoreAndTour();
    }

    //IT IS TECHNICALLY OUR MAIN //(learned from documentation)
    @Override                               //override javaFX native method
    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;              //must-have assignment
        window.setTitle(windowName);        //window TITLE

        mainScene = new Scene(borderPane, windowWidth, windowHeight);//10 left padding, 40*20 tiles space, 10 right padding

        Snake snake = new Snake();
        PeripheralWall peripheralWall = new PeripheralWall(sizeWidth, sizeHeight);

        //display wall only once
        //EVENT FOR KEYBOARD
        EventHandler<KeyEvent> keyEventEventHandler = event -> {
            snake.setLastKey(event.getCode());    //call snake method, to filter the input and choose further direction
            //event.consume();                 //don't allow to propagete event value further(next calls)
        };

        //add event handler constructed right above this line to WHOLE WINDOW(mainScene)^
        mainScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEventEventHandler);

        window.setScene(mainScene);
        window.show();                      //display mainScene on the window

        /* GAME LOOP. we must mull this over, how we'll handle everything in here*/
        AnimationTimer timer;
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                //helpful for managing frames, second is 10^9 nanoseconds
                long second = 100000000;
                long timeBetweenFrames = now - previousFrameTime;

                //simplifying must-have statements (right side returns TRUE or FALSE)
                boolean isAlive = (snake.getLifeStatus() == LifeStatus.ALIVE);
                boolean isProperFrame = (second / timeBetweenFrames <= fps || previousFrameTime == 0);

                //FPS = 1s/timeBetweenFrames or if it's first frame!!!
                // (because previousFrameTime is 0 before hitting the  if statement;
                if (isProperFrame) {
                    if (isAlive) {

                        snake.considerAction();         //update snake's position

                    }
                    previousFrameTime = now;            //save current frame as older than next 'now' values
                }
            }

        };
        timer.start();
    }

    public static void main(String[] args) {
        //tutorials are saying, that this main will not be useful in further project evaluation
        launch(args);//must-have call (javaFX standard)
    }
}
