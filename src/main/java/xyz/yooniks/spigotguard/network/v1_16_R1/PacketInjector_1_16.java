package xyz.yooniks.spigotguard.network.v1_16_R1;

import org.bukkit.entity.*;
import org.bukkit.craftbukkit.v1_16_R1.entity.*;
import xyz.yooniks.spigotguard.user.*;
import io.netty.channel.*;
import java.util.logging.*;
import xyz.yooniks.spigotguard.logger.*;
import org.bukkit.*;
import xyz.yooniks.spigotguard.*;
import org.bukkit.plugin.*;
import xyz.yooniks.spigotguard.network.*;

public class PacketInjector_1_16 extends PacketInjector
{
    private final Channel channel;
    
    public PacketInjector_1_16(final Player player) {
        super(player);
        this.channel = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel;
    }
    
    @Override
    public void injectListener(final User user) {
        final PacketDecoder packetDecoder = new PacketDecoder_1_16(this, user);
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
            }, 30L);
        }
    }
    
    @Override
    public void uninjectListener() {
        if (this.channel.pipeline().get("SpigotGuard_" + this.player.getName()) != null) {
            this.channel.pipeline().remove("SpigotGuard_" + this.player.getName());
        }
    }
}
