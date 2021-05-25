package xyz.yooniks.spigotguard.network.v1_16_R1;

import xyz.yooniks.spigotguard.user.*;
import xyz.yooniks.spigotguard.network.*;
import io.netty.channel.*;
import xyz.yooniks.spigotguard.config.*;
import xyz.yooniks.spigotguard.*;
import org.bukkit.plugin.*;
import java.util.logging.*;
import xyz.yooniks.spigotguard.logger.*;
import org.bukkit.event.inventory.*;
import org.bukkit.craftbukkit.v1_16_R1.inventory.*;
import java.nio.charset.*;
import org.bukkit.entity.*;
import java.lang.reflect.*;
import org.bukkit.inventory.*;
import xyz.yooniks.spigotguard.limitation.*;
import io.netty.buffer.*;
import xyz.yooniks.spigotguard.helper.*;
import net.minecraft.server.v1_16_R1.*;
import java.util.regex.*;
import java.util.*;
import io.netty.util.concurrent.*;
import xyz.yooniks.spigotguard.event.*;
import org.bukkit.*;
import org.bukkit.event.*;

@ChannelHandler.Sharable
public class PacketDecoder_1_16 extends PacketDecoder
{
    private static final Pattern URL_MATCHER;
    private static final PacketLimitation LIMITATION;
    private final User user;
    private boolean disconnected;
    private long lastBookplace;
    
    public PacketDecoder_1_16(final PacketInjector injector, final User user) {
        super(injector);
        this.disconnected = false;
        this.lastBookplace = -1L;
        this.user = user;
    }
    
    @Override
    public User getUser() {
        return this.user;
    }
    
    protected void decode(final ChannelHandlerContext ctx, final Object packet0, final List<Object> out) {
        if (this.disconnected) {
            return;
        }
        final ExploitDetails failure = this.failure(packet0);
        if (failure == null) {
            out.add(packet0);
            return;
        }
        if (failure.isCancelOnly()) {
            return;
        }
        this.disconnected = true;
        if (Settings.IMP.KICK.TYPE == 0) {
            ctx.close().addListener(future -> Bukkit.getScheduler().runTask((Plugin)SpigotGuardPlugin.getInstance(), () -> Bukkit.getPluginManager().callEvent((Event)new ExploitDetectedEvent((OfflinePlayer)this.injector.getPlayer(), failure))));
        }
        else {
            Bukkit.getScheduler().runTask((Plugin)SpigotGuardPlugin.getInstance(), () -> this.injector.getPlayer().kickPlayer(MessageBuilder.newBuilder(Settings.IMP.KICK.MESSAGE).stripped().coloured().toString()));
        }
        SpigotGuardLogger.log(Level.INFO, SpigotGuardPlugin.getInstance().getSpigotGuardClassLoaded() + " Player {0} tried to crash the server, packet: {1}, details: {2}", this.injector.getPlayer().getName(), packet0.getClass().getSimpleName(), failure.getDetails());
    }
    
    @Override
    public ExploitDetails failure(final Object packet0) {
        final Player player = this.injector.getPlayer();
        final String packetName = packet0.getClass().getSimpleName();
        if (packet0.getClass().getSimpleName().equals("PacketPlayInWindowClick")) {
            if (Settings.IMP.DEBUG) {
                SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", packetName, player.getName());
            }
            final PacketPlayInWindowClick packet = (PacketPlayInWindowClick)packet0;
            try {
                ItemStack packetItem = ((PacketPlayInWindowClick)packet0).f();
                if (packetItem == null) {
                    try {
                        final Field itemField = PacketPlayInWindowClick.class.getDeclaredField("item");
                        itemField.setAccessible(true);
                        packetItem = (ItemStack)itemField.get(packet0);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (packetItem != null) {
                    final ExploitDetails nbtTags = this.checkNbtTags(packetName, packetItem);
                    if (nbtTags != null) {
                        return nbtTags;
                    }
                }
                final int slot = packet.b();
                if (slot > 127 || slot < -999) {
                    return new ExploitDetails(this.user, packetName, "Invalid slot " + slot, false, true, "can't be fixed, it's surely exploit");
                }
                final InventoryView inventoryView = player.getOpenInventory();
                if (inventoryView != null) {
                    final Inventory topInventory = inventoryView.getTopInventory();
                    final Inventory bottomInventory = inventoryView.getBottomInventory();
                    int maxSlots = 127;
                    if (topInventory.getType() == InventoryType.CRAFTING && bottomInventory.getType() == InventoryType.PLAYER) {
                        maxSlots += 4;
                    }
                    if (slot >= maxSlots && PacketDecoder_1_16.LIMITATION != null) {
                        final LimitablePacket invalidSlot = this.hasReachedLimit("PacketPlayInWindowClick_InvalidSlot");
                        if (invalidSlot != null) {
                            return new ExploitDetails(this.user, packetName, "invalid slot, slot: " + slot + "", invalidSlot.isCancelOnly(), false, "increase packet limit of PacketPlayInWindowClick_InvalidSlot in config.yml");
                        }
                    }
                    if (packetItem != null) {
                        final org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack.asBukkitCopy(packetItem);
                        if ((bukkitItem.getType() == Material.CHEST || bukkitItem.getType() == Material.HOPPER) && bukkitItem.hasItemMeta() && bukkitItem.getItemMeta().toString().getBytes().length > 262144) {
                            return new ExploitDetails(this.user, packetName, "too big chest data", false, false);
                        }
                    }
                }
            }
            catch (Exception ex4) {}
        }
        else if (packet0.getClass().getSimpleName().equals("PacketPlayInBlockPlace")) {
            final org.bukkit.inventory.ItemStack itemInHand = player.getInventory().getItemInHand();
            if (itemInHand != null && itemInHand.getType().name().contains("BOOK")) {
                this.lastBookplace = System.currentTimeMillis();
            }
        }
        else if (packet0.getClass().getSimpleName().equals("PacketPlayInSetCreativeSlot")) {
            if (Settings.IMP.DEBUG) {
                SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", packetName, player.getName());
            }
            if (player.getGameMode() != GameMode.CREATIVE && !Settings.IMP.ALLOW_CREATIVE_INVENTORY_CLICK_WITHOUT_GAMEMODE) {
                return new ExploitDetails(this.user, packetName, "clicking in creative inventory without gamemode 1", false, true, "set \"allow-creative-inventory-click-without-gamemode\" to true in settings.yml");
            }
            ItemStack packetItem2 = null;
            try {
                final Field itemField2 = PacketPlayInSetCreativeSlot.class.getDeclaredField("b");
                itemField2.setAccessible(true);
                packetItem2 = (ItemStack)itemField2.get(packet0);
            }
            catch (Exception ex2) {
                ex2.printStackTrace();
            }
            if (packetItem2 != null) {
                final ExploitDetails nbtTags2 = this.checkNbtTags(packetName, packetItem2);
                if (nbtTags2 != null) {
                    return nbtTags2;
                }
                final org.bukkit.inventory.ItemStack bukkitItem2 = CraftItemStack.asBukkitCopy(packetItem2);
                if ((bukkitItem2.getType() == Material.CHEST || bukkitItem2.getType() == Material.HOPPER) && bukkitItem2.hasItemMeta() && bukkitItem2.getItemMeta().toString().getBytes().length > 262144) {
                    return new ExploitDetails(this.user, packetName, "too big chest data", false, false);
                }
            }
        }
        else if (packet0.getClass().getSimpleName().equals("PacketPlayInCustomPayload")) {
            final PacketPlayInCustomPayload payload = (PacketPlayInCustomPayload)packet0;
            final String channel = payload.tag.toString();
            final PacketDataSerializer data = payload.data;
            if (data.capacity() > Settings.IMP.PAYLOAD_SETTINGS.MAX_CAPACITY) {
                return new ExploitDetails(this.user, packetName, "invalid bytebuf capacity", false, true, "set \"payload-settings.max-capacity\" to higher value in settings.yml");
            }
            Label_1239: {
                if (channel.equals("MC|BEdit") || channel.equals("MC|BSign") || channel.equals("minecraft:bedit") || channel.equals("minecraft:bsign")) {
                    if (Settings.IMP.DEBUG) {
                        SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", packetName, player.getName());
                    }
                    try {
                        final ItemStack item = data.m();
                        if (item != null) {
                            if (System.currentTimeMillis() - this.lastBookplace > 60000L && Settings.IMP.PAYLOAD_SETTINGS.BLOCK_BOOK_SIGNING_WHEN_NO_BOOK_PLACED) {
                                return new ExploitDetails(this.user, packetName, "book sign, but no book used", false, false, "set \"payload-settings.block-book-signing-when-no-book-placed\" in settings.yml to false");
                            }
                            if (Settings.IMP.PAYLOAD_SETTINGS.FAIL_WHEN_NOT_HOLDING_BOOK && !player.getInventory().contains(Material.valueOf("BOOK_AND_QUILL")) && !player.getInventory().contains(Material.WRITTEN_BOOK)) {
                                return new ExploitDetails(this.user, packetName, "book interact, but no book exists in player's inventory", false, true, "set \"payload-settings.fail-when-not-holding-book\" in settings.yml to false");
                            }
                            final ExploitDetails nbtTags3 = this.checkNbtTags(packetName, item);
                            if (nbtTags3 != null) {
                                return nbtTags3;
                            }
                        }
                        break Label_1239;
                    }
                    catch (Exception ex3) {
                        return new ExploitDetails(this.user, packetName, "exception: " + ex3.getMessage(), false, false);
                    }
                }
                if (channel.equals("REGISTER") || channel.equalsIgnoreCase("UNREGISTER") || channel.toLowerCase().contains("fml")) {
                    ByteBuf buffer = null;
                    try {
                        buffer = data.copy();
                        if (buffer.toString(StandardCharsets.UTF_8).split("\u0000").length > 124) {
                            return new ExploitDetails(this.user, packetName, "too many channels", false, false);
                        }
                    }
                    catch (Exception ex5) {}
                    finally {
                        if (buffer != null) {
                            buffer.release();
                        }
                    }
                }
                else if (channel.equals("MC|ItemName") && player.getInventory() != null && player.getOpenInventory().getType() != InventoryType.ANVIL && Settings.IMP.BLOCK_ITEMNAME_WHEN_NO_ANVIL) {
                    return new ExploitDetails(this.user, packetName, "trying to use MC|ItemName but no anvil exists", false, false, "set \"block-itemname-when-no-anvil\" in settings.yml to false");
                }
            }
        }
        else if (packet0 instanceof PacketPlayInFlying) {
            final PacketPlayInFlying packet2 = (PacketPlayInFlying)packet0;
            final double x = packet2.a(0.0);
            final double y = packet2.b(0.0);
            final double z = packet2.c(0.0);
            if ((x >= Double.MAX_VALUE || y > Double.MAX_VALUE || z >= Double.MAX_VALUE) && !Settings.IMP.POSITION_CHECKS.ALLOW_INVALID_MOVEMENT_V2) {
                return new ExploitDetails(this.user, packetName, "Double.MAX_VALUE position", false, true, "set \"position-checks.allow-invalid-movement-v2\" to true");
            }
            if (x >= 2.147483647E9 || y > 2.147483647E9 || (z >= 2.147483647E9 && !Settings.IMP.POSITION_CHECKS.ALLOW_INVALID_MOVEMENT_V1)) {
                return new ExploitDetails(this.user, packetName, "Integer.MAX_VALUE position", false, true, "set \"position-checks.allow-invalid-movement-v1\" to true");
            }
            final float yaw = packet2.a(0.0f);
            final float pitch = packet2.b(0.0f);
            if (yaw == Float.NEGATIVE_INFINITY || pitch == Float.NEGATIVE_INFINITY || yaw >= Float.MAX_VALUE || pitch >= Float.MAX_VALUE) {
                return new ExploitDetails(this.user, packetName, "invalid float position", false, true);
            }
        }
        else if (packet0 instanceof PacketPlayInHeldItemSlot) {
            final int slot2 = ((PacketPlayInHeldItemSlot)packet0).b();
            if (slot2 >= 36 || slot2 < 0) {
                return new ExploitDetails(this.user, packetName, "invalid held item slot", false, true);
            }
        }
        else if (packet0.getClass().getSimpleName().equals("PacketPlayInSteerVehicle")) {
            final PacketPlayInSteerVehicle p = (PacketPlayInSteerVehicle)packet0;
            if (p.b() >= Float.MAX_VALUE || p.c() >= Float.MAX_VALUE) {
                return new ExploitDetails(this.user, packetName, "invalid vehicle movement", false, true);
            }
        }
        else if (packet0.getClass().getSimpleName().equals("PacketPlayInVehicleMove")) {
            final PacketPlayInVehicleMove p2 = (PacketPlayInVehicleMove)packet0;
            if (p2.getX() >= Double.MAX_VALUE || p2.getY() >= Double.MAX_VALUE || p2.getZ() >= Double.MAX_VALUE) {
                return new ExploitDetails(this.user, packetName, "invalid vehicle movement v2", false, true);
            }
            if (player.getLocation().distance(new Location(player.getWorld(), p2.getX(), p2.getY(), p2.getZ())) > 30.0 && !Settings.IMP.POSITION_CHECKS.ALLOW_INVALID_VEHICLE_MOVEMENT_V3) {
                return new ExploitDetails(this.user, packetName, "invalid vehicle movement v3", false, true, "set position-checks.allow-invalid-vehicle-movement-v3 to true");
            }
            if (player.getLocation().distance(new Location(player.getWorld(), p2.getX(), p2.getY(), p2.getZ())) > 3.0 && !Settings.IMP.POSITION_CHECKS.ALLOW_INVALID_VEHICLE_MOVEMENT_V4) {
                final LimitablePacket packetLimit = this.hasReachedLimit("PacketPlayInVehicleMove_Invalid_v4");
                if (packetLimit != null) {
                    return new ExploitDetails(this.user, packetName, "invalid vehicle movement v4", false, true, "set position-checks.allow-invalid-vehicle-movement-v4 to true");
                }
            }
        }
        else if (packet0.getClass().getSimpleName().equals("PacketPlayInUseItem")) {
            final PacketPlayInUseItem p3 = (PacketPlayInUseItem)packet0;
            final BlockPosition position = p3.c().getBlockPosition();
            final int max_distance_between_player_and_block_position = Settings.IMP.POSITION_CHECKS.MAX_DISTANCE_BETWEEN_PLAYER_AND_BLOCK_POSITION;
            if (max_distance_between_player_and_block_position > 0 && new Location(player.getWorld(), (double)position.getX(), (double)position.getY(), (double)position.getZ()).distance(player.getLocation()) > max_distance_between_player_and_block_position) {
                return new ExploitDetails(this.user, packetName, "invalid UseItem blockPosition", false, true, "set position-checks.max-distance-between-player-and-block-position to -1");
            }
        }
        final LimitablePacket packetLimit2 = this.hasReachedLimit(packetName);
        if (packetLimit2 != null) {
            return new ExploitDetails(this.user, packetName, "packet limit exceed", packetLimit2.isCancelOnly(), false);
        }
        return null;
    }
    
    private LimitablePacket hasReachedLimit(final String packetName) {
        if (!PacketDecoder_1_16.LIMITATION.isLimitable(packetName)) {
            return null;
        }
        final LimitablePacket limitablePacket = PacketDecoder_1_16.LIMITATION.getLimit(packetName);
        if (this.user.increaseAndGetReceivedPackets(packetName) < limitablePacket.getLimit()) {
            return null;
        }
        return limitablePacket;
    }
    
    private ExploitDetails checkNbtTags(final String packet, final ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        final NBTTagCompound tagCompound = itemStack.getTag();
        final Item item = itemStack.getItem();
        if (tagCompound == null) {
            if (Settings.IMP.DEBUG) {
                SpigotGuardLogger.log(Level.INFO, packet + " -> " + item.getName() + " -> no nbt detected", new Object[0]);
            }
            return null;
        }
        if (Settings.IMP.BOOK.FAST_CHECK && (item instanceof ItemWrittenBook || item instanceof ItemBookAndQuill) && String.valueOf(tagCompound.get("pages")).length() > Settings.IMP.BOOK.FAST_CHECK_MAX_LENGTH) {
            tagCompound.remove("pages");
            tagCompound.remove("author");
            tagCompound.remove("title");
            return new ExploitDetails(this.user, packet, "too large pages tag (fast-check)", false, false, "set \"book.fast-check\" in settings.yml to false or increase limit of \"book.fast-check-max-length\" ");
        }
        if (item instanceof ItemFireworks && tagCompound.toString().length() > Settings.IMP.OTHER_NBT.FIREWORK_LIMIT) {
            return new ExploitDetails(this.user, packet, "too big firework data", false, true, "set \"other-nbt.firework-limit\" in settings.yml to higher value");
        }
        if (item instanceof ItemFireworksCharge && tagCompound.toString().length() > Settings.IMP.OTHER_NBT.FIREWORKS_CHARGE_LIMIT) {
            return new ExploitDetails(this.user, packet, "too big firework_charge data", false, false, "set \"other-nbt.fireworks-charge-limit\" in settings.yml to higher value");
        }
        if (item instanceof ItemBanner && tagCompound.toString().length() > Settings.IMP.OTHER_NBT.BANNER_LIMIT) {
            return new ExploitDetails(this.user, packet, "too big banner data", false, false, "set \"other-nbt.banner-limit\" in settings.yml to higher value");
        }
        if (Settings.IMP.DEBUG) {
            SpigotGuardLogger.log(Level.INFO, packet + " -> " + item.getName() + " -> " + tagCompound.toString(), new Object[0]);
        }
        final Set<String> keys = (Set<String>)tagCompound.getKeys();
        final Settings settings = Settings.IMP;
        if (keys.size() > settings.OTHER_NBT.MAX_KEYS) {
            return new ExploitDetails(this.user, packet, "too many keys (" + keys.size() + ")", false, true, "set \"other-nbt.max-keys\" in settings.yml to higher value");
        }
        if (tagCompound.hasKey("pages")) {
            if (settings.BOOK.BAN_BOOKS) {
                return new ExploitDetails(this.user, packet, "using book while it is banned", false, true, "set \"book.ban-books\" in settings.yml to false");
            }
            final NBTTagList pages = tagCompound.getList("pages", 8);
            if (pages.size() > settings.BOOK.MAX_PAGES) {
                return new ExploitDetails(this.user, packet, "too many pages (" + pages.size() + ")", false, true, "set \"book.max-pages\" in settings.yml to higher value");
            }
            String lastPage = "";
            int similarPages = 0;
            for (int i = 0; i < pages.size(); ++i) {
                final String page = pages.getString(i);
                if (page.length() > settings.BOOK.MAX_PAGE_SIZE) {
                    return new ExploitDetails(this.user, packet, "too large page content (" + page.length() + ")", false, false, "increase value of \"book.max-page-size\" (bigger than: " + page.length() + ") in settings.yml");
                }
                if (page.split("extra").length > 8) {
                    return new ExploitDetails(this.user, packet, "too many extra words", false, true);
                }
                if (lastPage.equals(page)) {
                    ++similarPages;
                }
                lastPage = page;
                if (similarPages > settings.BOOK.MAX_SIMILAR_PAGES) {
                    return new ExploitDetails(this.user, packet, "too many similar pages", false, true, "set \"book.max-similar-pages\" in settings.yml to higher value");
                }
                final String strippedPage = ChatColor.stripColor(page.replaceAll("\\+", ""));
                if (strippedPage == null || strippedPage.equals("null")) {
                    return new ExploitDetails(this.user, packet, "null stripped page", false, true);
                }
                if (strippedPage.length() > settings.BOOK.MAX_STRIPPED_PAGE_SIZE) {
                    return new ExploitDetails(this.user, packet, "too large stripped page content (" + page.length() + ")", false, true, "set \"book.max-stripped-page-size\" in settings.yml to value higher than " + page.length());
                }
                if (settings.BOOK.MAX_2BYTE_CHARS > 0) {
                    int tooBigChars = 0;
                    for (int charI = 0; charI < page.length(); ++charI) {
                        final char current = page.charAt(charI);
                        if (String.valueOf(current).getBytes().length > 1 && ++tooBigChars > settings.BOOK.MAX_2BYTE_CHARS) {
                            return new ExploitDetails(this.user, packet, "Too many 2byte chars in page content. Allowed: " + settings.BOOK.MAX_2BYTE_CHARS + ", current: " + tooBigChars, false, true, "increase \"book.max-2byte-chars\" (must be bigger than " + ++tooBigChars + ") in settings.yml");
                        }
                    }
                }
                final String noSpaces = page.replace(" ", "");
                if (noSpaces.startsWith("{\"translate\"")) {
                    for (final String crashTranslation : MojangCrashTranslations.MOJANG_CRASH_TRANSLATIONS) {
                        final String translationJson = String.format("{\"translate\":\"%s\"}", crashTranslation);
                        if (page.equalsIgnoreCase(translationJson)) {
                            return new ExploitDetails(this.user, packet, "Crash book! TranslationJson: " + translationJson, false, true);
                        }
                    }
                }
            }
        }
        if (Settings.IMP.OTHER_NBT.SIMPLE_NBT_LIMIT) {
            final String name = item.getName().toLowerCase();
            if (!name.contains("chest") && !name.contains("hopper") && !name.contains("shulker")) {
                final int length = String.valueOf(tagCompound).getBytes(StandardCharsets.UTF_8).length;
                if (length > Settings.IMP.OTHER_NBT.MAX_SIMPLE_NBT_LIMIT) {
                    SpigotGuardLogger.log(Level.INFO, "{0} too big NBT data sent! ({1}) If it is false positive please change max-simple-nbt-limit in settings.yml to bigger value than player reached.", this.injector.getPlayer().getName(), length);
                    return new ExploitDetails(this.user, packet, "Too big NBT data! (" + length + ")", false, true, "increase \"other-nbt.max-simple-nbt-limit\" in settings.yml or set \"other-nbt.simple-nbt-limit\" to false");
                }
            }
        }
        if (tagCompound.hasKey("SkullOwner")) {
            final NBTTagCompound skullOwner = tagCompound.getCompound("SkullOwner");
            if (skullOwner.hasKey("Properties")) {
                final NBTTagCompound properties = skullOwner.getCompound("Properties");
                if (properties.hasKey("textures")) {
                    final NBTTagList textures = properties.getList("textures", 10);
                    for (int i = 0; i < textures.size(); ++i) {
                        final NBTTagCompound entry = textures.getCompound(i);
                        if (entry.hasKey("Value")) {
                            final String b64 = entry.getString("Value");
                            String decoded;
                            try {
                                decoded = new String(Base64.getDecoder().decode(b64));
                            }
                            catch (IllegalArgumentException e) {
                                break;
                            }
                            decoded = decoded.trim().replace(" ", "").replace("\"", "").toLowerCase();
                            final Matcher matcher = PacketDecoder_1_16.URL_MATCHER.matcher(decoded);
                            while (matcher.find()) {
                                final String url = decoded.substring(matcher.end() + 1);
                                if (!url.startsWith("http://textures.minecraft.net") && !url.startsWith("https://textures.minecraft.net") && Settings.IMP.SKULL_CHECKS.BLOCK_INVALID_HEAD_TEXTURE) {
                                    return new ExploitDetails(this.user, packet, "Invalid head texture 2", false, true, "set \"skull-checks.invalid-head-texture\" to false");
                                }
                            }
                        }
                    }
                }
            }
        }
        int listsAmount = 0;
        for (final String key : keys) {
            if (tagCompound.hasKeyOfType(key, 9)) {
                if (++listsAmount > Settings.IMP.OTHER_NBT.MAX_LISTS) {
                    return new ExploitDetails(this.user, packet, "too many NBTLists (" + listsAmount + ")", false, true, "set \"other-nbt.max-lists\" to value bigger than " + listsAmount + " in settings.yml");
                }
                final NBTTagList list = tagCompound.getList(key, 8);
                final int size = list.size();
                if (size > settings.OTHER_NBT.MAX_LIST_SIZE) {
                    tagCompound.remove(key);
                    return new ExploitDetails(this.user, packet, "too big NBTList (" + size + ")", false, true, "set \"other-nbt.max-list-size\" to value higher than " + size + " in settings.yml");
                }
                for (int j = 0; j < list.size(); ++j) {
                    final String content = list.getString(j);
                    if (content == null || content.equals("null")) {
                        return new ExploitDetails(this.user, packet, "null list content", false, true);
                    }
                    if (content.length() > settings.OTHER_NBT.MAX_LIST_CONTENT) {
                        return new ExploitDetails(this.user, packet, "too big list content (" + content.length() + ")", false, false, "set \"other-nbt.max-list-content\" to value higher than " + content.length() + " in settings.yml");
                    }
                }
            }
            if (tagCompound.hasKeyOfType(key, 11)) {
                final int[] intArray = tagCompound.getIntArray(key);
                if (intArray.length > settings.OTHER_NBT.MAX_ARRAY_SIZE) {
                    return new ExploitDetails(this.user, packet, "too large int array", false, true, "set \"other-nbt.max-array-size\" to value higher than " + intArray.length + " in settings.yml");
                }
                continue;
            }
        }
        return null;
    }
    
    static {
        URL_MATCHER = Pattern.compile("url");
        LIMITATION = SpigotGuardPlugin.getInstance().getPacketLimitation();
    }
}
