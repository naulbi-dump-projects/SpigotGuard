package xyz.yooniks.spigotguard.user;

import org.bukkit.entity.*;
import java.util.*;

public class UserManager
{
    private final Map<UUID, User> userMap;
    
    public UserManager() {
        this.userMap = new HashMap<UUID, User>();
    }
    
    public User findOrCreate(final Player player) {
        User user = this.userMap.get(player.getUniqueId());
        if (user == null) {
            this.userMap.put(player.getUniqueId(), user = new User(player.getName(), player.getAddress().getAddress().getHostAddress(), player.getUniqueId()));
        }
        return user;
    }
    
    public User findById(final UUID id) {
        return this.userMap.get(id);
    }
    
    public User findByName(final String name) {
        return this.getUsers().stream().filter(user -> user.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    
    public void addUser(final User user) {
        this.userMap.put(user.getId(), user);
    }
    
    public List<User> getUsers() {
        return new ArrayList<User>(this.userMap.values());
    }
}
