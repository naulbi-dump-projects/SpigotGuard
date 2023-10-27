package xyz.yooniks.spigotguard.network;

import org.bukkit.entity.Player;
import xyz.yooniks.spigotguard.user.User;

public abstract class PacketInjector {
  protected PacketDecoder packetDecoder;
  
  protected final Player player;
  
  public Player getPlayer() {
    return this.player;
  }
  
  public abstract void injectListener(User paramUser);
  
  public abstract void uninjectListener();
  
  public PacketInjector(Player paramPlayer) {
    this.player = paramPlayer;
  }
  
  public PacketDecoder getPacketDecoder() {
    return this.packetDecoder;
  }
}