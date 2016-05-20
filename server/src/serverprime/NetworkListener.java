/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverprime;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;
import serverprime.Packet.*;

/**
 *
 * @author Mateusz
 */
//That will be pulling for any conections etc
public class NetworkListener extends Listener {

    int conectionCounter = 0;
    private static final Semaphore sem = new Semaphore(1);
    List<Player> listA = new ArrayList<Player>();
    PacketHead odp1 = new PacketHead();
    int deadPlayets = 0;
    int tour = 1;
    int readyPlayers = 0;
    private final static int sizeWidth = 120;
    private final static int sizeHeight = 60;
    private int tabDeadPlayer[] = new int[4];
    private int playersScore[] = new int[4];

    public void connected(Connection cnctn) {
        conectionCounter++;
        Player player = new Player();

        if (conectionCounter < 5) {
            if (conectionCounter == 1) {

                player.x = 9;
                player.y = 9;
                odp1.x1 = player.x;
                odp1.y1 = player.y;
                odp1.count = 1;
                player.c = cnctn;
                player.id = cnctn.getID();

            } else if (conectionCounter == 2) {

                player.x = 20;
                player.y = 20;
                odp1.x2 = player.x;
                odp1.y2 = player.y;
                odp1.count = 2;
                player.c = cnctn;
                player.id = cnctn.getID();
            } else if (conectionCounter == 3) {
                player.x = 2;
                player.y = 17;
                odp1.count = 3;
                odp1.x3 = player.x;
                odp1.y3 = player.y;
                player.c = cnctn;
                player.id = cnctn.getID();
            } else {
                player.x = 13;
                player.y = 20;
                odp1.x4 = player.x;
                odp1.y4 = player.y;
                odp1.count = 4;
                player.c = cnctn;
                player.id = cnctn.getID();

            }

            ServerPrime.server.sendToAllTCP(odp1);
            //ServerPrime.mask[player.x][player.y] = BarrierType.BLUE_SNAKE;
            System.out.println(player.id);
            PacketAddPlayer p = new PacketAddPlayer();
            p.x = player.x;
            p.y = player.y;
            p.id = player.id;
            //ServerPrime.server.sendToAllTCP(p);
            cnctn.sendTCP(p);
            //cnctn.sendTCP(p);
            Log.info("[SERVER] Someone has connected.");

        } else {
            Log.info("[SERVER] Too much!");
        }

    }

    public void disconnected(Connection cnctn) {
        Log.info("[SERVER] Someone has disconnected.");
    }

    public void newTour(Connection c) {
        if (tour <= 5) {
            Log.info("New Tour");
            tour += 1;
            for (int i = 0; i < 4; i++) {
                playersScore[i] += tabDeadPlayer[i];
            }
            Random generator = new Random();
            PacketNewTour odp = new PacketNewTour();
            odp.x1 = generator.nextInt(sizeWidth - 2) + 1;
            odp.y1 = generator.nextInt(sizeHeight - 2) + 1;
            odp.x2 = generator.nextInt(sizeWidth - 2) + 1;
            odp.y2 = generator.nextInt(sizeHeight - 2) + 1;
            odp.x3 = generator.nextInt(sizeWidth - 2) + 1;
            odp.y3 = generator.nextInt(sizeHeight - 2) + 1;
            odp.x4 = generator.nextInt(sizeWidth - 2) + 1;
            odp.y4 = generator.nextInt(sizeHeight - 2) + 1;
            odp.score1 = playersScore[0];
            odp.score2 = playersScore[1];
            odp.score3 = playersScore[2];
            odp.score4 = playersScore[3];
            odp.count = conectionCounter;
            odp.tour = tour;
            ServerPrime.initBoard();

            ServerPrime.server.sendToAllTCP(odp);
            readyPlayers = 0;
            deadPlayets = 0;
        } else {
            System.out.println("END");
        }

    }

    public void received(Connection c, Object o) {
        if (o instanceof PacketReadyPlayer) {
            readyPlayers += 1;
            if (readyPlayers == conectionCounter) {
                PacketStart odp = new PacketStart();
                ServerPrime.server.sendToAllTCP(odp);

            }

        }
        if (o instanceof PacketLoginRequested) {
            PacketLoginAccepted loginAnswer = new PacketLoginAccepted();
            if (conectionCounter < 5) {
                loginAnswer.accepted = true;
            } else {
                loginAnswer.accepted = false;
            }
            c.sendUDP(loginAnswer);
        }
        if (o instanceof PacketMessage) {
            String message = ((PacketMessage) o).message;
            Log.info(ServerPrime.connections.toString());
            Log.info(message);
        }
        if (o instanceof PacketPoint) {
            sem.acquireUninterruptibly();
            int x = ((PacketPoint) o).x;
            int y = ((PacketPoint) o).y;
            Log.info("Odebrano nowy point" + x + " " + y);
            //jezeli pkt jest ok
            if (ServerPrime.mask[x][y] == BarrierType.EMPTY) {

                ServerPrime.mask[x][y] = BarrierType.BLUE_SNAKE;
                PacketPoint p = new PacketPoint();
                p.x = x;
                p.y = y;
                p.id = c.getID();
                ServerPrime.server.sendToAllTCP(p);

            } else {
                Log.info("Umieeram");
                PacketDead odp = new PacketDead();
                tabDeadPlayer[((PacketPoint) o).id - 1] = deadPlayets + 1;
                deadPlayets += 1;
                if (deadPlayets == conectionCounter) {
                    newTour(c);
                }
                c.sendUDP(odp);
                //ServerPrime.server.sendToUDP(c.getID(), odp);
                //ServerPrime.initBoard();

            }
            sem.release();

        }
    }

}
