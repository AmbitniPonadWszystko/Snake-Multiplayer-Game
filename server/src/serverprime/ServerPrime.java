package serverprime;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import java.io.IOException;

public class ServerPrime {

    private final static int sizeWidth = 120;
    private final static int sizeHeight = 60;
    static Server server;
    static BarrierType[][] mask = new BarrierType[sizeWidth][sizeHeight];

    public ServerPrime() {
        server = new Server();
        registerPackets();
        server.addListener(new NetworkListener());
        try {
            //To bind the server to the ports, 
            //we simply call the bind method within our server object.
            //The order of ports is TCP, followed by UDP. 
            server.bind(7474, 7474);
            server.start();

        } catch (IOException ex) {
            System.out.println("Wrong Port");
        }
        initBoard();

    }

    //init our mask Board with values giving in Enum BarrierType
    public static void initBoard() {
        for (int x = 0; x < sizeWidth; x++) {
            for (int y = 0; y < sizeHeight; y++) {
                if (x == 0 || x == sizeWidth - 1 || y == 0 || y == sizeHeight - 1) {
                    mask[x][y] = BarrierType.WALL;
                } else {
                    mask[x][y] = BarrierType.EMPTY;
                }
            }
        }
    }

    private void registerPackets() {
        // Some type of Serializer which encodes info to readable thing
        // something that can be send over network
        // all packets needs to be registered
        Kryo kryo = server.getKryo();
        kryo.register(Packet.PacketLoginRequested.class);
        kryo.register(Packet.PacketLoginAccepted.class);
        kryo.register(Packet.PacketMessage.class);
        kryo.register(Packet.PacketPoint.class);
        kryo.register(Packet.PacketPointAccepted.class);
        kryo.register(Packet.PacketPointRefused.class);
        kryo.register(Packet.PacketNames.class);
        kryo.register(Packet.PacketDead.class);
        kryo.register(Packet.PacketAddPlayer.class);
        kryo.register(Packet.PacketHead.class);
        kryo.register(Packet.PacketNewTour.class);
        kryo.register(Packet.PacketReadyPlayer.class);
        kryo.register(Packet.PacketStart.class);
        kryo.register(Packet.PacketEndGame.class);
        
    }

    public static void main(String[] args) {
        new ServerPrime();
        Log.set(Log.LEVEL_DEBUG);
    }

}
