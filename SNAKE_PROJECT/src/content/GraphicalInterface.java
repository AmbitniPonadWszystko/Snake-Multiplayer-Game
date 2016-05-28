package content;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import content.Enums.*;
import static content.Snake.board;
import javafx.scene.layout.Pane;

import javafx.stage.StageStyle;

public class GraphicalInterface extends Application {

    //---------------------------
    //private
    //javaFX windows and scene
    private Stage window;                   //entire window handler
    private Scene mainScene;                //scene that holds whole content

    //layouts
    //private Pane borderPane;          //holds boardGridPane and infoGridPane within
    public static Pane pane;         //layout to store our labels(board)
    //public static Pane infoGridPane;              //layout for info bar at the top of the window
    
    //Important for FPS calculations
    private long previousFrameTime;         //time in nanosecond of the latest frame

    
    //images
    private Image bg;                       //background
    private Image brick;                    //peripheral wall
    private Image infoBarBg;                //template from paint
    private final static int sizeWidth = 120;          //Width of our Label board
    private final static int sizeHeight = 60;         //Height our Label board


    //-----------------------------
    //static
    private final static int infoBarHeight = 80;      //constant variable which determines InfoBar Height

    static LoginWindow loginWindow;
    private static String windowName = "Snake Multiplayer Game";
    private static int windowWidth = sizeWidth * 10;
    private static int windowHeight = (sizeHeight * 10) + infoBarHeight; //how many round have to be done until the game ends

    private static int fps = 4;             //how many frames/moves are in one second

    //mask containing position of snakes, walls, etc
    public static Label[] names = new Label[4];    
    //----------------------------
    //methods
    /*  initializing just our board             */
    public void initBoard(boolean refreshOnly) {

        for (int x = 0; x < sizeWidth; x++) {
            for (int y = 0; y < sizeHeight; y++) {

                if (!refreshOnly) {///important(if snake dies, we dont need to reallocate memory for labels
                    board[x][y] = new Label();
                    board[x][y].setLayoutX(10*x);
                    board[x][y].setLayoutY(10*y+80);
                    board[x][y].setMaxSize(0.5, 0.5);
                    pane.getChildren().add(board[x][y]);
                }
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
                if(x==0||x==sizeWidth-1||y==0||y==sizeHeight-1)
                    board[x][y].setGraphic(new ImageView(brick)); // adding walls 
                else
                    board[x][y].setGraphic(new ImageView(bg));  //always fill board with background color    
             
            }
        }
    }
    

    @Override                                //override javaFX native method
    public void init() {   
        initImages();                   //call Images initialization for further use

        //TOP
        pane = new Pane();
        pane.setMinHeight(infoBarHeight);
        pane.getChildren().add(new ImageView(infoBarBg));
        initBoard(false);               //call board initialization method
        initMap();
        
    }

    //IT IS TECHNICALLY OUR MAIN //(learned from documentation)
    @Override                               //override javaFX native method
    public void start(Stage primaryStage) throws Exception {
        loginWindow = new LoginWindow(primaryStage);
        LoginWindow.GameConfiguration config = loginWindow.getConfiguration();

        window = primaryStage;              //must-have assignment
        window.setTitle(windowName);        //window TITLE
        mainScene = new Scene(pane, windowWidth, windowHeight);//10 left padding, 40*20 tiles space, 10 right padding
        Snake snake = new Snake();
        
        //EVENT FOR KEYBOARD
        EventHandler<KeyEvent> keyEventEventHandler;
        keyEventEventHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                snake.setLastKey(event.getCode());    //call snake method, to filter the input and choose further direction
                
            }
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
