package xyz.yooniks.spigotguard.api.inventory;

import org.bukkit.inventory.*;
import org.bukkit.*;
import org.bukkit.configuration.*;
import org.bukkit.enchantments.*;
import org.bukkit.inventory.meta.*;
import java.util.*;

public class ItemBuilder
{
    private ItemStack item;
    
    public ItemBuilder(final Material material) {
        if (material == Material.AIR) {
            throw new IllegalArgumentException("Material cannot be AIR!");
        }
        this.item = new ItemStack(material);
    }
    
    public ItemBuilder(final Material material, final int amount, final short durability) {
        if (material == Material.AIR) {
            throw new IllegalArgumentException("Material cannot be AIR!");
        }
        this.item = new ItemStack(material, amount, durability);
    }
    
    public ItemBuilder(final ItemStack item) {
        this.item = item;
    }
    
    public static ItemBuilder withSection(final ConfigurationSection section) {
        if (section == null) {
            return new ItemBuilder(Material.GRASS).withName("Section is null");
        }
        final ItemBuilder builder = new ItemBuilder(Material.GRASS);
        if (section.isString("material")) {
            final Material material = Material.matchMaterial(section.getString("material"));
            if (material != null) {
                builder.withType(material);
            }
        }
        if (section.isList("lore")) {
            builder.withLore(section.getStringList("lore"));
        }
        if (section.isString("name")) {
            builder.withName(section.getString("name"));
        }
        if (section.isInt("amount")) {
            builder.withAmount(section.getInt("amount"));
        }
        if (section.isInt("data")) {
            builder.withDurability((short)section.getInt("data"));
        }
        if (section.isList("enchants")) {
            for (final String enchant : section.getStringList("enchants")) {
                final String[] part = enchant.split(";");
                if (part.length < 1) {
                    continue;
                }
                final Enchantment ench = Enchantment.getByName(part[0]);
                if (ench == null) {
                    continue;
                }
                int level;
                try {
                    level = Integer.parseInt(part[1]);
                }
                catch (NumberFormatException ex) {
                    continue;
                }
                builder.addEnchantment(ench, level);
            }
        }
        return builder;
    }
    
    public ItemBuilder withType(final Material material) {
        if (material == Material.AIR) {
            throw new IllegalArgumentException("Material cannot be AIR!");
        }
        if (this.item == null) {
            this.item = new ItemStack(material);
            return this;
        }
        this.item.setType(material);
        return this;
    }
    
    public ItemBuilder withName(final String name) {
        final ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(MessageHelper.colored(name));
        this.item.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder withLore(final String... lore) {
        return this.withLore(Arrays.asList(lore));
    }
    
    public ItemBuilder withLore(final List<String> lore) {
        final ItemMeta meta = this.item.getItemMeta();
        meta.setLore((List)MessageHelper.colored(lore));
        this.item.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder withAmount(final int amount) {
        this.item.setAmount(amount);
        return this;
    }
    
    public ItemBuilder withDurability(final short durability) {
        this.item.setDurability(durability);
        return this;
    }
    
    public ItemBuilder addEnchantment(final Enchantment enchant, final int level) {
        final ItemMeta meta = this.item.getItemMeta();
        meta.addEnchant(enchant, level, true);
        this.item.setItemMeta(meta);
        return this;
    }
    
    public ItemStack build() {
        return this.item;
    }
}
