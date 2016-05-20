package content;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import javafx.scene.input.KeyCode;
import java.awt.Point;
import java.io.IOException;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.Scanner;
import content.Enums.*;
import content.Packet.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import static content.GraphicalInterface.*;
import javafx.geometry.Insets;

/**
 * Created by Michał Martyniak and company :P on 19.03.2016.
 */
public class Snake extends Listener {

    private static final Semaphore sem = new Semaphore(1);
    private final static int sizeWidth = 120;          //Width of our Label board
    private final static int sizeHeight = 60;
    
    public static Label[][] board = new Label[sizeWidth][sizeHeight];
    public static Label[] scores = new Label[4];
    public static Label tour;
    public static BarrierType[][] mask = new BarrierType[sizeWidth][sizeHeight];
    static Map<Integer, MPPlayer> players = new HashMap<Integer, MPPlayer>();

    private Point head;             //coordinates of snake's head
    private KeyCode lastKey;        //direction variable, allow to continue snake's movement in that direction constantly

    private Integer points;             //player's points
    private LifeStatus lifeStatus;  //to know more look a defined enum a few lines above this one ^
    private Image image;
    private String playerName;
    int connectionID = 0;

    public static Client client;
    public static Scanner scanner;
    private Point actualTranslation;
    private List<Point> inne = new ArrayList<>();

    private Image player1;
    private Image player2;
    private Image player3;
    private Image player4;
    private Image bg;
    
    private GraphicalInterface gui;
    private boolean ready;
    private boolean start;
    
    private boolean canMove = true;
    private KeyCode temKey;


    public Snake(GraphicalInterface graphicalInterface) {
        ready=false;
        start=false;
        gui=graphicalInterface;
        scanner = new Scanner(System.in);
        actualTranslation = new Point(0, 0);
        client = new Client();

        register();
        initImages();
        client.addListener(this);
        client.start();
        try {
            Log.info("Please Enter the IP");
            // 1 timeout, 2 - IP, 3 - PORT
            client.connect(500000000, "localhost", 7474, 7474);
        } catch (Exception ex) {
            ex.printStackTrace();
            client.stop();

        }

        lastKey = KeyCode.K;                      //no key is pressed at the beginning
        points = 0;
        lifeStatus = LifeStatus.ALIVE;            //snake is alive
        int[] temp=new int[4];
        initScoreAndTour(temp, 1);
    }

    public void initImages() {
        player1 = new Image(getClass().getResourceAsStream("resources/blue.png"));
        player2 = new Image(getClass().getResourceAsStream("resources/red.png"));
        player3 = new Image(getClass().getResourceAsStream("resources/yellow.png"));
        player4 = new Image(getClass().getResourceAsStream("resources/pink.png"));
        bg = new Image(getClass().getResourceAsStream("resources/bg.png"));
        
    }

    public void sendPoint() {
        if(lifeStatus==LifeStatus.ALIVE && start==true){
            PacketPoint p = new PacketPoint();
            p.x = head.x + actualTranslation.x;
            p.y = head.y + actualTranslation.y;
            p.id = connectionID;
            if(canMove == true)
            {
                client.sendTCP(p);
                canMove = false;
            }

        }
        else
            System.out.println("Waiting...");
    }


    /*  changes the coordinates of snake's head (and convert old head to new body element)  */
    //change to boolean later(for collision)
    public void considerAction() {
        
        actualTranslation.x = 0;
        actualTranslation.y = 0;
        //temporary helper that doesn't move our snake yet!!
        //it says, where snake should move
        //(proper value is after switch statement
        switch (lastKey) {
            case L:
                lifeStatus = LifeStatus.RESIGNED;   //Snake gave up completely in that round
                temKey=lastKey;
                return;                             //EXIT whole method, no further instructions must be executed!
            case W:
                actualTranslation.y = -1;            //one up
                temKey=lastKey;
                sendPoint();
                break;
            case S:
                actualTranslation.y = 1;             //one down
                temKey=lastKey;
                sendPoint();
                break;
            case A:
                actualTranslation.x = -1;            //one left
                temKey=lastKey;
                sendPoint();
                break;
            case D:
                actualTranslation.x = 1;             //one right
                temKey=lastKey;
                sendPoint();
                break;
            case R:
                lastKey=temKey;
                if(!ready){
                    PacketReadyPlayer ready=new PacketReadyPlayer();            //one right
                    client.sendUDP(ready);
                    this.ready=true;
                }
                break;
            default:                                 //no key - skip that method
                return;
        }
    }

    /*  returns only head coordinates (useful for drawing)  */
    public Point getHead() {
        return head;
    }

    public Image getImage() {
        return image;
    }

    public Integer getPoints() {
        return points;
    }

    public String getPlayerName() {
        return playerName;
    }

    /*  gets key from event and holds it as further direction   */
 /*  returns value of life ^^ */
    public LifeStatus getLifeStatus() {
        return lifeStatus;
    }

    /*set snake's life value. It is unused yet!*/
    public void setLife(LifeStatus value) {
        lifeStatus = value;
    }



    public void setLastKey(KeyCode key) {
        lastKey = key;
    }

    private void register() {
        //Some type of Serializer which encodes info to readable thing
        // something that can be send over network
        Kryo kryo = client.getKryo();

        kryo.register(Packet.PacketLoginRequested.class);
        kryo.register(Packet.PacketLoginAccepted.class);
        kryo.register(Packet.PacketMessage.class);
        kryo.register(Packet.PacketPoint.class);
        kryo.register(Packet.PacketPointAccepted.class);
        kryo.register(Packet.PacketPointRefused.class);
        kryo.register(Packet.PacketDead.class);
        kryo.register(Packet.PacketAddPlayer.class);
        kryo.register(Packet.PacketHead.class);
        kryo.register(Packet.PacketNewTour.class);
        kryo.register(Packet.PacketReadyPlayer.class);
        kryo.register(Packet.PacketStart.class);
    }

    public void connected(Connection cnctn) {
        Log.info("[CLIENT] You have connected.");
        client.sendTCP(new Packet.PacketLoginRequested());
    }

    public void disconnected(Connection cnctn) {
        Log.info("[CLIENT] You have disconnected.");
    }
    private void setter(Label label, int position){
        infoGridPane.setConstraints(label,0,0);
        infoGridPane.setMargin(label,new Insets(0,0,0,position));
        infoGridPane.getChildren().add(label);
    
    }
    public void removeScoreAndTour(){
        for (int i = 0; i < 4; i++) {
            infoGridPane.getChildren().remove(scores[i]);
        }
        infoGridPane.getChildren().remove(tour);
    }
    public void initScoreAndTour(int[] sc, int t){
        for(int i=0; i<4; i++){
            scores[i] = new Label((new Integer(sc[i])).toString());
            setter(scores[i],220+i*265);          
        } 
        tour = new Label("tura: "+ (new Integer(t)).toString());
        setter(tour,1100);
    
    
    }

    public void received(Connection c, Object o) {
        if (o instanceof Packet.PacketLoginAccepted) {
            boolean answer = ((Packet.PacketLoginAccepted) o).accepted;

            if (answer) {
                Log.info("Log success");              
            } else {                
                c.close();
                Log.info("Too many players");
                System.exit(0);
            }

        }
        if (o instanceof Packet.PacketMessage) {
            String message = ((Packet.PacketMessage) o).message;
            Log.info(message);
        }

        if (o instanceof Packet.PacketDead) {
            canMove = true;
            lifeStatus = LifeStatus.DEAD;
        }
        if (o instanceof Packet.PacketStart) {
            start=true;
            sendPoint();
            Log.info("startuje");
        }
        if (o instanceof Packet.PacketAddPlayer) {
            PacketAddPlayer packet = (PacketAddPlayer) o;
            MPPlayer newPlayer = new MPPlayer();
            players.put(packet.id, newPlayer);
            head = new Point(packet.x, packet.y);
            connectionID = packet.id;
            if (packet.id == 1) {
                image = player1;
            } else if (packet.id == 2) {
                image = player2;
            } else if (packet.id == 3) {
                image = player3;
            } else if (packet.id == 4) {
                image = player4;
            }

        }
        
        if (o instanceof PacketNewTour){
               
               Log.info("New Tour");
               int[] sc = new int[4];
               sc[2] = ((PacketNewTour) o).score1;
               sc[0] = ((PacketNewTour) o).score2;
               sc[3] = ((PacketNewTour) o).score3;
               sc[1] = ((PacketNewTour) o).score4;
               
            
               Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    removeScoreAndTour();
                    initScoreAndTour(sc, ((PacketNewTour) o).tour);
                    ready=false;
                    start=false;

                    switch (connectionID) {
                        case 1:
                            head.x=((PacketNewTour) o).x1;
                            head.y=((PacketNewTour) o).y1;
                            break;
                        case 2:
                            head.x=((PacketNewTour) o).x2;
                            head.y=((PacketNewTour) o).y2;
                            break;
                        case 3:
                            head.x=((PacketNewTour) o).x3;
                            head.y=((PacketNewTour) o).y3;
                            break;
                        case 4: 
                            head.x=((PacketNewTour) o).x4;
                            head.y=((PacketNewTour) o).y4;
                            break;
                        default:
                            break;
                    }
               
                    
                    for (int x = 1; x < sizeWidth-1; x++) {
                        for (int y = 1; y < sizeHeight-1; y++) {
                                board[x][y].setGraphic(new ImageView(bg));
                        }
                    }
                    board[((PacketNewTour) o).x1][((PacketNewTour) o).y1].setGraphic(new ImageView(player1));
                    if(((PacketNewTour) o).count>1)
                        board[((PacketNewTour) o).x2][((PacketNewTour) o).y2].setGraphic(new ImageView(player2));
                    if(((PacketNewTour) o).count>2)
                        board[((PacketNewTour) o).x3][((PacketNewTour) o).y3].setGraphic(new ImageView(player3));
                    if(((PacketNewTour) o).count>3)
                        board[((PacketNewTour) o).x4][((PacketNewTour) o).y4].setGraphic(new ImageView(player4));
                    lifeStatus=LifeStatus.ALIVE;
                    lastKey = KeyCode.K; 
                    
                }
            });
        }

        //Points for another snakes
        if (o instanceof PacketPoint) {
            int x = ((PacketPoint) o).x;
            int y = ((PacketPoint) o).y;
            int id = ((PacketPoint) o).id;
            //you can't update the UI from a thread that is not the 
            //application thread. But still, if you really want to modify your UI from a different thread 
            // use the Platform.runlater(new Runnable()) method. And put your modifications inside the Runnable object.
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //1 - blue, 2 - red, 3 - yellow, 4 - pinky winky
                    if (id == 1) {
                        board[x][y].setGraphic(new ImageView(player1));
                    }
                    if (id == 2) {
                        board[x][y].setGraphic(new ImageView(player2));
                    }
                    if (id == 3) {
                        board[x][y].setGraphic(new ImageView(player3));
                    }
                    if (id == 4) {
                        board[x][y].setGraphic(new ImageView(player4));
                    }
                    if (id == connectionID) {
                        canMove = true;
                        head.setLocation(new Point(x,y));               
                    }
                }
            });
        }
        if (o instanceof PacketHead) {
            int c1 = ((PacketHead) o).count;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (c1 >= 1) {
                        int x1 = ((PacketHead) o).x1;
                        int y1 = ((PacketHead) o).y1;
                        board[x1][y1].setGraphic(new ImageView(player1));
                    }
                    if (c1 >= 2) {
                        int x2 = ((PacketHead) o).x2;
                        int y2 = ((PacketHead) o).y2;
                        board[x2][y2].setGraphic(new ImageView(player2));
                    }
                    if (c1 >= 3) {
                        int x3 = ((PacketHead) o).x3;
                        int y3 = ((PacketHead) o).y3;
                        board[x3][y3].setGraphic(new ImageView(player3));
                    }
                    if (c1 >= 4) {
                        int x4 = ((PacketHead) o).x4;
                        int y4 = ((PacketHead) o).y4;
                        board[x4][y4].setGraphic(new ImageView(player4));
                    }
                }
            });
        }

    }

}
