package xyz.yooniks.spigotguard.network.v1_13_R2;

import org.bukkit.entity.*;
import org.bukkit.craftbukkit.v1_13_R2.entity.*;
import xyz.yooniks.spigotguard.user.*;
import io.netty.channel.*;
import xyz.yooniks.spigotguard.network.*;

public class PacketInjector_1_13 extends PacketInjector
{
    private final Channel channel;
    
    public PacketInjector_1_13(final Player player) {
        super(player);
        this.channel = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel;
    }
    
    @Override
    public void injectListener(final User user) {
        final PacketDecoder packetDecoder = new PacketDecoder_1_13(this, user);
        this.channel.pipeline().addAfter("decoder", "SpigotGuard_" + this.player.getName(), (ChannelHandler)packetDecoder);
    }
    
    @Override
    public void uninjectListener() {
        if (this.channel.pipeline().get("SpigotGuard_" + this.player.getName()) != null) {
            this.channel.pipeline().remove("SpigotGuard_" + this.player.getName());
        }
    }
}
