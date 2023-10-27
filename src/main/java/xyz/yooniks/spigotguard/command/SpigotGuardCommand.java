package xyz.yooniks.spigotguard.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import xyz.yooniks.spigotguard.SpigotGuardPlugin;
import xyz.yooniks.spigotguard.api.inventory.ItemBuilder;
import xyz.yooniks.spigotguard.api.inventory.MessageHelper;
import xyz.yooniks.spigotguard.api.inventory.PhasmatosInventory;
import xyz.yooniks.spigotguard.config.Settings;
import xyz.yooniks.spigotguard.event.ExploitDetails;
import xyz.yooniks.spigotguard.helper.MessageBuilder;
import xyz.yooniks.spigotguard.nms.NMSVersion;
import xyz.yooniks.spigotguard.user.User;

public class SpigotGuardCommand implements CommandExecutor {
  private final PhasmatosInventory inventory;
  private final String message = MessageBuilder.newBuilder("\n&7This server is protected with &cSpigotGuard&7 &8(&c" + SpigotGuardPlugin.getInstance().getDescription().getVersion() + "&8)\n&7More about SpigotGuard: &6https://mc-protection.eu\n&7Buy here: &6https://mc-protection.eu&7The most advanced &6AntiCrash&7 created by &6yooniks\n\n&7Our discord: &6https://mc-protection.eu/discord\n").coloured().toString();

  private static String lambda$onCommand$1(ExploitDetails var0, DateFormat var1, User var2, String var3) {
    return (new MessageBuilder(var3)).withField("{PACKET}", var0.getPacket()).withField("{DETAILS}", var0.getDetails()).withField("{LAST-SEEN}", var1.format(new Date(var2.getLastJoin()))).withField("{TIME}", var1.format(new Date(var0.getTime()))).withField("{IP}", var2.getIp()).withField("{HOW-TO-FIX-FALSE-POSITIVE}", var0.getHowToFixFalseDetect()).toString();
  }

  private static void lambda$onCommand$2(DateFormat var0, User var1, Inventory var2, ExploitDetails var3) {
    List var4 = (List)Settings.IMP.INVENTORIES.PLAYER_INFO_INVENTORY.CRASH_ATTEMPT_ITEM.LORE.stream().map(SpigotGuardCommand::lambda$onCommand$1).collect(Collectors.toList());
    Material var5 = SpigotGuardPlugin.getInstance().getNmsVersion() != NMSVersion.ONE_DOT_FIVETEEN && SpigotGuardPlugin.getInstance().getNmsVersion() != NMSVersion.ONE_DOT_SIXTEEN && SpigotGuardPlugin.getInstance().getNmsVersion() != NMSVersion.ONE_DOT_SIXTEEN_R2 && SpigotGuardPlugin.getInstance().getNmsVersion() != NMSVersion.ONE_DOT_SIXTEEN_R3 ? Material.valueOf("SKULL_ITEM") : Material.valueOf("PLAYER_HEAD");
    ItemStack var6 = (new ItemBuilder(var5, 1, (short)3)).withName(Settings.IMP.INVENTORIES.PLAYER_INFO_INVENTORY.CRASH_ATTEMPT_ITEM.NAME.replace("{TIME}", var0.format(new Date(var3.getTime())))).withLore(var4).build();
    var2.addItem(new ItemStack[]{var6});
  }

  public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
    if (!var1.hasPermission("spigotguard.command")) {
      var1.sendMessage(this.message);
      return true;
    } else {
      if (var1 instanceof Player) {
        if (var4.length > 0) {
          User var5 = SpigotGuardPlugin.getInstance().getUserManager().findByName(var4[0]);
          if (var5 == null) {
            var1.sendMessage((new MessageBuilder("&6[&eSpigotGuard&6] &cUser &e" + var4[0] + "&c has never been here")).coloured().toString());
            return true;
          }

          if (var5.getAttempts().size() == 0) {
            var1.sendMessage((new MessageBuilder("&6[&eSpigotGuard&6] &cUser &e" + var4[0] + "&c has never tried to crash the server")).coloured().toString());
            return true;
          }

          Material var6 = SpigotGuardPlugin.getInstance().getNmsVersion() != NMSVersion.ONE_DOT_THIRTEEN && SpigotGuardPlugin.getInstance().getNmsVersion() != NMSVersion.ONE_DOT_FOURTEEN && SpigotGuardPlugin.getInstance().getNmsVersion() != NMSVersion.ONE_DOT_FIVETEEN && SpigotGuardPlugin.getInstance().getNmsVersion() != NMSVersion.ONE_DOT_SIXTEEN && SpigotGuardPlugin.getInstance().getNmsVersion() != NMSVersion.ONE_DOT_SIXTEEN_R2 && SpigotGuardPlugin.getInstance().getNmsVersion() != NMSVersion.ONE_DOT_SIXTEEN_R3 ? Material.valueOf("STAINED_GLASS_PANE") : Material.valueOf("GRAY_STAINED_GLASS_PANE");
          ItemStack var7 = new ItemStack(var6, 1, (short)5);
          SimpleDateFormat var8 = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
          Inventory var9 = Bukkit.createInventory((InventoryHolder)null, Settings.IMP.INVENTORIES.PLAYER_INFO_INVENTORY.SIZE, MessageHelper.colored(Settings.IMP.INVENTORIES.PLAYER_INFO_INVENTORY.NAME.replace("{PLAYER}", var5.getName())));
          List var10 = var5.getAttempts();
          Comparator var11 = SpigotGuardCommand::lambda$onCommand$0;
          var10.sort(var11);
          var10.forEach(SpigotGuardCommand::lambda$onCommand$2);

          boolean var10000;
          for(int var12 = 0; var12 < var9.getSize(); var10000 = false) {
            if (var9.getItem(var12) == null) {
              var9.setItem(var12, var7);
            }

            ++var12;
          }

          ((Player)var1).openInventory(var9);
          return true;
        }

        this.inventory.open((Player)var1);
      }

      return true;
    }
  }

  private static int lambda$onCommand$0(ExploitDetails var0, ExploitDetails var1) {
    return (new Date(var1.getTime())).compareTo(new Date(var0.getTime()));
  }

  public SpigotGuardCommand(PhasmatosInventory var1) {
    this.inventory = var1;
  }
}
