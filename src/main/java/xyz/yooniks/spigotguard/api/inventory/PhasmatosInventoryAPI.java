package xyz.yooniks.spigotguard.api.inventory;

import java.util.*;

public interface PhasmatosInventoryAPI
{
    void addInventory(final PhasmatosInventory p0);
    
    List<PhasmatosInventory> getInventories();
    
    PhasmatosInventory findByTitle(final String p0);
    
    PhasmatosInventory findByTitleAndSize(final String p0, final int p1);
}
