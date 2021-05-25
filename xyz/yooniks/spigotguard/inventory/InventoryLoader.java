package xyz.yooniks.spigotguard.inventory;

import xyz.yooniks.spigotguard.*;
import xyz.yooniks.spigotguard.config.*;
import java.util.logging.*;
import xyz.yooniks.spigotguard.logger.*;
import xyz.yooniks.spigotguard.nms.*;
import java.io.*;
import xyz.yooniks.spigotguard.helper.*;
import org.bukkit.*;
import xyz.yooniks.spigotguard.event.*;
import java.util.stream.*;
import xyz.yooniks.spigotguard.api.inventory.*;
import org.bukkit.plugin.*;
import java.text.*;
import org.bukkit.inventory.*;
import xyz.yooniks.spigotguard.user.*;
import java.util.*;
import org.bukkit.entity.*;

public class InventoryLoader
{
    public void loadInventories(final SpigotGuardPlugin plugin) {
        if (Settings.IMP.INVENTORIES.PLAYER_INFO_INVENTORY.NAME.length() < 7) {
            SpigotGuardLogger.log(Level.WARNING, "player-info-inventory in settings.yml MUST have at least 7 chars! Inventory will not work!", new Object[0]);
        }
        final PhasmatosBukkitInventoryAPI inventoryAPI = new PhasmatosBukkitInventoryAPI();
        final Material material = (plugin.getNmsVersion() == NMSVersion.ONE_DOT_THIRTEEN || plugin.getNmsVersion() == NMSVersion.ONE_DOT_FOURTEEN || plugin.getNmsVersion() == NMSVersion.ONE_DOT_FIVETEEN || plugin.getNmsVersion() == NMSVersion.ONE_DOT_SIXTEEN || plugin.getNmsVersion() == NMSVersion.ONE_DOT_SIXTEEN_R2) ? Material.valueOf("GRAY_STAINED_GLASS_PANE") : Material.valueOf("STAINED_GLASS_PANE");
        final ItemStack emptyItem = new ItemStack(material, 1);
        final SpigotGuardMainInventory mainInventory = new SpigotGuardMainInventory(MessageHelper.colored(Settings.IMP.INVENTORIES.MAIN_INVENTORY.NAME), Settings.IMP.INVENTORIES.MAIN_INVENTORY.SIZE);
        final ItemStack reloadItem = new ItemBuilder(Material.matchMaterial(Settings.IMP.INVENTORIES.MAIN_INVENTORY.RELOAD_ITEM.MATERIAL)).withName(Settings.IMP.INVENTORIES.MAIN_INVENTORY.RELOAD_ITEM.NAME).withLore(Settings.IMP.INVENTORIES.MAIN_INVENTORY.RELOAD_ITEM.LORE).build();
        mainInventory.addItem(Settings.IMP.INVENTORIES.MAIN_INVENTORY.RELOAD_ITEM.SLOT, reloadItem);
        mainInventory.addItemAction(Settings.IMP.INVENTORIES.MAIN_INVENTORY.RELOAD_ITEM.SLOT, player -> {
            player.closeInventory();
            Settings.IMP.reload(new File(SpigotGuardPlugin.getInstance().getDataFolder(), "settings.yml"));
            player.sendMessage(new MessageBuilder("&cSpigotGuard &8> &7Reloaded settings.yml! Remember that config.yml is not reloaded! You need to restart server to reload config.yml").coloured().toString());
            return;
        });
        final ItemStack recentDetectionsItem = new ItemBuilder(Material.matchMaterial(Settings.IMP.INVENTORIES.MAIN_INVENTORY.RECENT_DETECTIONS_ITEM.MATERIAL)).withName(Settings.IMP.INVENTORIES.MAIN_INVENTORY.RECENT_DETECTIONS_ITEM.NAME).withLore(Settings.IMP.INVENTORIES.MAIN_INVENTORY.RECENT_DETECTIONS_ITEM.LORE).build();
        mainInventory.addItem(Settings.IMP.INVENTORIES.MAIN_INVENTORY.RECENT_DETECTIONS_ITEM.SLOT, recentDetectionsItem);
        final DateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        final Inventory inventory;
        final List<ExploitDetails> allCrashAttempts;
        final Comparator<ExploitDetails> valueComparator;
        final User user2;
        final DateFormat dateFormat;
        final List<String> itemLore;
        final Material headMaterial;
        final ItemStack item;
        final Inventory inventory2;
        ItemStack item2;
        int i;
        final ItemStack itemStack;
        mainInventory.addItemAction(Settings.IMP.INVENTORIES.MAIN_INVENTORY.RECENT_DETECTIONS_ITEM.SLOT, player -> {
            player.closeInventory();
            inventory = Bukkit.createInventory((InventoryHolder)null, Settings.IMP.INVENTORIES.RECENT_DETECTIONS_INVENTORY.SIZE, MessageHelper.colored(Settings.IMP.INVENTORIES.RECENT_DETECTIONS_INVENTORY.NAME));
            allCrashAttempts = new ArrayList<ExploitDetails>();
            plugin.getUserManager().getUsers().forEach(user -> allCrashAttempts.addAll(user.getAttempts()));
            valueComparator = ((o1, o2) -> new Date(o2.getTime()).compareTo(new Date(o1.getTime())));
            allCrashAttempts.sort(valueComparator);
            allCrashAttempts.forEach(attempt -> {
                user2 = SpigotGuardPlugin.getInstance().getUserManager().findById(attempt.getUserId());
                itemLore = Settings.IMP.INVENTORIES.RECENT_DETECTIONS_INVENTORY.RECENT_DETECTION_ITEM.LORE.stream().map(lore -> new MessageBuilder(lore).withField("{PACKET}", attempt.getPacket()).withField("{DETAILS}", attempt.getDetails()).withField("{PLAYER-LAST-SEEN}", dateFormat.format(new Date(user2.getLastJoin()))).withField("{TIME}", dateFormat.format(new Date(attempt.getTime()))).withField("{HOW-TO-FIX-FALSE-POSITIVE}", attempt.getHowToFixFalseDetect()).toString()).collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
                headMaterial = ((plugin.getNmsVersion() == NMSVersion.ONE_DOT_FIVETEEN || plugin.getNmsVersion() == NMSVersion.ONE_DOT_SIXTEEN || plugin.getNmsVersion() == NMSVersion.ONE_DOT_SIXTEEN_R2) ? Material.valueOf("PLAYER_HEAD") : Material.valueOf("SKULL_ITEM"));
                item = new ItemBuilder(headMaterial, 1, (short)3).withName(Settings.IMP.INVENTORIES.RECENT_DETECTIONS_INVENTORY.RECENT_DETECTION_ITEM.NAME.replace("{PLAYER-NAME}", user2.getName())).withLore(itemLore).build();
                inventory2.addItem(new ItemStack[] { item });
                return;
            });
            if (allCrashAttempts.size() == 0 && Settings.IMP.INVENTORIES.RECENT_DETECTIONS_INVENTORY.SIZE > 26) {
                item2 = new ItemBuilder(Material.BEDROCK).withName(Settings.IMP.INVENTORIES.RECENT_DETECTIONS_INVENTORY.NO_DETECTIONS_ITEM.NAME).withLore(Settings.IMP.INVENTORIES.RECENT_DETECTIONS_INVENTORY.NO_DETECTIONS_ITEM.LORE).build();
                inventory.setItem(13, item2);
            }
            player.openInventory(inventory);
            for (i = 0; i < inventory.getSize(); ++i) {
                if (inventory.getItem(i) == null) {
                    inventory.setItem(i, itemStack);
                }
            }
            return;
        });
        final Map<Integer, ItemStack> items = mainInventory.getItems();
        for (int j = 0; j < mainInventory.getSize(); ++j) {
            if (!items.containsKey(j)) {
                items.put(j, emptyItem);
            }
        }
        inventoryAPI.addInventory(mainInventory);
        plugin.setManagementInventory(mainInventory);
        inventoryAPI.register((Plugin)plugin);
    }
}
