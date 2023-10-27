package xyz.yooniks.spigotguard.network.v1_7_R4;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;
import net.minecraft.server.v1_7_R4.Item;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.PacketDataSerializer;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockPlace;
import net.minecraft.server.v1_7_R4.PacketPlayInCustomPayload;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayInHeldItemSlot;
import net.minecraft.server.v1_7_R4.PacketPlayInPosition;
import net.minecraft.server.v1_7_R4.PacketPlayInSetCreativeSlot;
import net.minecraft.server.v1_7_R4.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_7_R4.PacketPlayInWindowClick;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import net.minecraft.util.io.netty.buffer.Unpooled;
import net.minecraft.util.io.netty.channel.ChannelHandler.Sharable;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.util.io.netty.util.concurrent.Future;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
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
import xyz.yooniks.spigotguard.network.PacketInjector;
import xyz.yooniks.spigotguard.user.User;

@Sharable
public class PacketDecoder_1_7 extends MessageToMessageDecoder<Object> {
  private final User user;
  
  private static final PacketLimitation LIMITATION;
  
  private final PacketInjector injector;
  
  private static final String BLOCKED = "wveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5";
  
  private boolean disconnected = false;
  
  private static final Pattern URL_MATCHER = Pattern.compile("url");
  
  private long lastBookplace = -1L;
  
  static {
    LIMITATION = SpigotGuardPlugin.getInstance().getPacketLimitation();
  }
  
  public User getUser() {
    return this.user;
  }
  
  private void lambda$decode$2() {
    this.injector.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
  }
  
  private LimitablePacket hasReachedLimit(String paramString) {}
  
  protected void decode(ChannelHandlerContext paramChannelHandlerContext, Object paramObject, List<Object> paramList) {}
  
  private void lambda$decode$0(ExploitDetails paramExploitDetails) {
    Bukkit.getPluginManager().callEvent((Event)new ExploitDetectedEvent((OfflinePlayer)this.injector.getPlayer(), paramExploitDetails));
  }
  
  private ExploitDetails checkNbtTags(String paramString, ItemStack paramItemStack) {}
  
  public ExploitDetails failure(Object paramObject) {
    Player player = this.injector.getPlayer();
    String str = paramObject.getClass().getSimpleName();
    if (paramObject.getClass().getSimpleName().equals("PacketPlayInWindowClick")) {
      if (Settings.IMP.DEBUG)
        SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", new Object[] { str, player.getName() }); 
      PacketPlayInWindowClick packetPlayInWindowClick = (PacketPlayInWindowClick)paramObject;
      try {
        ItemStack itemStack = ((PacketPlayInWindowClick)paramObject).g();
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
        InventoryView inventoryView = player.getOpenInventory();
        if (inventoryView != null) {
          Inventory inventory1 = inventoryView.getTopInventory();
          Inventory inventory2 = inventoryView.getBottomInventory();
          int i = packetPlayInWindowClick.slot;
          int j = inventoryView.countSlots();
          if (inventory1.getType() == InventoryType.CRAFTING && inventory2.getType() == InventoryType.PLAYER)
            j += 4; 
          if (i >= j && LIMITATION != null) {
            LimitablePacket limitablePacket1 = hasReachedLimit("PacketPlayInWindowClick_InvalidSlot");
            if (limitablePacket1 != null)
              return new ExploitDetails(this.user, str, "invalid slot, slot: " + i + "", limitablePacket1.isCancelOnly(), false, "increase packet limit of PacketPlayInWindowClick_InvalidSlot in config.yml"); 
          } 
          if (itemStack != null) {
            ItemStack itemStack1 = inventoryView.getItem(i);
            Item item = itemStack.getItem();
            if (item instanceof net.minecraft.server.v1_7_R4.ItemWrittenBook || item instanceof net.minecraft.server.v1_7_R4.ItemBookAndQuill) {
              itemStack.getTag().remove("pages");
              itemStack.getTag().remove("author");
              itemStack.getTag().remove("title");
              if (itemStack1 == null || (itemStack1.getType() != Material.valueOf("BOOK_AND_QUILL") && itemStack1.getType() != Material.WRITTEN_BOOK))
                return new ExploitDetails(this.user, str, "tried to use book but real item is " + ((itemStack1 == null) ? "null" : itemStack1.getType().name()), false, true); 
            } 
            if ((item instanceof net.minecraft.server.v1_7_R4.ItemFireworks || item instanceof net.minecraft.server.v1_7_R4.ItemFireworksCharge) && (itemStack1 == null || (itemStack1.getType() != Material.valueOf("FIREWORK") && itemStack1.getType() != Material.valueOf("FIREWORK_CHARGE"))))
              return new ExploitDetails(this.user, str, "tried to use firework but real item is " + ((itemStack1 == null) ? "null" : itemStack1.getType().name()), false, true); 
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
    } else if (paramObject.getClass().getSimpleName().equals("PacketPlayInBlockPlace")) {
      if (Settings.IMP.DEBUG)
        SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", new Object[] { str, player.getName() }); 
      ItemStack itemStack = ((PacketPlayInBlockPlace)paramObject).getItemStack();
      if (itemStack == null)
        try {
          Field field = PacketPlayInBlockPlace.class.getDeclaredField("e");
          field.setAccessible(true);
          itemStack = (ItemStack)field.get(paramObject);
          false;
        } catch (Exception exception) {
          exception.printStackTrace();
        }  
      if (itemStack != null) {
        Item item = itemStack.getItem();
        if (item instanceof net.minecraft.server.v1_7_R4.ItemWrittenBook || item instanceof net.minecraft.server.v1_7_R4.ItemBookAndQuill) {
          this.lastBookplace = System.currentTimeMillis();
          ItemStack itemStack1 = player.getItemInHand();
          if (itemStack1 == null || (itemStack1.getType() != Material.WRITTEN_BOOK && itemStack1.getType() != Material.valueOf("BOOK_AND_QUILL")))
            return new ExploitDetails(this.user, str, "placing book but not holding it", false, true); 
        } 
        ExploitDetails exploitDetails = checkNbtTags(str, itemStack);
      } 
      false;
    } else if (paramObject.getClass().getSimpleName().equals("PacketPlayInCustomPayload")) {
      PacketPlayInCustomPayload packetPlayInCustomPayload = (PacketPlayInCustomPayload)paramObject;
      String str1 = packetPlayInCustomPayload.c();
      if (str1 != null && packetPlayInCustomPayload.e() != null)
        try {
          ByteBuf byteBuf = Unpooled.wrappedBuffer(packetPlayInCustomPayload.e());
          if (byteBuf.capacity() > Settings.IMP.PAYLOAD_SETTINGS.MAX_CAPACITY)
            return new ExploitDetails(this.user, str, "invalid bytebuf capacity", false, true, "set \"payload-settings.max-capacity\" to higher value in settings.yml"); 
          if (str1.equals("MC|BEdit") || str1.equals("MC|BSign") || str1.equals("minecraft:bedit") || str1.equals("minecraft:bsign")) {
            if (Settings.IMP.DEBUG)
              SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", new Object[] { str, player.getName() }); 
            try {
              PacketDataSerializer packetDataSerializer = new PacketDataSerializer(byteBuf);
              ItemStack itemStack = packetDataSerializer.c();
              if (itemStack != null) {
                if (System.currentTimeMillis() - this.lastBookplace > 60000L)
                  return new ExploitDetails(this.user, str, "book sign, but no book used", false, false); 
                if (Settings.IMP.PAYLOAD_SETTINGS.FAIL_WHEN_NOT_HOLDING_BOOK && !player.getInventory().contains(Material.valueOf("BOOK_AND_QUILL")) && !player.getInventory().contains(Material.WRITTEN_BOOK))
                  return new ExploitDetails(this.user, str, "book interact, but no book exists", false, true); 
                ExploitDetails exploitDetails = checkNbtTags(str, itemStack);
                if (exploitDetails != null)
                  return exploitDetails; 
              } 
              false;
            } catch (Exception exception) {
              return new ExploitDetails(this.user, str, "exception: " + exception.getMessage(), false, false);
            } 
          } else if (str1.equals("REGISTER") || Integer.valueOf(1321107516).equals(Integer.valueOf(str1.toUpperCase().hashCode())) || str1.toLowerCase().contains("fml")) {
            ByteBuf byteBuf1 = null;
            try {
              byteBuf1 = byteBuf.copy();
              if ((byteBuf1.toString(StandardCharsets.UTF_8).split("\000")).length > 124)
                return new ExploitDetails(this.user, str, "too many channels", false, false); 
            } catch (Exception exception) {
            
            } finally {
              if (byteBuf1 != null);
            } 
            false;
          } else if (str1.equals("MC|ItemName") && player.getInventory() != null && player.getOpenInventory().getType() != InventoryType.ANVIL && Settings.IMP.BLOCK_ITEMNAME_WHEN_NO_ANVIL) {
            return new ExploitDetails(this.user, str, "trying to use MC|ItemName but no anvil exists", false, false, "set \"block-itemname-when-no-anvil\" in settings.yml to false");
          } 
          false;
        } catch (NullPointerException nullPointerException) {} 
      false;
    } else if (paramObject instanceof PacketPlayInPosition) {
      PacketPlayInPosition packetPlayInPosition = (PacketPlayInPosition)paramObject;
      double d1 = packetPlayInPosition.c();
      double d2 = packetPlayInPosition.d();
      double d3 = packetPlayInPosition.e();
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
    } else if (paramObject.getClass().getSimpleName().equals("PacketPlayInFlying")) {
      PacketPlayInFlying packetPlayInFlying = (PacketPlayInFlying)paramObject;
      double d1 = packetPlayInFlying.c();
      double d2 = packetPlayInFlying.d();
      double d3 = packetPlayInFlying.e();
      if (d1 >= Double.MAX_VALUE || d2 > Double.MAX_VALUE || d3 >= Double.MAX_VALUE)
        return new ExploitDetails(this.user, str, "Double.MAX_VALUE position", false, true); 
      float f1 = packetPlayInFlying.g();
      float f2 = packetPlayInFlying.h();
      if (f1 == Float.NEGATIVE_INFINITY || f2 == Float.NEGATIVE_INFINITY || f1 >= Float.MAX_VALUE || f2 >= Float.MAX_VALUE)
        return new ExploitDetails(this.user, str, "invalid float position", false, true); 
      false;
    } else if (paramObject instanceof PacketPlayInHeldItemSlot) {
      int i = ((PacketPlayInHeldItemSlot)paramObject).c();
      if (i >= 36 || i < 0)
        return new ExploitDetails(this.user, str, "invalid held item slot", false, true); 
      false;
    } else if (paramObject.getClass().getSimpleName().equals("PacketPlayInSteerVehicle")) {
      PacketPlayInSteerVehicle packetPlayInSteerVehicle = (PacketPlayInSteerVehicle)paramObject;
      if (packetPlayInSteerVehicle.c() >= Float.MAX_VALUE || packetPlayInSteerVehicle.d() >= Float.MAX_VALUE)
        return new ExploitDetails(this.user, str, "invalid vehicle movement", false, true); 
    } 
    LimitablePacket limitablePacket = hasReachedLimit(str);
    return (limitablePacket != null) ? new ExploitDetails(this.user, str, "packet limit exceed", limitablePacket.isCancelOnly(), false, "increase packet limit of " + str + " in config.yml or completely remove it") : null;
  }
  
  public PacketDecoder_1_7(PacketInjector paramPacketInjector, User paramUser) {
    this.injector = paramPacketInjector;
    this.user = paramUser;
  }
  
  private void lambda$decode$1(ExploitDetails paramExploitDetails, Future paramFuture) throws Exception {}
}