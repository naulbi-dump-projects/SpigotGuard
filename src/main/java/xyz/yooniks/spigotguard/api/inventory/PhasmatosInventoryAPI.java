package xyz.yooniks.spigotguard.api.inventory;

import java.util.List;

public interface PhasmatosInventoryAPI {
  PhasmatosInventory findByTitleAndSize(String paramString, int paramInt);
  
  PhasmatosInventory findByTitle(String paramString);
  
  void addInventory(PhasmatosInventory paramPhasmatosInventory);
  
  List<PhasmatosInventory> getInventories();
}