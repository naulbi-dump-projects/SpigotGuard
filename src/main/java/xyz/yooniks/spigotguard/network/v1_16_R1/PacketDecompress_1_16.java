package xyz.yooniks.spigotguard.network.v1_16_R1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.concurrent.Future;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import xyz.yooniks.spigotguard.SpigotGuardPlugin;
import xyz.yooniks.spigotguard.config.Settings;
import xyz.yooniks.spigotguard.event.ExploitDetails;
import xyz.yooniks.spigotguard.event.ExploitDetectedEvent;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;
import xyz.yooniks.spigotguard.network.PacketInjector;

public class PacketDecompress_1_16 extends ByteToMessageDecoder {
  private final PacketInjector injector;
  
  private void lambda$decode$1(ChannelHandlerContext paramChannelHandlerContext, ByteBuf paramByteBuf, Future paramFuture) throws Exception {
    SpigotGuardLogger.log(Level.INFO, "{0} sent too large packet (size: {1}). If it is false positive please increase the limit of viaversion-max-capacity or just disable viaversion-integration (viaversion is not needed to let this check work correctly, so do not worry if you don't have viaversion and this information).", new Object[] { this.injector.getPlayer().getName(), Integer.valueOf(paramByteBuf.capacity()) });
  }
  
  public PacketDecompress_1_16(PacketInjector paramPacketInjector) {
    this.injector = paramPacketInjector;
  }
  
  protected void decode(ChannelHandlerContext paramChannelHandlerContext, ByteBuf paramByteBuf, List<Object> paramList) {
    if (paramByteBuf.capacity() > Settings.IMP.VIAVERSION_MAX_CAPACITY) {
      if (paramByteBuf.refCnt() > 0);
      return;
    } 
  }
  
  private void lambda$decode$0() {
    Bukkit.getPluginManager().callEvent((Event)new ExploitDetectedEvent((OfflinePlayer)this.injector.getPlayer(), new ExploitDetails(SpigotGuardPlugin.getInstance().getUserManager().findById(this.injector.getPlayer().getUniqueId()), "undefined", "too big packet size", false, false, "increase viaversion-max-capacity in settings.yml or disable viaversion-integration")));
  }
}