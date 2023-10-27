package xyz.yooniks.spigotguard.listener;

import xyz.yooniks.spigotguard.sql.*;
import java.util.concurrent.*;
import xyz.yooniks.spigotguard.*;
import xyz.yooniks.spigotguard.helper.*;
import xyz.yooniks.spigotguard.config.*;
import java.util.logging.*;
import xyz.yooniks.spigotguard.logger.*;
import org.bukkit.entity.*;
import xyz.yooniks.spigotguard.user.*;
import xyz.yooniks.spigotguard.network.*;
import org.bukkit.event.*;
import org.bukkit.*;
import java.net.*;
import org.bukkit.event.player.*;
import xyz.yooniks.spigotguard.nms.*;
import xyz.yooniks.spigotguard.network.v1_12_R1.*;
import xyz.yooniks.spigotguard.network.v1_13_R2.*;
import xyz.yooniks.spigotguard.network.v1_14_R1.*;
import xyz.yooniks.spigotguard.network.v1_15_R1.*;
import xyz.yooniks.spigotguard.network.v1_16_R1.*;
import xyz.yooniks.spigotguard.network.v1_7_R4.*;
import xyz.yooniks.spigotguard.network.v1_9_R2.*;
import xyz.yooniks.spigotguard.network.v1_16_R2.*;
import xyz.yooniks.spigotguard.network.v1_8_R3.*;

public class PacketInjectorListener implements Listener
{
    private final UserManager userManager;
    private final PacketInjections packetInjections;
    private final SqlDatabase sqlDatabase;
    private final Executor EXECUTOR;
    private final Executor DATABASE;
    
    public PacketInjectorListener(final UserManager userManager, final PacketInjections packetInjections, final SqlDatabase sqlDatabase) {
        this.EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        this.DATABASE = Executors.newCachedThreadPool();
        this.userManager = userManager;
        this.packetInjections = packetInjections;
        this.sqlDatabase = sqlDatabase;
    }
    
    @EventHandler
    public void onChat(final AsyncPlayerChatEvent event) {
        if (event.getMessage().equalsIgnoreCase("#spigotguard")) {
            final String message = MessageBuilder.newBuilder("\n&7This server is protected with &cSpigotGuard&7 v&c" + SpigotGuardPlugin.getInstance().getDescription().getVersion() + "\n&7Discord: &chttps://mc-protection.eu/discord &7and buy here: &chttps://minemen.com/resources/175/\n&7The most advanced &cAntiCrash&7 created by &cyooniks\n").coloured().toString();
            event.setCancelled(true);
            event.getPlayer().sendMessage(message);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final User user = this.userManager.findOrCreate(player);
        user.setName(player.getName());
        user.setIp(player.getAddress().getAddress().getHostAddress());
        user.setLastJoin(System.currentTimeMillis());
        final long start = System.currentTimeMillis();
        final PacketInjector packetInjector = this.packetInjections.findInjection(player);
        user.setPacketInjector(packetInjector);
        SpigotGuardPlugin.getInstance().getSpigotGuardClassLoaded().getSerializable();
        packetInjector.injectListener(user);
        user.setInjectedPacketDecoder(true);
        if (Settings.IMP.DEBUG) {
            SpigotGuardLogger.log(Level.INFO, "Injected packet listener of {0} in {1}ms!", player.getName(), System.currentTimeMillis() - start);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        final InetAddress address = event.getAddress();
        if (address == null && Settings.IMP.BLOCK_NULL_ADDRESS) {
            event.setKickMessage(ChatColor.translateAlternateColorCodes('&', Settings.IMP.NULL_ADDRESS_KICK));
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        this.quit(event.getPlayer());
    }
    
    @EventHandler
    public void onKick(final PlayerKickEvent event) {
        this.quit(event.getPlayer());
    }
    
    private void quit(final Player player) {
        final User user = this.userManager.findOrCreate(player);
        user.setInjectedPacketDecoder(false);
        final long start = System.currentTimeMillis();
        final PacketInjector packetInjector = user.getPacketInjector();
        if (packetInjector == null) {
            return;
        }
        final PacketInjector packetInjector2;
        final long n;
        this.EXECUTOR.execute(() -> {
            SpigotGuardPlugin.getInstance().getSpigotGuardClassLoaded().getSerializable();
            packetInjector2.uninjectListener();
            if (Settings.IMP.DEBUG) {
                SpigotGuardLogger.log(Level.INFO, "Uninjected packet listener of {0} in {1}ms!", player.getName(), System.currentTimeMillis() - n);
            }
            return;
        });
        this.DATABASE.execute(() -> this.sqlDatabase.saveUser(user));
    }
    
    public static class PacketInjections
    {
        private final NMSVersion version;
        
        public PacketInjections(final NMSVersion version) {
            this.version = version;
        }
        
        public PacketInjector findInjection(final Player player) {
            switch (this.version) {
                case ONE_DOT_TVELVE_R1: {
                    return new PacketInjector_1_12(player);
                }
                case ONE_DOT_THIRTEEN: {
                    return new PacketInjector_1_13(player);
                }
                case ONE_DOT_FOURTEEN: {
                    return new PacketInjector_1_14(player);
                }
                case ONE_DOT_FIVETEEN: {
                    return new PacketInjector_1_15(player);
                }
                case ONE_DOT_SIXTEEN: {
                    return new PacketInjector_1_16(player);
                }
                case ONE_DOT_SEVEN_R4: {
                    return new PacketInjector_1_7(player);
                }
                case ONE_DOT_NINE_R2: {
                    return new PacketInjector_1_9(player);
                }
                case ONE_DOT_SIXTEEN_R2: {
                    return new PacketInjector_1_16_R2(player);
                }
                default: {
                    return new PacketInjector_1_8(player);
                }
            }
        }
    }
}
