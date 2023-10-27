package xyz.yooniks.spigotguard.listener;

import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;

public class MoveListenerTests implements Listener {
  @EventHandler
  private void onMove(PlayerMoveEvent paramPlayerMoveEvent) {
    Player player = paramPlayerMoveEvent.getPlayer();
    if (paramPlayerMoveEvent.getFrom().distance(paramPlayerMoveEvent.getTo()) < 0.0D) {
      player.kickPlayer("MoveExploit #1");
      SpigotGuardLogger.log(Level.WARNING, "Exploit detected, (Move #1 - player: " + player.getName() + ")", new Object[0]);
      return;
    } 
    if (paramPlayerMoveEvent.getFrom().distance(paramPlayerMoveEvent.getTo()) > 18.0D) {
      player.kickPlayer("MoveExploit #2");
      SpigotGuardLogger.log(Level.WARNING, "Exploit detected, (Move #2 - player: " + player.getName() + ")", new Object[0]);
      return;
    } 
    try {
      false;
    } catch (Exception exception) {
      player.kickPlayer("MoveExploit #3");
      SpigotGuardLogger.log(Level.WARNING, "Exploit detected, (Move #3 - player: " + player.getName() + ")", new Object[0]);
    } 
  }
}