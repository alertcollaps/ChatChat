package com.example.client.packet;

import com.example.client.ClientLoader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ClientInfoStatus;

public class PacketSessionKey extends OPacket{
    private String sessionKey;

    PacketSessionKey(){

    }

    PacketSessionKey(String key){
        sessionKey = key;
    }

    @Override
    public short getId() {
        return 5;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeUTF(sessionKey);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {

        sessionKey = dis.readUTF();
        sessionKey = KeyManagerDH.decryptGost(sessionKey, KeyManagerDH.StringToKey(ClientLoader.getSessionKey()));

    }

    @Override
    public void handle() {
        ClientLoader.setSessionKey(sessionKey);
    }
}
