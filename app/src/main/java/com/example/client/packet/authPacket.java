package com.example.client.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class authPacket extends OPacket{
    private String sender;

    @Override
    public short getId() {
        return 4;
    }
    authPacket(String nickname){
        sender = nickname;
    }
    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeUTF(sender);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        sender = dis.readUTF();
    }

    @Override
    public void handle() {

    }
}
