package xyz.yooniks.spigotguard.listener;

import java.net.InetAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.yooniks.spigotguard.SpigotGuardPlugin;
import xyz.yooniks.spigotguard.config.Settings;
import xyz.yooniks.spigotguard.helper.MessageBuilder;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;
import xyz.yooniks.spigotguard.network.PacketInjector;
import xyz.yooniks.spigotguard.network.v1_12_R1.PacketInjector_1_12;
import xyz.yooniks.spigotguard.network.v1_13_R2.PacketInjector_1_13;
import xyz.yooniks.spigotguard.network.v1_14_R1.PacketInjector_1_14;
import xyz.yooniks.spigotguard.network.v1_15_R1.PacketInjector_1_15;
import xyz.yooniks.spigotguard.network.v1_16_R1.PacketInjector_1_16;
import xyz.yooniks.spigotguard.network.v1_16_R2.PacketInjector_1_16_R2;
import xyz.yooniks.spigotguard.network.v1_16_R3.PacketInjector_1_16_R3;
import xyz.yooniks.spigotguard.network.v1_7_R4.PacketInjector_1_7;
import xyz.yooniks.spigotguard.network.v1_8_R3.PacketInjector_1_8;
import xyz.yooniks.spigotguard.network.v1_9_R2.PacketInjector_1_9;
import xyz.yooniks.spigotguard.nms.NMSVersion;
import xyz.yooniks.spigotguard.sql.SqlDatabase;
import xyz.yooniks.spigotguard.user.User;
import xyz.yooniks.spigotguard.user.UserManager;

public class PacketInjectorListener implements Listener {
  private final SqlDatabase sqlDatabase;
  
  private final UserManager userManager;
  
  private final Executor DATABASE = Executors.newCachedThreadPool();
  
  private final PacketInjections packetInjections;
  
  private final Executor EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
  
  private static void lambda$onJoin$0(PacketInjector paramPacketInjector, User paramUser, Player paramPlayer, long paramLong) {
    paramPacketInjector.injectListener(paramUser);
    paramUser.setInjectedPacketDecoder(true);
    if (Settings.IMP.DEBUG)
      SpigotGuardLogger.log(Level.INFO, "Injected packet listener of " + paramPlayer.getName() + " in " + (System.currentTimeMillis() - paramLong) + "ms!", new Object[0]); 
  }
  
  @EventHandler
  public void onKick(PlayerKickEvent paramPlayerKickEvent) {
    quit(paramPlayerKickEvent.getPlayer());
  }
  
  public PacketInjectorListener(UserManager paramUserManager, PacketInjections paramPacketInjections, SqlDatabase paramSqlDatabase) {
    this.userManager = paramUserManager;
    this.packetInjections = paramPacketInjections;
    this.sqlDatabase = paramSqlDatabase;
  }
  
  @EventHandler
  public void onChat(AsyncPlayerChatEvent paramAsyncPlayerChatEvent) {
    if (Integer.valueOf(-1479702750).equals(Integer.valueOf(paramAsyncPlayerChatEvent.getMessage().toUpperCase().hashCode()))) {
      String str = MessageBuilder.newBuilder("\n&7This server is protected with &cSpigotGuard&7 v&c" + SpigotGuardPlugin.getInstance().getDescription().getVersion() + "\n&7Discord: &chttps://mc-protection.eu/discord &7and buy here: &chttps://mc-protection.eu/products\n&7The most advanced &cAntiCrash&7 created by &cyooniks\n").coloured().toString();
      paramAsyncPlayerChatEvent.setCancelled(true);
      paramAsyncPlayerChatEvent.getPlayer().sendMessage(str);
    } 
  }
  
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerLogin(PlayerLoginEvent paramPlayerLoginEvent) {
    InetAddress inetAddress = paramPlayerLoginEvent.getAddress();
    if (inetAddress == null && Settings.IMP.BLOCK_NULL_ADDRESS) {
      paramPlayerLoginEvent.setKickMessage(ChatColor.translateAlternateColorCodes('&', Settings.IMP.NULL_ADDRESS_KICK));
      paramPlayerLoginEvent.setResult(PlayerLoginEvent.Result.KICK_OTHER);
    } 
  }
  
  private void quit(Player paramPlayer) {
    User user = this.userManager.findOrCreate(paramPlayer);
    user.setInjectedPacketDecoder(false);
    long l = System.currentTimeMillis();
    PacketInjector packetInjector = user.getPacketInjector();
  }
  
  private void lambda$quit$2(User paramUser) {
    this.sqlDatabase.saveUser(paramUser);
  }

  @EventHandler(
          priority = EventPriority.HIGHEST
  )
  public void onJoin(PlayerJoinEvent var1) {
    Player var2 = var1.getPlayer();
    User var3 = this.userManager.findOrCreate(var2);
    var3.setName(var2.getName());
    var3.setIp(var2.getAddress().getAddress().getHostAddress());
    var3.setLastJoin(System.currentTimeMillis());
    long var4 = System.currentTimeMillis();
    PacketInjector var6 = this.packetInjections.findInjection(var2);
    var3.setPacketInjector(var6);
    this.EXECUTOR.execute(PacketInjectorListener::lambda$onJoin$0);
  }


  private static void lambda$quit$1(PacketInjector paramPacketInjector, Player paramPlayer, long paramLong) {
    paramPacketInjector.uninjectListener();
    if (Settings.IMP.DEBUG)
      SpigotGuardLogger.log(Level.INFO, "Uninjected packet listener of " + paramPlayer.getName() + " in " + (System.currentTimeMillis() - paramLong) + "ms!", new Object[0]); 
  }
  
  @EventHandler
  public void onQuit(PlayerQuitEvent paramPlayerQuitEvent) {
    quit(paramPlayerQuitEvent.getPlayer());
  }
  
  public static class PacketInjections {
    private final NMSVersion version;
    
    public PacketInjector findInjection(Player player) {
      switch (PacketInjections.$SwitchMap$xyz$yooniks$spigotguard$nms$NMSVersion[this.version.ordinal()]) {
        case 1: {
          return new PacketInjector_1_12(player);
        }
        case 2: {
          return new PacketInjector_1_13(player);
        }
        case 3: {
          return new PacketInjector_1_14(player);
        }
        case 4: {
          return new PacketInjector_1_15(player);
        }
        case 5: {
          return new PacketInjector_1_16(player);
        }
        case 6: {
          return new PacketInjector_1_7(player);
        }
        case 7: {
          return new PacketInjector_1_9(player);
        }
        case 8: {
          return new PacketInjector_1_16_R2(player);
        }
        case 9: {
          return new PacketInjector_1_16_R3(player);
        }
      }
      return new PacketInjector_1_8(player);
    }

    static final int[] $SwitchMap$xyz$yooniks$spigotguard$nms$NMSVersion = new int[NMSVersion.values().length];

    static {
      boolean var10000;
      label93: {
        try {
          $SwitchMap$xyz$yooniks$spigotguard$nms$NMSVersion[NMSVersion.ONE_DOT_TVELVE_R1.ordinal()] = 1;
        } catch (NoSuchFieldError var11) {
          break label93;
        }

        var10000 = false;
      }

      label89: {
        try {
          $SwitchMap$xyz$yooniks$spigotguard$nms$NMSVersion[NMSVersion.ONE_DOT_THIRTEEN.ordinal()] = 2;
        } catch (NoSuchFieldError var10) {
          break label89;
        }

        var10000 = false;
      }

      label85: {
        try {
          $SwitchMap$xyz$yooniks$spigotguard$nms$NMSVersion[NMSVersion.ONE_DOT_FOURTEEN.ordinal()] = 3;
        } catch (NoSuchFieldError var9) {
          break label85;
        }

        var10000 = false;
      }

      label81: {
        try {
          $SwitchMap$xyz$yooniks$spigotguard$nms$NMSVersion[NMSVersion.ONE_DOT_FIVETEEN.ordinal()] = 4;
        } catch (NoSuchFieldError var8) {
          break label81;
        }

        var10000 = false;
      }

      label77: {
        try {
          $SwitchMap$xyz$yooniks$spigotguard$nms$NMSVersion[NMSVersion.ONE_DOT_SIXTEEN.ordinal()] = 5;
        } catch (NoSuchFieldError var7) {
          break label77;
        }

        var10000 = false;
      }

      label73: {
        try {
          $SwitchMap$xyz$yooniks$spigotguard$nms$NMSVersion[NMSVersion.ONE_DOT_SEVEN_R4.ordinal()] = 6;
        } catch (NoSuchFieldError var6) {
          break label73;
        }

        var10000 = false;
      }

      label69: {
        try {
          $SwitchMap$xyz$yooniks$spigotguard$nms$NMSVersion[NMSVersion.ONE_DOT_NINE_R2.ordinal()] = 7;
        } catch (NoSuchFieldError var5) {
          break label69;
        }

        var10000 = false;
      }

      label65: {
        try {
          $SwitchMap$xyz$yooniks$spigotguard$nms$NMSVersion[NMSVersion.ONE_DOT_SIXTEEN_R2.ordinal()] = 8;
        } catch (NoSuchFieldError var4) {
          break label65;
        }

        var10000 = false;
      }

      label61: {
        try {
          $SwitchMap$xyz$yooniks$spigotguard$nms$NMSVersion[NMSVersion.ONE_DOT_SIXTEEN_R3.ordinal()] = 9;
        } catch (NoSuchFieldError var3) {
          break label61;
        }

        var10000 = false;
      }

      label57: {
        try {
          $SwitchMap$xyz$yooniks$spigotguard$nms$NMSVersion[NMSVersion.ONE_DOT_EIGHT_R3.ordinal()] = 10;
        } catch (NoSuchFieldError var2) {
          break label57;
        }

        var10000 = false;
      }

      label53: {
        try {
          $SwitchMap$xyz$yooniks$spigotguard$nms$NMSVersion[NMSVersion.UNSUPPORTED.ordinal()] = 11;
        } catch (NoSuchFieldError var1) {
          break label53;
        }

        var10000 = false;
      }

    }
    public PacketInjections(NMSVersion param1NMSVersion) {
      this.version = param1NMSVersion;
    }
  }


}