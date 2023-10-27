package xyz.yooniks.spigotguard.listener;

import xyz.yooniks.spigotguard.notification.*;
import xyz.yooniks.spigotguard.config.*;
import xyz.yooniks.spigotguard.helper.*;
import xyz.yooniks.spigotguard.*;
import java.util.*;
import java.util.logging.*;
import xyz.yooniks.spigotguard.logger.*;
import org.bukkit.command.*;
import org.bukkit.plugin.*;
import xyz.yooniks.spigotguard.event.*;
import xyz.yooniks.spigotguard.user.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import xyz.upperlevel.spigot.book.*;
import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.entity.*;

public class NotificationListener implements Listener
{
    private final NotificationCache notificationCache;
    
    public NotificationListener(final NotificationCache notificationCache) {
        this.notificationCache = notificationCache;
    }
    
    @EventHandler
    public void onExploitDetected(final ExploitDetectedEvent event) {
        final ExploitDetails details = event.getExploitDetails();
        final String message = MessageBuilder.newBuilder(Settings.IMP.MESSAGES.NOTIFICATION_MESSAGE).withField("{PACKET}", details.getPacket()).withField("{DETAILS}", details.getDetails()).withField("{PLAYER}", event.getPlayer().getName()).prefix().stripped().coloured().toString();
        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(Settings.IMP.MESSAGES.NOTIFICATION_PERMISSION)).forEach(player -> player.sendMessage(message));
        final User user = SpigotGuardPlugin.getInstance().getUserManager().findById(event.getPlayer().getUniqueId());
        if (user != null) {
            user.addAttempts(Collections.singletonList(details));
        }
        if (Settings.IMP.HACKER_BOOK.ENABLED) {
            this.notificationCache.addCache(event.getPlayer().getUniqueId(), details);
        }
        if (details.isSurelyExploit() && !Settings.IMP.COMMANDS_WHEN_SURE.isEmpty()) {
            SpigotGuardLogger.log(Level.INFO, "Executing BAN commands from settings.yml, player {0} surely tried to crash server.", event.getPlayer().getName());
            Bukkit.getScheduler().runTask((Plugin)SpigotGuardPlugin.getInstance(), () -> Settings.IMP.COMMANDS_WHEN_SURE.forEach(cmd -> Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), MessageBuilder.newBuilder(cmd).withField("{PLAYER}", event.getPlayer().getName()).withField("{IP}", event.getPlayer().getPlayer().getAddress().getAddress().getHostAddress()).toString())));
        }
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        SpigotGuardPlugin.getInstance().getSpigotGuardClassLoaded().getSerializable();
        if (!Settings.IMP.HACKER_BOOK.ENABLED) {
            return;
        }
        final Player player = event.getPlayer();
        final ExploitDetails details = this.notificationCache.findCache(player.getUniqueId());
        if (details != null) {
            final Player p;
            final BaseComponent[][] pages;
            final BookUtil.PageBuilder pageBuilder;
            final BaseComponent component;
            final ExploitDetails exploitDetails;
            final Object o;
            final BookUtil.BookBuilder bookBuilder2;
            final BookUtil.BookBuilder bookBuilder;
            Bukkit.getScheduler().runTaskLater((Plugin)SpigotGuardPlugin.getInstance(), () -> {
                BookUtil.writtenBook().author("yooniks").title("SpigotGuard");
                pages = new BaseComponent[][] { { (BaseComponent)new TextComponent(MessageBuilder.newBuilder(Settings.IMP.HACKER_BOOK.CONTENT).stripped().withField("{PLAYER}", p.getName()).coloured().toString()) }, null };
                pageBuilder = new BookUtil.PageBuilder();
                new TextComponent(MessageBuilder.newBuilder("&7Packet: &c" + exploitDetails.getPacket()).coloured().toString());
                pages[o] = pageBuilder.add(component).newLine().add((BaseComponent)new TextComponent(MessageBuilder.newBuilder("&7Blocked easily by: &cSpigotGuard").coloured().toString())).newLine().add(BookUtil.TextBuilder.of(MessageBuilder.newBuilder("&7Open &bSpigotGuard&7 website&r").coloured().toString()).color(ChatColor.GOLD).style(ChatColor.BOLD, ChatColor.ITALIC).onClick(BookUtil.ClickAction.openUrl("https://minemen.com/resources/175/")).onHover(BookUtil.HoverAction.showText("Open SpigotGuard website!")).build()).add(BookUtil.TextBuilder.of(MessageBuilder.newBuilder("&7Open &9discord&7 server&r").coloured().toString()).color(ChatColor.GOLD).style(ChatColor.BOLD, ChatColor.ITALIC).onClick(BookUtil.ClickAction.openUrl("https://mc-protection.eu/discord")).onHover(BookUtil.HoverAction.showText("Open discord server!")).build()).newLine().newLine().add((BaseComponent)new TextComponent(MessageBuilder.newBuilder(Settings.IMP.HACKER_BOOK.NEXT_PAGE).coloured().toString())).build();
                bookBuilder = bookBuilder2.pages(pages);
                BookUtil.openPlayer(p, bookBuilder.build());
                this.notificationCache.removeCache(p.getUniqueId());
            }, 15L);
        }
    }
}
