package xyz.yooniks.spigotguard.network.v1_9_R2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.concurrent.Future;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import xyz.yooniks.spigotguard.SpigotGuardPlugin;
import xyz.yooniks.spigotguard.config.Settings;
import xyz.yooniks.spigotguard.event.ExploitDetails;
import xyz.yooniks.spigotguard.event.ExploitDetectedEvent;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;
import xyz.yooniks.spigotguard.network.PacketInjector;

public class PacketDecompress_1_9 extends ByteToMessageDecoder {
  private final PacketInjector injector;

  private void lambda$decode$1(ByteBuf var1, Future var2) throws Exception {
    SpigotGuardLogger.log(Level.INFO, "{0} sent too large packet (size: {1}). If it is false positive please increase the limit of viaversion-max-capacity or just disable viaversion-integration (viaversion is not needed to let this check work correctly, so do not worry if you don't have viaversion and this information).", new Object[]{this.injector.getPlayer().getName(), var1.capacity()});
    Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), this::lambda$decode$0);
  }

  public PacketDecompress_1_9(PacketInjector var1) {
    this.injector = var1;
  }

  protected void decode(ChannelHandlerContext var1, ByteBuf var2, List var3) {
    if (var2.capacity() > Settings.IMP.VIAVERSION_MAX_CAPACITY) {
      var1.close().addListener(this::lambda$decode$1);
      if (var2.refCnt() > 0) {
        var2.clear();
      }

    } else {
      var3.add(var2.readBytes(var2.readableBytes()));
    }
  }

  private void lambda$decode$0() {
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(this.injector.getPlayer(), new ExploitDetails(SpigotGuardPlugin.getInstance().getUserManager().findById(this.injector.getPlayer().getUniqueId()), "undefined", "too big packet size", false, false, "increase viaversion-max-capacity in settings.yml or disable viaversion-integration")));
  }
}
