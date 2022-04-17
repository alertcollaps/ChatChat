package com.example.client.packet;

import com.example.chatchat.LoginActivity;
import com.example.client.ClientLoader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ClientInfoStatus;
import java.util.Objects;

public class PacketSessionKey extends OPacket{
    private String sessionKey, error;

    PacketSessionKey(){

    }

    PacketSessionKey(String key, String error){
        sessionKey = key;
        this.error = error;
    }

    @Override
    public short getId() {
        return 5;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeUTF(sessionKey);
        dos.writeUTF(error);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        sessionKey = dis.readUTF();
        sessionKey = KeyManagerDH.decryptGost(sessionKey, KeyManagerDH.StringToKey(ClientLoader.getSessionKey()));
        error = dis.readUTF();
    }

    @Override
    public void handle() {
        if (!error.equals("NONE")) {

            ClientLoader.getLoginActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ClientLoader.getLoginActivity().poppupWindow(error);
                }
            });
            return;
        }

        ClientLoader.setSessionKey(sessionKey);
        ClientLoader.sendPacket(new PacketLastMessages());
    }
}
