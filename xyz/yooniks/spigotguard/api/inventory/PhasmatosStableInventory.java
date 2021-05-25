package xyz.yooniks.spigotguard.api.inventory;

import org.bukkit.*;
import org.bukkit.inventory.*;
import java.util.*;
import org.bukkit.entity.*;

public class PhasmatosStableInventory implements PhasmatosInventory
{
    private final Inventory inventory;
    private final String name;
    
    public PhasmatosStableInventory(final String title, final int size) {
        this.inventory = Bukkit.createInventory((InventoryHolder)null, size, title);
        this.name = title;
    }
    
    @Override
    public PhasmatosInventory addItem(final int slot, final ItemStack item) {
        this.inventory.setItem(slot, item);
        return this;
    }
    
    @Override
    public Map<Integer, ItemStack> getItems() {
        final Map<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            final ItemStack item = this.inventory.getItem(i);
            if (item != null) {
                items.put(i, item);
            }
        }
        return items;
    }
    
    @Override
    public void open(final Player player) {
        player.openInventory(this.inventory);
    }
    
    @Override
    public int getSize() {
        return this.inventory.getSize();
    }
    
    @Override
    public String getTitle() {
        return this.name;
    }
}
