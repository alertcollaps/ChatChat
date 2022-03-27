package com.example.client.packet;

import com.example.chatchat.MainActivity;
import com.example.client.ClientLoader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.example.client.packet.KeyManagerDH.StringToKey;

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
        message = KeyManagerDH.encryptGost(message, StringToKey(ClientLoader.getSessionKey()));
        dos.writeUTF(message);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        sender = dis.readUTF();
        message = dis.readUTF();
        sender = KeyManagerDH.decryptGost(sender, StringToKey(ClientLoader.getSessionKey()));
        message = KeyManagerDH.decryptGost(message, StringToKey(ClientLoader.getSessionKey()));
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
