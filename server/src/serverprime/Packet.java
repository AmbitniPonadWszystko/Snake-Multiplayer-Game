package serverprime;

public class Packet {

    public static class PacketLoginRequested {
        String name;
    }

    public static class PacketLoginAccepted {

        boolean accepted = true;
    }

    // Client -> Server message
    public static class PacketMessage {

        String message;

    }

    // PAKIEY ODPOWIADAJACE NA ZMIANE POLOZENIA 
    public static class PacketPoint {

        int x, y, id;
    }

    public static class PacketPointAccepted {
    }

    public static class PacketDead {
    }

    public static class PacketPointRefused {
    }

    public static class PacketBroadcastMap {
    }

    public static class PacketAddPlayer {

        public int id, x, y;
    }

    public static class PacketHead {

        public int x1, y1;
        public int x2, y2;
        public int x3, y3;
        public int x4, y4;
        public int count;
    }

    public static class PacketNewTour {

        public int x1, y1, score1;
        public int x2, y2, score2;
        public int x3, y3, score3;
        public int x4, y4, score4;
        public int count, tour;
    }
    public static class PacketNames {

        String name1;
        String name2;
        String name3;
        String name4;
        
    }

    public static class PacketReadyPlayer {
    }

    public static class PacketStart {
    }

}
