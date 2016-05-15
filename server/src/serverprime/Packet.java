/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverprime;

import java.awt.Point;

public class Packet {
    public static class PacketLoginRequested{}
    public static class PacketLoginAccepted {boolean accepted = true;}
    // Client -> Server message
    public static class PacketMessage{ 
        String message; 
        
    }
    // PAKIEY ODPOWIADAJACE NA ZMIANE POLOZENIA 
    public static class PacketPoint{
        int x,y,id;
}
    public static class PacketPointAccepted{}
    public static class PacketDead{}
    public static class PacketPointRefused{}
    
    public static class PacketBroadcastMap{}
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
