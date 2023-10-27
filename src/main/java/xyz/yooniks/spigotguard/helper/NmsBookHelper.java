package xyz.yooniks.spigotguard.helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public final class NmsBookHelper {
  public static void openBook(Player paramPlayer, ItemStack paramItemStack, boolean paramBoolean) {
    try {
      (new Object[2])[0] = nmsCopy(paramItemStack);
      false;
      false;
    } catch (Exception exception) {
      throw new UnsupportedVersionException(exception);
    } 
  }
  
  public static Class<?> getNmsClass(String paramString, boolean paramBoolean) {
    try {
      return Class.forName("net.minecraft.server." + version + "." + paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      if (paramBoolean)
        throw new RuntimeException("Cannot find NMS class " + paramString, classNotFoundException); 
      return null;
    } 
  }
  
  public static void setPages(BookMeta paramBookMeta, BaseComponent[][] paramArrayOfBaseComponent) {
    try {
      List list = (List)craftMetaBookField.get(paramBookMeta);
      if (list != null)
        list.clear(); 
      BaseComponent[][] arrayOfBaseComponent = paramArrayOfBaseComponent;
      int i = arrayOfBaseComponent.length;
      byte b = 0;
      while (b < i) {
        BaseComponent[] arrayOfBaseComponent1 = arrayOfBaseComponent[b];
        String str = (arrayOfBaseComponent1 != null) ? ComponentSerializer.toString(arrayOfBaseComponent1) : "";
        false;
        BaseComponent[] arrayOfBaseComponent2 = (arrayOfBaseComponent1 != null) ? arrayOfBaseComponent1 : jsonToComponents("");
        str = ComponentSerializer.toString(arrayOfBaseComponent2);
        b++;
        false;
      } 
      false;
    } catch (Exception exception) {
      throw new UnsupportedVersionException(exception);
    } 
  }
  
  private static Class<?> getCraftClass(String paramString) {
    try {
      return Class.forName("org.bukkit.craftbukkit." + version + "." + paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new RuntimeException("Cannot find CraftBukkit class at path: " + paramString, classNotFoundException);
    } 
  }
  
  public static Object nmsCopy(ItemStack paramItemStack) throws IllegalAccessException, InvocationTargetException {
    return craftItemStackAsNMSCopy.invoke((Object)null, new Object[] { paramItemStack });
  }
  
  public static Object toNms(Player paramPlayer) throws IllegalAccessException, InvocationTargetException {
    return craftPlayerGetHandle.invoke(paramPlayer, new Object[0]);
  }
  
  static {
    Pattern pattern = Pattern.compile("v([0-9]+)_([0-9]+)");
    Matcher matcher = pattern.matcher(version);
    if (matcher.find()) {
      i = Integer.parseInt(matcher.group(1));
      j = Integer.parseInt(matcher.group(2));
      false;
    } else {
      throw new IllegalStateException("Cannot parse version \"" + version + "\", make sure it follows \"v<major>_<minor>...\"");
    } 
    false;
    doubleHands = (i <= 1 && j >= 9);
    try {
      craftMetaBookClass = getCraftClass("inventory.CraftMetaBook");
      craftMetaBookField = craftMetaBookClass.getDeclaredField("pages");
      craftMetaBookField.setAccessible(true);
      Method method = null;
      try {
        method = craftMetaBookClass.getDeclaredMethod("internalAddPage", new Class[] { String.class });
        method.setAccessible(true);
        false;
      } catch (NoSuchMethodException noSuchMethodException) {}
      craftMetaBookInternalAddPageMethod = method;
      Class<?> clazz1 = getNmsClass("IChatBaseComponent$ChatSerializer", false);
      if (clazz1 == null)
        clazz1 = getNmsClass("ChatSerializer"); 
      chatSerializerA = clazz1.getDeclaredMethod("a", new Class[] { String.class });
      Class<?> clazz2 = getCraftClass("entity.CraftPlayer");
      craftPlayerGetHandle = clazz2.getMethod("getHandle", new Class[0]);
      Class<?> clazz3 = getNmsClass("EntityPlayer");
      Class<?> clazz4 = getNmsClass("ItemStack");
      if (doubleHands) {
        Method method1;
        Class<?> clazz = getNmsClass("EnumHand");
        try {
          method1 = clazz3.getMethod("a", new Class[] { clazz4, clazz });
          false;
        } catch (NoSuchMethodException noSuchMethodException) {
          method1 = clazz3.getMethod("openBook", new Class[] { clazz4, clazz });
        } 
        entityPlayerOpenBook = method1;
        hands = clazz.getEnumConstants();
        false;
      } else {
        entityPlayerOpenBook = clazz3.getMethod("openBook", new Class[] { clazz4 });
        hands = null;
      } 
      Class<?> clazz5 = getCraftClass("inventory.CraftItemStack");
      craftItemStackAsNMSCopy = clazz5.getMethod("asNMSCopy", new Class[] { ItemStack.class });
      Class<?> clazz6 = getNmsClass("ItemStack");
      Class<?> clazz7 = getNmsClass("NBTTagCompound");
      nmsItemStackSave = clazz6.getMethod("save", new Class[] { clazz7 });
      nbtTagCompoundConstructor = clazz7.getConstructor(new Class[0]);
      false;
    } catch (Exception exception) {
      throw new IllegalStateException("Cannot initiate reflections for " + version, exception);
    } 
  }
  
  public static BaseComponent[] itemToComponents(ItemStack paramItemStack) {
    return jsonToComponents(itemToJson(paramItemStack));
  }
  
  public static Class<?> getNmsClass(String paramString) {
    return getNmsClass(paramString, false);
  }
  
  private static String itemToJson(ItemStack paramItemStack) {
    try {
      Object object1 = nmsCopy(paramItemStack);
      Object object2 = nbtTagCompoundConstructor.newInstance(new Object[0]);
      Object object3 = nmsItemStackSave.invoke(object1, new Object[] { object2 });
      return object3.toString();
    } catch (Exception exception) {
      throw new UnsupportedVersionException(exception);
    } 
  }
  
  public static BaseComponent[] jsonToComponents(String paramString) {
    return new BaseComponent[] { (BaseComponent)new TextComponent(paramString) };
  }
  
  static {
    int i;
    int j;
  }
  
  private static final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
  
  private static final Method entityPlayerOpenBook;
  
  private static final Method craftMetaBookInternalAddPageMethod;
  
  private static final Method craftItemStackAsNMSCopy;
  
  private static final Method chatSerializerA;
  
  private static final Class<?> craftMetaBookClass;
  
  private static final Constructor<?> nbtTagCompoundConstructor;
  
  private static final Method craftPlayerGetHandle;
  
  private static final Field craftMetaBookField;
  
  private static final Method nmsItemStackSave;
  
  private static final boolean doubleHands;
  
  private static final Object[] hands;
  
  public static class UnsupportedVersionException extends RuntimeException {
    private final String version = NmsBookHelper.version;
    
    public UnsupportedVersionException(Exception param1Exception) {
      super("Error while executing reflections, submit to developers the following log (version: " + NmsBookHelper.version + ")", param1Exception);
    }
    
    public String getVersion() {
      return this.version;
    }
  }
}