package xyz.yooniks.spigotguard;

import org.bukkit.plugin.java.*;
import xyz.yooniks.spigotguard.limitation.*;
import xyz.yooniks.spigotguard.nms.*;
import xyz.yooniks.spigotguard.classloader.*;
import xyz.yooniks.spigotguard.sql.*;
import xyz.yooniks.spigotguard.api.inventory.*;
import xyz.yooniks.spigotguard.config.*;
import java.util.logging.*;
import xyz.yooniks.spigotguard.logger.*;
import org.bukkit.*;
import org.bukkit.event.*;
import xyz.yooniks.spigotguard.listener.*;
import xyz.yooniks.spigotguard.listener.inventory.*;
import xyz.yooniks.spigotguard.inventory.*;
import xyz.yooniks.spigotguard.command.*;
import org.bukkit.command.*;
import xyz.yooniks.spigotguard.user.*;
import xyz.yooniks.spigotguard.notification.*;
import org.bukkit.plugin.*;
import xyz.yooniks.spigotguard.thread.*;
import java.security.*;
import java.io.*;

public final class SpigotGuardPlugin extends JavaPlugin
{
    private PacketLimitation packetLimitation;
    private UserManager userManager;
    private NMSVersion nmsVersion;
    private SpigotGuardClassLoaded spigotGuardClassLoaded;
    private SocketClassLoader socketClassLoader;
    private SqlDatabase sqlDatabase;
    private PhasmatosInventory managementInventory;
    
    public static SpigotGuardPlugin getInstance() {
        return (SpigotGuardPlugin)getPlugin((Class)SpigotGuardPlugin.class);
    }
    
    public void onEnable() {
        this.saveDefaultConfig();
        Settings.IMP.reload(new File(this.getDataFolder(), "settings.yml"));
        try {
            final SocketClassLoader socketClassLoader = new SocketClassLoader();
            this.socketClassLoader = socketClassLoader;
            if (!socketClassLoader.start(this)) {
                return;
            }
        }
        catch (IOException e) {
            SpigotGuardLogger.log(Level.WARNING, "Could not send license request to license server! Plugin will try to work normally if it is our license server issue. Just ignore this message. (dev: " + e.getMessage() + ")\n", new Object[0]);
            if (!e.getMessage().contains("timed") && !e.getMessage().contains("reset")) {
                return;
            }
            this.spigotGuardClassLoaded = new SpigotGuardClassLoaded("[1]");
        }
        if (!this.getDescription().getAuthors().contains("yooniks") || !this.getDescription().getName().equals("SpigotGuard")) {
            this.getLogger().warning("Please do not change the SpigotGuard plugin.yml =c");
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        String version = Settings.IMP.NMS_VERSION;
        if (version.equalsIgnoreCase("find")) {
            try {
                version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            }
            catch (ArrayIndexOutOfBoundsException ex) {
                version = "1_8";
                SpigotGuardLogger.log(Level.WARNING, "Could not get the NMS Version of your spigot server! We are using 1_8_R3 as default, if it's not your nms version then change it manually in settings.yml", new Object[0]);
            }
        }
        else {
            SpigotGuardLogger.log(Level.INFO, "Using manually set nms version! " + version, new Object[0]);
        }
        this.nmsVersion = NMSVersion.find(version);
        SpigotGuardLogger.log(Level.INFO, "Using " + this.nmsVersion.getName() + " NMS version as PacketInjector & PacketDecoder!", new Object[0]);
        if (this.nmsVersion == NMSVersion.UNSUPPORTED) {
            SpigotGuardLogger.log(Level.WARNING, "Your version is UNSUPPORTED! Are you sure you are running valid mc server version? The plugin might not work if your version isn't supported", new Object[0]);
        }
        NotificationCache notificationCache;
        if (this.nmsVersion == NMSVersion.ONE_DOT_SEVEN_R4) {
            notificationCache = new NotificationCache_1_7();
        }
        else {
            notificationCache = new NotificationCacheDefault();
        }
        (this.packetLimitation = new PacketLimitation()).initialize(this.getConfig());
        this.userManager = new UserManager();
        this.sqlDatabase = new SqlDatabase();
        this.getServer().getScheduler().runTaskAsynchronously((Plugin)this, () -> this.sqlDatabase.setupConnect(this.userManager));
        final PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents((Listener)new PacketInjectorListener(this.userManager, new PacketInjectorListener.PacketInjections(this.nmsVersion), this.sqlDatabase), (Plugin)this);
        pluginManager.registerEvents((Listener)new NotificationListener(notificationCache), (Plugin)this);
        pluginManager.registerEvents((Listener)new SignChangeListener(), (Plugin)this);
        if (this.nmsVersion == NMSVersion.ONE_DOT_FOURTEEN || this.nmsVersion == NMSVersion.ONE_DOT_FIVETEEN || this.nmsVersion == NMSVersion.ONE_DOT_SIXTEEN || this.nmsVersion == NMSVersion.ONE_DOT_SIXTEEN_R2) {
            pluginManager.registerEvents((Listener)new InventoryListener_1_14(), (Plugin)this);
        }
        else {
            pluginManager.registerEvents((Listener)new DefaultInventoryListener(), (Plugin)this);
        }
        final InventoryLoader inventoryLoader = new InventoryLoader();
        inventoryLoader.loadInventories(this);
        if (this.socketClassLoader.getHost()[0] != 57 && this.socketClassLoader.getHost()[1] != 49) {
            this.getServer().getScheduler().runTaskLater((Plugin)this, () -> {
                this.getPluginLoader().disablePlugin((Plugin)this);
                try {
                    throw new IOException();
                }
                catch (IOException exception) {
                    exception.printStackTrace();
                }
            }, 60L);
            return;
        }
        this.getCommand("spigotguard").setExecutor((CommandExecutor)new SpigotGuardCommand(this.managementInventory));
        this.getServer().getScheduler().runTaskTimerAsynchronously((Plugin)this, () -> this.userManager.getUsers().forEach(User::cleanup), 10L, 10L);
        this.getServer().getScheduler().runTaskLaterAsynchronously((Plugin)this, () -> SpigotGuardLogger.log(Level.INFO, "> You are running: " + this.getDescription().getVersion() + " SpigotGuard, Our discord: https://mc-protection.eu/discord (private: yooniks#0289), Webpage: https://minemen.com/resources/175/", new Object[0]), 100L);
        this.getLogger().info("Tell your friends to buy SpigotGuard if they need AntiCrash! https://minemen.com/resources/175/");
    }
    
    public void onDisable() {
        if (this.userManager == null) {
            return;
        }
        new Thread(new SpigotGuardUninjector(this.userManager.getUsers())).start();
        new Thread(() -> {
            SpigotGuardLogger.log(Level.INFO, "[Database] Saving sql users...", new Object[0]);
            this.userManager.getUsers().forEach(user -> this.sqlDatabase.saveUser(user));
            this.sqlDatabase.close();
            SpigotGuardLogger.log(Level.INFO, "[Database] Saved sql users!", new Object[0]);
        });
    }
    
    public SpigotGuardClassLoaded getSpigotGuardClassLoaded() {
        return this.spigotGuardClassLoaded;
    }
    
    public void setSpigotGuardClassLoaded(final SpigotGuardClassLoaded spigotGuardClassLoaded) {
        this.spigotGuardClassLoaded = spigotGuardClassLoaded;
    }
    
    public NMSVersion getNmsVersion() {
        return this.nmsVersion;
    }
    
    public PacketLimitation getPacketLimitation() {
        return this.packetLimitation;
    }
    
    public UserManager getUserManager() {
        return this.userManager;
    }
    
    public PhasmatosInventory getManagementInventory() {
        return this.managementInventory;
    }
    
    public void setManagementInventory(final PhasmatosInventory managementInventory) {
        this.managementInventory = managementInventory;
    }
    
    public static String getFileChecksum(final MessageDigest digest, final File file) throws IOException {
        final FileInputStream fis = new FileInputStream(file);
        final byte[] byteArray = new byte[1024];
        int bytesCount = 0;
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        fis.close();
        final byte[] bytes = digest.digest();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            sb.append(Integer.toString((bytes[i] & 0xFF) + 256, 16).substring(1));
        }
        return sb.toString();
    }
}
