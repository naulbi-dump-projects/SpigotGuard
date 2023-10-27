package xyz.yooniks.spigotguard.notification;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import xyz.yooniks.spigotguard.event.ExploitDetails;

public class NotificationCacheDefault implements NotificationCache {
  private final Cache<UUID, ExploitDetails> hackers = CacheBuilder.newBuilder().expireAfterWrite(10L, TimeUnit.MINUTES).build();
  
  public void removeCache(UUID paramUUID) {
    this.hackers.invalidate(paramUUID);
  }
  
  public void addCache(UUID paramUUID, ExploitDetails paramExploitDetails) {
    this.hackers.put(paramUUID, paramExploitDetails);
  }
  
  public ExploitDetails findCache(UUID paramUUID) {
    return (ExploitDetails)this.hackers.getIfPresent(paramUUID);
  }
}