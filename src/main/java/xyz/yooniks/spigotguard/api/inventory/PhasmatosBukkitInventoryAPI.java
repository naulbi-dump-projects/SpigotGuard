package xyz.yooniks.spigotguard.api.inventory;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PhasmatosBukkitInventoryAPI implements PhasmatosInventoryAPI {
  private final List inventories = new ArrayList();

  public List getInventories() {
    return new ArrayList(this.inventories);
  }

  private static boolean lambda$findByTitle$0(String var0, PhasmatosInventory var1) {
    return Integer.valueOf(var0.toUpperCase().hashCode()).equals(var1.getTitle().toUpperCase().hashCode());
  }

  public void register(Plugin var1) {
    PluginManager var2 = var1.getServer().getPluginManager();
    String var3 = Bukkit.getServer().getVersion();
    if (!var3.contains("1.14") && !var3.contains("1.15") && !var3.contains("1.16")) {
      var2.registerEvents(new PhasmatosInventoryListeners(this), var1);
    } else {
      var2.registerEvents(new PhasmatosInventoryListeners_14(this), var1);
    }

  }

  public void addInventory(PhasmatosInventory var1) {
    this.inventories.add(var1);
  }

  public PhasmatosInventory findByTitle(String var1) {
    return (PhasmatosInventory)this.inventories.stream().filter(PhasmatosBukkitInventoryAPI::lambda$findByTitle$0).findFirst().orElse((Object)null);
  }

  public PhasmatosInventory findByTitleAndSize(String var1, int var2) {
    return (PhasmatosInventory)this.inventories.stream().filter(PhasmatosBukkitInventoryAPI::lambda$findByTitleAndSize$1).findFirst().orElse((Object)null);
  }

  private static boolean lambda$findByTitleAndSize$1(String var0, int var1, PhasmatosInventory var2) {
    boolean var10000;
    if (Integer.valueOf(var0.toUpperCase().hashCode()).equals(var2.getTitle().toUpperCase().hashCode()) && var2.getSize() == var1) {
      var10000 = true;
      boolean var10001 = false;
    } else {
      var10000 = false;
    }

    return var10000;
  }
}
