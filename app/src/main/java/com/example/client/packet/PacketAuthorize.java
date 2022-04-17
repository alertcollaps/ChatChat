package com.example.client.packet;

import com.example.client.ClientLoader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.Key;

public class PacketAuthorize extends OPacket {
        private String nickname, email, password;

    public PacketAuthorize(){

    }

    public PacketAuthorize(String nickname){
        this.nickname = nickname;
    }

    public PacketAuthorize(String nickname, String email, String password){
        this.nickname = nickname;
        this.email =   email;
        this.password = password;
    }

    @Override
    public short getId() {
        return 1;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        String key = ClientLoader.getSessionKey();
        nickname = KeyManagerDH.encryptGost(nickname,KeyManagerDH.StringToKey(key));
        email = KeyManagerDH.encryptGost(email,KeyManagerDH.StringToKey(key));
        password = KeyManagerDH.encryptGost(password,KeyManagerDH.StringToKey(key));

        dos.writeUTF(nickname);
        dos.writeUTF(email);
        dos.writeUTF(password);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {

    }

    @Override
    public void handle(){

    }
}
