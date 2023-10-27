package xyz.yooniks.spigotguard.api.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PhasmatosInventoryListeners_14 implements Listener {
  private final PhasmatosInventoryAPI inventoryAPI;
  
  @EventHandler
  public void onClick(InventoryClickEvent paramInventoryClickEvent) {}
  
  public PhasmatosInventoryListeners_14(PhasmatosInventoryAPI paramPhasmatosInventoryAPI) {
    this.inventoryAPI = paramPhasmatosInventoryAPI;
  }
}