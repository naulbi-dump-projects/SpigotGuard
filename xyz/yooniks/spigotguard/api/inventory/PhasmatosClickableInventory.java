package xyz.yooniks.spigotguard.api.inventory;

import java.util.function.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;

public interface PhasmatosClickableInventory
{
    PhasmatosClickableInventory addItemAction(final int p0, final Consumer<Player> p1);
    
    void onClick(final InventoryClickEvent p0);
}
