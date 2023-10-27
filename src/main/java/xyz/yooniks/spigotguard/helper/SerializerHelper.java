package xyz.yooniks.spigotguard.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public final class SerializerHelper {
  public static String toString(List<?> paramList) throws IllegalStateException {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);
      bukkitObjectOutputStream.writeObject(paramList);
      String str = Base64Coder.encodeLines(byteArrayOutputStream.toByteArray());
      byteArrayOutputStream.close();
      bukkitObjectOutputStream.close();
      return str;
    } catch (Exception exception) {
      throw new IllegalStateException("Unable to save itemstack array", exception);
    } 
  }
  
  public static List<?> fromString(String paramString) throws IOException {
    try {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64Coder.decodeLines(paramString));
      BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);
      List<?> list = (List)bukkitObjectInputStream.readObject();
      byteArrayInputStream.close();
      bukkitObjectInputStream.close();
      return list;
    } catch (ClassNotFoundException classNotFoundException) {
      throw new IOException("Unable to read class type", classNotFoundException);
    } 
  }
}