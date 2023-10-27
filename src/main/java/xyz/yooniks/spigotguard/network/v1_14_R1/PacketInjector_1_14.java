package xyz.yooniks.spigotguard.network.v1_14_R1;

import io.netty.channel.Channel;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyz.yooniks.spigotguard.network.PacketInjector;
import xyz.yooniks.spigotguard.user.User;

public class PacketInjector_1_14 extends PacketInjector {
  private final Channel channel;
  
  public void uninjectListener() {
    if (this.channel.pipeline().get("SpigotGuard_" + this.player.getName()) != null);
  }
  
  public void injectListener(User paramUser) {
    PacketDecoder_1_14 packetDecoder_1_14 = new PacketDecoder_1_14(this, paramUser);
  }
  
  public PacketInjector_1_14(Player paramPlayer) {
    super(paramPlayer);
    this.channel = (((CraftPlayer)paramPlayer).getHandle()).playerConnection.networkManager.channel;
  }
}