package com.example.client;


import android.os.Build;
import android.widget.EditText;
import androidx.annotation.RequiresApi;
import com.example.chatchat.LoginActivity;
import com.example.chatchat.MainActivity;
import com.example.client.packet.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;


public class ClientLoader extends Thread  {
    private static Socket socket;
    private static String host = "193.109.79.51";
    private static int port = 8888;
    private static String nickname = "default";
    private static boolean sentNickname = false;
    private static MainActivity mA;
    private static String pubKey = "";
    private static String sessionKey = "";

    public ClientLoader(){

    }

    public static void setNickname(String nickname) {
        ClientLoader.nickname = nickname;
    }

    public static String getNickname() {
        return nickname;
    }

    public ClientLoader(MainActivity mA){
        ClientLoader.mA = mA;
    }

    public static void setPubKey(String pubKey) {
        ClientLoader.pubKey = pubKey;
    }

    public static String getSessionKey() {
        return sessionKey;
    }

    public static String getPubKey() {
        return pubKey;
    }

    public static MainActivity getMainActivity(){
        return mA;
    }

    public static void setmA(MainActivity mA) {
        ClientLoader.mA = mA;
    }

    public static void setSessionKey(String sessionKey) {
        ClientLoader.sessionKey = sessionKey;
    }

    public void run() {
        try {
            connect();
            LoginActivity.connectSuccess = true;
        } catch (IOException e) {
            LoginActivity.connectSuccess = false;
            return;
        }
        try {
            keyHandler();
            LoginActivity.keySuccess = true;
        } catch (EOFException e) {
            LoginActivity.keySuccess = false;
        }
    }

    public static void connect() throws IOException {
        socket = new Socket(host, port);

    }

    public static void keyHandler() throws EOFException {
        Thread handleKey = new Thread(){
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
                        if (id != 3){
                            continue;
                        }
                        OPacket packet = PacketManager.getPacket(id);
                        packet.read(dis);
                        packet.handle();
                        break;
                    }catch (IOException ex){
                        ex.printStackTrace();
                    }

                }
            }
        };
        handleKey.start();
        String pub = pubKey;

        KeyManagerDH.generateKeyPair();
        sendPacket(new PacketKeys());

        for (int i = 0; i < 10; i++){
            if (pub == ClientLoader.getPubKey()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            } else {
                break;
            }
            if (i == 9){
                throw new EOFException("Can't get packet");
            }
        }
        KeyPair pair = KeyManagerDH.getPair();
        try {
            sessionKey = KeyManagerDH.doECDH(KeyManagerDH.savePrivateKey(pair.getPrivate()), KeyManagerDH.hexToByteArray(pubKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread handleAuth = new Thread(){
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
                        if (id != 5){
                            continue;
                        }
                        OPacket packet = PacketManager.getPacket(id);
                        packet.read(dis);
                        packet.handle();
                        break;
                    }catch (IOException ex){
                        ex.printStackTrace();
                    }

                }
            }
        };
        handleAuth.start();
        String session = sessionKey;
        sendPacket(new PacketAuthorize(ClientLoader.getNickname()));
        for (int i = 0; i < 10; i++){
            if (session == ClientLoader.getSessionKey()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            } else {
                break;
            }
            if (i == 9){
                throw new EOFException("Can't get sessionKey");
            }
        }
    }

    public static void handle(){
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
