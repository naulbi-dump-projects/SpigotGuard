package xyz.yooniks.spigotguard.listener.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class DefaultInventoryListener implements Listener {
  @EventHandler
  public void onClick(InventoryClickEvent paramInventoryClickEvent) {
    if (paramInventoryClickEvent.getWhoClicked() instanceof org.bukkit.entity.Player);
  }
}