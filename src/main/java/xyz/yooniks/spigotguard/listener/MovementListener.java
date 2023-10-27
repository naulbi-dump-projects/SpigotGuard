package xyz.yooniks.spigotguard.listener;

import java.util.logging.Level;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.yooniks.spigotguard.config.Settings;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;

public class MovementListener implements Listener {
  @EventHandler(ignoreCancelled = true)
  public void onPlayerMove(PlayerMoveEvent paramPlayerMoveEvent) {
    Location location = paramPlayerMoveEvent.getTo();
    Chunk chunk = location.getChunk();
    if ((Settings.IMP.POSITION_CHECKS.PREVENT_MOVING_INTO_UNLOADED_CHUNKS && !chunk.isLoaded()) || !location.getWorld().isChunkLoaded(chunk)) {
      paramPlayerMoveEvent.setCancelled(true);
      SpigotGuardLogger.log(Level.INFO, "Prevented player " + paramPlayerMoveEvent.getPlayer().getName() + " from moving into unloaded chunk.", new Object[0]);
    } 
  }
}