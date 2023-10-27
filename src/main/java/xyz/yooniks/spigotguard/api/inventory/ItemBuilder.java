package xyz.yooniks.spigotguard.api.inventory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {
  private ItemStack item;

  public ItemBuilder withDurability(short var1) {
    this.item.setDurability(var1);
    return this;
  }

  public ItemBuilder withAmount(int var1) {
    this.item.setAmount(var1);
    return this;
  }

  public ItemBuilder withLore(List var1) {
    ItemMeta var2 = this.item.getItemMeta();
    var2.setLore(MessageHelper.colored(var1));
    this.item.setItemMeta(var2);
    return this;
  }

  public ItemBuilder withLore(String... var1) {
    return this.withLore(Arrays.asList(var1));
  }

  public static ItemBuilder withSection(ConfigurationSection var0) {
    if (var0 == null) {
      return (new ItemBuilder(Material.GRASS)).withName("Section is null");
    } else {
      ItemBuilder var1 = new ItemBuilder(Material.GRASS);
      if (var0.isString("material")) {
        Material var2 = Material.matchMaterial(var0.getString("material"));
        if (var2 != null) {
          var1.withType(var2);
        }
      }

      if (var0.isList("lore")) {
        var1.withLore(var0.getStringList("lore"));
      }

      if (var0.isString("name")) {
        var1.withName(var0.getString("name"));
      }

      if (var0.isInt("amount")) {
        var1.withAmount(var0.getInt("amount"));
      }

      if (var0.isInt("data")) {
        var1.withDurability((short)var0.getInt("data"));
      }

      if (var0.isList("enchants")) {
        Iterator var9 = var0.getStringList("enchants").iterator();

        while(true) {
          while(var9.hasNext()) {
            String var3 = (String)var9.next();
            String[] var4 = var3.split(";");
            boolean var10000;
            if (var4.length < 1) {
              var10000 = false;
            } else {
              Enchantment var5 = Enchantment.getByName(var4[0]);
              if (var5 == null) {
                var10000 = false;
              } else {
                int var6;
                try {
                  var6 = Integer.parseInt(var4[1]);
                } catch (NumberFormatException var8) {
                  var10000 = false;
                  continue;
                }

                var10000 = false;
                var1.addEnchantment(var5, var6);
                boolean var10001 = false;
              }
            }
          }

          return var1;
        }
      } else {
        return var1;
      }
    }
  }

  public ItemBuilder(Material var1) {
    if (iiiiii(var1, Material.AIR)) {
      throw new IllegalArgumentException("Material cannot be AIR!");
    } else {
      this.item = new ItemStack(var1);
    }
  }

  public ItemBuilder withName(String var1) {
    ItemMeta var2 = this.item.getItemMeta();
    var2.setDisplayName(MessageHelper.colored(var1));
    this.item.setItemMeta(var2);
    return this;
  }

  public ItemBuilder withType(Material var1) {
    if (iiiiii(var1, Material.AIR)) {
      throw new IllegalArgumentException("Material cannot be AIR!");
    } else if (this.item == null) {
      this.item = new ItemStack(var1);
      return this;
    } else {
      this.item.setType(var1);
      return this;
    }
  }

  public ItemBuilder(ItemStack var1) {
    this.item = var1;
  }

  public ItemBuilder addEnchantment(Enchantment var1, int var2) {
    ItemMeta var3 = this.item.getItemMeta();
    var3.addEnchant(var1, var2, true);
    this.item.setItemMeta(var3);
    return this;
  }

  public ItemBuilder(Material var1, int var2, short var3) {
    if (iiiiii(var1, Material.AIR)) {
      throw new IllegalArgumentException("Material cannot be AIR!");
    } else {
      this.item = new ItemStack(var1, var2, var3);
    }
  }

  public ItemStack build() {
    return this.item;
  }

  private static boolean iiiiii(Object var0, Object var1) {
    return var0 == var1;
  }
}
