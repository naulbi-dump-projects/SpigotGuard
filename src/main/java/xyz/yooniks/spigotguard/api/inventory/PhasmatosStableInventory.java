package xyz.yooniks.spigotguard.api.inventory;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PhasmatosStableInventory implements PhasmatosInventory {
  private final Inventory inventory;
  
  private final String name;
  
  public void open(Player paramPlayer) {}
  
  public PhasmatosInventory addItem(int paramInt, ItemStack paramItemStack) {
    this.inventory.setItem(paramInt, paramItemStack);
    return this;
  }
  
  public PhasmatosStableInventory(String paramString, int paramInt) {
    this.inventory = Bukkit.createInventory(null, paramInt, paramString);
    this.name = paramString;
  }
  
  public Map<Integer, ItemStack> getItems() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    byte b = 0;
    while (b < this.inventory.getSize()) {
      ItemStack itemStack = this.inventory.getItem(b);
      if (itemStack == null) {
        false;
      } else {
      
      } 
      b++;
      false;
    } 
    return (Map)hashMap;
  }
  
  public String getTitle() {
    return this.name;
  }
  
  public int getSize() {
    return this.inventory.getSize();
  }
}