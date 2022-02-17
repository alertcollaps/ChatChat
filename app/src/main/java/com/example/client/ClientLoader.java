package com.example.client;


import android.os.Build;
import android.widget.EditText;
import androidx.annotation.RequiresApi;
import com.example.chatchat.MainActivity;
import com.example.client.packet.OPacket;
import com.example.client.packet.PacketAuthorize;
import com.example.client.packet.PacketManager;
import com.example.client.packet.PacketMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ClientLoader extends Thread  {
    private static Socket socket;
    private static String host = "10.0.2.2";
    private static int port = 8888;
    private static String nickname = "default";
    private static boolean sentNickname = false;
    private static MainActivity mA;

    public ClientLoader(){

    }

    public ClientLoader(MainActivity mA){
        ClientLoader.mA = mA;
    }

    public static MainActivity getMainActivity(){
        return mA;
    }

    public void run() {
        connect();
        handle();
    }

    private static void connect(){
        try { socket = new Socket(host, port);
            //MainActivity.poppupWindow(R.string.connection_established);
        } catch (IOException e) {
            //MainActivity.poppupWindow(R.string.connection_not_established);
            e.printStackTrace();
        }
    }

    private static void handle(){
        Thread handler = new Thread(){
            @Override
            public void run(){
                while(true){
                    try{
                        DataInputStream dis = new DataInputStream(socket.getInputStream());
                        if (dis.available() <= 0){
                            ClientLoader.sleep();
                            continue;
                        }
                        short id = dis.readShort();

                        OPacket packet = PacketManager.getPacket(id);
                        packet.read(dis);

                        packet.handle();
                    }catch (IOException ex){
                        ex.printStackTrace();
                    }

                }
            }
        };
        handler.start();

    }



    public static String readChat(String message) {
        if (socket.isClosed()){
            sentNickname = false;
            return "Socket is closed";
        }
        Thread read = new Thread(){
            @Override
            public void run(){
                if (!sentNickname){
                    sendPacket(new PacketAuthorize(message));
                    sentNickname = true;
                    return;
                }
                sendPacket(new PacketMessage(null, message));
            }
        };
        read.start();
        return "";
    }

    private static void end(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void sendPacket(OPacket packet){
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeShort(packet.getId());
            packet.write(dos);
            dos.flush();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public static void sleep(){
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
