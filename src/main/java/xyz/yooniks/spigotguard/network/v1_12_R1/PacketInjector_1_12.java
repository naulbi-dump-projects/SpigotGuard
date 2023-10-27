package xyz.yooniks.spigotguard.network.v1_12_R1;

import io.netty.channel.Channel;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyz.yooniks.spigotguard.SpigotGuardPlugin;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;
import xyz.yooniks.spigotguard.network.PacketDecoder;
import xyz.yooniks.spigotguard.network.PacketInjector;
import xyz.yooniks.spigotguard.user.User;

public class PacketInjector_1_12 extends PacketInjector {
  private final Channel channel;

  public void uninjectListener() {
    try {
      if (this.channel.pipeline().get("SpigotGuard_" + this.player.getName()) != null) {
        this.channel.pipeline().remove("SpigotGuard_" + this.player.getName());
      }
    } catch (Exception var2) {
      SpigotGuardLogger.log(Level.WARNING, "Could not uninject packetListener for {0}, reason: {1}", new Object[]{this.player.getName(), var2.getMessage()});
      return;
    }

    boolean var10000 = false;
  }

  public void injectListener(User var1) {
    PacketDecoder_1_12 var2 = new PacketDecoder_1_12(this, var1);

    try {
      this.channel.pipeline().addAfter("decoder", "SpigotGuard_" + this.player.getName(), var2);
    } catch (Throwable var4) {
      SpigotGuardLogger.log(Level.WARNING, "Could not inject packetListener for {0}, trying again in 1.5 sec ({1})", new Object[]{this.player.getName(), var4.getMessage()});
      Bukkit.getScheduler().runTaskLaterAsynchronously(SpigotGuardPlugin.getInstance(), this::lambda$injectListener$0, 30L);
      return;
    }

    boolean var10001 = false;
  }

  public PacketInjector_1_12(Player var1) {
    super(var1);
    this.channel = ((CraftPlayer)var1).getHandle().playerConnection.networkManager.channel;
  }

  private void lambda$injectListener$0(PacketDecoder var1) {
    try {
      this.channel.pipeline().addAfter("decoder", "SpigotGuard_" + this.player.getName(), var1);
    } catch (Throwable var3) {
      SpigotGuardLogger.log(Level.WARNING, "Could not inject packetListener for {0}, even after 1.5 sec, reason: {1}", new Object[]{this.player.getName(), var3.getMessage()});
      return;
    }

    boolean var10001 = false;
  }
}
