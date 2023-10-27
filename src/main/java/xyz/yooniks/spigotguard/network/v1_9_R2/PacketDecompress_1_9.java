package xyz.yooniks.spigotguard.network.v1_9_R2;

import io.netty.handler.codec.*;
import xyz.yooniks.spigotguard.network.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import java.util.*;
import xyz.yooniks.spigotguard.config.*;
import io.netty.util.concurrent.*;
import java.util.logging.*;
import xyz.yooniks.spigotguard.logger.*;
import xyz.yooniks.spigotguard.*;
import xyz.yooniks.spigotguard.event.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.plugin.*;

public class PacketDecompress_1_9 extends ByteToMessageDecoder
{
    private final PacketInjector injector;
    
    public PacketDecompress_1_9(final PacketInjector packetInjector) {
        this.injector = packetInjector;
    }
    
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf bytebuf, final List<Object> list) {
        if (bytebuf.capacity() > Settings.IMP.VIAVERSION_MAX_CAPACITY) {
            ctx.close().addListener(future -> {
                SpigotGuardLogger.log(Level.INFO, "{0} sent too large packet (size: {1}). If it is false positive please increase the limit of viaversion-max-capacity or just disable viaversion-integration (viaversion is not needed to let this check work correctly, so do not worry if you don't have viaversion and this information).", this.injector.getPlayer().getName(), bytebuf.capacity());
                final ExploitDetectedEvent exploitDetectedEvent;
                final Object o;
                Bukkit.getScheduler().runTask((Plugin)SpigotGuardPlugin.getInstance(), () -> {
                    Bukkit.getPluginManager();
                    new ExploitDetectedEvent((OfflinePlayer)this.injector.getPlayer(), new ExploitDetails(SpigotGuardPlugin.getInstance().getUserManager().findById(this.injector.getPlayer().getUniqueId()), "undefined", "too big packet size", false, false, "increase viaversion-max-capacity in settings.yml or disable viaversion-integration"));
                    ((PluginManager)o).callEvent((Event)exploitDetectedEvent);
                });
            });
            if (bytebuf.refCnt() > 0) {
                bytebuf.clear();
            }
            return;
        }
        list.add(bytebuf.readBytes(bytebuf.readableBytes()));
    }
}
