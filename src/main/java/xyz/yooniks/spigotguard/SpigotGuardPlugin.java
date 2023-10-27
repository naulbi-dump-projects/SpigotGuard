package xyz.yooniks.spigotguard;

import java.io.File;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.yooniks.spigotguard.api.inventory.PhasmatosInventory;
import xyz.yooniks.spigotguard.classloader.SpigotGuardClassLoaded;
import xyz.yooniks.spigotguard.command.SpigotGuardCommand;
import xyz.yooniks.spigotguard.config.Settings;
import xyz.yooniks.spigotguard.inventory.InventoryLoader;
import xyz.yooniks.spigotguard.limitation.PacketLimitation;
import xyz.yooniks.spigotguard.listener.FaweTabCompletionListener;
import xyz.yooniks.spigotguard.listener.MovementListener;
import xyz.yooniks.spigotguard.listener.NotificationListener;
import xyz.yooniks.spigotguard.listener.PacketInjectorListener;
import xyz.yooniks.spigotguard.listener.SignChangeListener;
import xyz.yooniks.spigotguard.listener.inventory.DefaultInventoryListener;
import xyz.yooniks.spigotguard.listener.inventory.InventoryListener_1_14;
import xyz.yooniks.spigotguard.logger.SpigotGuardLogger;
import xyz.yooniks.spigotguard.nms.NMSVersion;
import xyz.yooniks.spigotguard.notification.NotificationCache;
import xyz.yooniks.spigotguard.notification.NotificationCacheDefault;
import xyz.yooniks.spigotguard.notification.NotificationCache_1_7;
import xyz.yooniks.spigotguard.sql.SqlDatabase;
import xyz.yooniks.spigotguard.user.User;
import xyz.yooniks.spigotguard.user.UserManager;

public final class SpigotGuardPlugin extends JavaPlugin {
  private PhasmatosInventory managementInventory;
  
  private PacketLimitation packetLimitation;
  
  private SqlDatabase sqlDatabase;
  
  private UserManager userManager;
  
  private SpigotGuardClassLoaded spigotGuardClassLoaded;
  
  private NMSVersion nmsVersion;
  
  public void onDisable() {}
  
  public NMSVersion getNmsVersion() {
    return this.nmsVersion;
  }
  
  public PacketLimitation getPacketLimitation() {
    return this.packetLimitation;
  }
  
  public SpigotGuardClassLoaded getSpigotGuardClassLoaded() {
    return this.spigotGuardClassLoaded;
  }
  
  private void lambda$onEnable$1() {
    this.sqlDatabase.setupConnect(this.userManager);
  }
  
  public void onEnable() {
    NotificationCacheDefault notificationCacheDefault;
    saveDefaultConfig();
    Settings.IMP.reload(new File(getDataFolder(), "settings.yml"));
    this.spigotGuardClassLoaded = new SpigotGuardClassLoaded("[1]");
    if (!getDescription().getAuthors().contains("yooniks") || !getDescription().getName().equals("SpigotGuard")) {
      getLogger().warning("Please do not change the SpigotGuard plugin.yml =c");
      getServer().getPluginManager().disablePlugin((Plugin)this);
      return;
    } 
    String str = Settings.IMP.NMS_VERSION;
    if (Integer.valueOf(2158009).equals(Integer.valueOf(str.toUpperCase().hashCode()))) {
      try {
        str = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        false;
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        str = "1_8";
        SpigotGuardLogger.log(Level.WARNING, "Could not get the NMS Version of your spigot server! We are using 1_8_R3 as default, if it's not your nms version then change it manually in settings.yml", new Object[0]);
        false;
      } 
    } else {
      SpigotGuardLogger.log(Level.INFO, "Using manually set nms version! " + str, new Object[0]);
    } 
    this.nmsVersion = NMSVersion.find(str);
    SpigotGuardLogger.log(Level.INFO, "Using " + this.nmsVersion.getName() + " NMS version as PacketInjector & PacketDecoder!", new Object[0]);
    if (this.nmsVersion == NMSVersion.UNSUPPORTED)
      SpigotGuardLogger.log(Level.WARNING, "Your version is UNSUPPORTED! Are you sure you are running valid mc server version? The plugin might not work if your version isn't supported", new Object[0]); 
    if (this.nmsVersion == NMSVersion.ONE_DOT_SEVEN_R4) {
      NotificationCache_1_7 notificationCache_1_7 = new NotificationCache_1_7();
      false;
    } else {
      notificationCacheDefault = new NotificationCacheDefault();
    } 
    this.packetLimitation = new PacketLimitation();
    this.packetLimitation.initialize(getConfig());
    this.userManager = new UserManager();
    this.sqlDatabase = new SqlDatabase();
    if (Settings.IMP.SQL.ENABLED);
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents((Listener)new PacketInjectorListener(this.userManager, new PacketInjectorListener.PacketInjections(this.nmsVersion), this.sqlDatabase), (Plugin)this);
    pluginManager.registerEvents((Listener)new NotificationListener((NotificationCache)notificationCacheDefault), (Plugin)this);
    pluginManager.registerEvents((Listener)new SignChangeListener(), (Plugin)this);
    pluginManager.registerEvents((Listener)new FaweTabCompletionListener(), (Plugin)this);
    if (Settings.IMP.POSITION_CHECKS.PREVENT_MOVING_INTO_UNLOADED_CHUNKS)
      pluginManager.registerEvents((Listener)new MovementListener(), (Plugin)this); 
    if (this.nmsVersion == NMSVersion.ONE_DOT_FOURTEEN || this.nmsVersion == NMSVersion.ONE_DOT_FIVETEEN || this.nmsVersion == NMSVersion.ONE_DOT_SIXTEEN || this.nmsVersion == NMSVersion.ONE_DOT_SIXTEEN_R2 || this.nmsVersion == NMSVersion.ONE_DOT_SIXTEEN_R3) {
      pluginManager.registerEvents((Listener)new InventoryListener_1_14(), (Plugin)this);
    } else {
      pluginManager.registerEvents((Listener)new DefaultInventoryListener(), (Plugin)this);
    } 
    InventoryLoader inventoryLoader = new InventoryLoader();
    inventoryLoader.loadInventories(this);
    getCommand("spigotguard").setExecutor((CommandExecutor)new SpigotGuardCommand(this.managementInventory));
    getLogger().info("Thanks for using our resource. We recommend you to buy our other products on: https://mc-protection.eu");
  }
  
  public UserManager getUserManager() {
    return this.userManager;
  }
  
  private void lambda$onDisable$5() {
    SpigotGuardLogger.log(Level.INFO, "[Database] Saving sql users...", new Object[0]);
    this.userManager.getUsers().forEach(this::lambda$onDisable$4);
    this.sqlDatabase.close();
    SpigotGuardLogger.log(Level.INFO, "[Database] Saved sql users!", new Object[0]);
  }
  
  private void lambda$onEnable$3() {
    SpigotGuardLogger.log(Level.INFO, "> You are running: " + getDescription().getVersion() + " SpigotGuard, Our discord: https://mc-protection.eu/discord (private: yooniks#0289), Webpage: https://mc-protection.eu", new Object[0]);
  }
  
  public PhasmatosInventory getManagementInventory() {
    return this.managementInventory;
  }
  
  public void setManagementInventory(PhasmatosInventory paramPhasmatosInventory) {
    this.managementInventory = paramPhasmatosInventory;
  }
  
  public void setSpigotGuardClassLoaded(SpigotGuardClassLoaded paramSpigotGuardClassLoaded) {
    this.spigotGuardClassLoaded = paramSpigotGuardClassLoaded;
  }
  
  public static SpigotGuardPlugin getInstance() {
    return (SpigotGuardPlugin)getPlugin(SpigotGuardPlugin.class);
  }
  
  private void lambda$onEnable$2() {
    this.userManager.getUsers().forEach(User::cleanup);
  }
  
  private void lambda$onDisable$4(User paramUser) {
    this.sqlDatabase.saveUser(paramUser);
  }
  
  private static void lambda$onEnable$0() {
    FastAsyncWorldEditFix fastAsyncWorldEditFix = new FastAsyncWorldEditFix();
    if (fastAsyncWorldEditFix.isLoaded())
      fastAsyncWorldEditFix.fixConfig(); 
  }
}