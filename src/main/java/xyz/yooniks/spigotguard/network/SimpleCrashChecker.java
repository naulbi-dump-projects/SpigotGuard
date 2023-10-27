package xyz.yooniks.spigotguard.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.yooniks.spigotguard.SpigotGuardPlugin;
import xyz.yooniks.spigotguard.config.Settings;
import xyz.yooniks.spigotguard.config.Settings.PACKET_DECODER;
import xyz.yooniks.spigotguard.event.ExploitDetails;
import xyz.yooniks.spigotguard.event.ExploitDetectedEvent;
import xyz.yooniks.spigotguard.helper.MessageBuilder;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;

public final class SimpleCrashChecker {
  private static void lambda$checkCrash$0(Player var0, ExploitDetails var1) {
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(var0, var1));
  }

  private static void lambda$checkCrash$3(Player var0, ExploitDetails var1) {
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(var0, var1));
  }

  public static boolean checkCrash(Object var0, ByteBuf var1, PACKET_DECODER var2, int var3, PacketInjector var4, Player var5, ChannelHandlerContext var6) {
    ExploitDetails var7;
    if (var0.getClass().getSimpleName().equals("PacketPlayInWindowClick") && var3 > var2.MAX_WINDOW_SIZE && var2.MAX_WINDOW_SIZE != -1) {
      var7 = new ExploitDetails(var4.getPacketDecoder().getUser(), var0.getClass().getSimpleName(), "Packet size too large (" + var3 + " > " + var2.MAX_WINDOW_SIZE + ")", false, false, "Increase packet-decoder.max-window-size or set it to -1 to disable it");
      if (var1.refCnt() > 1) {
        var1.clear();
      }

      if (Settings.IMP.KICK.TYPE == 0) {
        var6.close().addListener(SimpleCrashChecker::lambda$checkCrash$1);
      } else {
        Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), SimpleCrashChecker::lambda$checkCrash$2);
      }

      SpigotGuardLogger.log(Level.INFO, SpigotGuardPlugin.getInstance().getSpigotGuardClassLoaded().getSerializable() + "[Adv. decoding] Player {0} tried to crash the server, packet: {1}, details: {2}", new Object[]{var5.getName(), var0.getClass().getSimpleName(), var7.getDetails()});
      return true;
    } else if (var0.getClass().getSimpleName().equals("PacketPlayInBlockPlace") && var3 > var2.MAX_PLACE_SIZE && var2.MAX_PLACE_SIZE != -1) {
      var7 = new ExploitDetails(var4.getPacketDecoder().getUser(), var0.getClass().getSimpleName(), "Packet size too large (" + var3 + " > " + var2.MAX_PLACE_SIZE + ")", false, false, "Increase packet-decoder.max-place-size or set it to -1 to disable it");
      if (var1.refCnt() > 0) {
        var1.clear();
      }

      if (Settings.IMP.KICK.TYPE == 0) {
        var6.close().addListener(SimpleCrashChecker::lambda$checkCrash$4);
      } else {
        Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), SimpleCrashChecker::lambda$checkCrash$5);
      }

      SpigotGuardLogger.log(Level.INFO, SpigotGuardPlugin.getInstance().getSpigotGuardClassLoaded().getSerializable() + "[Adv. decoding] Player {0} tried to crash the server, packet: {1}, details: {2}", new Object[]{var5.getName(), var0.getClass().getSimpleName(), var7.getDetails()});
      return true;
    } else if (var0.getClass().getSimpleName().equals("PacketPlayInSetCreativeSlot") && var3 > var2.MAX_CREATIVE_SIZE && var2.MAX_CREATIVE_SIZE != -1) {
      var7 = new ExploitDetails(var4.getPacketDecoder().getUser(), var0.getClass().getSimpleName(), "Packet size too large (" + var3 + " > " + var2.MAX_CREATIVE_SIZE + ")", false, false, "Increase packet-decoder.max-creative-size or set it to -1 to disable it");
      if (var1.refCnt() > 0) {
        var1.clear();
      }

      if (Settings.IMP.KICK.TYPE == 0) {
        var6.close().addListener(SimpleCrashChecker::lambda$checkCrash$7);
      } else {
        Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), SimpleCrashChecker::lambda$checkCrash$8);
      }

      SpigotGuardLogger.log(Level.INFO, SpigotGuardPlugin.getInstance().getSpigotGuardClassLoaded().getSerializable() + "[Adv. decoding] Player {0} tried to crash the server, packet: {1}, details: {2}", new Object[]{var5.getName(), var0.getClass().getSimpleName(), var7.getDetails()});
      return true;
    } else if ((var0.getClass().getSimpleName().equals("PacketPlayInCustomPayload") || var0.getClass().getSimpleName().equals("PacketPlayInBEdit")) && var3 > var2.MAX_PAYLOAD_SIZE && var2.MAX_PAYLOAD_SIZE != -1) {
      var7 = new ExploitDetails(var4.getPacketDecoder().getUser(), var0.getClass().getSimpleName(), "Packet size too large (" + var3 + " > " + var2.MAX_PAYLOAD_SIZE + ")", false, false, "Increase packet-decoder.max-payload-size or set it to -1 to disable it");
      if (var1.refCnt() > 0) {
        var1.clear();
      }

      if (Settings.IMP.KICK.TYPE == 0) {
        var6.close().addListener(SimpleCrashChecker::lambda$checkCrash$10);
      } else {
        Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), SimpleCrashChecker::lambda$checkCrash$11);
      }

      SpigotGuardLogger.log(Level.INFO, SpigotGuardPlugin.getInstance().getSpigotGuardClassLoaded().getSerializable() + "[Adv. decoding] Player {0} tried to crash the server, packet: {1}, details: {2}", new Object[]{var5.getName(), var0.getClass().getSimpleName(), var7.getDetails()});
      return true;
    } else {
      return false;
    }
  }

  private static void lambda$checkCrash$10(Player var0, ExploitDetails var1, Future var2) throws Exception {
    Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), SimpleCrashChecker::lambda$checkCrash$9);
  }

  private static void lambda$checkCrash$9(Player var0, ExploitDetails var1) {
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(var0, var1));
  }

  private static void lambda$checkCrash$8(PacketInjector var0, Player var1, ExploitDetails var2) {
    var0.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(var1, var2));
  }

  private SimpleCrashChecker() {
  }

  private static void lambda$checkCrash$1(Player var0, ExploitDetails var1, Future var2) throws Exception {
    Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), SimpleCrashChecker::lambda$checkCrash$0);
  }

  private static void lambda$checkCrash$2(PacketInjector var0, Player var1, ExploitDetails var2) {
    var0.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(var1, var2));
  }

  private static void lambda$checkCrash$11(PacketInjector var0, Player var1, ExploitDetails var2) {
    var0.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(var1, var2));
  }

  private static void lambda$checkCrash$4(Player var0, ExploitDetails var1, Future var2) throws Exception {
    Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), SimpleCrashChecker::lambda$checkCrash$3);
  }

  private static void lambda$checkCrash$5(PacketInjector var0, Player var1, ExploitDetails var2) {
    var0.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(var1, var2));
  }

  private static void lambda$checkCrash$7(Player var0, ExploitDetails var1, Future var2) throws Exception {
    Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), SimpleCrashChecker::lambda$checkCrash$6);
  }

  private static void lambda$checkCrash$6(Player var0, ExploitDetails var1) {
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(var0, var1));
  }
}
