package com.example.client.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketOK extends OPacket{
    @Override
    public short getId() {
        return 6;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {

    }

    @Override
    public void read(DataInputStream dis) throws IOException {

    }

    @Override
    public void handle() {

    }
}
