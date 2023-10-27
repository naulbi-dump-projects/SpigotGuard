package xyz.yooniks.spigotguard.helper;

import net.minecraft.server.v1_8_R3.*;
import java.io.*;

public class PacketHelper
{
    public static int checkHandshake(final PacketDataSerializer buf) throws IOException {
        if (buf.readableBytes() > 300 || buf.readableBytes() < 5) {
            throw new IOException("Invalid Handshake packet 1");
        }
        final int packetId = buf.e();
        if (!buf.isReadable()) {
            throw new IOException("Invalid Handshake packet 2");
        }
        final int protocolId = buf.e();
        if (!buf.isReadable() || protocolId <= 0) {
            throw new IOException("Invalid Handshake packet 3");
        }
        final byte[] host = new byte[buf.e()];
        buf.readBytes(host);
        if (buf.readableBytes() <= 2) {
            throw new IOException("Invalid Handshake packet 4");
        }
        final int port = buf.readUnsignedShort();
        if (!buf.isReadable() || port <= 0 || buf.readableBytes() > 1) {
            throw new IOException("Invalid Handshake packet 5");
        }
        final int stateId = buf.e();
        if (buf.isReadable() || (stateId != 1 && stateId != 2)) {
            throw new IOException("Invalid Handshake packet 6");
        }
        return stateId;
    }
    
    public static void checkLogin(final PacketDataSerializer buf) throws IOException {
        if (buf.readableBytes() > 40 || buf.readableBytes() < 5) {
            throw new IOException("Too long LoginStart");
        }
        final int packetId = buf.e();
        if (!buf.isReadable() || packetId != 0) {
            throw new IOException("Invalid LoginStart packet id");
        }
        final byte[] bytes = new byte[buf.e()];
        buf.readBytes(bytes);
        final String nick = new String(bytes);
        if (buf.isReadable() || nick.length() > 16 || nick.length() <= 2) {
            throw new IOException("Invalid LoginStart packet");
        }
    }
}
