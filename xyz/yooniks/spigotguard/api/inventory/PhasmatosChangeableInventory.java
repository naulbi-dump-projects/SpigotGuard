package xyz.yooniks.spigotguard.api.inventory;

import java.util.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.inventory.*;

public abstract class PhasmatosChangeableInventory implements PhasmatosInventory
{
    private final String title;
    private final int size;
    private final Map<Integer, ItemStack> items;
    
    public PhasmatosChangeableInventory(final String title, final int size) {
        this.items = new HashMap<Integer, ItemStack>();
        this.title = title;
        this.size = size;
    }
    
    @Override
    public PhasmatosInventory addItem(final int slot, final ItemStack item) {
        this.items.put(slot, item);
        return this;
    }
    
    @Override
    public Map<Integer, ItemStack> getItems() {
        return new HashMap<Integer, ItemStack>(this.items);
    }
    
    @Override
    public void open(final Player player) {
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, this.size, this.title);
        final Inventory inventory2;
        final int n;
        final ItemStack itemStack;
        this.items.forEach((slot, item) -> {
            slot;
            item = this.updateItem(item, slot, player);
            inventory2.setItem(n, itemStack);
            return;
        });
        player.openInventory(inventory);
    }
    
    @Override
    public String getTitle() {
        return this.title;
    }
    
    @Override
    public int getSize() {
        return this.size;
    }
    
    public abstract ItemStack updateItem(final ItemStack p0, final int p1, final Player p2);
}
