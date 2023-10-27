package xyz.yooniks.spigotguard.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import xyz.yooniks.spigotguard.config.Settings;
import xyz.yooniks.spigotguard.config.Settings.SQL;
import xyz.yooniks.spigotguard.helper.SerializerHelper;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;
import xyz.yooniks.spigotguard.user.User;
import xyz.yooniks.spigotguard.user.UserManager;

public class SqlDatabase {
  private boolean initialized = false;
  private final ExecutorService executor = Executors.newCachedThreadPool();
  private Connection connection;
  private boolean connecting = false;

  private void lambda$removeUser$1(String var1) {
    try {
      PreparedStatement var2 = this.connection.prepareStatement(var1);

      try {
        var2.execute();
      } catch (Throwable var6) {
        if (var2 != null) {
          try {
            var2.close();
          } catch (Throwable var5) {
            var6.addSuppressed(var5);
            throw var6;
          }

          boolean var10000 = false;
        }

        throw var6;
      }

      if (var2 != null) {
        var2.close();
      }
    } catch (SQLException var7) {
      return;
    }

    boolean var10001 = false;
  }

  public void saveUser(User param1) {
    // $FF: Couldn't be decompiled
  }

  private void connectToDatabase(String var1, String var2, String var3) throws SQLException {
    this.connection = DriverManager.getConnection(var1, var2, var3);
    SpigotGuardLogger.log(Level.INFO, "[Database] Connected to sqlite database!", new Object[0]);
  }

  public void close() {
    this.executor.shutdownNow();

    label17: {
      try {
        if (this.connection != null) {
          this.connection.close();
        }
      } catch (SQLException var2) {
        break label17;
      }

      boolean var10001 = false;
    }

    this.connection = null;
    SpigotGuardLogger.log(Level.INFO, "[Database] Closed sql connection!", new Object[0]);
  }

  private void loadUsers(UserManager var1) throws SQLException, IOException, ClassNotFoundException {
    PreparedStatement var2 = this.connection.prepareStatement("SELECT * FROM `spigotguard_users`;");

    boolean var10000;
    try {
      ResultSet var3 = var2.executeQuery();

      try {
        while(var3.next()) {
          String var4 = var3.getString("uuid");
          long var5 = var3.getLong("lastJoin");
          String var7 = var3.getString("name");
          if (iiiiiii(System.currentTimeMillis() - var5, 86400000L * (long)Settings.IMP.SQL.PURGE_TIME) > 0) {
            this.removeUser("DELETE FROM `spigotguard_users` WHERE `uuid`='" + var4 + "'");
            if (Settings.IMP.SQL.PURGE_CONSOLE_INFO) {
              SpigotGuardLogger.log(Level.INFO, "[Database] Removing user " + var7 + ", he has not joined server for " + Settings.IMP.SQL.PURGE_TIME + " days, so he is wasting memory only!", new Object[0]);
            }
          } else {
            List var8 = SerializerHelper.fromString(var3.getString("exploits"));
            String var9 = var3.getString("ip");
            User var10 = new User(var7, var9, UUID.fromString(var4));
            var10.addAttempts(var8);
            var10.setLastJoin(var5);
            var1.addUser(var10);
            var10000 = false;
          }
        }

        SpigotGuardLogger.log(Level.INFO, "[Database] Loaded " + var1.getUsers().size() + " users!", new Object[0]);
      } catch (Throwable var13) {
        if (var3 != null) {
          try {
            var3.close();
          } catch (Throwable var12) {
            var13.addSuppressed(var12);
            throw var13;
          }

          var10000 = false;
        }

        throw var13;
      }

      if (var3 != null) {
        var3.close();
      }
    } catch (Throwable var14) {
      if (var2 != null) {
        try {
          var2.close();
        } catch (Throwable var11) {
          var14.addSuppressed(var11);
          throw var14;
        }

        var10000 = false;
      }

      throw var14;
    }

    if (var2 != null) {
      var2.close();
    }

  }

  private void lambda$saveUser$2(User var1) {
    String var2 = "SELECT `uuid` FROM `spigotguard_users` where `uuid` = '" + var1.getId().toString() + "' LIMIT 1;";

    try {
      Statement var3 = this.connection.createStatement();

      boolean var10000;
      try {
        ResultSet var4 = var3.executeQuery(var2);

        try {
          if (!var4.next()) {
            var2 = "INSERT INTO `spigotguard_users` (`uuid`, `name`, `ip`, `exploits`, `lastJoin`) VALUES ('" + var1.getId().toString() + "','" + var1.getName() + "','" + var1.getIp() + "','" + SerializerHelper.toString(var1.getAttempts()) + "','" + var1.getLastJoin() + "');";
            var3.executeUpdate(var2);
          } else {
            List var5 = var1.getAttempts();
            String var6 = SerializerHelper.toString(var5);
            var2 = "UPDATE `spigotguard_users` SET `name` = '" + var1.getName() + "', `lastJoin` = '" + var1.getLastJoin() + "', `exploits` = '" + var6 + "' where `uuid` = '" + var1.getId().toString() + "';";
            var3.executeUpdate(var2);
          }
        } catch (Throwable var9) {
          if (var4 != null) {
            try {
              var4.close();
            } catch (Throwable var8) {
              var9.addSuppressed(var8);
              throw var9;
            }

            var10000 = false;
          }

          throw var9;
        }

        if (var4 != null) {
          var4.close();
        }
      } catch (Throwable var10) {
        if (var3 != null) {
          try {
            var3.close();
          } catch (Throwable var7) {
            var10.addSuppressed(var7);
            throw var10;
          }

          var10000 = false;
        }

        throw var10;
      }

      if (var3 != null) {
        var3.close();
      }
    } catch (Exception var11) {
      var11.printStackTrace();
      return;
    }

    boolean var10001 = false;
  }

  public void setupConnect(UserManager var1) {
    if (this.initialized) {
      throw new UnsupportedOperationException("SqlDatabase has been already initialized");
    } else {
      this.initialized = true;
      this.executor.submit(this::lambda$setupConnect$0);
    }
  }

  private static int iiiiiii(long var0, long var2) {
    long var4;
    return (var4 = var0 - var2) == 0L ? 0 : (var4 < 0L ? -1 : 1);
  }

  private boolean isInvalidName(String var1) {
    boolean var10000;
    if (!var1.contains("'") && !var1.contains("\"")) {
      var10000 = false;
    } else {
      var10000 = true;
      boolean var10001 = false;
    }

    return var10000;
  }

  private void removeUser(String var1) {
    if (this.connection != null) {
      this.executor.execute(this::lambda$removeUser$1);
    }

  }

  private void lambda$setupConnect$0(UserManager var1) {
    label109: {
      boolean var8;
      label91: {
        try {
          this.connecting = true;
          if (!this.executor.isShutdown()) {
            if (this.connection != null && this.connection.isValid(3)) {
              this.initialized = true;
              return;
            }

            String var10000 = Settings.IMP.SQL.STORAGE_TYPE;
            if (Integer.valueOf(73844866).equals(var10000.toUpperCase().hashCode())) {
              SQL var2 = Settings.IMP.SQL;
              this.connectToDatabase(String.format("JDBC:mysql://%s:%s/%s?useSSL=false&useUnicode=true&characterEncoding=utf-8&autoReconnect=true", var2.HOSTNAME, var2.PORT, var2.DATABASE), var2.USER, var2.PASSWORD);
              var8 = false;
            } else {
              Class.forName("org.sqlite.JDBC");
              this.connectToDatabase("JDBC:sqlite:plugins/SpigotGuard/database.db", (String)null, (String)null);
            }

            this.createTable();
            if (Settings.IMP.SQL.ENABLED) {
              this.loadUsers(var1);
            }
            break label109;
          }

          this.initialized = true;
        } catch (ClassNotFoundException | IOException | SQLException var6) {
          var6.printStackTrace();
          this.connection = null;
          break label91;
        } finally {
          this.connecting = false;
          this.initialized = true;
        }

        return;
      }

      var8 = false;
      return;
    }

    boolean var10001 = false;
  }

  private void createTable() throws SQLException {
    String var1 = "CREATE TABLE IF NOT EXISTS `spigotguard_users` (`uuid` CHAR(36) PRIMARY KEY NOT NULL,`name` VARCHAR(16) NOT NULL,`ip` VARCHAR(16) NOT NULL,`exploits` text,`lastJoin` BIGINT NOT NULL);";
    PreparedStatement var2 = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS `spigotguard_users` (`uuid` CHAR(36) PRIMARY KEY NOT NULL,`name` VARCHAR(16) NOT NULL,`ip` VARCHAR(16) NOT NULL,`exploits` text,`lastJoin` BIGINT NOT NULL);");

    try {
      var2.executeUpdate();
    } catch (Throwable var6) {
      if (var2 != null) {
        try {
          var2.close();
        } catch (Throwable var5) {
          var6.addSuppressed(var5);
          throw var6;
        }

        boolean var10000 = false;
      }

      throw var6;
    }

    if (var2 != null) {
      var2.close();
    }

    SpigotGuardLogger.log(Level.INFO, "[Database] Created sql database table!", new Object[0]);
  }
}
