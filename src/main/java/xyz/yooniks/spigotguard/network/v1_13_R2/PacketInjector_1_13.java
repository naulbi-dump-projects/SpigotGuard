package xyz.yooniks.spigotguard.network.v1_13_R2;

import io.netty.channel.Channel;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyz.yooniks.spigotguard.network.PacketInjector;
import xyz.yooniks.spigotguard.user.User;

public class PacketInjector_1_13 extends PacketInjector {
  private final Channel channel;
  
  public void uninjectListener() {
    if (this.channel.pipeline().get("SpigotGuard_" + this.player.getName()) != null);
  }
  
  public PacketInjector_1_13(Player paramPlayer) {
    super(paramPlayer);
    this.channel = (((CraftPlayer)paramPlayer).getHandle()).playerConnection.networkManager.channel;
  }
  
  public void injectListener(User paramUser) {
    PacketDecoder_1_13 packetDecoder_1_13 = new PacketDecoder_1_13(this, paramUser);
  }
}