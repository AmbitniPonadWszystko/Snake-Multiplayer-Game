
package serverprime;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
//import com.esotericsoftware.kryo.ServerPrime;


public class ServerPrime {
    private final static int sizeWidth = 120;          
    private final static int sizeHeight = 60;
    static Server server;
    static List<Connection> connections;
    static BarrierType[][] mask = new BarrierType[sizeWidth][sizeHeight];
   // static int[sizeWidth][sizeHeight] mask;
    public ServerPrime(){
        connections = new ArrayList<Connection>();
        server = new Server();
        registerPackets();
        server.addListener(new NetworkListener());
        try {
            server.bind(7474,7474); // TCP connection
            server.start();
            
            
        } catch (IOException ex) {
            System.out.println("Wrong Port");
        }
        initBoard();       
        
    }
    
    public static void initBoard(){        
        for(int x = 0; x < sizeWidth; x++)
            for(int y = 0; y < sizeHeight; y++){
                if(x==0||x==sizeWidth-1||y==0||y==sizeHeight-1)
                    mask[x][y] = BarrierType.WALL; 
                else
                    mask[x][y] = BarrierType.EMPTY;      
            }        
    }
    
    private void registerPackets(){
        //Some type of Serializer which encodes info to readable thing
        // something that can be send over network
        Kryo kryo = server.getKryo();
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

    public static void main(String[] args) {
        new ServerPrime();
        Log.set(Log.LEVEL_DEBUG);
    }
    
}
