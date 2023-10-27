package xyz.yooniks.spigotguard.api.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PhasmatosClickableStableInventory extends PhasmatosStableInventory implements PhasmatosClickableInventory {
  private final Map<Integer, Consumer<Player>> itemActions = new HashMap<>();
  
  public PhasmatosClickableStableInventory(String paramString, int paramInt) {
    super(paramString, paramInt);
  }
  
  public PhasmatosClickableInventory addItemAction(int paramInt, Consumer<Player> paramConsumer) {
    return this;
  }
  
  public void onClick(InventoryClickEvent paramInventoryClickEvent) {}
}