package xyz.yooniks.spigotguard.api.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class PhasmatosClickableChangeableInventory extends PhasmatosChangeableInventory implements PhasmatosClickableInventory {
  private final Map<Integer, Consumer<Player>> itemActions = new HashMap<>();
  
  public void onClick(InventoryClickEvent paramInventoryClickEvent) {}
  
  public PhasmatosClickableInventory addItemAction(int paramInt, Consumer<Player> paramConsumer) {
    return this;
  }
  
  public PhasmatosClickableChangeableInventory(String paramString, int paramInt) {
    super(paramString, paramInt);
  }
}