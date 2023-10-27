package xyz.yooniks.spigotguard.api.inventory;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class PhasmatosChangeableInventory implements PhasmatosInventory {
  private final int size;
  private final String title;
  private final Map items = new HashMap();

  private void lambda$open$0(Inventory var1, Player var2, Integer var3, ItemStack var4) {
    var1.setItem(var3, this.updateItem(var4, var3, var2));
  }

  public int getSize() {
    return this.size;
  }

  public PhasmatosInventory addItem(int var1, ItemStack var2) {
    this.items.put(var1, var2);
    return this;
  }

  public String getTitle() {
    return this.title;
  }

  public void open(Player var1) {
    Inventory var2 = Bukkit.createInventory((InventoryHolder)null, this.size, this.title);
    this.items.forEach(this::lambda$open$0);
    var1.openInventory(var2);
  }

  public abstract ItemStack updateItem(ItemStack var1, int var2, Player var3);

  public PhasmatosChangeableInventory(String var1, int var2) {
    this.title = var1;
    this.size = var2;
  }

  public Map getItems() {
    return new HashMap(this.items);
  }
}
