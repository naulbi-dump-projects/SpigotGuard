package xyz.yooniks.spigotguard.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.*;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;

public class Config {
  private Field getField(String[] var1, Object var2) {
    try {
      Field var3 = var2.getClass().getField(this.toFieldName(var1[var1.length - 1]));
      this.setAccessible(var3);
      return var3;
    } catch (Exception var4) {
      SpigotGuardLogger.log(Level.WARNING, "Invalid config field: {0} for {1}", new Object[]{String.join(".", var1), this.toNodeName(var2.getClass().getSimpleName())});
      return null;
    }
  }

  private String toFieldName(String var1) {
    return var1.toUpperCase().replaceAll("-", "_");
  }

  private Object getInstance(String[] var1, Class var2) {
    boolean var10000;
    try {
      Class var3 = var2 == null ? MethodHandles.lookup().lookupClass() : var2;
      Object var4 = this;

      while(var1.length > 0) {
        Class var5;
        Class[] var7;
        int var8;
        int var9;
        switch(var1.length) {
          case 1:
            return var4;
          default:
            var5 = null;
            Class[] var6 = var3.getDeclaredClasses();
            var7 = var6;
            var8 = var6.length;
            var9 = 0;
        }

        while(true) {
          if (var9 < var8) {
            Class var10 = var7[var9];
            if (!Integer.valueOf(this.toFieldName(var1[0]).toUpperCase().hashCode()).equals(var10.getSimpleName().toUpperCase().hashCode())) {
              ++var9;
              var10000 = false;
              continue;
            }

            var5 = var10;
            var10000 = false;
          }

          try {
            Field var13 = var3.getDeclaredField(this.toFieldName(var1[0]));
            this.setAccessible(var13);
            Object var14 = var13.get(var4);
            if (var14 == null) {
              var14 = var5.newInstance();
              var13.set(var4, var14);
            }

            var3 = var5;
            var4 = var14;
            var1 = (String[])Arrays.copyOfRange(var1, 1, var1.length);
          } catch (Exception var11) {
            return null;
          }

          var10000 = false;
          break;
        }
      }
    } catch (Exception var12) {
      var12.printStackTrace();
      return null;
    }

    var10000 = false;
    return null;
  }

  public void set(ConfigurationSection var1, String var2) {
    Iterator var3 = var1.getKeys(false).iterator();

    while(var3.hasNext()) {
      String var4 = (String)var3.next();
      Object var5 = var1.get(var4);
      String var6 = var2 + (var2.isEmpty() ? "" : ".") + var4;
      if (var5 instanceof ConfigurationSection) {
        this.set((ConfigurationSection)var5, var6);
      } else {
        this.set(var6, var5);
        boolean var10000 = false;
      }
    }

  }

  private String toYamlString(Object var1, String var2) {
    if (!(var1 instanceof List)) {
      if (var1 instanceof String) {
        String var7 = (String)var1;
        var7.isEmpty();
        return "\"" + var7 + "\"";
      } else {
        return var1 != null ? var1.toString() : "null";
      }
    } else {
      Collection var3 = (Collection)var1;
      var3.isEmpty();
      StringBuilder var4 = new StringBuilder();

      boolean var10002;
      for(Iterator var5 = var3.iterator(); var5.hasNext(); var10002 = false) {
        Object var6 = var5.next();
        var4.append(System.lineSeparator()).append(var2).append("- ").append(this.toYamlString(var6, var2));
      }

      return String.valueOf(var4);
    }
  }

  private void setAccessible(Field var1) throws Exception {
    var1.setAccessible(true);
    var1.setAccessible(true);
    int var2 = var1.getModifiers();
    if (Modifier.isFinal(var2)) {
      boolean var10000;
      try {
        Field var3 = Field.class.getDeclaredField("modifiers");
        var3.setAccessible(true);
        var3.setInt(var1, var2 & -17);
      } catch (NoSuchFieldException var10) {
        Method var4 = Class.class.getDeclaredMethod("getDeclaredFields0", Boolean.TYPE);
        var4.setAccessible(true);
        Field[] var5 = (Field[])var4.invoke(Field.class, false);
        Field[] var6 = var5;
        int var7 = var5.length;

        for(int var8 = 0; var8 < var7; var10000 = false) {
          Field var9 = var6[var8];
          if ("modifiers".equals(var9.getName())) {
            var9.setAccessible(true);
            var9.set(var1, var2 & -17);
            return;
          }

          ++var8;
        }

        return;
      }

      var10000 = false;
    }

  }

  private void save(List var1, Class var2, Object var3, int var4) {
    boolean var10000;
    try {
      String var5 = this.repeat(" ", var4);
      Field[] var6 = var2.getFields();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; var10000 = false) {
        Field var9 = var6[var8];
        if (var9.getAnnotation(xyz.yooniks.spigotguard.config.Config.Ignore.class) != null) {
          var10000 = false;
        } else {
          Class var10 = var9.getType();
          if (var9.getAnnotation(xyz.yooniks.spigotguard.config.Config.Ignore.class) != null) {
            var10000 = false;
          } else {
            xyz.yooniks.spigotguard.config.Config.Comment var11 = (xyz.yooniks.spigotguard.config.Config.Comment)var9.getAnnotation(xyz.yooniks.spigotguard.config.Config.Comment.class);
            boolean var10001;
            if (var11 != null) {
              String[] var12 = var11.value();
              int var13 = var12.length;

              for(int var14 = 0; var14 < var13; var10001 = false) {
                String var15 = var12[var14];
                var1.add(var5 + "# " + var15);
                ++var14;
              }
            }

            xyz.yooniks.spigotguard.config.Config.Create var19 = (xyz.yooniks.spigotguard.config.Config.Create)var9.getAnnotation(xyz.yooniks.spigotguard.config.Config.Create.class);
            if (var19 != null) {
              Object var20 = var9.get(var3);
              this.setAccessible(var9);
              if (var4 == 0) {
                var1.add("");
              }

              var11 = (xyz.yooniks.spigotguard.config.Config.Comment)var10.getAnnotation(xyz.yooniks.spigotguard.config.Config.Comment.class);
              if (var11 != null) {
                String[] var21 = var11.value();
                int var22 = var21.length;

                for(int var16 = 0; var16 < var22; var10001 = false) {
                  String var17 = var21[var16];
                  var1.add(var5 + "# " + var17);
                  ++var16;
                }
              }

              var1.add(var5 + this.toNodeName(var10.getSimpleName()) + ":");
              if (var20 == null) {
                var9.set(var3, var20 = var10.newInstance());
              }

              this.save(var1, var10, var20, var4 + 2);
              var10001 = false;
            } else {
              var1.add(var5 + this.toNodeName(var9.getName() + ": ") + this.toYamlString(var9.get(var3), var5));
            }
          }
        }

        ++var8;
      }
    } catch (Exception var18) {
      SpigotGuardLogger.exception("Error:", var18);
      return;
    }

    var10000 = false;
  }

  public void save(File var1) {
    try {
      label29: {
        File var2 = var1.getParentFile();
        if (var2 != null) {
          var1.getParentFile().mkdirs();
        }

        Path var3 = var1.toPath();
        Path var4 = (new File(var1.getParentFile(), "__tmpcfg")).toPath();
        ArrayList var5 = new ArrayList();
        this.save(var5, this.getClass(), this, 0);
        Files.write(var4, var5, StandardCharsets.UTF_8, StandardOpenOption.CREATE);

        try {
          Files.move(var4, var3, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException var7) {
          Files.move(var4, var3, StandardCopyOption.REPLACE_EXISTING);
          break label29;
        }

        boolean var10002 = false;
      }
    } catch (IOException var8) {
      SpigotGuardLogger.exception("Error: ", var8);
      return;
    }

    boolean var10001 = false;
  }

  private String repeat(String var1, int var2) {
    StringBuilder var3 = new StringBuilder();

    boolean var10001;
    for(int var4 = 0; var4 < var2; var10001 = false) {
      var3.append(var1);
      ++var4;
    }

    return String.valueOf(var3);
  }

  public Config() {
    this.save(new ArrayList(), this.getClass(), this, 0);
  }

  private void set(String var1, Object var2) {
    String[] var3 = var1.split("\\.");
    Object var4 = this.getInstance(var3, this.getClass());
    if (var4 != null) {
      Field var5 = this.getField(var3, var4);
      if (var5 != null) {
        var5.getAnnotation(xyz.yooniks.spigotguard.config.Config.Final.class);

        try {
          if (var5.getType() == String.class && !(var2 instanceof String)) {
            var2 = var2 + "";
          }

          var5.set(var4, var2);
          return;
        } catch (IllegalArgumentException | IllegalAccessException var7) {
        }
      }
    }

    SpigotGuardLogger.log(Level.WARNING, "Failed to set config option: {0}: {1} | {2} ", new Object[]{var1, var2, var4});
  }

  private String toNodeName(String var1) {
    return var1.toLowerCase().replace("_", "-");
  }

  public boolean load(File var1) {
    var1.exists();

    YamlConfiguration var2;
    try {
      InputStreamReader var3 = new InputStreamReader(new FileInputStream(var1), StandardCharsets.UTF_8);

      try {
        var2 = YamlConfiguration.loadConfiguration(var3);
      } catch (Throwable var7) {
        try {
          var3.close();
        } catch (Throwable var6) {
          var7.addSuppressed(var6);
          throw var7;
        }

        boolean var10000 = false;
        throw var7;
      }

      var3.close();
    } catch (IOException var8) {
      SpigotGuardLogger.exception("Unable to load config.", var8);
      return false;
    }

    boolean var10001 = false;
    this.set((ConfigurationSection)var2, (String)"");
    return true;
  }

  @Target({ElementType.FIELD, ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface Comment {
    String[] value();
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD})
  public static @interface Create {}
  
  @Target({ElementType.FIELD, ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface Ignore {}
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD})
  public static @interface Final {}
}