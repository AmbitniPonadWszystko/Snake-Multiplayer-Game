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

/**
 * Created by Michał Martyniak and company :P on 19.03.2016.
 */
public class Snake extends Listener {

    private static final Semaphore sem = new Semaphore(1);
    private final static int sizeWidth = 120;          //Width of our Label board
    private final static int sizeHeight = 60;
    public static Label[][] board = new Label[sizeWidth][sizeHeight];
    public static BarrierType[][] mask = new BarrierType[sizeWidth][sizeHeight];
    static Map<Integer, MPPlayer> players = new HashMap<Integer, MPPlayer>();

    private Point head;             //coordinates of snake's head
    private ArrayList<Point> body;  //holds body segments
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

    /*  create snake that has ony HEAD with given coordinates   */
    public Snake() {
        scanner = new Scanner(System.in);
        actualTranslation = new Point(0, 0);
        client = new Client();

        //register packets
        register();
        initImages();
        client.addListener(this);
        client.start();
        try {
            Log.info("Please Enter the IP");
            // 1 timeout, 2 - IP, 3 - PORT
            client.connect(500000000, "192.168.0.100", 7474, 7474);
        } catch (Exception ex) {
            ex.printStackTrace();
            client.stop();

        }

        //head = startingPoint;                     //head always exist
        body = new ArrayList<>();                 //at the beginning body is empty
        lastKey = KeyCode.K;                      //no key is pressed at the beginning

        points = 0;
        //playerName=name;
        lifeStatus = LifeStatus.ALIVE;            //snake is alive

    }

    public void initImages() {
        player1 = new Image(getClass().getResourceAsStream("resources/blue.png"));
        player2 = new Image(getClass().getResourceAsStream("resources/red.png"));
        player3 = new Image(getClass().getResourceAsStream("resources/yellow.png"));
        player4 = new Image(getClass().getResourceAsStream("resources/grey.png"));
    }

    public void sendPoint() {
        PacketPoint p = new PacketPoint();
        p.x = head.x + actualTranslation.x;
        p.y = head.y + actualTranslation.y;
        p.id = 0;
        client.sendTCP(p);
        //universal statement considering all cases of movement

    }


    /*  changes the coordinates of snake's head (and convert old head to new body element)  */
    //change to boolean later(for collision)
    public void considerAction() {
        //body.add(head);                             //save head as a body now
        actualTranslation.x = 0;
        actualTranslation.y = 0;
        //temporary helper that doesn't move our snake yet!!
        //it says, where snake should move
        //(proper value is after switch statement
        switch (lastKey) {
            case L:
                lifeStatus = LifeStatus.RESIGNED;   //Snake gave up completely in that round
                body.remove(body.size() - 1);       //! if adding head to body list was inappropriate
                return;                             //EXIT whole method, no further instructions must be executed!
            case W:
                actualTranslation.y = -1;            //one up
                sendPoint();
                break;
            case S:
                actualTranslation.y = 1;             //one down
                sendPoint();
                break;
            case A:
                actualTranslation.x = -1;            //one left
                sendPoint();
                break;
            case D:
                actualTranslation.x = 1;             //one right
                sendPoint();
                break;
            default:                                 //no key - skip that method
                return;
        }
        //actualTranslation holds always direction to which snake is following
        //move(actualTranslation); //wywalilem maske bitowa zrobic w domu dalej

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

    /*  returns list of ALL coordinates that belong to that snake */
    public ArrayList<Point> wholeSnake() {
        ArrayList<Point> wholeSnakeList = body;
        wholeSnakeList.add(head);
        return wholeSnakeList;
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
    }

    public void connected(Connection cnctn) {
        Log.info("[CLIENT] You have connected.");
        client.sendTCP(new Packet.PacketLoginRequested());
    }

    public void disconnected(Connection cnctn) {
        Log.info("[CLIENT] You have disconnected.");
    }

    public void received(Connection c, Object o) {
        if (o instanceof Packet.PacketLoginAccepted) {
            boolean answer = ((Packet.PacketLoginAccepted) o).accepted;

            if (answer) {
                Log.info("Log success"); //<--------------------------------------?tak to sie pisze?               
            } else {
                c.close();
            }

        }
        if (o instanceof Packet.PacketMessage) {
            String message = ((Packet.PacketMessage) o).message;
            Log.info(message);
        }

        if (o instanceof Packet.PacketDead) {
            lifeStatus = LifeStatus.DEAD;
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
                    //jezeli dostaniemy Point od węża niebieskiego 1 - niebieski
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
                        head.x = x;
                        head.y = y;
                      
                    }
                }
            });
        }
        if (o instanceof PacketHead) {
            int c1 = ((PacketHead) o).count;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //jezeli dostaniemy Point od węża niebieskiego 1 - niebieski
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

    public List<Point> getList() {
        return inne;
    }
}
