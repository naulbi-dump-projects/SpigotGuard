package xyz.upperlevel.spigot.book;

import org.bukkit.inventory.meta.*;
import java.util.*;
import net.md_5.bungee.chat.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import net.md_5.bungee.api.chat.*;
import java.lang.reflect.*;
import org.bukkit.*;
import java.util.regex.*;

public final class NmsBookHelper
{
    private static final String version;
    private static final boolean doubleHands;
    private static final Class<?> craftMetaBookClass;
    private static final Field craftMetaBookField;
    private static final Method chatSerializerA;
    private static final Method craftPlayerGetHandle;
    private static final Method entityPlayerOpenBook;
    private static final Object[] hands;
    private static final Method nmsItemStackSave;
    private static final Constructor<?> nbtTagCompoundConstructor;
    private static final Method craftItemStackAsNMSCopy;
    
    public static void setPages(final BookMeta meta, final BaseComponent[][] components) {
        try {
            final List<Object> pages = (List<Object>)NmsBookHelper.craftMetaBookField.get(meta);
            pages.clear();
            for (final BaseComponent[] c : components) {
                final String json = ComponentSerializer.toString(c);
                pages.add(NmsBookHelper.chatSerializerA.invoke(null, json));
            }
        }
        catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }
    
    public static void openBook(final Player player, final ItemStack book, final boolean offHand) {
        try {
            if (NmsBookHelper.doubleHands) {
                NmsBookHelper.entityPlayerOpenBook.invoke(toNms(player), nmsCopy(book), NmsBookHelper.hands[offHand]);
            }
            else {
                NmsBookHelper.entityPlayerOpenBook.invoke(toNms(player), nmsCopy(book));
            }
        }
        catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }
    
    public static BaseComponent[] itemToComponents(final ItemStack item) {
        return jsonToComponents(itemToJson(item));
    }
    
    public static BaseComponent[] jsonToComponents(final String json) {
        return new BaseComponent[] { (BaseComponent)new TextComponent(json) };
    }
    
    private static String itemToJson(final ItemStack item) {
        try {
            final Object nmsItemStack = nmsCopy(item);
            final Object emptyTag = NmsBookHelper.nbtTagCompoundConstructor.newInstance(new Object[0]);
            final Object json = NmsBookHelper.nmsItemStackSave.invoke(nmsItemStack, emptyTag);
            return json.toString();
        }
        catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }
    
    public static Object toNms(final Player player) throws InvocationTargetException, IllegalAccessException {
        return NmsBookHelper.craftPlayerGetHandle.invoke(player, new Object[0]);
    }
    
    public static Object nmsCopy(final ItemStack item) throws InvocationTargetException, IllegalAccessException {
        return NmsBookHelper.craftItemStackAsNMSCopy.invoke(null, item);
    }
    
    public static Class<?> getNmsClass(final String className, final boolean required) {
        try {
            return Class.forName("net.minecraft.server." + NmsBookHelper.version + "." + className);
        }
        catch (ClassNotFoundException e) {
            if (required) {
                throw new RuntimeException("Cannot find NMS class " + className, e);
            }
            return null;
        }
    }
    
    public static Class<?> getNmsClass(final String className) {
        return getNmsClass(className, false);
    }
    
    private static Class<?> getCraftClass(final String path) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + NmsBookHelper.version + "." + path);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find CraftBukkit class at path: " + path, e);
        }
    }
    
    static {
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        final Pattern pattern = Pattern.compile("v([0-9]+)_([0-9]+)");
        final Matcher m = pattern.matcher(NmsBookHelper.version);
        if (m.find()) {
            final int major = Integer.parseInt(m.group(1));
            final int minor = Integer.parseInt(m.group(2));
            doubleHands = (major <= 1 && minor >= 9);
            try {
                craftMetaBookClass = getCraftClass("inventory.CraftMetaBook");
                (craftMetaBookField = NmsBookHelper.craftMetaBookClass.getDeclaredField("pages")).setAccessible(true);
                Class<?> chatSerializer = getNmsClass("IChatBaseComponent$ChatSerializer", false);
                if (chatSerializer == null) {
                    chatSerializer = getNmsClass("ChatSerializer");
                }
                chatSerializerA = chatSerializer.getDeclaredMethod("a", String.class);
                final Class<?> craftPlayerClass = getCraftClass("entity.CraftPlayer");
                craftPlayerGetHandle = craftPlayerClass.getMethod("getHandle", (Class<?>[])new Class[0]);
                final Class<?> entityPlayerClass = getNmsClass("EntityPlayer");
                final Class<?> itemStackClass = getNmsClass("ItemStack");
                if (NmsBookHelper.doubleHands) {
                    final Class<?> enumHandClass = getNmsClass("EnumHand");
                    Method openBookMethod;
                    try {
                        openBookMethod = entityPlayerClass.getMethod("a", itemStackClass, enumHandClass);
                    }
                    catch (NoSuchMethodException e2) {
                        openBookMethod = entityPlayerClass.getMethod("openBook", itemStackClass, enumHandClass);
                    }
                    entityPlayerOpenBook = openBookMethod;
                    hands = (Object[])enumHandClass.getEnumConstants();
                }
                else {
                    entityPlayerOpenBook = entityPlayerClass.getMethod("openBook", itemStackClass);
                    hands = null;
                }
                final Class<?> craftItemStackClass = getCraftClass("inventory.CraftItemStack");
                craftItemStackAsNMSCopy = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
                final Class<?> nmsItemStackClazz = getNmsClass("ItemStack");
                final Class<?> nbtTagCompoundClazz = getNmsClass("NBTTagCompound");
                nmsItemStackSave = nmsItemStackClazz.getMethod("save", nbtTagCompoundClazz);
                nbtTagCompoundConstructor = nbtTagCompoundClazz.getConstructor((Class<?>[])new Class[0]);
            }
            catch (Exception e) {
                throw new IllegalStateException("Cannot initiate reflections for " + NmsBookHelper.version, e);
            }
            return;
        }
        throw new IllegalStateException("Cannot parse version \"" + NmsBookHelper.version + "\", make sure it follows \"v<major>_<minor>...\"");
    }
    
    public static class UnsupportedVersionException extends RuntimeException
    {
        private final String version;
        
        public UnsupportedVersionException(final Exception e) {
            super("Error while executing reflections, submit to developers the following log (version: " + NmsBookHelper.version + ")", e);
            this.version = NmsBookHelper.version;
        }
    }
}
