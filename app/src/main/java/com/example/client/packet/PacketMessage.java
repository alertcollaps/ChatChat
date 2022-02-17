package com.example.client.packet;

import com.example.chatchat.MainActivity;
import com.example.client.ClientLoader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketMessage extends OPacket{

    private String sender;
    private String message;

    public PacketMessage(){

    }

    public PacketMessage(String sender, String message){
        this.sender = sender;
        this.message = message;
    }

    @Override
    public short getId() {
        return 2;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeUTF(message);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        sender = dis.readUTF();
        message = dis.readUTF();
    }

    @Override
    public void handle() {
        System.out.printf("[%s]: %s\n", sender, message);

        ClientLoader.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ClientLoader.getMainActivity().setText(String.format("[%s]: %s\n", sender, message));
            }
        });
    }
}
