package xyz.yooniks.spigotguard.command;

import xyz.yooniks.spigotguard.*;
import xyz.yooniks.spigotguard.helper.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import xyz.yooniks.spigotguard.nms.*;
import xyz.yooniks.spigotguard.config.*;
import org.bukkit.*;
import xyz.yooniks.spigotguard.event.*;
import java.util.*;
import java.util.stream.*;
import xyz.yooniks.spigotguard.api.inventory.*;
import xyz.yooniks.spigotguard.user.*;
import java.text.*;
import org.bukkit.inventory.*;

public class SpigotGuardCommand implements CommandExecutor
{
    private final String message;
    private final PhasmatosInventory inventory;
    
    public SpigotGuardCommand(final PhasmatosInventory inventory) {
        this.message = MessageBuilder.newBuilder("\n&7This server is protected with &cSpigotGuard&7 v&c" + SpigotGuardPlugin.getInstance().getDescription().getVersion() + "\n&7More about SpigotGuard: &chttps://discord.gg/AmvcUfn\n&7Buy here: &chttps://minemen.com/resources/175/\n&7The most advanced &cAntiCrash&7 created by &cyooniks\n\n&cYou have no permission to view management inventory.\n").coloured().toString();
        this.inventory = inventory;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!sender.hasPermission("spigotguard.command")) {
            sender.sendMessage(this.message);
            return true;
        }
        if (sender instanceof Player) {
            if (args.length > 0) {
                final User target = SpigotGuardPlugin.getInstance().getUserManager().findByName(args[0]);
                if (target == null) {
                    sender.sendMessage(new MessageBuilder("&6[&eSpigotGuard&6] &cUser &e" + args[0] + "&c has never been here").coloured().toString());
                    return true;
                }
                if (target.getAttempts().size() == 0) {
                    sender.sendMessage(new MessageBuilder("&6[&eSpigotGuard&6] &cUser &e" + args[0] + "&c has never tried to crash the server").coloured().toString());
                    return true;
                }
                final Material material = (SpigotGuardPlugin.getInstance().getNmsVersion() == NMSVersion.ONE_DOT_THIRTEEN || SpigotGuardPlugin.getInstance().getNmsVersion() == NMSVersion.ONE_DOT_FOURTEEN || SpigotGuardPlugin.getInstance().getNmsVersion() == NMSVersion.ONE_DOT_FIVETEEN || SpigotGuardPlugin.getInstance().getNmsVersion() == NMSVersion.ONE_DOT_SIXTEEN || SpigotGuardPlugin.getInstance().getNmsVersion() == NMSVersion.ONE_DOT_SIXTEEN_R2) ? Material.valueOf("GRAY_STAINED_GLASS_PANE") : Material.valueOf("STAINED_GLASS_PANE");
                final ItemStack emptyItem = new ItemStack(material, 1, (short)5);
                final DateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, Settings.IMP.INVENTORIES.PLAYER_INFO_INVENTORY.SIZE, MessageHelper.colored(Settings.IMP.INVENTORIES.PLAYER_INFO_INVENTORY.NAME.replace("{PLAYER}", target.getName())));
                final List<ExploitDetails> allCrashAttempts = target.getAttempts();
                final Comparator<ExploitDetails> valueComparator = (o1, o2) -> new Date(o2.getTime()).compareTo(new Date(o1.getTime()));
                allCrashAttempts.sort(valueComparator);
                final DateFormat dateFormat;
                final User user;
                final List<String> itemLore;
                final Material headMaterial;
                final DateFormat dateFormat2;
                final ItemStack item;
                final Inventory inventory2;
                allCrashAttempts.forEach(attempt -> {
                    itemLore = Settings.IMP.INVENTORIES.PLAYER_INFO_INVENTORY.CRASH_ATTEMPT_ITEM.LORE.stream().map(lore -> new MessageBuilder(lore).withField("{PACKET}", attempt.getPacket()).withField("{DETAILS}", attempt.getDetails()).withField("{LAST-SEEN}", dateFormat.format(new Date(user.getLastJoin()))).withField("{TIME}", dateFormat.format(new Date(attempt.getTime()))).withField("{IP}", user.getIp()).withField("{HOW-TO-FIX-FALSE-POSITIVE}", attempt.getHowToFixFalseDetect()).toString()).collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
                    headMaterial = ((SpigotGuardPlugin.getInstance().getNmsVersion() == NMSVersion.ONE_DOT_FIVETEEN || SpigotGuardPlugin.getInstance().getNmsVersion() == NMSVersion.ONE_DOT_SIXTEEN || SpigotGuardPlugin.getInstance().getNmsVersion() == NMSVersion.ONE_DOT_SIXTEEN_R2) ? Material.valueOf("PLAYER_HEAD") : Material.valueOf("SKULL_ITEM"));
                    item = new ItemBuilder(headMaterial, 1, (short)3).withName(Settings.IMP.INVENTORIES.PLAYER_INFO_INVENTORY.CRASH_ATTEMPT_ITEM.NAME.replace("{TIME}", dateFormat2.format(new Date(attempt.getTime())))).withLore(itemLore).build();
                    inventory2.addItem(new ItemStack[] { item });
                    return;
                });
                for (int i = 0; i < inventory.getSize(); ++i) {
                    if (inventory.getItem(i) == null) {
                        inventory.setItem(i, emptyItem);
                    }
                }
                ((Player)sender).openInventory(inventory);
                return true;
            }
            else {
                this.inventory.open((Player)sender);
            }
        }
        return true;
    }
}
