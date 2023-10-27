package xyz.yooniks.spigotguard.network.v1_7_R4;

import net.minecraft.util.io.netty.handler.codec.*;
import xyz.yooniks.spigotguard.network.*;
import xyz.yooniks.spigotguard.user.*;
import net.minecraft.util.io.netty.channel.*;
import xyz.yooniks.spigotguard.config.*;
import xyz.yooniks.spigotguard.*;
import org.bukkit.plugin.*;
import java.util.logging.*;
import xyz.yooniks.spigotguard.logger.*;
import org.bukkit.event.inventory.*;
import org.bukkit.craftbukkit.v1_7_R4.inventory.*;
import java.nio.charset.*;
import org.bukkit.entity.*;
import java.lang.reflect.*;
import org.bukkit.inventory.*;
import xyz.yooniks.spigotguard.limitation.*;
import net.minecraft.util.io.netty.buffer.*;
import xyz.yooniks.spigotguard.helper.*;
import net.minecraft.server.v1_7_R4.*;
import java.util.regex.*;
import java.util.*;
import net.minecraft.util.io.netty.util.concurrent.*;
import xyz.yooniks.spigotguard.event.*;
import org.bukkit.*;
import org.bukkit.event.*;

@ChannelHandler.Sharable
public class PacketDecoder_1_7 extends MessageToMessageDecoder<Object>
{
    private static final Pattern URL_MATCHER;
    private static final String BLOCKED = "wveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5";
    private static final PacketLimitation LIMITATION;
    private final PacketInjector injector;
    private final User user;
    private boolean disconnected;
    private long lastBookplace;
    
    public PacketDecoder_1_7(final PacketInjector injector, final User user) {
        this.disconnected = false;
        this.lastBookplace = -1L;
        this.injector = injector;
        this.user = user;
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
        SpigotGuardLogger.log(Level.INFO, "[1] Player {0} tried to crash the server, packet: {1}, details: {2}", this.injector.getPlayer().getName(), packet0.getClass().getSimpleName(), failure.getDetails());
    }
    
    public User getUser() {
        return this.user;
    }
    
    public ExploitDetails failure(final Object packet0) {
        final Player player = this.injector.getPlayer();
        final String packetName = packet0.getClass().getSimpleName();
        if (packet0.getClass().getSimpleName().equals("PacketPlayInWindowClick")) {
            if (Settings.IMP.DEBUG) {
                SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", packetName, player.getName());
            }
            final PacketPlayInWindowClick packet = (PacketPlayInWindowClick)packet0;
            try {
                ItemStack packetItem = ((PacketPlayInWindowClick)packet0).g();
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
                final InventoryView inventoryView = player.getOpenInventory();
                if (inventoryView != null) {
                    final Inventory topInventory = inventoryView.getTopInventory();
                    final Inventory bottomInventory = inventoryView.getBottomInventory();
                    final int slot = packet.slot;
                    int maxSlots = inventoryView.countSlots();
                    if (topInventory.getType() == InventoryType.CRAFTING && bottomInventory.getType() == InventoryType.PLAYER) {
                        maxSlots += 4;
                    }
                    if (slot >= maxSlots && PacketDecoder_1_7.LIMITATION != null) {
                        final LimitablePacket invalidSlot = this.hasReachedLimit("PacketPlayInWindowClick_InvalidSlot");
                        if (invalidSlot != null) {
                            return new ExploitDetails(this.user, packetName, "invalid slot, slot: " + slot + "", invalidSlot.isCancelOnly(), false, "increase packet limit of PacketPlayInWindowClick_InvalidSlot in config.yml");
                        }
                    }
                    if (packetItem != null) {
                        final org.bukkit.inventory.ItemStack realItem = inventoryView.getItem(slot);
                        final Item item = packetItem.getItem();
                        if (item instanceof ItemWrittenBook || item instanceof ItemBookAndQuill) {
                            packetItem.getTag().remove("pages");
                            packetItem.getTag().remove("author");
                            packetItem.getTag().remove("title");
                            if (realItem == null || (realItem.getType() != Material.valueOf("BOOK_AND_QUILL") && realItem.getType() != Material.WRITTEN_BOOK)) {
                                return new ExploitDetails(this.user, packetName, "tried to use book but real item is " + ((realItem == null) ? "null" : realItem.getType().name()), false, true);
                            }
                        }
                        if ((item instanceof ItemFireworks || item instanceof ItemFireworksCharge) && (realItem == null || (realItem.getType() != Material.valueOf("FIREWORK") && realItem.getType() != Material.valueOf("FIREWORK_CHARGE")))) {
                            return new ExploitDetails(this.user, packetName, "tried to use firework but real item is " + ((realItem == null) ? "null" : realItem.getType().name()), false, true);
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
            catch (Exception ex2) {
                return new ExploitDetails(this.user, packetName, "exception " + ex2.getMessage(), false, false);
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
        else if (packet0.getClass().getSimpleName().equals("PacketPlayInBlockPlace")) {
            if (Settings.IMP.DEBUG) {
                SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", packetName, player.getName());
            }
            ItemStack packetItem2 = ((PacketPlayInBlockPlace)packet0).getItemStack();
            if (packetItem2 == null) {
                try {
                    final Field itemField2 = PacketPlayInBlockPlace.class.getDeclaredField("e");
                    itemField2.setAccessible(true);
                    packetItem2 = (ItemStack)itemField2.get(packet0);
                }
                catch (Exception ex2) {
                    ex2.printStackTrace();
                }
            }
            if (packetItem2 != null) {
                final Item item2 = packetItem2.getItem();
                if (item2 instanceof ItemWrittenBook || item2 instanceof ItemBookAndQuill) {
                    this.lastBookplace = System.currentTimeMillis();
                    final org.bukkit.inventory.ItemStack realItem2 = player.getItemInHand();
                    if (realItem2 == null || (realItem2.getType() != Material.WRITTEN_BOOK && realItem2.getType() != Material.valueOf("BOOK_AND_QUILL"))) {
                        return new ExploitDetails(this.user, packetName, "placing book but not holding it", false, true);
                    }
                }
                if (item2 instanceof ItemFireworks || item2 instanceof ItemFireworksCharge) {
                    final org.bukkit.inventory.ItemStack realItem2 = player.getItemInHand();
                    if (realItem2 == null || (realItem2.getType() != Material.valueOf("FIREWORK_CHARGE") && realItem2.getType() != Material.valueOf("FIREWORK"))) {
                        return new ExploitDetails(this.user, packetName, "placing firework but not holding it", false, true);
                    }
                }
                final ExploitDetails nbtTags = this.checkNbtTags(packetName, packetItem2);
                if (nbtTags != null) {
                    return nbtTags;
                }
                final org.bukkit.inventory.ItemStack bukkitItem3 = CraftItemStack.asBukkitCopy(packetItem2);
                if ((bukkitItem3.getType() == Material.CHEST || bukkitItem3.getType() == Material.HOPPER) && bukkitItem3.hasItemMeta() && bukkitItem3.getItemMeta().toString().getBytes().length > 262144) {
                    return new ExploitDetails(this.user, packetName, "Too big chest data", false, false);
                }
            }
        }
        else if (packet0.getClass().getSimpleName().equals("PacketPlayInCustomPayload")) {
            final PacketPlayInCustomPayload payload = (PacketPlayInCustomPayload)packet0;
            final String channel = payload.c();
            if (channel != null && payload.e() != null) {
                try {
                    final ByteBuf data = Unpooled.wrappedBuffer(payload.e());
                    if (data.capacity() > Settings.IMP.PAYLOAD_SETTINGS.MAX_CAPACITY) {
                        return new ExploitDetails(this.user, packetName, "invalid bytebuf capacity", false, true, "set \"payload-settings.max-capacity\" to higher value in settings.yml");
                    }
                    Label_1773: {
                        if (channel.equals("MC|BEdit") || channel.equals("MC|BSign") || channel.equals("minecraft:bedit") || channel.equals("minecraft:bsign")) {
                            if (Settings.IMP.DEBUG) {
                                SpigotGuardLogger.log(Level.INFO, "Received {0} from {1}", packetName, player.getName());
                            }
                            try {
                                final PacketDataSerializer serializer = new PacketDataSerializer(data);
                                final ItemStack item3 = serializer.c();
                                if (item3 != null) {
                                    if (System.currentTimeMillis() - this.lastBookplace > 60000L) {
                                        return new ExploitDetails(this.user, packetName, "book sign, but no book used", false, false);
                                    }
                                    if (Settings.IMP.PAYLOAD_SETTINGS.FAIL_WHEN_NOT_HOLDING_BOOK && !player.getInventory().contains(Material.valueOf("BOOK_AND_QUILL")) && !player.getInventory().contains(Material.WRITTEN_BOOK)) {
                                        return new ExploitDetails(this.user, packetName, "book interact, but no book exists", false, true);
                                    }
                                    final ExploitDetails nbtTags3 = this.checkNbtTags(packetName, item3);
                                    if (nbtTags3 != null) {
                                        return nbtTags3;
                                    }
                                }
                                break Label_1773;
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
                            catch (Exception ex4) {}
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
                catch (NullPointerException ex5) {}
            }
        }
        else if (packet0.getClass().getSimpleName().equals("PacketPlayInFlying")) {
            final PacketPlayInFlying packet2 = (PacketPlayInFlying)packet0;
            final double x = packet2.c();
            final double y = packet2.d();
            final double z = packet2.e();
            if (x >= Double.MAX_VALUE || y > Double.MAX_VALUE || z >= Double.MAX_VALUE) {
                return new ExploitDetails(this.user, packetName, "Double.MAX_VALUE position", false, true);
            }
            final float yaw = packet2.g();
            final float pitch = packet2.h();
            if (yaw == Float.NEGATIVE_INFINITY || pitch == Float.NEGATIVE_INFINITY || yaw >= Float.MAX_VALUE || pitch >= Float.MAX_VALUE) {
                return new ExploitDetails(this.user, packetName, "invalid float position", false, true);
            }
        }
        else if (packet0 instanceof PacketPlayInHeldItemSlot) {
            final int slot2 = ((PacketPlayInHeldItemSlot)packet0).c();
            if (slot2 >= 36 || slot2 < 0) {
                return new ExploitDetails(this.user, packetName, "invalid held item slot", false, true);
            }
        }
        else if (packet0.getClass().getSimpleName().equals("PacketPlayInSteerVehicle")) {
            final PacketPlayInSteerVehicle p = (PacketPlayInSteerVehicle)packet0;
            if (p.c() >= Float.MAX_VALUE || p.d() >= Float.MAX_VALUE) {
                return new ExploitDetails(this.user, packetName, "invalid vehicle movement", false, true);
            }
        }
        final LimitablePacket packetLimit = this.hasReachedLimit(packetName);
        if (packetLimit != null) {
            return new ExploitDetails(this.user, packetName, "packet limit exceed", packetLimit.isCancelOnly(), false, "increase packet limit of " + packetName + " in config.yml or completely remove it");
        }
        return null;
    }
    
    private LimitablePacket hasReachedLimit(final String packetName) {
        if (!PacketDecoder_1_7.LIMITATION.isLimitable(packetName)) {
            return null;
        }
        final LimitablePacket limitablePacket = PacketDecoder_1_7.LIMITATION.getLimit(packetName);
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
        if (Settings.IMP.DEBUG) {
            SpigotGuardLogger.log(Level.INFO, packet + " -> " + item.getName() + " -> " + tagCompound.toString(), new Object[0]);
        }
        final Set<String> keys = (Set<String>)tagCompound.c();
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
                if (page.contains("wveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5") || page.equalsIgnoreCase("wveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5")) {
                    itemStack.setTag(new NBTTagCompound());
                    return new ExploitDetails(this.user, packet, "crash client detected (invalid book page data)", false, true);
                }
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
            if (tagCompound.hasKey("SkullOwner")) {
                final NBTTagCompound skullOwner = tagCompound.getCompound("SkullOwner");
                if (skullOwner.hasKey("Properties")) {
                    final NBTTagCompound properties = skullOwner.getCompound("Properties");
                    if (properties.hasKey("textures")) {
                        final NBTTagList textures = properties.getList("textures", 10);
                        for (int j = 0; j < textures.size(); ++j) {
                            final NBTTagCompound entry = textures.get(j);
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
                                final Matcher matcher = PacketDecoder_1_7.URL_MATCHER.matcher(decoded);
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
                for (int k = 0; k < list.size(); ++k) {
                    final String content = list.getString(k);
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
