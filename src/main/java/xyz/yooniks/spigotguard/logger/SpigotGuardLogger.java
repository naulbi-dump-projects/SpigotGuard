package xyz.yooniks.spigotguard.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.yooniks.spigotguard.SpigotGuardPlugin;

public class SpigotGuardLogger {
  private static final Executor executor = Executors.newCachedThreadPool();
  private static final Logger LOGGER = ((SpigotGuardPlugin)SpigotGuardPlugin.getPlugin(SpigotGuardPlugin.class)).getLogger();
  private static final File fileLog = new File(SpigotGuardPlugin.getInstance().getDataFolder(), "logs.txt");

  private static void lambda$log$0(String var0, Object[] var1) {
    BufferedWriter var2 = null;
    FileWriter var3 = null;

    try {
      var3 = new FileWriter(fileLog, true);
      var2 = new BufferedWriter(var3);
      var2.write(String.format(var0, var1));
      var2.write("\n");
    } catch (IOException var12) {
      var12.printStackTrace();
    } finally {
      label100: {
        try {
          if (var2 != null) {
            var2.close();
          }

          if (var3 != null) {
            var3.close();
          }
        } catch (IOException var13) {
          var13.printStackTrace();
          break label100;
        }

        boolean var10000 = false;
      }

    }

  }

  public static void exception(String var0, Exception var1) {
    LOGGER.log(Level.WARNING, var0, var1);
  }

  public static void log(Level var0, String var1, Object... var2) {
    LOGGER.log(var0, var1, var2);
    executor.execute(SpigotGuardLogger::lambda$log$0);
  }
}
