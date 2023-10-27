package xyz.yooniks.spigotguard.network.v1_9_R2;

import io.netty.channel.Channel;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyz.yooniks.spigotguard.SpigotGuardPlugin;
import xyz.yooniks.spigotguard.config.Settings;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;
import xyz.yooniks.spigotguard.network.PacketDecoder;
import xyz.yooniks.spigotguard.network.PacketInjector;
import xyz.yooniks.spigotguard.user.User;

public class PacketInjector_1_9 extends PacketInjector {
  private final Channel channel;

  public void injectListener(User var1) {
    PacketDecoder_1_9 var2 = new PacketDecoder_1_9(this, var1);
    this.channel.pipeline().addAfter("decoder", "SpigotGuard_" + this.player.getName(), var2);
    boolean var10001 = false;
    if (Settings.IMP.VIAVERSION_INTEGRATION && this.channel.pipeline().get("decompress") != null) {
      this.channel.pipeline().addBefore("decompress", "sg_decompress", new PacketDecompress_1_9(this));
    } else if (Settings.IMP.VIAVERSION_INTEGRATION) {
      Bukkit.getScheduler().runTaskLaterAsynchronously(SpigotGuardPlugin.getInstance(), this::lambda$injectListener$1, 30L);
    }

  }

  private void lambda$injectListener$0(PacketDecoder var1) {
    this.channel.pipeline().addAfter("decoder", "SpigotGuard_" + this.player.getName(), var1);
    boolean var10001 = false;
  }

  public void uninjectListener() {
    try {
      if (this.channel.pipeline().get("SpigotGuard_" + this.player.getName()) != null) {
        this.channel.pipeline().remove("SpigotGuard_" + this.player.getName());
      }

      if (Settings.IMP.VIAVERSION_INTEGRATION && this.channel.pipeline().get("sg_decompress") != null) {
        this.channel.pipeline().remove("sg_decompress");
      }
    } catch (Exception var2) {
      SpigotGuardLogger.log(Level.WARNING, "Could not uninject packetListener for {0}, reason: {1}", new Object[]{this.player.getName(), var2.getMessage()});
      return;
    }

    boolean var10000 = false;
  }

  private void lambda$injectListener$1() {
    try {
      this.channel.pipeline().addBefore("decompress", "sg_decompress", new PacketDecompress_1_9(this));
    } catch (Throwable var2) {
      SpigotGuardLogger.log(Level.WARNING, "Could not inject packetDecompress for {0}, even after 1.5 sec, reason: {1}", new Object[]{this.player.getName(), var2.getMessage()});
      return;
    }

    boolean var10001 = false;
  }

  public PacketInjector_1_9(Player var1) {
    super(var1);
    this.channel = ((CraftPlayer)var1).getHandle().playerConnection.networkManager.channel;
  }
}
