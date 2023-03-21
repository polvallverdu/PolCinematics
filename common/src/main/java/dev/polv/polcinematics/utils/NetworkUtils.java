package dev.polv.polcinematics.utils;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

public class NetworkUtils {

    public final static PacketByteBuf EMPTY_BUFFER = new PacketByteBuf(Unpooled.buffer());

    public static PacketByteBuf createBuffer() {
        return new PacketByteBuf(Unpooled.buffer());
    }

}
