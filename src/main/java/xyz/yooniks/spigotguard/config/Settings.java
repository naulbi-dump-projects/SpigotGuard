package xyz.yooniks.spigotguard.config;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Settings extends Config {
  @Create
  public INVENTORIES INVENTORIES;
  
  public boolean DEBUG = false;
  
  @Comment({"Should we allow creativeSlot packets when player is without gamemode? He may be not able to use printer if it is set to FALSE."})
  public boolean ALLOW_CREATIVE_INVENTORY_CLICK_WITHOUT_GAMEMODE = false;
  
  @Create
  public KICK KICK;
  
  @Comment({"Should we block null address in LoginEvents?"})
  public boolean BLOCK_NULL_ADDRESS = true;
  
  @Comment({"Set it to bigger value if stuff like <tried to place book but is holding xxx> has false positives"})
  public int BLOCK_FAKE_PACKETS_ONLY_WHEN_ITEMS_NBT_BIGGER_THAN = 1000;
  
  @Create
  public PACKET_DECODER PACKET_DECODER;
  
  @Create
  public HACKER_BOOK HACKER_BOOK;
  
  @Create
  public SKULL_CHECKS SKULL_CHECKS;
  
  @Comment({"Increase it if <ByteBuf/Too big packet size> has false positives", "Max packet size"})
  public int VIAVERSION_MAX_CAPACITY = 5000;
  
  @Create
  public PLACE_CHECKS PLACE_CHECKS;
  
  @Create
  public PAYLOAD_SETTINGS PAYLOAD_SETTINGS;
  
  @Comment({"Should we use our own checks built-in into ViaVersion? (packet-size checks)"})
  public boolean VIAVERSION_INTEGRATION = true;
  
  @Comment({"How big should be the difference beetwen NOW & LAST arm animation packets"})
  public int ARM_ANIMATION_TIMESTAMP = 20;
  
  @Create
  public POSITION_CHECKS POSITION_CHECKS;
  
  @Comment({"SpigotGuard - the best AntiCrash protection for your minecraft server", "Made by yooniks, discord: yooniks#0289, discord server: https://www.mc-protection.eu/discord", "For BungeeCord security ( Anti Bot & Anti Crash ) we recommend: https://www.mc-protection.eu/products/", "Read more about SpigotGuard or tell your friends to buy it too! https://www.mc-protection.eu/products/"})
  @Create
  public MESSAGES MESSAGES;
  
  @Comment({"Commands that well be executed when player tried to crash server", "Available variables: {PLAYER} and {IP}", "Do not use dashes!"})
  public List<String> COMMANDS_WHEN_SURE = Arrays.asList(new String[] { "exampleCommand {PLAYER}" });
  
  @Comment({"Available types:", "FIND <- Automatically finds a NMS version", "v1_7, v1_8, v1_12, v1_14, v1_15", "You can set it manually if you are using custom spigot and the SpigotGuard cannot get your nms server version."})
  public String NMS_VERSION = "FIND";
  
  @Comment({"Put your license key here. If you dont have a one, please contact us on discord: https://mc-protection.eu/discord and send proof of payment to get your license key."})
  public String LICENSE = "yourLicenseKeyHere";
  
  @Create
  public BOOK BOOK;
  
  @Create
  public OTHER_NBT OTHER_NBT;
  
  public String NULL_ADDRESS_KICK = "&cYour login data is invalid! Please try relogging.";
  
  @Ignore
  public static final Settings IMP = new Settings();
  
  public boolean BLOCK_ITEMNAME_WHEN_NO_ANVIL = true;
  
  @Create
  public SQL SQL;
  
  public void reload(File paramFile) {
    save(paramFile);
  }
  
  public static class OTHER_NBT {
    public int MAX_KEYS = 20;
    
    public int MAX_SIMPLE_NBT_LIMIT = 10000;
    
    public int FIREWORKS_CHARGE_LIMIT = 800;
    
    public int MAX_ARRAY_SIZE = 50;
    
    public int FIREWORK_LIMIT = 300;
    
    public int MAX_LISTS = 10;
    
    @Comment({"Set it to higher value if stuff like <too large list length> has false positives"})
    public int MAX_LIST_CONTENT = 900;
    
    public int MAX_LIST_SIZE = 50;
    
    public boolean SIMPLE_NBT_LIMIT = true;
    
    public int BANNER_LIMIT = 200;
    
    public boolean LIMIT_CHEST_NBT = true;
  }
  
  public static class BOOK {
    @Comment({"Set it to higher value if stuff like <too large page> has false positives"})
    public int MAX_PAGE_SIZE = 900;
    
    @Comment({"Max page size without color codes"})
    public int MAX_STRIPPED_PAGE_SIZE = 256;
    
    @Comment({"Should we block BOOK_AND_QUILL and WRITTEN_BOOK on server? People will be kicked for using it"})
    public boolean BAN_BOOKS = false;
    
    @Comment({"Max similar pages. Default: 4, Crash books very often use the same page text like 40 times."})
    public int MAX_SIMILAR_PAGES = 4;
    
    public int MAX_PAGES = 50;
    
    @Comment({"Set it to true if your server does not use books very often", "It is fast, lightweight check that detects crash books very fast", "You could enable it if people do not even have access to book_and_quill and written_book on your server"})
    public boolean FAST_CHECK = false;
    
    @Comment({"Limit of 2-byte chars. 2-byte chars are non-english chars like China chars, Emojis and similars."})
    public int MAX_2BYTE_CHARS = 15;
    
    @Comment({"Max fast-check nbt item length"})
    public int FAST_CHECK_MAX_LENGTH = 500;
  }
  
  @Comment({"Do not use '\\ n', use %nl%"})
  public static class MESSAGES {
    public String NOTIFICATION_PERMISSION = "spigotguard.notification";
    
    public String PREFIX = "&cSpigotGuard &8>> ";
    
    public String NOTIFICATION_MESSAGE = "%nl%{PREFIX}&7Player &c{PLAYER}&7 tried to crash the server.%nl%{PREFIX}&7Packet: &c{PACKET}%nl%{PREFIX}&7Details: &c{DETAILS}%nl%{PREFIX}&7Read more about this crash attempt: &c/spigotguard {PLAYER}&7%nl%&7";
    
    private static boolean iiiiiiii(int param1Int1, int param1Int2) {
      return (param1Int1 < param1Int2);
    }
  }
  
  public static class PACKET_DECODER {
    public boolean ENABLED = true;
    
    public int BLOCK_CHESTS_WITH_NBT_BLOCK_SIZE_HIGHER_THAN = 250;
    
    @Comment({"Most of people use chests with nbt to crash your Creative server by placing a lot of chests with chests inside on their plotme home. It overloads chunks and make server crash."})
    public boolean BLOCK_CHESTS_WITH_NBT_ON_CREATIVE = true;
    
    public String BLOCK_CHESTS_WITH_NBT_KICK_MESSAGE = "&8[&6SpigotGuard&8] &cPlease do not use chests with already existing NBT data on creative.";
    
    @Comment({"Max packet decoding size"})
    public int MAX_MAIN_SIZE = 5000;
    
    @Create
    public VIAVERSION VIAVERSION;
    
    public int MAX_WINDOW_SIZE = 1520;
    
    public int MAX_CREATIVE_SIZE = 5000;
    
    public int MAX_PLACE_SIZE = 1520;
    
    public int MAX_PAYLOAD_SIZE = 1700;
    
    @Comment({"1.12+ crasher. Fixed by Tuinity since 1.15 i think. If you are running 1.12.2 server, mostly Creative, we fix it for you.", "Set it to -1 to disable if you have false positives."})
    public int MAX_AUTO_RECIPE_RATE = 5;
    
    @Comment({"Some changes when you use our ViaVersion fork"})
    public static class VIAVERSION {
      @Comment({"FOR ViaVersion - Max packet size when there item inside the packet"})
      public int MAX_ITEM_SIZE = 2100;
      
      @Comment({"Should we block (clear) every BOOK (written books, book_and_quill) packet, even if it does not exceed packet size limit?", "It could be very useful if you do not allow using/crafting books (written books, book and quill) on your server and want to block every exploit faster"})
      public boolean CLEAR_ALL_BOOKS = false;
      
      @Comment({"Should we ONLY check packet size inside ViaVersion if the item is book?"})
      public boolean CHECK_ONLY_BOOKS = true;
    }
  }
  
  public static class SKULL_CHECKS {
    @Comment({"Should we block head/skull textures that do not start with http://textures.minecraft.net ?"})
    public boolean BLOCK_INVALID_HEAD_TEXTURE = true;
  }
  
  public static class INVENTORIES {
    @Create
    public MAIN_INVENTORY MAIN_INVENTORY;
    
    @Create
    public RECENT_DETECTIONS_INVENTORY RECENT_DETECTIONS_INVENTORY;
    
    @Create
    public PLAYER_INFO_INVENTORY PLAYER_INFO_INVENTORY;
    
    public static class MAIN_INVENTORY {
      @Create
      public RECENT_DETECTIONS_ITEM RECENT_DETECTIONS_ITEM;
      
      public String NAME = "&cSpigotGuard management";
      
      public int SIZE = 27;
      
      @Create
      public RELOAD_ITEM RELOAD_ITEM;
      
      private static boolean iiii(int param2Int1, int param2Int2) {
        return (param2Int1 < param2Int2);
      }
      
      public static class RELOAD_ITEM {
        public List<String> LORE = Arrays.asList(new String[] { "&8&l&m>&r &7Click to reload configuration files" });
        
        public int SLOT = 14;
        
        public String MATERIAL = "STICK";
        
        public String NAME = "&cReload configs";
      }
      
      public static class RECENT_DETECTIONS_ITEM {
        public int SLOT = 12;
        
        public String MATERIAL = "DIAMOND_CHESTPLATE";
        
        public List<String> LORE = Arrays.asList(new String[] { "&8&l&m>&r &7Click to open inventory with recent detections", "&8&l&m>&r &7You will see who tried to crash the server in last time!" });
        
        public String NAME = "&cRecent detections";
      }
    }
    
    public static class PLAYER_INFO_INVENTORY {
      public int SIZE = 27;
      
      @Create
      public CRASH_ATTEMPT_ITEM CRASH_ATTEMPT_ITEM;
      
      public String NAME = "&cCrash attempts of {PLAYER}";
      
      public static class CRASH_ATTEMPT_ITEM {
        public String NAME = "&c{TIME}";
        
        public List<String> LORE = Arrays.asList(new String[] { " &7Packet name: &c{PACKET}", " &7Details: &c{DETAILS}", " &7Time of crash attempt: &c{TIME}", " &7Player was last seen at: &c{LAST-SEEN}", " &7IP: &c{IP}", " ", "   &9&lHow to fix it, if it is false positive/detection?", " &7{HOW-TO-FIX-FALSE-POSITIVE}" });
      }
    }
    
    public static class RECENT_DETECTIONS_INVENTORY {
      public String NAME = "&cRecent detections";
      
      @Create
      public RECENT_DETECTION_ITEM RECENT_DETECTION_ITEM;
      
      public int SIZE = 27;
      
      @Create
      public NO_DETECTIONS_ITEM NO_DETECTIONS_ITEM;
      
      public static class RECENT_DETECTION_ITEM {
        public String NAME = "&c{PLAYER-NAME}";
        
        public List<String> LORE = Arrays.asList(new String[] { " &7Packet name: &c{PACKET}", " &7Details: &c{DETAILS}", " &7Time of crash attempt: &c{TIME}", " &7Player last seen at: &c{PLAYER-LAST-SEEN}", " ", "   &9&lHow to fix it, if it is false positive/detection?", " &7{HOW-TO-FIX-FALSE-POSITIVE}" });
      }
      
      public static class NO_DETECTIONS_ITEM {
        public List<String> LORE = Arrays.asList(new String[] { " &7We did not find any people that tried to crash the server :(", " &7Looks like your players are afraid to try crashers!" });
        
        public String NAME = "&cNo detections found";
      }
    }
  }
  
  @Comment({"Database Setup"})
  public static class SQL {
    @Comment({"Inform console about purging"})
    public boolean PURGE_CONSOLE_INFO = true;
    
    @Comment({"Available types: sqlite & mysql"})
    public String STORAGE_TYPE = "sqlite";
    
    public String PASSWORD = "password";
    
    @Comment({"After how many days to remove players from the database, which have been on server and no longer entered. Use 0 or less to stop"})
    public int PURGE_TIME = 10;
    
    @Comment({"Should we even enable database? If no, spigotguard will save no data."})
    public boolean ENABLED = true;
    
    @Comment({"Settings for mysql"})
    public String HOSTNAME = "localhost";
    
    public String USER = "user";
    
    public String DATABASE = "database";
    
    public int PORT = 3306;
  }
  
  @Comment({"How should we kick player when he tries to crash the server?"})
  public static class KICK {
    @Comment({"Available types:", "0 - Instant channel close, kick without any message", "1 - Kick with message (a bit slower because we have to send packet to player that he is kicked)"})
    public int TYPE = 0;
    
    public String MESSAGE = "&cSpigotGuard &8> &7You have been kicked for trying to &ccrash&7 the server.";
  }
  
  public static class PAYLOAD_SETTINGS {
    @Comment({"Should we block sending payload with MC|BSign when no book was used?"})
    public boolean BLOCK_BOOK_SIGNING_WHEN_NO_BOOK_PLACED = true;
    
    @Comment({"Max CustomPayload packet size"})
    public int MAX_CAPACITY = 2500;
    
    @Comment({"Should we block sending payload (book) packets while player does not hold a book in a hand?"})
    public boolean FAIL_WHEN_NOT_HOLDING_BOOK = true;
  }
  
  public static class POSITION_CHECKS {
    public boolean CHECK_YAW_CRASHER = true;
    
    @Comment({"Should we allow POSITION packets with Double.MAX_VALUE values? Phase cheats usually use it! It can also lag a server a bit."})
    public boolean ALLOW_INVALID_MOVEMENT_V2 = false;
    
    public boolean PREVENT_MOVING_INTO_UNLOADED_CHUNKS = false;
    
    @Comment({"Should we enable CHUNK CRASHER v2 check?", "If it has false positives, just disable it."})
    public boolean CHECK_CHUNK_CRASHER_V2 = true;
    
    @Comment({"Should we allow POSITION packets with Integer.MAX_VALUE values? Phase cheats usually use it! It can also lag a server a bit."})
    public boolean ALLOW_INVALID_MOVEMENT_V1 = true;
    
    public int MAX_DISTANCE_BETWEEN_PLAYER_AND_BLOCK_POSITION = 30;
    
    public boolean ALLOW_INVALID_VEHICLE_MOVEMENT_V3 = false;
    
    public boolean ALLOW_INVALID_VEHICLE_MOVEMENT_V4 = false;
    
    @Comment({"Should we enable CHUNK CRASHER check?", "If it has false positives, just disable it."})
    public boolean CHECK_CHUNK_CRASHER = true;
    
    public boolean ALLOW_INVALID_VEHICLE_MOVEMENT_V5 = false;
    
    @Comment({"Should we enable FLY CRASHER check?", "If it has false positives, just disable it."})
    public boolean CHECK_FLY_CRASHER = true;
  }
  
  public static class PLACE_CHECKS {
    @Comment({"Should player be able to send PacketPlayInBlockPlace with BANNER and similars when he is not holding it? It is surely exploit."})
    public boolean BLOCK_PLACING_BANNER_WHEN_NOT_HOLDING_IT = true;
    
    @Comment({"Should player be able to send PacketPlayInBlockPlace with FIREWORK and similars when he is not holding it? It is surely exploit."})
    public boolean BLOCK_PLACING_FIREWORK_WHEN_NOT_HOLDING_IT = true;
    
    @Comment({"Should player be able to send PacketPlayInBlockPlace with WRITTEN_BOOK and similars when he is not holding it? It is surely exploit."})
    public boolean BLOCK_PLACING_BOOK_WHEN_NOT_HOLDING_IT = true;
  }
  
  public static class HACKER_BOOK {
    public boolean ENABLED = true;
    
    public String CONTENT = "&n&cHello, &9&l{PLAYER}&r!%nl%Looks like the last time you were here you got kicked for suspicious activity and we think you tried to crash the server. To see details please open next page.";
    
    public String NEXT_PAGE = "&eIf it is our mistake, please &econtact the staff.";
  }
}