package xyz.yooniks.spigotguard.network.v1_8_R3;

import io.netty.channel.Channel;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.yooniks.spigotguard.SpigotGuardPlugin;
import xyz.yooniks.spigotguard.config.Settings;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;
import xyz.yooniks.spigotguard.network.PacketDecoder;
import xyz.yooniks.spigotguard.network.PacketInjector;
import xyz.yooniks.spigotguard.network.ViaVersionCheck;
import xyz.yooniks.spigotguard.user.User;

public class PacketInjector_1_8 extends PacketInjector {
  private final Channel channel;

  public void uninjectListener() {
    try {
      if (this.channel.pipeline().get("SpigotGuard_" + this.player.getName()) != null) {
        this.channel.pipeline().remove("SpigotGuard_" + this.player.getName());
      }

      if (this.channel.pipeline().get("sg_decompress") != null) {
        this.channel.pipeline().remove("sg_decompress");
      }

      if (this.channel.pipeline().get("sg_listener") != null) {
        this.channel.pipeline().remove("sg_listener");
      }
    } catch (Exception var2) {
      SpigotGuardLogger.log(Level.WARNING, "Could not uninject packetListener for " + this.player.getName() + ", reason: " + var2.getMessage(), new Object[0]);
      return;
    }

    boolean var10000 = false;
  }

  private void lambda$injectListener$1(User var1) {
    try {
      this.channel.pipeline().addBefore("packet_handler", "sg_listener", new PacketListener_1_8(this, var1));
    } catch (Throwable var3) {
      SpigotGuardLogger.log(Level.WARNING, "Could not inject packetHandler for " + this.player.getName() + ", even after 1.5 sec, reason: " + var3.getMessage(), new Object[0]);
      return;
    }

    boolean var10001 = false;
  }

  public PacketInjector_1_8(Player var1) {
    super(var1);
    this.channel = ((CraftPlayer)var1).getHandle().playerConnection.networkManager.channel;
  }

  public void injectListener(User var1) {
    PacketDecoder_1_8 var2 = new PacketDecoder_1_8(this, var1);
    this.packetDecoder = var2;

    label56: {
      try {
        this.channel.pipeline().addAfter("decoder", "SpigotGuard_" + this.player.getName(), var2);
      } catch (Throwable var8) {
        SpigotGuardLogger.log(Level.WARNING, "Could not inject packetListener for " + this.player.getName() + ", trying again in 1.5 sec (" + var8.getMessage() + ")", new Object[0]);
        Bukkit.getScheduler().runTaskLaterAsynchronously(SpigotGuardPlugin.getInstance(), this::lambda$injectListener$0, 30L);
        break label56;
      }

      boolean var10001 = false;
    }

    Plugin var3 = Bukkit.getPluginManager().getPlugin("ViaVersion");
    boolean var10002;
    if (var3 != null && ViaVersionCheck.shouldChangeCompressor(this.player)) {
      if (ViaVersionCheck.shouldChangeCompressor(this.player) && Settings.IMP.PACKET_DECODER.ENABLED) {
        if (this.channel.pipeline().get("decompress") != null) {
          this.channel.pipeline().addAfter("decompress", "sg_decompress", new PacketDecompress_1_8(this));
        } else {
          label45: {
            try {
              this.channel.pipeline().addAfter("splitter", "sg_decompress", new PacketDecompress_1_8(this));
            } catch (Exception var7) {
              boolean var10000 = false;
              break label45;
            }

            var10002 = false;
          }
        }
      }
    } else if (var3 == null && Settings.IMP.PACKET_DECODER.ENABLED) {
      if (this.channel.pipeline().get("decompress") != null) {
        this.channel.pipeline().addAfter("decompress", "sg_decompress", new PacketDecompress_1_8(this));
      } else {
        label39: {
          try {
            this.channel.pipeline().addAfter("splitter", "sg_decompress", new PacketDecompress_1_8(this));
          } catch (Exception var6) {
            break label39;
          }

          var10002 = false;
        }
      }
    }

    try {
      this.channel.pipeline().addBefore("packet_handler", "sg_listener", new PacketListener_1_8(this, var1));
    } catch (Throwable var5) {
      SpigotGuardLogger.log(Level.WARNING, "Could not inject packetHandler for " + this.player.getName() + ", trying again in 1.5 sec (" + var5.getMessage() + ")", new Object[0]);
      Bukkit.getScheduler().runTaskLaterAsynchronously(SpigotGuardPlugin.getInstance(), this::lambda$injectListener$1, 30L);
      return;
    }

    var10002 = false;
  }

  private void lambda$injectListener$0(PacketDecoder var1) {
    try {
      this.channel.pipeline().addAfter("decoder", "SpigotGuard_" + this.player.getName(), var1);
    } catch (Throwable var3) {
      SpigotGuardLogger.log(Level.WARNING, "Could not inject packetListener for " + this.player.getName() + ", even after 1.5 sec, reason: " + var3.getMessage(), new Object[0]);
      return;
    }

    boolean var10001 = false;
  }
}
