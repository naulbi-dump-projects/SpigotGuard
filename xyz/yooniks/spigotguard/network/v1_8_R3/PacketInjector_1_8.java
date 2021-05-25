package xyz.yooniks.spigotguard.network.v1_8_R3;

import org.bukkit.entity.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import xyz.yooniks.spigotguard.user.*;
import io.netty.channel.*;
import java.util.logging.*;
import xyz.yooniks.spigotguard.logger.*;
import org.bukkit.*;
import xyz.yooniks.spigotguard.*;
import org.bukkit.plugin.*;
import xyz.yooniks.spigotguard.config.*;
import xyz.yooniks.spigotguard.network.*;

public class PacketInjector_1_8 extends PacketInjector
{
    private final Channel channel;
    private static final boolean VIA_VERSION;
    
    public PacketInjector_1_8(final Player player) {
        super(player);
        this.channel = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel;
    }
    
    @Override
    public void injectListener(final User user) {
        final PacketDecoder packetDecoder = new PacketDecoder_1_8(this, user);
        this.packetDecoder = packetDecoder;
        try {
            this.channel.pipeline().addAfter("decoder", "SpigotGuard_" + this.player.getName(), (ChannelHandler)packetDecoder);
        }
        catch (Throwable ex) {
            SpigotGuardLogger.log(Level.WARNING, "Could not inject packetListener for {0}, trying again in 1.5 sec ({1})", this.player.getName(), ex.getMessage());
            final ChannelHandler channelHandler;
            Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)SpigotGuardPlugin.getInstance(), () -> {
                try {
                    this.channel.pipeline().addAfter("decoder", "SpigotGuard_" + this.player.getName(), channelHandler);
                }
                catch (Throwable ex2) {
                    SpigotGuardLogger.log(Level.WARNING, "Could not inject packetListener for {0}, even after 1.5 sec, reason: {1}", this.player.getName(), ex2.getMessage());
                }
                return;
            }, 30L);
        }
        if (PacketInjector_1_8.VIA_VERSION && ViaVersionCheck.shouldChangeCompressor(this.player) && Settings.IMP.PACKET_DECODER.ENABLED) {
            if (this.channel.pipeline().get("decompress") != null) {
                this.channel.pipeline().addAfter("decompress", "sg_decompress", (ChannelHandler)new PacketDecompress_1_8(this));
            }
            else {
                this.channel.pipeline().addAfter("splitter", "sg_decompress", (ChannelHandler)new PacketDecompress_1_8(this));
            }
        }
        try {
            this.channel.pipeline().addBefore("packet_handler", "sg_listener", (ChannelHandler)new PacketListener_1_8(this, user));
        }
        catch (Throwable ex) {
            SpigotGuardLogger.log(Level.WARNING, "Could not inject packetHandler for {0}, trying again in 1.5 sec ({1})", this.player.getName(), ex.getMessage());
            Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)SpigotGuardPlugin.getInstance(), () -> {
                try {
                    this.channel.pipeline().addBefore("packet_handler", "sg_listener", (ChannelHandler)new PacketListener_1_8(this, user));
                }
                catch (Throwable ex3) {
                    SpigotGuardLogger.log(Level.WARNING, "Could not inject packetHandler for {0}, even after 1.5 sec, reason: {1}", this.player.getName(), ex3.getMessage());
                }
            }, 30L);
        }
    }
    
    @Override
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
        }
        catch (Exception ex) {
            SpigotGuardLogger.log(Level.WARNING, "Could not uninject packetListener for {0}, reason: {1}", this.player.getName(), ex.getMessage());
        }
    }
    
    static {
        VIA_VERSION = (Bukkit.getPluginManager().getPlugin("ViaVersion") != null);
    }
}
