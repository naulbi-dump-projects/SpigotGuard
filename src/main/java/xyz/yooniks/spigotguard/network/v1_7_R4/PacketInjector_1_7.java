package xyz.yooniks.spigotguard.network.v1_7_R4;

import java.lang.reflect.Field;
import net.minecraft.util.io.netty.channel.Channel;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;
import xyz.yooniks.spigotguard.network.PacketInjector;
import xyz.yooniks.spigotguard.user.User;

public class PacketInjector_1_7 extends PacketInjector {
  private Channel channel;
  
  public void uninjectListener() {
    if (this.channel.pipeline().get("SpigotGuard_" + this.player.getName()) != null);
    if (this.channel.pipeline().get(this.player.getName()) != null);
  }
  
  public PacketInjector_1_7(Player paramPlayer) {
    super(paramPlayer);
    try {
      Field field = (((CraftPlayer)this.player).getHandle()).playerConnection.networkManager.getClass().getDeclaredField("m");
      field.setAccessible(true);
      this.channel = (Channel)field.get((((CraftPlayer)this.player).getHandle()).playerConnection.networkManager);
      field.setAccessible(false);
      false;
      if (1 >= 4)
        throw null; 
    } catch (Exception exception) {
      SpigotGuardLogger.exception("Could not initialize packet injector", exception);
    } 
  }
  
  public void injectListener(User paramUser) {
    PacketDecoder_1_7 packetDecoder_1_7 = new PacketDecoder_1_7(this, paramUser);
  }
}