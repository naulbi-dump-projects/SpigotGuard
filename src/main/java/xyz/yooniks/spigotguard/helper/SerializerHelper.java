package xyz.yooniks.spigotguard.helper;

import java.util.*;
import org.yaml.snakeyaml.external.biz.base64Coder.*;
import org.bukkit.util.io.*;
import java.io.*;

public final class SerializerHelper
{
    private SerializerHelper() {
    }
    
    public static String toString(final List<?> items) throws IllegalStateException {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream((OutputStream)outputStream);
            dataOutput.writeObject((Object)items);
            final String encoded = Base64Coder.encodeLines(outputStream.toByteArray());
            outputStream.close();
            dataOutput.close();
            return encoded;
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to save itemstack array", e);
        }
    }
    
    public static List<?> fromString(final String data) throws IOException {
        try {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            final BukkitObjectInputStream dataInput = new BukkitObjectInputStream((InputStream)inputStream);
            final List<?> read = (List<?>)dataInput.readObject();
            inputStream.close();
            dataInput.close();
            return read;
        }
        catch (ClassNotFoundException e) {
            throw new IOException("Unable to read class type", e);
        }
    }
}
