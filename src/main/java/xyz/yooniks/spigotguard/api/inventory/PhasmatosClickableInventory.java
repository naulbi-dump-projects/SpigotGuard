package xyz.yooniks.spigotguard.api.inventory;

import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface PhasmatosClickableInventory {
  void onClick(InventoryClickEvent paramInventoryClickEvent);
  
  PhasmatosClickableInventory addItemAction(int paramInt, Consumer<Player> paramConsumer);
}