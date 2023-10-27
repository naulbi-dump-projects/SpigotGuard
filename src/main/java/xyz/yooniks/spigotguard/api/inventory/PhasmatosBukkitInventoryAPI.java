package xyz.yooniks.spigotguard.api.inventory;

import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.plugin.*;
import java.util.*;

public class PhasmatosBukkitInventoryAPI implements PhasmatosInventoryAPI
{
    private final List<PhasmatosInventory> inventories;
    
    public PhasmatosBukkitInventoryAPI() {
        this.inventories = new ArrayList<PhasmatosInventory>();
    }
    
    public void register(final Plugin plugin) {
        final PluginManager pluginManager = plugin.getServer().getPluginManager();
        final String version = Bukkit.getServer().getVersion();
        if (version.contains("1.14") || version.contains("1.15") || version.contains("1.16")) {
            pluginManager.registerEvents((Listener)new PhasmatosInventoryListeners_14(this), plugin);
        }
        else {
            pluginManager.registerEvents((Listener)new PhasmatosInventoryListeners(this), plugin);
        }
    }
    
    @Override
    public PhasmatosInventory findByTitle(final String title) {
        return this.inventories.stream().filter(inventory -> inventory.getTitle().equalsIgnoreCase(title)).findFirst().orElse(null);
    }
    
    @Override
    public PhasmatosInventory findByTitleAndSize(final String title, final int size) {
        return this.inventories.stream().filter(inventory -> inventory.getTitle().equalsIgnoreCase(title) && inventory.getSize() == size).findFirst().orElse(null);
    }
    
    @Override
    public void addInventory(final PhasmatosInventory inventory) {
        this.inventories.add(inventory);
    }
    
    @Override
    public List<PhasmatosInventory> getInventories() {
        return new ArrayList<PhasmatosInventory>(this.inventories);
    }
}
