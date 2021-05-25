package xyz.yooniks.spigotguard.network;

import org.bukkit.entity.*;
import xyz.yooniks.spigotguard.user.*;

public abstract class PacketInjector
{
    protected final Player player;
    protected PacketDecoder packetDecoder;
    
    public PacketInjector(final Player player) {
        this.player = player;
    }
    
    public abstract void injectListener(final User p0);
    
    public abstract void uninjectListener();
    
    public Player getPlayer() {
        return this.player;
    }
    
    public PacketDecoder getPacketDecoder() {
        return this.packetDecoder;
    }
}
