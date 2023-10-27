package xyz.yooniks.spigotguard.api.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PhasmatosInventoryListeners implements Listener {
  private final PhasmatosInventoryAPI inventoryAPI;
  
  public PhasmatosInventoryListeners(PhasmatosInventoryAPI paramPhasmatosInventoryAPI) {
    this.inventoryAPI = paramPhasmatosInventoryAPI;
  }
  
  @EventHandler
  public void onClick(InventoryClickEvent paramInventoryClickEvent) {}
}