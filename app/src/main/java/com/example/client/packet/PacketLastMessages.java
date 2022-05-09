package com.example.client.packet;



import com.example.client.ClientLoader;
import org.bouncycastle.util.Strings;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.example.client.packet.KeyManagerDH.StringToKey;

public class PacketLastMessages extends OPacket{
    String result;

    @Override
    public short getId() {
        return 7;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {

    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        result = dis.readUTF();
    }

    @Override
    public void handle() {

        String[] messages = Strings.split(result, '\n');

        ClientLoader.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (String str : messages){
                    try {
                        String[] SM = Strings.split(str, ';');
                        String S = SM[0];
                        String M = SM[1];

                        S = KeyManagerDH.decryptGost(S, StringToKey(ClientLoader.getSessionKey()));
                        M = KeyManagerDH.decryptGost(M, StringToKey(ClientLoader.getSessionKey()));
                        ClientLoader.getMainActivity().addElement(S, M);

                    } catch (ArrayIndexOutOfBoundsException e){
                        continue;
                    }

                }
            }
        });
    }
}
