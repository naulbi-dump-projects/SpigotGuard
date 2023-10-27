package xyz.yooniks.spigotguard.api.inventory;

import org.bukkit.event.inventory.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.event.*;

public class PhasmatosInventoryListeners_14 implements Listener
{
    private final PhasmatosInventoryAPI inventoryAPI;
    
    public PhasmatosInventoryListeners_14(final PhasmatosInventoryAPI inventoryAPI) {
        this.inventoryAPI = inventoryAPI;
    }
    
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        final Player player = (Player)event.getWhoClicked();
        final Inventory inventory = event.getClickedInventory();
        if (inventory == null || inventory.equals(player.getInventory())) {
            return;
        }
        final PhasmatosInventory phasmatosInventory = this.inventoryAPI.findByTitleAndSize(event.getView().getTitle(), inventory.getSize());
        if (phasmatosInventory instanceof PhasmatosClickableInventory) {
            ((PhasmatosClickableInventory)phasmatosInventory).onClick(event);
        }
    }
}
