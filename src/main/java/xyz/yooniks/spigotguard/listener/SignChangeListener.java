package xyz.yooniks.spigotguard.listener;

import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;

public class SignChangeListener implements Listener {
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onSignChange(SignChangeEvent paramSignChangeEvent) {
    Player player = paramSignChangeEvent.getPlayer();
    String[] arrayOfString = paramSignChangeEvent.getLines();
    int i = arrayOfString.length;
    byte b = 0;
    while (b < i) {
      String str = arrayOfString[b];
      if (str.length() >= 46) {
        paramSignChangeEvent.setCancelled(true);
        SpigotGuardLogger.log(Level.INFO, player.getName() + " -> Too long sign line!", new Object[0]);
        return;
      } 
      b++;
      false;
    } 
  }
}