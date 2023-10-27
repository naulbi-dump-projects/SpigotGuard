package xyz.yooniks.spigotguard.notification;

import java.util.UUID;
import xyz.yooniks.spigotguard.event.ExploitDetails;

public interface NotificationCache {
  ExploitDetails findCache(UUID paramUUID);
  
  void removeCache(UUID paramUUID);
  
  void addCache(UUID paramUUID, ExploitDetails paramExploitDetails);
}