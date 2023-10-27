package xyz.yooniks.spigotguard.network.v1_8_R3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import net.minecraft.server.v1_8_R3.EnumProtocol;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInCustomPayload;
import net.minecraft.server.v1_8_R3.PacketPlayInSetCreativeSlot;
import net.minecraft.server.v1_8_R3.PacketPlayInWindowClick;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import xyz.yooniks.spigotguard.SpigotGuardPlugin;
import xyz.yooniks.spigotguard.config.Settings;
import xyz.yooniks.spigotguard.config.Settings.PACKET_DECODER;
import xyz.yooniks.spigotguard.event.ExploitDetails;
import xyz.yooniks.spigotguard.event.ExploitDetectedEvent;
import xyz.yooniks.spigotguard.helper.MessageBuilder;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;
import xyz.yooniks.spigotguard.network.PacketInjector;
import xyz.yooniks.spigotguard.network.SimpleCrashChecker;

public class PacketDecompress_1_8 extends ByteToMessageDecoder {
  private int errors = 0;
  private boolean disconnected = false;
  private final PacketInjector injector;

  private void lambda$decode$15() {
    this.injector.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
  }

  private static void lambda$decode$8(Player var0, ExploitDetails var1, Future var2) throws Exception {
    Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), PacketDecompress_1_8::lambda$decode$7);
  }

  private void lambda$decode$6(Player var1, ExploitDetails var2) {
    this.injector.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(var1, var2));
  }

  private void lambda$decode$14(ExploitDetails var1, Future var2) throws Exception {
    Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), this::lambda$decode$13);
  }

  private void lambda$decode$9(Player var1, ExploitDetails var2) {
    this.injector.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(var1, var2));
  }

  private void lambda$decode$2(Player var1) {
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(var1, new ExploitDetails(SpigotGuardPlugin.getInstance().getUserManager().findOrCreate(this.injector.getPlayer()), "undefined (bf check)", "too small packet size (capacity < 0)", false, false)));
  }

  private void lambda$decode$11(ExploitDetails var1, Future var2) throws Exception {
    Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), this::lambda$decode$10);
  }

  private void lambda$decode$18() {
    this.injector.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
  }

  private static void lambda$decode$4(Player var0, ExploitDetails var1) {
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(var0, var1));
  }

  private void lambda$decode$1(Player var1, int var2, Future var3) throws Exception {
    SpigotGuardLogger.log(Level.INFO, "{0} sent too large packet (size: {1}). If it is false positive please increase the limit of packet-decoder.max-main-size or set it to -1.", new Object[]{var1.getName(), var2});
    Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), this::lambda$decode$0);
  }

  private void lambda$decode$0(Player var1) {
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(var1, new ExploitDetails(SpigotGuardPlugin.getInstance().getUserManager().findOrCreate(this.injector.getPlayer()), "undefined (bf check)", "too big packet size", false, false, "set \"packet-decoder.max-main-size\" in settings.yml to higher value or set it to -1 to disable it")));
  }

  private void lambda$decode$13(ExploitDetails var1) {
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(this.injector.getPlayer(), var1));
  }

  private void lambda$decode$10(ExploitDetails var1) {
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(this.injector.getPlayer(), var1));
  }

  private void lambda$decode$12() {
    this.injector.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
  }

  private static void lambda$decode$5(Player var0, ExploitDetails var1, Future var2) throws Exception {
    Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), PacketDecompress_1_8::lambda$decode$4);
  }

  private void lambda$decode$16(ExploitDetails var1) {
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(this.injector.getPlayer(), var1));
  }

  private void lambda$decode$17(ExploitDetails var1, Future var2) throws Exception {
    Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), this::lambda$decode$16);
  }

  public PacketDecompress_1_8(PacketInjector var1) {
    this.injector = var1;
  }

  private void lambda$decode$3(Player var1) {
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(var1, new ExploitDetails(SpigotGuardPlugin.getInstance().getUserManager().findOrCreate(this.injector.getPlayer()), "undefined (bf check)", "too small packet size (refCnt < 1)", false, false)));
  }

  private static void lambda$decode$7(Player var0, ExploitDetails var1) {
    Bukkit.getPluginManager().callEvent(new ExploitDetectedEvent(var0, var1));
  }

  protected void decode(ChannelHandlerContext var1, ByteBuf var2, List var3) {
    boolean var10000;
    try {
      label283: {
        Channel var4 = var1.channel();
        AttributeKey var5 = NetworkManager.c;
        if (var2 instanceof EmptyByteBuf) {
          var3.add(var2.readBytes(var2.readableBytes()));
          return;
        }

        if (this.disconnected) {
          if (var2.refCnt() > 0) {
            var2.clear();
          }

          return;
        }

        Player var6 = this.injector.getPlayer();
        if (var6 != null && var6.isOnline()) {
          PACKET_DECODER var7 = Settings.IMP.PACKET_DECODER;
          int var8 = var2.capacity();
          int var9 = var7.MAX_MAIN_SIZE;
          if (var9 != -1 && var8 > var9) {
            var1.close().addListener(this::lambda$decode$1);
            if (var2.refCnt() > 0) {
              var2.clear();
            }

            return;
          }

          if (var8 < 0) {
            Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), this::lambda$decode$2);
            return;
          }

          if (var2.refCnt() < 1) {
            Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), this::lambda$decode$3);
            return;
          }

          ByteBuf var10 = var2.copy();
          PacketDataSerializer var11 = new PacketDataSerializer(var10);
          int var12 = var11.e();

          Packet var13;
          try {
            var13 = ((EnumProtocol)var4.attr(var5).get()).a(EnumProtocolDirection.SERVERBOUND, var12);
          } catch (Throwable var23) {
            var3.add(var2.readBytes(var2.readableBytes()));
            return;
          }

          var10000 = false;
          if (var13 == null) {
            if (var10.refCnt() > 0) {
              var10.clear();
            }

            var3.add(var2.readBytes(var2.readableBytes()));
            return;
          }

          try {
            var13.a(var11);
          } catch (Throwable var22) {
            var3.add(var2.readBytes(var2.readableBytes()));
            return;
          }

          var10000 = false;
          if (!this.injector.getPacketDecoder().getUser().isInjectedPacketDecoder() && (var13 instanceof PacketPlayInWindowClick || var13 instanceof PacketPlayInSetCreativeSlot || var13 instanceof PacketPlayInBlockPlace || var13 instanceof PacketPlayInCustomPayload)) {
            if (var13 instanceof PacketPlayInCustomPayload) {
              String var14 = ((PacketPlayInCustomPayload)var13).a();
              if (Integer.valueOf(-296262810).equals(var14.toUpperCase().hashCode()) || Integer.valueOf(-295840999).equals(var14.toUpperCase().hashCode())) {
                var2.skipBytes(var2.readableBytes());
                if (var10.refCnt() > 0) {
                  var10.clear();
                }

                SpigotGuardLogger.log(Level.INFO, "Player {0} has tried to send invalid packet (PROBABLY), but we did not inject packet_decoder yet!", new Object[0]);
                return;
              }
            }

            if (var8 > 100) {
              SpigotGuardLogger.log(Level.INFO, "Player {0} has tried to send invalid packet (PROBABLY), but we did not inject packet_decoder yet!", new Object[0]);
              var2.skipBytes(var2.readableBytes());
              if (var10.refCnt() > 0) {
                var10.clear();
              }

              return;
            }
          }

          ItemStack var25 = null;
          int var19;
          ExploitDetails var28;
          if (var13 instanceof PacketPlayInCustomPayload) {
            String var15 = ((PacketPlayInCustomPayload)var13).a();
            if (var15 != null && (Integer.valueOf(-296262810).equals(var15.toUpperCase().hashCode()) || Integer.valueOf(-295840999).equals(var15.toUpperCase().hashCode()) || Integer.valueOf(-295953498).equals(var15.toUpperCase().hashCode()))) {
              boolean var16 = false;
              org.bukkit.inventory.ItemStack[] var17 = var6.getInventory().getContents();
              int var18 = var17.length;
              var19 = 0;

              while(true) {
                if (var19 >= var18) {
                  if (!var16) {
                    var28 = new ExploitDetails(this.injector.getPacketDecoder().getUser(), var13.getClass().getSimpleName(), "Payload without book?! (capacity: " + var8 + ")", false, false, "Not fixable, it is not possible to make this bug, it must be an exploit!");
                    if (var10.refCnt() > 0) {
                      var10.clear();
                    }

                    if (Settings.IMP.KICK.TYPE == 0) {
                      var1.close().addListener(PacketDecompress_1_8::lambda$decode$5);
                    } else {
                      Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), this::lambda$decode$6);
                    }

                    SpigotGuardLogger.log(Level.INFO, SpigotGuardPlugin.getInstance().getSpigotGuardClassLoaded().getSerializable() + "[PacketDecoder] Player {0} tried to crash the server, packet: {1}, details: {2}", new Object[]{var6.getName(), var13.getClass().getSimpleName(), var28.getDetails()});
                    this.disconnected = true;
                    if (var10.refCnt() > 0) {
                      var10.clear();
                    }

                    var2.skipBytes(var2.readableBytes());
                    return;
                  }
                  break;
                }

                org.bukkit.inventory.ItemStack var20 = var17[var19];
                if (var20.getType() == Material.BOOK_AND_QUILL || var20.getType() == Material.WRITTEN_BOOK) {
                  var16 = true;
                }

                ++var19;
                var10000 = false;
              }
            }

            if (var15 != null && ((PacketPlayInCustomPayload)var13).b() != null) {
              var25 = ((PacketPlayInCustomPayload)var13).b().i();
            }

            var10000 = false;
          } else if (var13 instanceof PacketPlayInWindowClick) {
            var25 = ((PacketPlayInWindowClick)var13).e();
            var10000 = false;
          } else if (var13 instanceof PacketPlayInSetCreativeSlot) {
            var25 = ((PacketPlayInSetCreativeSlot)var13).getItemStack();
            var10000 = false;
          } else if (var13 instanceof PacketPlayInBlockPlace) {
            var25 = ((PacketPlayInBlockPlace)var13).getItemStack();
          }

          if (var25 != null) {
            CraftItemStack var26 = CraftItemStack.asCraftMirror(var25);
            NBTTagCompound var27 = var25.hasTag() ? var25.getTag() : null;
            if (var27 != null && (var26.getType() == Material.WRITTEN_BOOK || var26.getType() == Material.BOOK_AND_QUILL)) {
              if (var27.hasKey(":[{extra:[{") || ((NBTTagCompound)Objects.requireNonNull(var27)).toString().contains(":[{extra:[{")) {
                var27.remove("pages");
                var27.remove("author");
                var27.remove("title");
                var28 = new ExploitDetails(this.injector.getPacketDecoder().getUser(), var13.getClass().getSimpleName(), "Bad WRITTEN_BOOK nbt data, extra:[{ data ?! (capacity: " + var8 + ")", false, false, "Not fixable, it is not possible to make this bug, it must be an exploit!");
                if (var10.refCnt() > 0) {
                  var10.clear();
                }

                if (Settings.IMP.KICK.TYPE == 0) {
                  var1.close().addListener(PacketDecompress_1_8::lambda$decode$8);
                } else {
                  Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), this::lambda$decode$9);
                }

                SpigotGuardLogger.log(Level.INFO, SpigotGuardPlugin.getInstance().getSpigotGuardClassLoaded().getSerializable() + "[PacketDecoder] Player {0} tried to crash the server, packet: {1}, details: {2}", new Object[]{var6.getName(), var13.getClass().getSimpleName(), var28.getDetails()});
                var2.skipBytes(var2.readableBytes());
                return;
              }

              Settings var29 = Settings.IMP;
              if (var27.hasKey("pages")) {
                if (var29.BOOK.BAN_BOOKS) {
                  var27.remove("pages");
                  var27.remove("author");
                  var27.remove("title");
                  var25.setTag(new NBTTagCompound());
                  ExploitDetails var31 = new ExploitDetails(this.injector.getPacketDecoder().getUser(), var13.getClass().getSimpleName(), "using book while it is banned", false, true, "set \"book.ban-books\" in settings.yml to false");
                  if (Settings.IMP.KICK.TYPE == 0) {
                    var1.close().addListener(this::lambda$decode$11);
                  } else {
                    Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), this::lambda$decode$12);
                  }

                  SpigotGuardLogger.log(Level.INFO, "[1] Player {0} tried to crash the server, packet: {1}, details: {2}", new Object[]{this.injector.getPlayer().getName(), var31.getClass().getSimpleName(), var31.getDetails()});
                  return;
                }

                NBTTagList var30 = var27.getList("pages", 8);
                if (var30.size() > var29.BOOK.MAX_PAGES) {
                  var27.remove("pages");
                  var27.remove("author");
                  var27.remove("title");
                  var25.setTag(new NBTTagCompound());
                  ExploitDetails var32 = new ExploitDetails(this.injector.getPacketDecoder().getUser(), var13.getClass().getSimpleName(), "too many pages (" + var30.size() + ")", false, true, "set \"book.max-pages\" in settings.yml to higher value");
                  if (Settings.IMP.KICK.TYPE == 0) {
                    var1.close().addListener(this::lambda$decode$14);
                  } else {
                    Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), this::lambda$decode$15);
                  }

                  SpigotGuardLogger.log(Level.INFO, "[1] Player {0} tried to crash the server, packet: {1}, details: {2}", new Object[]{this.injector.getPlayer().getName(), var32.getClass().getSimpleName(), var32.getDetails()});
                  return;
                }

                for(var19 = 0; var19 < var30.size(); var10000 = false) {
                  String var33 = var30.getString(var19);
                  if (var33.length() > var29.BOOK.MAX_PAGE_SIZE) {
                    var27.remove("pages");
                    var27.remove("author");
                    var27.remove("title");
                    var25.setTag(new NBTTagCompound());
                    ExploitDetails var21 = new ExploitDetails(this.injector.getPacketDecoder().getUser(), var13.getClass().getSimpleName(), "too large page content (" + var33.length() + ")", false, false, "increase value of \"book.max-page-size\" (bigger than: " + var33.length() + ") in settings.yml");
                    if (Settings.IMP.KICK.TYPE == 0) {
                      var1.close().addListener(this::lambda$decode$17);
                    } else {
                      Bukkit.getScheduler().runTask(SpigotGuardPlugin.getInstance(), this::lambda$decode$18);
                    }

                    SpigotGuardLogger.log(Level.INFO, "[1] Player {0} tried to crash the server, packet: {1}, details: {2}", new Object[]{this.injector.getPlayer().getName(), var21.getClass().getSimpleName(), var21.getDetails()});
                    return;
                  }

                  ++var19;
                }
              }
            }
          }

          if (!SimpleCrashChecker.checkCrash(var13, var10, var7, var8, this.injector, var6, var1)) {
            break label283;
          }

          this.disconnected = true;
          if (var10.refCnt() > 0) {
            var10.clear();
          }

          var2.skipBytes(var2.readableBytes());
          return;
        }

        if (var2.refCnt() > 0) {
          var2.clear();
        }

        return;
      }
    } catch (Throwable var24) {
      var3.add(var2.readBytes(var2.readableBytes()));
      return;
    }

    var10000 = false;
    var3.add(var2.readBytes(var2.readableBytes()));
  }
}
