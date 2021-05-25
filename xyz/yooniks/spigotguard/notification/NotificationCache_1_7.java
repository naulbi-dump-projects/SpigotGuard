package xyz.yooniks.spigotguard.notification;

import xyz.yooniks.spigotguard.event.*;
import java.util.*;

public class NotificationCache_1_7 implements NotificationCache
{
    private final Map<UUID, ExploitDetails> hackers;
    
    public NotificationCache_1_7() {
        this.hackers = new HashMap<UUID, ExploitDetails>();
    }
    
    @Override
    public ExploitDetails findCache(final UUID uuid) {
        return this.hackers.get(uuid);
    }
    
    @Override
    public void addCache(final UUID uuid, final ExploitDetails details) {
        this.hackers.put(uuid, details);
    }
    
    @Override
    public void removeCache(final UUID uuid) {
        this.hackers.remove(uuid);
    }
}
