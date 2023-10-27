package xyz.yooniks.spigotguard.network;

import io.netty.handler.codec.*;
import xyz.yooniks.spigotguard.event.*;
import xyz.yooniks.spigotguard.user.*;

public abstract class PacketDecoder extends MessageToMessageDecoder<Object>
{
    protected final PacketInjector injector;
    
    public PacketDecoder(final PacketInjector injector) {
        this.injector = injector;
    }
    
    public abstract ExploitDetails failure(final Object p0);
    
    public abstract User getUser();
}
