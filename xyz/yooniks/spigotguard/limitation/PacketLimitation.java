package xyz.yooniks.spigotguard.limitation;

import org.bukkit.configuration.file.*;
import java.util.*;
import org.bukkit.configuration.*;

public class PacketLimitation
{
    private final Map<String, LimitablePacket> limitablePacketMap;
    
    public PacketLimitation() {
        this.limitablePacketMap = new HashMap<String, LimitablePacket>();
    }
    
    public void initialize(final FileConfiguration configuration) {
        for (final String id : configuration.getConfigurationSection("packet-limitter.limits").getKeys(false)) {
            final ConfigurationSection data = configuration.getConfigurationSection("packet-limitter.limits." + id);
            final int limit = data.getInt("limit");
            final boolean cancelOnly = data.getBoolean("cancel-only");
            this.limitablePacketMap.put(id, new LimitablePacket(limit, cancelOnly));
        }
    }
    
    public boolean isLimitable(final String name) {
        return this.limitablePacketMap.containsKey(name);
    }
    
    public LimitablePacket getLimit(final String name) {
        return this.limitablePacketMap.get(name);
    }
}
