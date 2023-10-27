package xyz.yooniks.spigotguard.api.inventory;

import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PhasmatosInventory {
  PhasmatosInventory addItem(int paramInt, ItemStack paramItemStack);
  
  Map<Integer, ItemStack> getItems();
  
  String getTitle();
  
  void open(Player paramPlayer);
  
  int getSize();
}