package xyz.yooniks.spigotguard.network;

import io.netty.handler.codec.MessageToMessageDecoder;
import xyz.yooniks.spigotguard.user.User;

public abstract class PacketDecoder extends MessageToMessageDecoder<Object> {
  protected final PacketInjector injector;
  
  public PacketDecoder(PacketInjector paramPacketInjector) {
    this.injector = paramPacketInjector;
  }
  
  public abstract User getUser();
}