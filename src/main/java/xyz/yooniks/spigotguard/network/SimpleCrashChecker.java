package xyz.yooniks.spigotguard.network;

import io.netty.buffer.*;
import xyz.yooniks.spigotguard.config.*;
import org.bukkit.entity.*;
import io.netty.channel.*;
import xyz.yooniks.spigotguard.*;
import xyz.yooniks.spigotguard.helper.*;
import xyz.yooniks.spigotguard.event.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.plugin.*;
import java.util.logging.*;
import xyz.yooniks.spigotguard.logger.*;
import io.netty.util.concurrent.*;

public final class SimpleCrashChecker
{
    public static boolean checkCrash(final Object packet, final ByteBuf copy, final Settings.PACKET_DECODER packet_decoder, final int capacity, final PacketInjector injector, final Player player, final ChannelHandlerContext ctx) {
        if (packet.getClass().getSimpleName().equals("PacketPlayInWindowClick") && capacity > packet_decoder.MAX_WINDOW_SIZE && packet_decoder.MAX_WINDOW_SIZE != -1) {
            final ExploitDetails failure = new ExploitDetails(injector.getPacketDecoder().getUser(), packet.getClass().getSimpleName(), "Packet size too large (" + capacity + " > " + packet_decoder.MAX_WINDOW_SIZE + ")", false, false, "Increase packet-decoder.max-window-size or set it to -1 to disable it");
            if (copy.refCnt() > 1) {
                copy.clear();
            }
            if (Settings.IMP.KICK.TYPE == 0) {
                ctx.close().addListener(future -> Bukkit.getScheduler().runTask((Plugin)SpigotGuardPlugin.getInstance(), () -> Bukkit.getPluginManager().callEvent((Event)new ExploitDetectedEvent((OfflinePlayer)player, failure))));
            }
            else {
                final ExploitDetails exploitDetails;
                Bukkit.getScheduler().runTask((Plugin)SpigotGuardPlugin.getInstance(), () -> {
                    injector.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
                    Bukkit.getPluginManager().callEvent((Event)new ExploitDetectedEvent((OfflinePlayer)player, exploitDetails));
                    return;
                });
            }
            SpigotGuardLogger.log(Level.INFO, SpigotGuardPlugin.getInstance().getSpigotGuardClassLoaded().getSerializable() + "[Adv. decoding] Player {0} tried to crash the server, packet: {1}, details: {2}", player.getName(), packet.getClass().getSimpleName(), failure.getDetails());
            return true;
        }
        if (packet.getClass().getSimpleName().equals("PacketPlayInBlockPlace") && capacity > packet_decoder.MAX_PLACE_SIZE && packet_decoder.MAX_PLACE_SIZE != -1) {
            final ExploitDetails failure = new ExploitDetails(injector.getPacketDecoder().getUser(), packet.getClass().getSimpleName(), "Packet size too large (" + capacity + " > " + packet_decoder.MAX_PLACE_SIZE + ")", false, false, "Increase packet-decoder.max-place-size or set it to -1 to disable it");
            if (copy.refCnt() > 0) {
                copy.clear();
            }
            if (Settings.IMP.KICK.TYPE == 0) {
                ctx.close().addListener(future -> Bukkit.getScheduler().runTask((Plugin)SpigotGuardPlugin.getInstance(), () -> Bukkit.getPluginManager().callEvent((Event)new ExploitDetectedEvent((OfflinePlayer)player, failure))));
            }
            else {
                final ExploitDetails exploitDetails2;
                Bukkit.getScheduler().runTask((Plugin)SpigotGuardPlugin.getInstance(), () -> {
                    injector.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
                    Bukkit.getPluginManager().callEvent((Event)new ExploitDetectedEvent((OfflinePlayer)player, exploitDetails2));
                    return;
                });
            }
            SpigotGuardLogger.log(Level.INFO, SpigotGuardPlugin.getInstance().getSpigotGuardClassLoaded().getSerializable() + "[Adv. decoding] Player {0} tried to crash the server, packet: {1}, details: {2}", player.getName(), packet.getClass().getSimpleName(), failure.getDetails());
            return true;
        }
        if (packet.getClass().getSimpleName().equals("PacketPlayInSetCreativeSlot") && capacity > packet_decoder.MAX_CREATIVE_SIZE && packet_decoder.MAX_CREATIVE_SIZE != -1) {
            final ExploitDetails failure = new ExploitDetails(injector.getPacketDecoder().getUser(), packet.getClass().getSimpleName(), "Packet size too large (" + capacity + " > " + packet_decoder.MAX_CREATIVE_SIZE + ")", false, false, "Increase packet-decoder.max-creative-size or set it to -1 to disable it");
            if (copy.refCnt() > 0) {
                copy.clear();
            }
            if (Settings.IMP.KICK.TYPE == 0) {
                ctx.close().addListener(future -> Bukkit.getScheduler().runTask((Plugin)SpigotGuardPlugin.getInstance(), () -> Bukkit.getPluginManager().callEvent((Event)new ExploitDetectedEvent((OfflinePlayer)player, failure))));
            }
            else {
                final ExploitDetails exploitDetails3;
                Bukkit.getScheduler().runTask((Plugin)SpigotGuardPlugin.getInstance(), () -> {
                    injector.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
                    Bukkit.getPluginManager().callEvent((Event)new ExploitDetectedEvent((OfflinePlayer)player, exploitDetails3));
                    return;
                });
            }
            SpigotGuardLogger.log(Level.INFO, SpigotGuardPlugin.getInstance().getSpigotGuardClassLoaded().getSerializable() + "[Adv. decoding] Player {0} tried to crash the server, packet: {1}, details: {2}", player.getName(), packet.getClass().getSimpleName(), failure.getDetails());
            return true;
        }
        if ((packet.getClass().getSimpleName().equals("PacketPlayInCustomPayload") || packet.getClass().getSimpleName().equals("PacketPlayInBEdit")) && capacity > packet_decoder.MAX_PAYLOAD_SIZE && packet_decoder.MAX_PAYLOAD_SIZE != -1) {
            final ExploitDetails failure = new ExploitDetails(injector.getPacketDecoder().getUser(), packet.getClass().getSimpleName(), "Packet size too large (" + capacity + " > " + packet_decoder.MAX_PAYLOAD_SIZE + ")", false, false, "Increase packet-decoder.max-payload-size or set it to -1 to disable it");
            if (copy.refCnt() > 0) {
                copy.clear();
            }
            if (Settings.IMP.KICK.TYPE == 0) {
                ctx.close().addListener(future -> Bukkit.getScheduler().runTask((Plugin)SpigotGuardPlugin.getInstance(), () -> Bukkit.getPluginManager().callEvent((Event)new ExploitDetectedEvent((OfflinePlayer)player, failure))));
            }
            else {
                final ExploitDetails exploitDetails4;
                Bukkit.getScheduler().runTask((Plugin)SpigotGuardPlugin.getInstance(), () -> {
                    injector.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
                    Bukkit.getPluginManager().callEvent((Event)new ExploitDetectedEvent((OfflinePlayer)player, exploitDetails4));
                    return;
                });
            }
            SpigotGuardLogger.log(Level.INFO, SpigotGuardPlugin.getInstance().getSpigotGuardClassLoaded().getSerializable() + "[Adv. decoding] Player {0} tried to crash the server, packet: {1}, details: {2}", player.getName(), packet.getClass().getSimpleName(), failure.getDetails());
            return true;
        }
        return false;
    }
    
    private SimpleCrashChecker() {
    }
}
