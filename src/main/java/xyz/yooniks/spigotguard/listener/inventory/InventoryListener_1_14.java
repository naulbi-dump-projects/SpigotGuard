package xyz.yooniks.spigotguard.listener.inventory;

import org.bukkit.event.inventory.*;
import org.bukkit.entity.*;
import xyz.yooniks.spigotguard.config.*;
import xyz.yooniks.spigotguard.api.inventory.*;
import org.bukkit.event.*;

public class InventoryListener_1_14 implements Listener
{
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player) || event.getView() == null) {
            return;
        }
        final String title = event.getView().getTitle();
        if (title == null || title.length() <= 0) {
            return;
        }
        if (title.equalsIgnoreCase(MessageHelper.colored(Settings.IMP.INVENTORIES.RECENT_DETECTIONS_INVENTORY.NAME))) {
            event.setCancelled(true);
        }
        if (title.length() >= 7 && MessageHelper.colored(Settings.IMP.INVENTORIES.PLAYER_INFO_INVENTORY.NAME).contains(title.substring(0, 7))) {
            event.setCancelled(true);
        }
    }
}
