package xyz.yooniks.spigotguard.thread;

import java.util.List;
import java.util.logging.Level;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;
import xyz.yooniks.spigotguard.user.User;

public class SpigotGuardUninjector implements Runnable {
  private final List<User> users;
  
  public void run() {
    SpigotGuardLogger.log(Level.INFO, "Uninjecting packet listeners of online users..", new Object[0]);
    for (User user : this.users) {
      if (user.getPacketInjector() == null) {
        false;
        continue;
      } 
      user.getPacketInjector().uninjectListener();
      false;
    } 
    SpigotGuardLogger.log(Level.INFO, "Uninjected packet listeners of online users!", new Object[0]);
  }
  
  public SpigotGuardUninjector(List<User> paramList) {
    this.users = paramList;
  }
}