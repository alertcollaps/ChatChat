package com.example.client.packet;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class PacketManager {

    private static Map<Short, Class<? extends OPacket>> packets = new HashMap<>();

    static {
        packets.put((short) 1, PacketAuthorize.class);
        packets.put((short) 2, PacketMessage.class);
        packets.put((short) 3, PacketKeys.class);
        packets.put((short) 4, authPacket.class);
        packets.put((short) 5, PacketSessionKey.class);
        packets.put((short) 6, PacketOK.class);
        packets.put((short) 7, PacketLastMessages.class);
    }


    public static OPacket getPacket(short id){
        try {
            return packets.get(id).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


}
