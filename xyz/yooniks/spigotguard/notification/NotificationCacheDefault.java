package xyz.yooniks.spigotguard.notification;

import java.util.*;
import xyz.yooniks.spigotguard.event.*;
import com.google.common.cache.*;
import java.util.concurrent.*;

public class NotificationCacheDefault implements NotificationCache
{
    private final Cache<UUID, ExploitDetails> hackers;
    
    public NotificationCacheDefault() {
        this.hackers = (Cache<UUID, ExploitDetails>)CacheBuilder.newBuilder().expireAfterWrite(10L, TimeUnit.MINUTES).build();
    }
    
    @Override
    public ExploitDetails findCache(final UUID uuid) {
        return (ExploitDetails)this.hackers.getIfPresent((Object)uuid);
    }
    
    @Override
    public void addCache(final UUID uuid, final ExploitDetails details) {
        this.hackers.put((Object)uuid, (Object)details);
    }
    
    @Override
    public void removeCache(final UUID uuid) {
        this.hackers.invalidate((Object)uuid);
    }
}
