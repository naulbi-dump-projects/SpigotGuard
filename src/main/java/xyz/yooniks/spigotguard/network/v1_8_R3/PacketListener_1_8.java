package xyz.yooniks.spigotguard.network.v1_8_R3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.regex.Pattern;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInCustomPayload;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInHeldItemSlot;
import net.minecraft.server.v1_8_R3.PacketPlayInSetCreativeSlot;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_8_R3.PacketPlayInWindowClick;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import xyz.yooniks.spigotguard.config.Settings;
import xyz.yooniks.spigotguard.event.ExploitDetails;
import xyz.yooniks.spigotguard.event.ExploitDetectedEvent;
import xyz.yooniks.spigotguard.helper.MessageBuilder;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;
import xyz.yooniks.spigotguard.network.PacketInjector;
import xyz.yooniks.spigotguard.user.User;

@Sharable
public class PacketListener_1_8 extends ChannelDuplexHandler {
  private final User user;
  
  private long lastBookplace = -1L;
  
  private final PacketInjector injector;
  
  private boolean disconnected = false;
  
  private static final String BLOCKED = "wveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5";
  
  private static final Pattern URL_MATCHER = Pattern.compile("url");
  
  public void channelRead(ChannelHandlerContext paramChannelHandlerContext, Object paramObject) throws Exception {}
  
  public PacketListener_1_8(PacketInjector paramPacketInjector, User paramUser) {
    this.injector = paramPacketInjector;
    this.user = paramUser;
  }
  
  private ExploitDetails failure(Object paramObject) {
    Player player = this.injector.getPlayer();
    if (paramObject != null && Settings.IMP.DEBUG)
      SpigotGuardLogger.log(Level.INFO, "{0} received " + paramObject.getClass().getSimpleName() + " -> " + paramObject.toString(), new Object[0]); 
    String str = paramObject.getClass().getSimpleName();
    if (paramObject instanceof PacketPlayInWindowClick) {
      if (Settings.IMP.DEBUG)
        SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", new Object[] { str, player.getName() }); 
      PacketPlayInWindowClick packetPlayInWindowClick = (PacketPlayInWindowClick)paramObject;
      try {
        ItemStack itemStack = ((PacketPlayInWindowClick)paramObject).e();
        if (itemStack == null)
          try {
            Field field = PacketPlayInWindowClick.class.getDeclaredField("item");
            field.setAccessible(true);
            itemStack = (ItemStack)field.get(paramObject);
            false;
          } catch (Exception exception) {
            exception.printStackTrace();
          }  
        int i = packetPlayInWindowClick.b();
        if (i > 127 || i < -999)
          return new ExploitDetails(this.user, str, "Invalid slot " + i, false, true, "can't be fixed, it's surely exploit"); 
        if (itemStack != null) {
          ExploitDetails exploitDetails = checkNbtTags(str, itemStack);
          if (exploitDetails != null)
            return exploitDetails; 
        } 
        try {
          InventoryView inventoryView = player.getOpenInventory();
          if (inventoryView != null) {
            Inventory inventory1 = inventoryView.getTopInventory();
            Inventory inventory2 = inventoryView.getBottomInventory();
            int j = inventoryView.countSlots();
            if (inventory1.getType() == InventoryType.CRAFTING && inventory2.getType() == InventoryType.PLAYER)
              j += 4; 
            if (itemStack != null && i > 0 && inventoryView.getType() == InventoryType.PLAYER)
              try {
                ItemStack itemStack1 = inventoryView.getItem(i);
                Item item = itemStack.getItem();
                if (item instanceof net.minecraft.server.v1_8_R3.ItemWrittenBook || item instanceof net.minecraft.server.v1_8_R3.ItemBookAndQuill) {
                  itemStack.getTag().remove("pages");
                  itemStack.getTag().remove("author");
                  itemStack.getTag().remove("title");
                  if (itemStack1 != null && itemStack1.getType() != Material.valueOf("BOOK_AND_QUILL") && itemStack1.getType() != Material.WRITTEN_BOOK && itemStack.getTag() != null && itemStack.getTag().toString().length() > Settings.IMP.BLOCK_FAKE_PACKETS_ONLY_WHEN_ITEMS_NBT_BIGGER_THAN)
                    return new ExploitDetails(this.user, str, "tried to use book but real item is " + itemStack1.getType().name(), false, true, "increase \"block-fake-packets-only-when-items-nbt-bigger-than\" value in settings.yml"); 
                } 
                if ((item instanceof net.minecraft.server.v1_8_R3.ItemFireworks || item instanceof net.minecraft.server.v1_8_R3.ItemFireworksCharge) && itemStack1 != null && itemStack1.getType() != Material.valueOf("FIREWORK") && itemStack1.getType() != Material.valueOf("FIREWORK_CHARGE") && itemStack.getTag() != null && itemStack.getTag().toString().length() > Settings.IMP.BLOCK_FAKE_PACKETS_ONLY_WHEN_ITEMS_NBT_BIGGER_THAN)
                  return new ExploitDetails(this.user, str, "tried to use firework but real item is " + itemStack1.getType().name(), false, true, "increase \"block-fake-packets-only-when-items-nbt-bigger-than\" value in settings.yml"); 
                if (item instanceof net.minecraft.server.v1_8_R3.ItemBanner && itemStack1 != null && !itemStack1.getType().name().contains("BANNER") && itemStack.getTag() != null && itemStack.getTag().toString().length() > Settings.IMP.BLOCK_FAKE_PACKETS_ONLY_WHEN_ITEMS_NBT_BIGGER_THAN)
                  return new ExploitDetails(this.user, str, "tried to use banner but real item is " + itemStack1.getType().name(), false, true, "increase \"block-fake-packets-only-when-items-nbt-bigger-than\" value in settings.yml"); 
                false;
              } catch (Exception exception) {} 
          } 
          false;
        } catch (Exception exception) {}
        if (itemStack != null) {
          ItemStack itemStack1 = CraftItemStack.asBukkitCopy(itemStack);
          if ((itemStack1.getType() == Material.CHEST || itemStack1.getType() == Material.HOPPER) && itemStack1.hasItemMeta() && (itemStack1.getItemMeta().toString().getBytes()).length > 262144)
            return new ExploitDetails(this.user, str, "too big chest data", false, false); 
        } 
        false;
      } catch (Exception exception) {
        return new ExploitDetails(this.user, str, "exception " + exception.getMessage(), false, false);
      } 
      false;
    } else if (paramObject instanceof PacketPlayInSetCreativeSlot) {
      if (Settings.IMP.DEBUG)
        SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", new Object[] { str, player.getName() }); 
      if (player.getGameMode() != GameMode.CREATIVE && !Settings.IMP.ALLOW_CREATIVE_INVENTORY_CLICK_WITHOUT_GAMEMODE) {
        ItemStack itemStack1 = null;
        try {
          Field field = PacketPlayInSetCreativeSlot.class.getDeclaredField("b");
          field.setAccessible(true);
          itemStack1 = (ItemStack)field.get(paramObject);
          false;
        } catch (Exception exception) {
          exception.printStackTrace();
        } 
        if (itemStack1 != null) {
          if (itemStack1.getName().toLowerCase().contains("book") && itemStack1.getTag() != null) {
            itemStack1.getTag().remove("pages");
            itemStack1.getTag().remove("author");
            itemStack1.getTag().remove("title");
          } 
          itemStack1.setTag(new NBTTagCompound());
        } 
        return new ExploitDetails(this.user, str, "clicking in creative inventory without gamemode 1", false, true, "set \"allow-creative-inventory-click-without-gamemode\" to true in settings.yml");
      } 
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
    } else if (paramObject instanceof PacketPlayInBlockPlace) {
      if (Settings.IMP.DEBUG)
        SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", new Object[] { str, player.getName() }); 
      ItemStack itemStack = ((PacketPlayInBlockPlace)paramObject).getItemStack();
      if (itemStack == null)
        try {
          Field field = PacketPlayInBlockPlace.class.getDeclaredField("d");
          field.setAccessible(true);
          itemStack = (ItemStack)field.get(paramObject);
          false;
        } catch (Exception exception) {
          exception.printStackTrace();
        }  
      if (itemStack != null) {
        Item item = itemStack.getItem();
        if (item instanceof net.minecraft.server.v1_8_R3.ItemWrittenBook || item instanceof net.minecraft.server.v1_8_R3.ItemBookAndQuill) {
          this.lastBookplace = System.currentTimeMillis();
          ItemStack itemStack1 = player.getItemInHand();
          if (itemStack1 == null || (itemStack1.getType() != Material.WRITTEN_BOOK && itemStack1.getType() != Material.valueOf("BOOK_AND_QUILL") && Settings.IMP.PLACE_CHECKS.BLOCK_PLACING_BOOK_WHEN_NOT_HOLDING_IT)) {
            if (itemStack.getTag() != null) {
              itemStack.getTag().remove("pages");
              itemStack.getTag().remove("author");
              itemStack.getTag().remove("title");
            } 
            return new ExploitDetails(this.user, str, "placing book but not holding it", false, true, "set \"place-checks.block-placing-book-when-not-holding-it\" in settings.yml to false");
          } 
        } 
        if (item instanceof net.minecraft.server.v1_8_R3.ItemFireworks || item instanceof net.minecraft.server.v1_8_R3.ItemFireworksCharge) {
          ItemStack itemStack1 = player.getItemInHand();
          if ((itemStack1 == null || (itemStack1.getType() != Material.valueOf("FIREWORK_CHARGE") && itemStack1.getType() != Material.valueOf("FIREWORK") && Settings.IMP.PLACE_CHECKS.BLOCK_PLACING_FIREWORK_WHEN_NOT_HOLDING_IT)) && itemStack.getTag() != null && itemStack.getTag().toString().length() > Settings.IMP.BLOCK_FAKE_PACKETS_ONLY_WHEN_ITEMS_NBT_BIGGER_THAN)
            return new ExploitDetails(this.user, str, "placing firework but not holding it", false, true, "set \"place-checks.block-placing-firework-when-not-holding-it\" in settings.yml to false"); 
        } 
        if (item instanceof net.minecraft.server.v1_8_R3.ItemBanner) {
          ItemStack itemStack1 = player.getItemInHand();
          if ((itemStack1 == null || (!itemStack1.getType().name().contains("BANNER") && Settings.IMP.PLACE_CHECKS.BLOCK_PLACING_BANNER_WHEN_NOT_HOLDING_IT)) && itemStack.getTag() != null && itemStack.getTag().toString().length() > Settings.IMP.BLOCK_FAKE_PACKETS_ONLY_WHEN_ITEMS_NBT_BIGGER_THAN)
            return new ExploitDetails(this.user, str, "placing banner but not holding it", false, true, "set \"place-checks.block-placing-banner-when-not-holding-it\" in settings.yml to false"); 
        } 
        ExploitDetails exploitDetails = checkNbtTags(str, itemStack);
      } 
      false;
    } else if (paramObject instanceof PacketPlayInCustomPayload) {
      PacketPlayInCustomPayload packetPlayInCustomPayload = (PacketPlayInCustomPayload)paramObject;
      String str1 = packetPlayInCustomPayload.a();
      PacketDataSerializer packetDataSerializer = packetPlayInCustomPayload.b();
      if (packetDataSerializer.capacity() > Settings.IMP.PAYLOAD_SETTINGS.MAX_CAPACITY)
        return new ExploitDetails(this.user, str, "invalid bytebuf capacity", false, true, "set \"payload-settings.max-capacity\" to higher value in settings.yml"); 
      if (Integer.valueOf(-296262810).equals(Integer.valueOf(str1.toUpperCase().hashCode())) || Integer.valueOf(-295840999).equals(Integer.valueOf(str1.toUpperCase().hashCode())) || Integer.valueOf(883258655).equals(Integer.valueOf(str1.toUpperCase().hashCode())) || Integer.valueOf(883680466).equals(Integer.valueOf(str1.toUpperCase().hashCode()))) {
        if (Settings.IMP.DEBUG)
          SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", new Object[] { str, player.getName() }); 
        try {
          PacketDataSerializer packetDataSerializer1 = new PacketDataSerializer(Unpooled.wrappedBuffer((ByteBuf)packetDataSerializer));
          ItemStack itemStack = packetDataSerializer1.i();
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
      double d1 = packetPlayInPosition.a();
      double d2 = packetPlayInPosition.b();
      double d3 = packetPlayInPosition.c();
      if ((d1 >= Double.MAX_VALUE || d2 > Double.MAX_VALUE || d3 >= Double.MAX_VALUE) && !Settings.IMP.POSITION_CHECKS.ALLOW_INVALID_MOVEMENT_V1)
        return new ExploitDetails(this.user, str, "Double.MAX_VALUE position", false, true, "set \"position-checks.allow-invalid-movement-v2\" to true"); 
      if (d1 >= 2.147483647E9D || d2 > 2.147483647E9D || (d3 >= 2.147483647E9D && !Settings.IMP.POSITION_CHECKS.ALLOW_INVALID_MOVEMENT_V1))
        return new ExploitDetails(this.user, str, "Integer.MAX_VALUE position", false, true, "set \"position-checks.allow-invalid-movement-v1\" to true"); 
      false;
    } else if (paramObject instanceof PacketPlayInFlying) {
      PacketPlayInFlying packetPlayInFlying = (PacketPlayInFlying)paramObject;
      double d1 = packetPlayInFlying.a();
      double d2 = packetPlayInFlying.b();
      double d3 = packetPlayInFlying.c();
      if ((d1 >= Double.MAX_VALUE || d2 > Double.MAX_VALUE || d3 >= Double.MAX_VALUE) && !Settings.IMP.POSITION_CHECKS.ALLOW_INVALID_MOVEMENT_V1)
        return new ExploitDetails(this.user, str, "Double.MAX_VALUE position", false, true, "set \"position-checks.allow-invalid-movement-v2\" to true"); 
      if (d1 >= 2.147483647E9D || d2 > 2.147483647E9D || (d3 >= 2.147483647E9D && !Settings.IMP.POSITION_CHECKS.ALLOW_INVALID_MOVEMENT_V1))
        return new ExploitDetails(this.user, str, "Integer.MAX_VALUE position", false, true, "set \"position-checks.allow-invalid-movement-v1\" to true"); 
      float f1 = packetPlayInFlying.d();
      float f2 = packetPlayInFlying.e();
      if (f1 == Float.NEGATIVE_INFINITY || f2 == Float.NEGATIVE_INFINITY || f1 >= Float.MAX_VALUE || f2 >= Float.MAX_VALUE)
        return new ExploitDetails(this.user, str, "invalid float position", false, true); 
      false;
    } else if (paramObject instanceof PacketPlayInHeldItemSlot) {
      int i = ((PacketPlayInHeldItemSlot)paramObject).a();
      if (i >= 36 || i < 0)
        return new ExploitDetails(this.user, str, "invalid held item slot", false, true); 
      false;
    } else if (paramObject instanceof PacketPlayInSteerVehicle) {
      PacketPlayInSteerVehicle packetPlayInSteerVehicle = (PacketPlayInSteerVehicle)paramObject;
      if (packetPlayInSteerVehicle.b() >= Float.MAX_VALUE || packetPlayInSteerVehicle.a() >= Float.MAX_VALUE)
        return new ExploitDetails(this.user, str, "invalid vehicle movement", false, true); 
    } 
    return null;
  }
  
  private void lambda$channelRead$2() {
    this.injector.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString());
  }
  
  private void lambda$channelRead$1(ExploitDetails paramExploitDetails, Future paramFuture) throws Exception {}
  
  private ExploitDetails checkNbtTags(String paramString, ItemStack paramItemStack) {}
  
  private void lambda$channelRead$0(ExploitDetails paramExploitDetails) {
    Bukkit.getPluginManager().callEvent((Event)new ExploitDetectedEvent((OfflinePlayer)this.injector.getPlayer(), paramExploitDetails));
  }
}