package xyz.yooniks.spigotguard.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class UserManager {
  private final Map<UUID, User> userMap = new HashMap<>();
  
  private static boolean lambda$findByName$0(String paramString, User paramUser) {
    return Integer.valueOf(paramString.toUpperCase().hashCode()).equals(Integer.valueOf(paramUser.getName().toUpperCase().hashCode()));
  }
  
  public User findById(UUID paramUUID) {
    return this.userMap.get(paramUUID);
  }
  
  public List<User> getUsers() {
    return new ArrayList<>(this.userMap.values());
  }
  
  public User findByName(String paramString) {
    return getUsers().stream().filter(paramString::lambda$findByName$0).findFirst().orElse(null);
  }
  
  public User findOrCreate(Player paramPlayer) {
    User user = this.userMap.get(paramPlayer.getUniqueId());
    if (user == null);
    return user;
  }
  
  public void addUser(User paramUser) {}
  
  public void removeUser(UUID paramUUID) {}
}