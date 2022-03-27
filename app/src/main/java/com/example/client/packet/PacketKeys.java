package com.example.client.packet;



import com.example.chatchat.LoginActivity;
import com.example.client.ClientLoader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.KeyPair;

public class PacketKeys extends OPacket {
    private String key;

    public PacketKeys(){
        KeyPair pair = KeyManagerDH.getPair();
        try {
            key = KeyManagerDH.bytesToHex(KeyManagerDH.savePublicKey(pair.getPublic()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public short getId() {
        return 3;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeUTF(key);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        key = dis.readUTF();
    }

    @Override
    public void handle() {
        ClientLoader.setPubKey(key);
    }
}
