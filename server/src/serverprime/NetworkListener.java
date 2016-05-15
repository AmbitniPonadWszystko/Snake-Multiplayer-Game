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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import serverprime.Packet.*;

/**
 *
 * @author Mateusz
 */
//That will be pulling for any conections etc
public class NetworkListener extends Listener{
  int conectionCounter = 0;
private static final Semaphore sem = new Semaphore(1);
  static Map<Integer, Player> players = new HashMap<Integer, Player>();
  PacketHead odp1 = new PacketHead();
    
   
    public void connected(Connection cnctn) {
        conectionCounter++;
        Player player = new Player();
 
        
        
        if(conectionCounter == 1)
        {
            
            player.x = 9;
            player.y = 9;
            odp1.x1 = player.x;
            odp1.y1 = player.y;
            odp1.count = 1;
            player.c = cnctn;
            player.id = cnctn.getID();

           
        }
        else if(conectionCounter == 2){

           player.x = 20;
           player.y = 20;
           odp1.x2 = player.x;
           odp1.y2 = player.y;
           odp1.count = 2;
           player.c = cnctn;
           player.id = cnctn.getID();           
        }
        else if (conectionCounter == 4){
           player.x = 13;
           player.y = 20;
           odp1.x4 = player.x;
           odp1.y4 = player.y;
           odp1.count = 4;
           player.c = cnctn;
           player.id = cnctn.getID(); 
           

            
        }
        else{
           player.x = 2;
           player.y = 17;
           odp1.count = 3;
           odp1.x3 = player.x;
           odp1.y3 = player.y;
           player.c = cnctn;
           player.id = cnctn.getID();           
        }
        ServerPrime.server.sendToAllTCP(odp1);
             ServerPrime.mask[player.x][player.y] = BarrierType.BLUE_SNAKE;
             System.out.println(player.id);       
            PacketAddPlayer p = new PacketAddPlayer();
            p.x = player.x;
            p.y = player.y;
            p.id = player.id;
            //ServerPrime.server.sendToAllTCP(p);
            cnctn.sendTCP(p);       
            //cnctn.sendTCP(p);
            players.put(cnctn.getID(), player);


        
        Log.info("[SERVER] Someone has connected.");
       
        
       
    }

 
    public void disconnected(Connection cnctn) {
        Log.info("[SERVER] Someone has disconnected.");     
    }


    public void received(Connection c, Object o) {
        if(o instanceof PacketLoginRequested) {
            PacketLoginAccepted loginAnswer = new PacketLoginAccepted();
            loginAnswer.accepted = true;
            c.sendUDP(loginAnswer);
        }
        if(o instanceof PacketMessage) {
            String message = ((PacketMessage) o).message;
            Log.info(ServerPrime.connections.toString());
            Log.info(message);
        }
        if(o instanceof PacketPoint) {
            sem.acquireUninterruptibly();
            int x = ((PacketPoint)o).x;
            int y = ((PacketPoint)o).y;
            Log.info("Odebrano nowy point" + x + " " + y);
            //jezeli pkt jest ok
            if(ServerPrime.mask[x][y] == BarrierType.EMPTY){

                ServerPrime.mask[x][y] = BarrierType.BLUE_SNAKE;
                PacketPoint p = new PacketPoint();
                p.x = x;
                p.y = y;
                p.id = c.getID();
                ServerPrime.server.sendToAllTCP(p);
                
            }
            else{
                Log.info("Umieeram");
                PacketDead odp = new PacketDead();
                //c.sendUDP(odp);
                //ServerPrime.server.sendToUDP(c.getID(), odp);
                //ServerPrime.initBoard();
                
            }
            sem.release();
            
        }
    }
    
    
}
