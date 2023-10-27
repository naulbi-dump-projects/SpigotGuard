package xyz.yooniks.spigotguard.listener;

import java.util.Collections;
import java.util.logging.Level;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.yooniks.spigotguard.SpigotGuardPlugin;
import xyz.yooniks.spigotguard.config.Settings;
import xyz.yooniks.spigotguard.event.ExploitDetails;
import xyz.yooniks.spigotguard.event.ExploitDetectedEvent;
import xyz.yooniks.spigotguard.helper.BookUtil;
import xyz.yooniks.spigotguard.helper.MessageBuilder;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;
import xyz.yooniks.spigotguard.notification.NotificationCache;
import xyz.yooniks.spigotguard.user.User;

public class NotificationListener implements Listener {
  private final NotificationCache notificationCache;
  
  private static void lambda$onExploitDetected$2(ExploitDetectedEvent paramExploitDetectedEvent, String paramString) {}
  
  private static void lambda$onExploitDetected$1(String paramString, Player paramPlayer) {
    paramPlayer.sendMessage(paramString);
  }
  
  private static boolean lambda$onExploitDetected$0(Player paramPlayer) {
    return paramPlayer.hasPermission(Settings.IMP.MESSAGES.NOTIFICATION_PERMISSION);
  }
  
  private void lambda$onJoin$4(Player paramPlayer, ExploitDetails paramExploitDetails) {
    BookUtil.BookBuilder bookBuilder = BookUtil.writtenBook().author("yooniks").title("SpigotGuard").pages(new BaseComponent[][] { { (BaseComponent)new TextComponent(MessageBuilder.newBuilder(Settings.IMP.HACKER_BOOK.CONTENT).stripped().withField("{PLAYER}", paramPlayer.getName()).coloured().toString()) }, (new BookUtil.PageBuilder()).add((BaseComponent)new TextComponent(MessageBuilder.newBuilder("&7Packet: &c" + paramExploitDetails.getPacket()).coloured().toString())).newLine().add((BaseComponent)new TextComponent(MessageBuilder.newBuilder("&7Blocked by: &cSpigotGuard").coloured().toString())).newLine().add(BookUtil.TextBuilder.of(MessageBuilder.newBuilder("&7Open &bSpigotGuard&7 website&r").coloured().toString()).color(ChatColor.GOLD).style(new ChatColor[] { ChatColor.BOLD, ChatColor.ITALIC }).onClick(BookUtil.ClickAction.openUrl("https://mc-protection.eu/")).onHover(BookUtil.HoverAction.showText("Open SpigotGuard website!")).build()).add(BookUtil.TextBuilder.of(MessageBuilder.newBuilder("&7Open &9discord&7 server&r").coloured().toString()).color(ChatColor.GOLD).style(new ChatColor[] { ChatColor.BOLD, ChatColor.ITALIC }).onClick(BookUtil.ClickAction.openUrl("https://mc-protection.eu/discord")).onHover(BookUtil.HoverAction.showText("Open discord server!")).build()).newLine().newLine().add((BaseComponent)new TextComponent(MessageBuilder.newBuilder(Settings.IMP.HACKER_BOOK.NEXT_PAGE).coloured().toString())).build() });
    BookUtil.openPlayer(paramPlayer, bookBuilder.build());
    this.notificationCache.removeCache(paramPlayer.getUniqueId());
  }
  
  @EventHandler
  public void onJoin(PlayerJoinEvent paramPlayerJoinEvent) {
    if (Settings.IMP.HACKER_BOOK.ENABLED);
  }
  
  private static void lambda$onExploitDetected$3(ExploitDetectedEvent paramExploitDetectedEvent) {
    Settings.IMP.COMMANDS_WHEN_SURE.forEach(paramExploitDetectedEvent::lambda$onExploitDetected$2);
  }
  
  public NotificationListener(NotificationCache paramNotificationCache) {
    this.notificationCache = paramNotificationCache;
  }
  
  @EventHandler
  public void onExploitDetected(ExploitDetectedEvent paramExploitDetectedEvent) {
    ExploitDetails exploitDetails = paramExploitDetectedEvent.getExploitDetails();
    String str = MessageBuilder.newBuilder(Settings.IMP.MESSAGES.NOTIFICATION_MESSAGE).withField("{PACKET}", exploitDetails.getPacket()).withField("{DETAILS}", exploitDetails.getDetails()).withField("{PLAYER}", paramExploitDetectedEvent.getPlayer().getName()).prefix().stripped().coloured().toString();
    Bukkit.getOnlinePlayers().stream().filter(NotificationListener::lambda$onExploitDetected$0).forEach(str::lambda$onExploitDetected$1);
    User user = SpigotGuardPlugin.getInstance().getUserManager().findById(paramExploitDetectedEvent.getPlayer().getUniqueId());
    if (user != null)
      user.addAttempts(Collections.singletonList(exploitDetails)); 
    if (Settings.IMP.HACKER_BOOK.ENABLED)
      this.notificationCache.addCache(paramExploitDetectedEvent.getPlayer().getUniqueId(), exploitDetails); 
    if (exploitDetails.isSurelyExploit() && !Settings.IMP.COMMANDS_WHEN_SURE.isEmpty())
      SpigotGuardLogger.log(Level.INFO, "Executing BAN commands from settings.yml, player {0} surely tried to crash server.", new Object[] { paramExploitDetectedEvent.getPlayer().getName() }); 
  }
}