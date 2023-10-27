package xyz.yooniks.spigotguard.network.v1_15_R1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.PacketDataSerializer;
import net.minecraft.server.v1_15_R1.PacketPlayInCustomPayload;
import net.minecraft.server.v1_15_R1.PacketPlayInFlying;
import net.minecraft.server.v1_15_R1.PacketPlayInHeldItemSlot;
import net.minecraft.server.v1_15_R1.PacketPlayInSetCreativeSlot;
import net.minecraft.server.v1_15_R1.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_15_R1.PacketPlayInUseItem;
import net.minecraft.server.v1_15_R1.PacketPlayInVehicleMove;
import net.minecraft.server.v1_15_R1.PacketPlayInWindowClick;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import xyz.yooniks.spigotguard.SpigotGuardPlugin;
import xyz.yooniks.spigotguard.config.Settings;
import xyz.yooniks.spigotguard.event.ExploitDetails;
import xyz.yooniks.spigotguard.event.ExploitDetectedEvent;
import xyz.yooniks.spigotguard.helper.MessageBuilder;
import xyz.yooniks.spigotguard.limitation.LimitablePacket;
import xyz.yooniks.spigotguard.limitation.PacketLimitation;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;
import xyz.yooniks.spigotguard.network.PacketDecoder;
import xyz.yooniks.spigotguard.network.PacketInjector;
import xyz.yooniks.spigotguard.user.User;

@Sharable
public class PacketDecoder_1_15 extends PacketDecoder {
  private static final Pattern URL_MATCHER = Pattern.compile("url");
  
  private static final PacketLimitation LIMITATION = SpigotGuardPlugin.getInstance().getPacketLimitation();
  
  private boolean disconnected = false;
  
  private final User user;
  
  private long lastBookplace = -1L;
  
  protected void decode(ChannelHandlerContext paramChannelHandlerContext, Object paramObject, List<Object> paramList) {}
  
  public ExploitDetails failure(Object paramObject) {
    Player player = this.injector.getPlayer();
    String str = paramObject.getClass().getSimpleName();
    if (paramObject.getClass().getSimpleName().equals("PacketPlayInWindowClick")) {
      if (Settings.IMP.DEBUG)
        SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", new Object[] { str, player.getName() }); 
      PacketPlayInWindowClick packetPlayInWindowClick = (PacketPlayInWindowClick)paramObject;
      try {
        ItemStack itemStack = ((PacketPlayInWindowClick)paramObject).f();
        if (itemStack == null)
          try {
            Field field = PacketPlayInWindowClick.class.getDeclaredField("item");
            field.setAccessible(true);
            itemStack = (ItemStack)field.get(paramObject);
            false;
          } catch (Exception exception) {
            exception.printStackTrace();
          }  
        if (itemStack != null) {
          ExploitDetails exploitDetails = checkNbtTags(str, itemStack);
          if (exploitDetails != null)
            return exploitDetails; 
        } 
        int i = packetPlayInWindowClick.b();
        if (i > 127 || i < -999)
          return new ExploitDetails(this.user, str, "Invalid slot " + i, false, true, "can't be fixed, it's surely exploit"); 
        InventoryView inventoryView = player.getOpenInventory();
        if (inventoryView != null) {
          Inventory inventory1 = inventoryView.getTopInventory();
          Inventory inventory2 = inventoryView.getBottomInventory();
          byte b = 127;
          if (inventory1.getType() == InventoryType.CRAFTING && inventory2.getType() == InventoryType.PLAYER)
            b += 4; 
          if (i >= b && LIMITATION != null) {
            LimitablePacket limitablePacket1 = hasReachedLimit("PacketPlayInWindowClick_InvalidSlot");
            if (limitablePacket1 != null)
              return new ExploitDetails(this.user, str, "invalid slot, slot: " + i + "", limitablePacket1.isCancelOnly(), false, "increase packet limit of PacketPlayInWindowClick_InvalidSlot in config.yml"); 
          } 
          if (itemStack != null) {
            ItemStack itemStack1 = CraftItemStack.asBukkitCopy(itemStack);
            if ((itemStack1.getType() == Material.CHEST || itemStack1.getType() == Material.HOPPER) && itemStack1.hasItemMeta() && (itemStack1.getItemMeta().toString().getBytes()).length > 262144)
              return new ExploitDetails(this.user, str, "too big chest data", false, false); 
          } 
        } 
        false;
      } catch (Exception exception) {}
      false;
    } else if (paramObject.getClass().getSimpleName().equals("PacketPlayInBlockPlace")) {
      ItemStack itemStack = player.getInventory().getItemInHand();
      if (itemStack != null && itemStack.getType().name().contains("BOOK"))
        this.lastBookplace = System.currentTimeMillis(); 
      false;
    } else if (paramObject.getClass().getSimpleName().equals("PacketPlayInSetCreativeSlot")) {
      if (Settings.IMP.DEBUG)
        SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", new Object[] { str, player.getName() }); 
      if (player.getGameMode() != GameMode.CREATIVE && !Settings.IMP.ALLOW_CREATIVE_INVENTORY_CLICK_WITHOUT_GAMEMODE)
        return new ExploitDetails(this.user, str, "clicking in creative inventory without gamemode 1", false, true, "set \"allow-creative-inventory-click-without-gamemode\" to true in settings.yml"); 
      ItemStack itemStack = null;
      try {
        Field field = PacketPlayInSetCreativeSlot.class.getDeclaredField("b");
        field.setAccessible(true);
        itemStack = (ItemStack)field.get(paramObject);
        false;
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      if (itemStack != null)
        ExploitDetails exploitDetails = checkNbtTags(str, itemStack); 
      false;
    } else if (paramObject.getClass().getSimpleName().equals("PacketPlayInCustomPayload")) {
      PacketPlayInCustomPayload packetPlayInCustomPayload = (PacketPlayInCustomPayload)paramObject;
      String str1 = packetPlayInCustomPayload.tag.toString();
      PacketDataSerializer packetDataSerializer = packetPlayInCustomPayload.data;
      if (packetDataSerializer.capacity() > Settings.IMP.PAYLOAD_SETTINGS.MAX_CAPACITY)
        return new ExploitDetails(this.user, str, "invalid bytebuf capacity", false, true, "set \"payload-settings.max-capacity\" to higher value in settings.yml"); 
      if (str1.equals("MC|BEdit") || str1.equals("MC|BSign") || str1.equals("minecraft:bedit") || str1.equals("minecraft:bsign")) {
        if (Settings.IMP.DEBUG)
          SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", new Object[] { str, player.getName() }); 
        try {
          ItemStack itemStack = packetDataSerializer.m();
          if (itemStack != null) {
            if (System.currentTimeMillis() - this.lastBookplace > 60000L && Settings.IMP.PAYLOAD_SETTINGS.BLOCK_BOOK_SIGNING_WHEN_NO_BOOK_PLACED)
              return new ExploitDetails(this.user, str, "book sign, but no book used", false, false, "set \"payload-settings.block-book-signing-when-no-book-placed\" in settings.yml to false"); 
            if (Settings.IMP.PAYLOAD_SETTINGS.FAIL_WHEN_NOT_HOLDING_BOOK && !player.getInventory().contains(Material.valueOf("BOOK_AND_QUILL")) && !player.getInventory().contains(Material.WRITTEN_BOOK))
              return new ExploitDetails(this.user, str, "book interact, but no book exists in player's inventory", false, true, "set \"payload-settings.fail-when-not-holding-book\" in settings.yml to false"); 
            ExploitDetails exploitDetails = checkNbtTags(str, itemStack);
            if (exploitDetails != null)
              return exploitDetails; 
          } 
          false;
        } catch (Exception exception) {
          return new ExploitDetails(this.user, str, "exception: " + exception.getMessage(), false, false);
        } 
      } else if (str1.equals("REGISTER") || Integer.valueOf(1321107516).equals(Integer.valueOf(str1.toUpperCase().hashCode())) || str1.toLowerCase().contains("fml")) {
        ByteBuf byteBuf = null;
        try {
          byteBuf = packetDataSerializer.copy();
          if ((byteBuf.toString(StandardCharsets.UTF_8).split("\000")).length > 124)
            return new ExploitDetails(this.user, str, "too many channels", false, false); 
        } catch (Exception exception) {
        
        } finally {
          if (byteBuf != null);
        } 
        false;
      } else if (str1.equals("MC|ItemName") && player.getInventory() != null && player.getOpenInventory().getType() != InventoryType.ANVIL && Settings.IMP.BLOCK_ITEMNAME_WHEN_NO_ANVIL) {
        return new ExploitDetails(this.user, str, "trying to use MC|ItemName but no anvil exists", false, false, "set \"block-itemname-when-no-anvil\" in settings.yml to false");
      } 
      false;
    } else if (paramObject instanceof PacketPlayInFlying.PacketPlayInPosition) {
      PacketPlayInFlying.PacketPlayInPosition packetPlayInPosition = (PacketPlayInFlying.PacketPlayInPosition)paramObject;
      double d1 = packetPlayInPosition.a(0.0F);
      double d2 = packetPlayInPosition.b(0.0F);
      double d3 = packetPlayInPosition.c(0.0D);
      if ((d1 >= Double.MAX_VALUE || d2 > Double.MAX_VALUE || d3 >= Double.MAX_VALUE) && !Settings.IMP.POSITION_CHECKS.ALLOW_INVALID_MOVEMENT_V2)
        return new ExploitDetails(this.user, str, "Double.MAX_VALUE position", false, true, "set \"position-checks.allow-invalid-movement-v2\" to true"); 
      if (d1 >= 2.147483647E9D || d2 > 2.147483647E9D || (d3 >= 2.147483647E9D && !Settings.IMP.POSITION_CHECKS.ALLOW_INVALID_MOVEMENT_V1))
        return new ExploitDetails(this.user, str, "Integer.MAX_VALUE position", false, true, "set \"position-checks.allow-invalid-movement-v1\" to true"); 
      if (Settings.IMP.POSITION_CHECKS.CHECK_CHUNK_CRASHER) {
        double d4 = Math.max(player.getLocation().getX(), d1) - Math.min(player.getLocation().getX(), d1);
        if (d4 >= 10.0D && d4 % 1.0D == 0.0D) {
          SpigotGuardLogger.log(Level.INFO, "Received invalid location packet from: " + player.getName() + " (" + player.getLocation().getX() + "," + player.getLocation().getY() + "," + player.getLocation().getZ() + " ||| Packet: " + d1 + "," + d2 + "," + d3 + "), difference: " + d4, new Object[0]);
          LimitablePacket limitablePacket1 = hasReachedLimit("PacketPlayInPosition_Invalid_v1");
          if (limitablePacket1 != null)
            return new ExploitDetails(this.user, str, "Chunk crasher (v1, invalid x coords, diff: " + d4 + ")", false, false, "set position-checks.check-chunk-crasher to false in settings.yml or increase PacketPlayInPosition_Invalid_v1.limit in config.yml"); 
        } 
        double d5 = Math.max(player.getLocation().getZ(), d3) - Math.min(player.getLocation().getZ(), d3);
        if (d5 >= 10.0D && d5 % 1.0D == 0.0D) {
          SpigotGuardLogger.log(Level.INFO, "Received invalid location packet from: " + player.getName() + " (" + player.getLocation().getX() + "," + player.getLocation().getY() + "," + player.getLocation().getZ() + " ||| Packet: " + d1 + "," + d2 + "," + d3 + "), difference: " + d5, new Object[0]);
          LimitablePacket limitablePacket1 = hasReachedLimit("PacketPlayInPosition_Invalid_v1");
          if (limitablePacket1 != null)
            return new ExploitDetails(this.user, str, "Chunk crasher (v1, invalid z coords, diff: " + d5 + ")", false, false, "set position-checks.check-chunk-crasher to false in settings.yml or increase PacketPlayInPosition_Invalid_v1.limit in config.yml"); 
        } 
        if (Settings.IMP.POSITION_CHECKS.CHECK_FLY_CRASHER && d2 == player.getLocation().getY() + 0.1D) {
          double d = Math.max(player.getLocation().getY(), d2) - Math.min(player.getLocation().getY(), d2);
          SpigotGuardLogger.log(Level.INFO, "Received invalid Y location packet from: " + player.getName() + " (" + player.getLocation().getX() + "," + player.getLocation().getY() + "," + player.getLocation().getZ() + " ||| Packet: " + d1 + "," + d2 + "," + d3 + "), difference: " + d, new Object[0]);
          LimitablePacket limitablePacket1 = hasReachedLimit("PacketPlayInPosition_Invalid_v2");
          if (limitablePacket1 != null)
            return new ExploitDetails(this.user, str, "Fly crasher (v1, invalid y coords, diff: " + d + ")", false, false, "set position-checks.check-fly-crasher to false in settings.yml or increase PacketPlayInPosition_Invalid_v2.limit in config.yml"); 
        } 
      } 
      false;
    } else if (paramObject instanceof PacketPlayInFlying) {
      PacketPlayInFlying packetPlayInFlying = (PacketPlayInFlying)paramObject;
      double d1 = packetPlayInFlying.a(0.0D);
      double d2 = packetPlayInFlying.b(0.0D);
      double d3 = packetPlayInFlying.c(0.0D);
      if ((d1 >= Double.MAX_VALUE || d2 > Double.MAX_VALUE || d3 >= Double.MAX_VALUE) && !Settings.IMP.POSITION_CHECKS.ALLOW_INVALID_MOVEMENT_V2)
        return new ExploitDetails(this.user, str, "Double.MAX_VALUE position", false, true, "set \"position-checks.allow-invalid-movement-v2\" to true"); 
      if (d1 >= 2.147483647E9D || d2 > 2.147483647E9D || (d3 >= 2.147483647E9D && !Settings.IMP.POSITION_CHECKS.ALLOW_INVALID_MOVEMENT_V1))
        return new ExploitDetails(this.user, str, "Integer.MAX_VALUE position", false, true, "set \"position-checks.allow-invalid-movement-v1\" to true"); 
      float f1 = packetPlayInFlying.a(0.0F);
      float f2 = packetPlayInFlying.b(0.0F);
      if (f1 == Float.NEGATIVE_INFINITY || f2 == Float.NEGATIVE_INFINITY || f1 >= Float.MAX_VALUE || f2 >= Float.MAX_VALUE)
        return new ExploitDetails(this.user, str, "invalid float position", false, true); 
      if (Settings.IMP.POSITION_CHECKS.CHECK_YAW_CRASHER && (f1 < -30000.0F || f2 < -30000.0F || f1 > 30000.0F || f2 > 30000.0F || f1 == 9.223372E18D || f2 == 9.223372E18D || f1 == 9.223372E18F || f2 == 9.223372E18F))
        return new ExploitDetails(this.user, str, "Invalid yaw/pitch v3 (yaw: " + f1 + ", pitch: " + f2 + ")", false, false, "set position-checks.check-yaw-crasher to false in settings.yml report to staff on mc-protection.eu/discord"); 
      false;
    } else if (paramObject instanceof PacketPlayInHeldItemSlot) {
      int i = ((PacketPlayInHeldItemSlot)paramObject).b();
      if (i >= 36 || i < 0)
        return new ExploitDetails(this.user, str, "invalid held item slot", false, true); 
      false;
    } else if (paramObject.getClass().getSimpleName().equals("PacketPlayInSteerVehicle")) {
      PacketPlayInSteerVehicle packetPlayInSteerVehicle = (PacketPlayInSteerVehicle)paramObject;
      if (packetPlayInSteerVehicle.b() >= Float.MAX_VALUE || packetPlayInSteerVehicle.c() >= Float.MAX_VALUE)
        return new ExploitDetails(this.user, str, "invalid vehicle movement", false, true); 
      false;
    } else if (paramObject.getClass().getSimpleName().equals("PacketPlayInVehicleMove")) {
      PacketPlayInVehicleMove packetPlayInVehicleMove = (PacketPlayInVehicleMove)paramObject;
      if (packetPlayInVehicleMove.getX() >= Double.MAX_VALUE || packetPlayInVehicleMove.getY() >= Double.MAX_VALUE || packetPlayInVehicleMove.getZ() >= Double.MAX_VALUE)
        return new ExploitDetails(this.user, str, "invalid vehicle movement v2", false, true); 
      if (player.getLocation().distance(new Location(player.getWorld(), packetPlayInVehicleMove.getX(), packetPlayInVehicleMove.getY(), packetPlayInVehicleMove.getZ())) > 30.0D && !Settings.IMP.POSITION_CHECKS.ALLOW_INVALID_VEHICLE_MOVEMENT_V3)
        return new ExploitDetails(this.user, str, "invalid vehicle movement v3", false, true, "set position-checks.allow-invalid-vehicle-movement-v3 to true"); 
      if (player.getLocation().distance(new Location(player.getWorld(), packetPlayInVehicleMove.getX(), packetPlayInVehicleMove.getY(), packetPlayInVehicleMove.getZ())) > 3.0D && !Settings.IMP.POSITION_CHECKS.ALLOW_INVALID_VEHICLE_MOVEMENT_V4) {
        LimitablePacket limitablePacket1 = hasReachedLimit("PacketPlayInVehicleMove_Invalid_v4");
        if (limitablePacket1 != null)
          return new ExploitDetails(this.user, str, "invalid vehicle movement v4", false, true, "set position-checks.allow-invalid-vehicle-movement-v4 to true"); 
      } 
      false;
    } else if (paramObject.getClass().getSimpleName().equals("PacketPlayInUseItem")) {
      PacketPlayInUseItem packetPlayInUseItem = (PacketPlayInUseItem)paramObject;
      BlockPosition blockPosition = packetPlayInUseItem.c().getBlockPosition();
      int i = Settings.IMP.POSITION_CHECKS.MAX_DISTANCE_BETWEEN_PLAYER_AND_BLOCK_POSITION;
      if (i > 0 && (new Location(player.getWorld(), blockPosition.getX(), blockPosition.getY(), blockPosition.getZ())).distance(player.getLocation()) > i)
        return new ExploitDetails(this.user, str, "invalid UseItem blockPosition", false, true, "set position-checks.max-distance-between-player-and-block-position to -1"); 
    } 
    LimitablePacket limitablePacket = hasReachedLimit(str);
    return (limitablePacket != null) ? new ExploitDetails(this.user, str, "packet limit exceed", limitablePacket.isCancelOnly(), false) : null;
  }
  
  public PacketDecoder_1_15(PacketInjector paramPacketInjector, User paramUser) {
    super(paramPacketInjector);
    this.user = paramUser;
  }
  
  private void lambda$decode$0(ExploitDetails paramExploitDetails) {
    Bukkit.getPluginManager().callEvent((Event)new ExploitDetectedEvent((OfflinePlayer)this.injector.getPlayer(), paramExploitDetails));
  }
  
  private void lambda$decode$1(ExploitDetails paramExploitDetails, Future paramFuture) throws Exception {}
  
  public User getUser() {
    return this.user;
  }
  
  private LimitablePacket hasReachedLimit(String paramString) {}
  
  private void lambda$decode$2() {
    this.injector.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
  }
  
  private ExploitDetails checkNbtTags(String paramString, ItemStack paramItemStack) {}
}