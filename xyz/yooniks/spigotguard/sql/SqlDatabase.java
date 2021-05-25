package xyz.yooniks.spigotguard.sql;

import java.util.concurrent.*;
import xyz.yooniks.spigotguard.config.*;
import java.io.*;
import java.util.logging.*;
import xyz.yooniks.spigotguard.logger.*;
import xyz.yooniks.spigotguard.helper.*;
import xyz.yooniks.spigotguard.user.*;
import java.util.*;
import xyz.yooniks.spigotguard.event.*;
import xyz.yooniks.spigotguard.*;
import java.sql.*;

public class SqlDatabase
{
    private final ExecutorService executor;
    private Connection connection;
    private boolean connecting;
    private boolean initialized;
    
    public SqlDatabase() {
        this.executor = Executors.newCachedThreadPool();
        this.connecting = false;
        this.initialized = false;
    }
    
    public void setupConnect(final UserManager userManager) {
        if (this.initialized) {
            throw new UnsupportedOperationException("SqlDatabase has been already initialized");
        }
        this.initialized = true;
        Settings.SQL s;
        this.executor.submit(() -> {
            try {
                this.connecting = true;
                if (this.executor.isShutdown()) {
                    this.initialized = true;
                }
                else if (this.connection != null && this.connection.isValid(3)) {
                    this.initialized = true;
                }
                else {
                    if (Settings.IMP.SQL.STORAGE_TYPE.equalsIgnoreCase("mysql")) {
                        s = Settings.IMP.SQL;
                        this.connectToDatabase(String.format("JDBC:mysql://%s:%s/%s?useSSL=false&useUnicode=true&characterEncoding=utf-8&autoReconnect=true", s.HOSTNAME, s.PORT, s.DATABASE), s.USER, s.PASSWORD);
                    }
                    else {
                        Class.forName("org.sqlite.JDBC");
                        this.connectToDatabase("JDBC:sqlite:plugins/SpigotGuard/database.db", null, null);
                    }
                    this.createTable();
                    this.loadUsers(userManager);
                }
            }
            catch (SQLException ex) {}
            catch (ClassNotFoundException ex2) {}
            catch (IOException e) {
                e.printStackTrace();
                this.connection = null;
            }
            finally {
                this.connecting = false;
                this.initialized = true;
            }
        });
    }
    
    private void connectToDatabase(final String url, final String user, final String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
        SpigotGuardLogger.log(Level.INFO, "[Database] Connected to sqlite database!", new Object[0]);
    }
    
    private void createTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS `spigotguard_users` (`uuid` VARCHAR(36) NOT NULL PRIMARY KEY UNIQUE,`name` VARCHAR(16) NOT NULL,`ip` VARCHAR(16) NOT NULL,`exploits` text,`lastJoin` BIGINT NOT NULL);";
        final PreparedStatement statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS `spigotguard_users` (`uuid` VARCHAR(36) NOT NULL PRIMARY KEY UNIQUE,`name` VARCHAR(16) NOT NULL,`ip` VARCHAR(16) NOT NULL,`exploits` text,`lastJoin` BIGINT NOT NULL);");
        try {
            statement.executeUpdate();
            if (statement != null) {
                statement.close();
            }
        }
        catch (Throwable t) {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (Throwable t2) {
                    t.addSuppressed(t2);
                }
            }
            throw t;
        }
    }
    
    private void loadUsers(final UserManager userManager) throws SQLException, IOException, ClassNotFoundException {
        final PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM `spigotguard_users`;");
        try {
            final ResultSet set = statement.executeQuery();
            try {
                while (set.next()) {
                    final String id = set.getString("uuid");
                    final long lastJoin = set.getLong("lastJoin");
                    final String name = set.getString("name");
                    if (System.currentTimeMillis() - lastJoin > 86400000L * Settings.IMP.SQL.PURGE_TIME) {
                        this.removeUser("DELETE FROM `spigotguard_users` WHERE `uuid`='" + id + "'");
                        if (!Settings.IMP.SQL.PURGE_CONSOLE_INFO) {
                            continue;
                        }
                        SpigotGuardLogger.log(Level.INFO, "[Database] Removing user " + name + ", he has not joined server for " + Settings.IMP.SQL.PURGE_TIME + " days, so he is wasting memory only!", new Object[0]);
                    }
                    else {
                        final List<ExploitDetails> attempts = (List<ExploitDetails>)SerializerHelper.fromString(set.getString("exploits"));
                        final String ip = set.getString("ip");
                        final User user = new User(name, ip, UUID.fromString(id));
                        user.addAttempts(attempts);
                        user.setLastJoin(lastJoin);
                        userManager.addUser(user);
                    }
                }
                SpigotGuardLogger.log(Level.INFO, "[Database] Loaded " + userManager.getUsers().size() + " users!", new Object[0]);
                if (set != null) {
                    set.close();
                }
            }
            catch (Throwable t) {
                if (set != null) {
                    try {
                        set.close();
                    }
                    catch (Throwable t2) {
                        t.addSuppressed(t2);
                    }
                }
                throw t;
            }
            if (statement != null) {
                statement.close();
            }
        }
        catch (Throwable t3) {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (Throwable t4) {
                    t3.addSuppressed(t4);
                }
            }
            throw t3;
        }
    }
    
    private boolean isInvalidName(final String name) {
        return name.contains("'") || name.contains("\"");
    }
    
    private void removeUser(final String sql) {
        if (this.connection != null) {
            PreparedStatement statement;
            this.executor.execute(() -> {
                try {
                    statement = this.connection.prepareStatement(sql);
                    try {
                        statement.execute();
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t) {
                        if (statement != null) {
                            try {
                                statement.close();
                            }
                            catch (Throwable t2) {
                                t.addSuppressed(t2);
                            }
                        }
                        throw t;
                    }
                }
                catch (SQLException ex) {}
            });
        }
    }
    
    public void saveUser(final User user) {
        if (this.connecting || this.isInvalidName(user.getName())) {
            return;
        }
        if (this.connection != null) {
            final String sql;
            Statement statement;
            ResultSet set;
            String sql2;
            List<ExploitDetails> attempts;
            String serializedAttempts;
            String sql3;
            this.executor.execute(() -> {
                sql = "SELECT `uuid` FROM `spigotguard_users` where `uuid` = '" + user.getId().toString() + "' LIMIT 1;";
                try {
                    statement = this.connection.createStatement();
                    try {
                        set = statement.executeQuery(sql);
                        try {
                            if (!set.next()) {
                                sql2 = "INSERT INTO `spigotguard_users` (`uuid`, `name`, `ip`, `exploits`, `lastJoin`) VALUES ('" + user.getId().toString() + "','" + user.getName() + "','" + user.getIp() + "','" + SerializerHelper.toString(user.getAttempts()) + "','" + user.getLastJoin() + "');";
                                statement.executeUpdate(sql2);
                            }
                            else {
                                attempts = user.getAttempts();
                                serializedAttempts = SerializerHelper.toString(attempts);
                                sql3 = "UPDATE `spigotguard_users` SET `name` = '" + user.getName() + "', `lastJoin` = '" + user.getLastJoin() + "', `exploits` = '" + serializedAttempts + "' where `uuid` = '" + user.getId().toString() + "';";
                                statement.executeUpdate(sql3);
                            }
                            if (set != null) {
                                set.close();
                            }
                        }
                        catch (Throwable t) {
                            if (set != null) {
                                try {
                                    set.close();
                                }
                                catch (Throwable t2) {
                                    t.addSuppressed(t2);
                                }
                            }
                            throw t;
                        }
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t3) {
                        if (statement != null) {
                            try {
                                statement.close();
                            }
                            catch (Throwable t4) {
                                t3.addSuppressed(t4);
                            }
                        }
                        throw t3;
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    if (ex instanceof SQLException) {
                        this.initialized = false;
                        this.executor.execute(() -> this.setupConnect(SpigotGuardPlugin.getInstance().getUserManager()));
                    }
                }
            });
        }
    }
    
    public void close() {
        this.executor.shutdownNow();
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        }
        catch (SQLException ex) {}
        this.connection = null;
        SpigotGuardLogger.log(Level.INFO, "[Database] Closed sql connection!", new Object[0]);
    }
}
