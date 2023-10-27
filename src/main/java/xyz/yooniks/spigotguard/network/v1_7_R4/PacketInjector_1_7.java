package xyz.yooniks.spigotguard.network.v1_7_R4;

import xyz.yooniks.spigotguard.network.*;
import org.bukkit.entity.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.*;
import xyz.yooniks.spigotguard.logger.*;
import java.lang.reflect.*;
import xyz.yooniks.spigotguard.user.*;
import net.minecraft.util.io.netty.channel.*;

public class PacketInjector_1_7 extends PacketInjector
{
    private Channel channel;
    
    public PacketInjector_1_7(final Player player) {
        super(player);
        try {
            final Field field = ((CraftPlayer)this.player).getHandle().playerConnection.networkManager.getClass().getDeclaredField("m");
            field.setAccessible(true);
            this.channel = (Channel)field.get(((CraftPlayer)this.player).getHandle().playerConnection.networkManager);
            field.setAccessible(false);
        }
        catch (Exception ex) {
            SpigotGuardLogger.exception("Could not initialize packet injector", ex);
        }
    }
    
    @Override
    public void injectListener(final User user) {
        final PacketDecoder_1_7 decoder = new PacketDecoder_1_7(this, user);
        this.channel.pipeline().addAfter("decoder", "SpigotGuard_" + this.player.getName(), (ChannelHandler)decoder);
    }
    
    @Override
    public void uninjectListener() {
        if (this.channel.pipeline().get("SpigotGuard_" + this.player.getName()) != null) {
            this.channel.pipeline().remove("SpigotGuard_" + this.player.getName());
        }
        if (this.channel.pipeline().get(this.player.getName()) != null) {
            this.channel.pipeline().remove(this.player.getName());
        }
    }
}
