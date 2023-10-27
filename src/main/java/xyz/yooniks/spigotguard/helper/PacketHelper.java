package xyz.yooniks.spigotguard.helper;

import java.io.IOException;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;

public class PacketHelper {
  public static int checkHandshake(PacketDataSerializer paramPacketDataSerializer) throws IOException {
    if (paramPacketDataSerializer.readableBytes() > 300 || paramPacketDataSerializer.readableBytes() < 5)
      throw new IOException("Invalid Handshake packet 1"); 
    int i = paramPacketDataSerializer.e();
    if (!paramPacketDataSerializer.isReadable())
      throw new IOException("Invalid Handshake packet 2"); 
    int j = paramPacketDataSerializer.e();
    if (!paramPacketDataSerializer.isReadable() || j <= 0)
      throw new IOException("Invalid Handshake packet 3"); 
    byte[] arrayOfByte = new byte[paramPacketDataSerializer.e()];
    if (paramPacketDataSerializer.readableBytes() <= 2)
      throw new IOException("Invalid Handshake packet 4"); 
    int k = paramPacketDataSerializer.readUnsignedShort();
    if (!paramPacketDataSerializer.isReadable() || k <= 0 || paramPacketDataSerializer.readableBytes() > 1)
      throw new IOException("Invalid Handshake packet 5"); 
    int m = paramPacketDataSerializer.e();
    if (paramPacketDataSerializer.isReadable() || (m != 1 && m != 2))
      throw new IOException("Invalid Handshake packet 6"); 
    return m;
  }
  
  public static void checkLogin(PacketDataSerializer paramPacketDataSerializer) throws IOException {
    if (paramPacketDataSerializer.readableBytes() > 40 || paramPacketDataSerializer.readableBytes() < 5)
      throw new IOException("Too long LoginStart"); 
    int i = paramPacketDataSerializer.e();
    if (!paramPacketDataSerializer.isReadable() || i != 0)
      throw new IOException("Invalid LoginStart packet id"); 
    byte[] arrayOfByte = new byte[paramPacketDataSerializer.e()];
    String str = new String(arrayOfByte);
    if (paramPacketDataSerializer.isReadable() || str.length() > 16 || str.length() <= 2)
      throw new IOException("Invalid LoginStart packet"); 
  }
}