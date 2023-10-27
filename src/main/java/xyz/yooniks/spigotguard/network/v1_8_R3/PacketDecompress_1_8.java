package xyz.yooniks.spigotguard.network.v1_8_R3;

import io.netty.handler.codec.*;
import java.util.*;
import io.netty.buffer.*;
import xyz.yooniks.spigotguard.config.*;
import xyz.yooniks.spigotguard.*;
import xyz.yooniks.spigotguard.event.*;
import io.netty.util.*;
import java.util.logging.*;
import xyz.yooniks.spigotguard.logger.*;
import xyz.yooniks.spigotguard.network.*;
import io.netty.channel.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.*;
import net.minecraft.server.v1_8_R3.*;
import io.netty.util.concurrent.*;
import org.bukkit.plugin.*;

public class PacketDecompress_1_8 extends ByteToMessageDecoder
{
    private final PacketInjector injector;
    private boolean disconnected;
    
    public PacketDecompress_1_8(final PacketInjector packetInjector) {
        this.disconnected = false;
        this.injector = packetInjector;
    }
    
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf byteBuf, final List<Object> list) {
        try {
            final Channel channel = ctx.channel();
            final AttributeKey<EnumProtocol> attributeKey = (AttributeKey<EnumProtocol>)NetworkManager.c;
            if (byteBuf instanceof EmptyByteBuf) {
                list.add(byteBuf.readBytes(byteBuf.readableBytes()));
                return;
            }
            if (this.disconnected) {
                if (byteBuf.refCnt() > 0) {
                    byteBuf.clear();
                }
                return;
            }
            final Player player = this.injector.getPlayer();
            if (player == null || !player.isOnline()) {
                if (byteBuf.refCnt() > 0) {
                    byteBuf.clear();
                }
                return;
            }
            final Settings.PACKET_DECODER packet_decoder = Settings.IMP.PACKET_DECODER;
            final int capacity = byteBuf.capacity();
            final int maxCapacity = packet_decoder.MAX_MAIN_SIZE;
            if (maxCapacity != -1 && capacity > maxCapacity) {
                ctx.close().addListener(future -> {
                    SpigotGuardLogger.log(Level.INFO, "{0} sent too large packet (size: {1}). If it is false positive please increase the limit of packet-decoder.max-main-size or set it to -1.", player.getName(), capacity);
                    final ExploitDetectedEvent exploitDetectedEvent;
                    final Object o;
                    Bukkit.getScheduler().runTask((Plugin)SpigotGuardPlugin.getInstance(), () -> {
                        Bukkit.getPluginManager();
                        new ExploitDetectedEvent((OfflinePlayer)player, new ExploitDetails(SpigotGuardPlugin.getInstance().getUserManager().findOrCreate(this.injector.getPlayer()), "undefined (bf check)", "too big packet size", false, false, "set \"packet-decoder.max-main-size\" in settings.yml to higher value or set it to -1 to disable it"));
                        ((PluginManager)o).callEvent((Event)exploitDetectedEvent);
                    });
                });
                if (byteBuf.refCnt() > 0) {
                    byteBuf.clear();
                }
                return;
            }
            if (capacity < 0) {
                final Event event;
                final OfflinePlayer player2;
                final Object o;
                Bukkit.getScheduler().runTask((Plugin)SpigotGuardPlugin.getInstance(), () -> {
                    Bukkit.getPluginManager();
                    new ExploitDetectedEvent(player2, new ExploitDetails(SpigotGuardPlugin.getInstance().getUserManager().findOrCreate(this.injector.getPlayer()), "undefined (bf check)", "too small packet size (capacity < 0)", false, false));
                    ((PluginManager)o).callEvent(event);
                });
                return;
            }
            if (byteBuf.refCnt() < 1) {
                final Event event2;
                final OfflinePlayer player3;
                final Object o2;
                Bukkit.getScheduler().runTask((Plugin)SpigotGuardPlugin.getInstance(), () -> {
                    Bukkit.getPluginManager();
                    new ExploitDetectedEvent(player3, new ExploitDetails(SpigotGuardPlugin.getInstance().getUserManager().findOrCreate(this.injector.getPlayer()), "undefined (bf check)", "too small packet size (refCnt < 1)", false, false));
                    ((PluginManager)o2).callEvent(event2);
                });
                return;
            }
            final ByteBuf copy = byteBuf.copy();
            final PacketDataSerializer packetDataSerializer = new PacketDataSerializer(copy);
            final int id = packetDataSerializer.e();
            Packet<?> packet;
            try {
                packet = (Packet<?>)((EnumProtocol)channel.attr((AttributeKey)attributeKey).get()).a(EnumProtocolDirection.SERVERBOUND, id);
            }
            catch (Throwable ex) {
                SpigotGuardLogger.log(Level.WARNING, "[1] Exception while packet decoding from {0}, id: {1}, error:", this.injector.getPlayer().getName(), id, ex.getMessage());
                ex.printStackTrace();
                list.add(byteBuf.readBytes(byteBuf.readableBytes()));
                return;
            }
            if (packet == null) {
                if (copy.refCnt() > 0) {
                    copy.clear();
                }
                list.add(byteBuf.readBytes(byteBuf.readableBytes()));
                return;
            }
            try {
                packet.a(packetDataSerializer);
            }
            catch (Throwable ex) {
                SpigotGuardLogger.log(Level.WARNING, "[2] Exception while packet decoding from {0}, id: {1}, error:", this.injector.getPlayer().getName(), id, ex.getMessage());
                ex.printStackTrace();
                list.add(byteBuf.readBytes(byteBuf.readableBytes()));
                SpigotGuardLogger.log(Level.WARNING, "If you have any problems with that error, please disable packet-decoder in settings.yml", new Object[0]);
                return;
            }
            if (!this.injector.getPacketDecoder().getUser().isInjectedPacketDecoder() && (packet instanceof PacketPlayInWindowClick || packet instanceof PacketPlayInSetCreativeSlot || packet instanceof PacketPlayInBlockPlace || packet instanceof PacketPlayInCustomPayload)) {
                if (packet instanceof PacketPlayInCustomPayload) {
                    final String payloadChannel = ((PacketPlayInCustomPayload)packet).a();
                    if (payloadChannel.equalsIgnoreCase("MC|BEdit") || payloadChannel.equalsIgnoreCase("MC|BSign")) {
                        byteBuf.skipBytes(byteBuf.readableBytes());
                        if (copy.refCnt() > 0) {
                            copy.clear();
                        }
                        SpigotGuardLogger.log(Level.INFO, "Player {0} has tried to send invalid packet (PROBABLY), but we did not inject packet_decoder yet!", new Object[0]);
                        return;
                    }
                }
                if (capacity > 100) {
                    SpigotGuardLogger.log(Level.INFO, "Player {0} has tried to send invalid packet (PROBABLY), but we did not inject packet_decoder yet!", new Object[0]);
                    byteBuf.skipBytes(byteBuf.readableBytes());
                    if (copy.refCnt() > 0) {
                        copy.clear();
                    }
                    return;
                }
            }
            if (SimpleCrashChecker.checkCrash(packet, copy, packet_decoder, capacity, this.injector, player, ctx)) {
                this.disconnected = true;
                if (copy.refCnt() > 0) {
                    copy.clear();
                }
                return;
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
            list.add(byteBuf.readBytes(byteBuf.readableBytes()));
            return;
        }
        list.add(byteBuf.readBytes(byteBuf.readableBytes()));
    }
}
