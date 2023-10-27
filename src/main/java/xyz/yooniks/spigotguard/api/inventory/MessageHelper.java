package xyz.yooniks.spigotguard.api.inventory;

import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;

public final class MessageHelper {
  private MessageHelper() {
  }

  public static String colored(String var0) {
    return ChatColor.translateAlternateColorCodes('&', var0);
  }

  public static List colored(List var0) {
    return (List)var0.stream().map(MessageHelper::colored).collect(Collectors.toList());
  }
}
