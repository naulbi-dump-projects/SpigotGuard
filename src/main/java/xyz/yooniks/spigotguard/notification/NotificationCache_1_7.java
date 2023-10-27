package xyz.yooniks.spigotguard.notification;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import xyz.yooniks.spigotguard.event.ExploitDetails;

public class NotificationCache_1_7 implements NotificationCache {
  private final Map<UUID, ExploitDetails> hackers = new HashMap<>();
  
  public void removeCache(UUID paramUUID) {}
  
  public ExploitDetails findCache(UUID paramUUID) {
    return this.hackers.get(paramUUID);
  }
  
  public void addCache(UUID paramUUID, ExploitDetails paramExploitDetails) {}
}