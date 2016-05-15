/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package content;

import java.awt.Point;

public class Packet {
    public static class PacketLoginRequested{}
    public static class PacketLoginAccepted {boolean accepted = true;}
    // Client -> Server message
    public static class PacketMessage{ 
        public String message; 
        
    }
    // PAKIEY ODPOWIADAJACE NA ZMIANE POLOZENIA WEZA
    public static class PacketPoint{
        public int x,y,id;
}
    public static class PacketPointAccepted{}
    public static class PacketPointRefused{}
    public static class PacketDead{}
    public static class PacketAddPlayer{
        public int id,x,y;
    }
    public static class PacketHead{
        public int x1,y1;
        public int x2,y2;
        public int x3,y3;
        public int x4,y4;
        public int count;
    }
  
}
