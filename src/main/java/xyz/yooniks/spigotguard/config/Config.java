package xyz.yooniks.spigotguard.config;

import java.util.logging.*;
import xyz.yooniks.spigotguard.logger.*;
import java.nio.charset.*;
import org.bukkit.configuration.file.*;
import java.io.*;
import org.bukkit.configuration.*;
import java.nio.file.*;
import java.lang.invoke.*;
import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;

public class Config
{
    public Config() {
        this.save(new ArrayList<String>(), this.getClass(), this, 0);
    }
    
    private void set(final String key, Object value) {
        final String[] split = key.split("\\.");
        final Object instance = this.getInstance(split, this.getClass());
        if (instance != null) {
            final Field field = this.getField(split, instance);
            if (field != null) {
                try {
                    if (field.getAnnotation(Final.class) != null) {
                        return;
                    }
                    if (field.getType() == String.class && !(value instanceof String)) {
                        value += "";
                    }
                    field.set(instance, value);
                    return;
                }
                catch (IllegalAccessException ex) {}
                catch (IllegalArgumentException ex2) {}
            }
        }
        SpigotGuardLogger.log(Level.WARNING, "Failed to set config option: {0}: {1} | {2} ", key, value, instance);
    }
    
    public boolean load(final File file) {
        if (!file.exists()) {
            return false;
        }
        YamlConfiguration yml;
        try {
            final InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            try {
                yml = YamlConfiguration.loadConfiguration((Reader)reader);
                reader.close();
            }
            catch (Throwable t) {
                try {
                    reader.close();
                }
                catch (Throwable t2) {
                    t.addSuppressed(t2);
                }
                throw t;
            }
        }
        catch (IOException ex) {
            SpigotGuardLogger.exception("Unable to load config.", ex);
            return false;
        }
        this.set((ConfigurationSection)yml, "");
        return true;
    }
    
    public void set(final ConfigurationSection yml, final String oldPath) {
        for (final String key : yml.getKeys(false)) {
            final Object value = yml.get(key);
            final String newPath = oldPath + (oldPath.isEmpty() ? "" : ".") + key;
            if (value instanceof ConfigurationSection) {
                this.set((ConfigurationSection)value, newPath);
            }
            else {
                this.set(newPath, value);
            }
        }
    }
    
    public void save(final File file) {
        try {
            final File parent = file.getParentFile();
            if (parent != null) {
                file.getParentFile().mkdirs();
            }
            final Path configFile = file.toPath();
            final Path tempCfg = new File(file.getParentFile(), "__tmpcfg").toPath();
            final List<String> lines = new ArrayList<String>();
            this.save(lines, this.getClass(), this, 0);
            Files.write(tempCfg, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            try {
                Files.move(tempCfg, configFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException e2) {
                Files.move(tempCfg, configFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            SpigotGuardLogger.exception("Error: ", e);
        }
    }
    
    private String toYamlString(final Object value, final String spacing) {
        if (value instanceof List) {
            final Collection<?> listValue = (Collection<?>)value;
            if (listValue.isEmpty()) {
                return "[]";
            }
            final StringBuilder m = new StringBuilder();
            for (final Object obj : listValue) {
                m.append(System.lineSeparator()).append(spacing).append("- ").append(this.toYamlString(obj, spacing));
            }
            return m.toString();
        }
        else {
            if (!(value instanceof String)) {
                return (value != null) ? value.toString() : "null";
            }
            final String stringValue = (String)value;
            if (stringValue.isEmpty()) {
                return "''";
            }
            return "\"" + stringValue + "\"";
        }
    }
    
    private void save(final List<String> lines, final Class clazz, final Object instance, final int indent) {
        try {
            final String spacing = this.repeat(" ", indent);
            for (final Field field : clazz.getFields()) {
                if (field.getAnnotation(Ignore.class) == null) {
                    final Class<?> current = field.getType();
                    if (field.getAnnotation(Ignore.class) == null) {
                        Comment comment = field.getAnnotation(Comment.class);
                        if (comment != null) {
                            for (final String commentLine : comment.value()) {
                                lines.add(spacing + "# " + commentLine);
                            }
                        }
                        final Create create = field.getAnnotation(Create.class);
                        if (create != null) {
                            Object value = field.get(instance);
                            this.setAccessible(field);
                            if (indent == 0) {
                                lines.add("");
                            }
                            comment = current.getAnnotation(Comment.class);
                            if (comment != null) {
                                for (final String commentLine2 : comment.value()) {
                                    lines.add(spacing + "# " + commentLine2);
                                }
                            }
                            lines.add(spacing + this.toNodeName(current.getSimpleName()) + ":");
                            if (value == null) {
                                field.set(instance, value = current.newInstance());
                            }
                            this.save(lines, current, value, indent + 2);
                        }
                        else {
                            lines.add(spacing + this.toNodeName(field.getName() + ": ") + this.toYamlString(field.get(instance), spacing));
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            SpigotGuardLogger.exception("Error:", e);
        }
    }
    
    private Field getField(final String[] split, final Object instance) {
        try {
            final Field field = instance.getClass().getField(this.toFieldName(split[split.length - 1]));
            this.setAccessible(field);
            return field;
        }
        catch (Exception e) {
            SpigotGuardLogger.log(Level.WARNING, "Invalid config field: {0} for {1}", String.join(".", (CharSequence[])split), this.toNodeName(instance.getClass().getSimpleName()));
            return null;
        }
    }
    
    private Object getInstance(String[] split, final Class root) {
        try {
            Class<?> clazz = (root == null) ? MethodHandles.lookup().lookupClass() : root;
            Object instance = this;
        Label_0187:
            while (split.length > 0) {
                switch (split.length) {
                    case 1: {
                        return instance;
                    }
                    default: {
                        Class found = null;
                        final Class<?>[] declaredClasses;
                        final Class<?>[] classes = declaredClasses = clazz.getDeclaredClasses();
                        for (final Class current : declaredClasses) {
                            if (current.getSimpleName().equalsIgnoreCase(this.toFieldName(split[0]))) {
                                found = current;
                                break;
                            }
                        }
                        try {
                            final Field instanceField = clazz.getDeclaredField(this.toFieldName(split[0]));
                            this.setAccessible(instanceField);
                            Object value = instanceField.get(instance);
                            if (value == null) {
                                value = found.newInstance();
                                instanceField.set(instance, value);
                            }
                            clazz = (Class<?>)found;
                            instance = value;
                            split = Arrays.copyOfRange(split, 1, split.length);
                            continue;
                        }
                        catch (Exception ex) {
                            return null;
                        }
                        break Label_0187;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String toFieldName(final String node) {
        return node.toUpperCase().replaceAll("-", "_");
    }
    
    private String toNodeName(final String field) {
        return field.toLowerCase().replace("_", "-");
    }
    
    private void setAccessible(final Field field) throws Exception {
        field.setAccessible(true);
        field.setAccessible(true);
        final int modifiers = field.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            try {
                final Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, modifiers & 0xFFFFFFEF);
            }
            catch (NoSuchFieldException e) {
                final Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", Boolean.TYPE);
                getDeclaredFields0.setAccessible(true);
                final Field[] array;
                final Field[] fields = array = (Field[])getDeclaredFields0.invoke(Field.class, false);
                Block_5: {
                    for (final Field classField : array) {
                        if ("modifiers".equals(classField.getName())) {
                            break Block_5;
                        }
                    }
                    return;
                }
                final Field classField;
                classField.setAccessible(true);
                classField.set(field, modifiers & 0xFFFFFFEF);
            }
        }
    }
    
    private String repeat(final String s, final int n) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            sb.append(s);
        }
        return sb.toString();
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.TYPE })
    public @interface Ignore {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.TYPE })
    public @interface Comment {
        String[] value();
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    public @interface Final {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    public @interface Create {
    }
}
