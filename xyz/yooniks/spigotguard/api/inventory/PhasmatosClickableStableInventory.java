package xyz.yooniks.spigotguard.api.inventory;

import java.util.function.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.event.inventory.*;

public class PhasmatosClickableStableInventory extends PhasmatosStableInventory implements PhasmatosClickableInventory
{
    private final Map<Integer, Consumer<Player>> itemActions;
    
    public PhasmatosClickableStableInventory(final String title, final int size) {
        super(title, size);
        this.itemActions = new HashMap<Integer, Consumer<Player>>();
    }
    
    @Override
    public void onClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        event.setCancelled(true);
        if (this.itemActions.containsKey(event.getSlot())) {
            this.itemActions.get(event.getSlot()).accept((Player)event.getWhoClicked());
        }
    }
    
    @Override
    public PhasmatosClickableInventory addItemAction(final int slot, final Consumer<Player> action) {
        this.itemActions.put(slot, action);
        return this;
    }
}
