package xyz.yooniks.spigotguard.config;

import java.util.*;
import java.io.*;

public class Settings extends Config
{
    @Ignore
    public static final Settings IMP;
    @Comment({ "SpigotGuard - the best AntiCrash protection for your minecraft server", "Made by yooniks, discord: yooniks#0289, discord server: https://mc-protection.eu/discord", "For BungeeCord security ( Anti Bot & Anti Crash ) we recommend: https://minemen.com/resources/216/", "Read more about SpigotGuard or tell your friends to buy it too! https://minemen.com/resources/175/" })
    @Create
    public MESSAGES MESSAGES;
    @Comment({ "Put your license key here.", "If you do not have a one, please contact us on discord: https://discord.gg/AmvcUfn and send proof of payment" })
    public String LICENSE;
    @Comment({ "Commands that well be executed when player tried to crash server", "Available variables: {PLAYER} and {IP}", "Do not use dashes!" })
    public List<String> COMMANDS_WHEN_SURE;
    public boolean DEBUG;
    @Comment({ "How big should be the difference beetwen NOW & LAST arm animation packets" })
    public int ARM_ANIMATION_TIMESTAMP;
    public boolean BLOCK_ITEMNAME_WHEN_NO_ANVIL;
    @Comment({ "Available types:", "FIND <- Automatically finds a NMS version", "v1_7, v1_8, v1_12, v1_14, v1_15", "You can set it manually if you are using custom spigot and the SpigotGuard cannot get your nms server version." })
    public String NMS_VERSION;
    @Comment({ "Set it to bigger value if stuff like <tried to place book but is holding xxx> has false positives" })
    public int BLOCK_FAKE_PACKETS_ONLY_WHEN_ITEMS_NBT_BIGGER_THAN;
    @Comment({ "Should we use our own checks built-in into ViaVersion? (packet-size checks)" })
    public boolean VIAVERSION_INTEGRATION;
    @Comment({ "Increase it if <ByteBuf/Too big packet size> has false positives", "Max packet size" })
    public int VIAVERSION_MAX_CAPACITY;
    @Comment({ "Should we allow creativeSlot packets when player is without gamemode? He may be not able to use printer if it is set to FALSE." })
    public boolean ALLOW_CREATIVE_INVENTORY_CLICK_WITHOUT_GAMEMODE;
    @Create
    public PAYLOAD_SETTINGS PAYLOAD_SETTINGS;
    @Comment({ "Should we block null address in LoginEvents?" })
    public boolean BLOCK_NULL_ADDRESS;
    public String NULL_ADDRESS_KICK;
    @Create
    public KICK KICK;
    @Create
    public BOOK BOOK;
    @Create
    public OTHER_NBT OTHER_NBT;
    @Create
    public POSITION_CHECKS POSITION_CHECKS;
    @Create
    public SKULL_CHECKS SKULL_CHECKS;
    @Create
    public PLACE_CHECKS PLACE_CHECKS;
    @Create
    public HACKER_BOOK HACKER_BOOK;
    @Create
    public PACKET_DECODER PACKET_DECODER;
    @Create
    public INVENTORIES INVENTORIES;
    @Create
    public SQL SQL;
    
    public Settings() {
        this.LICENSE = "yourLicenseKey";
        this.COMMANDS_WHEN_SURE = Arrays.asList("exampleCommand {PLAYER}");
        this.DEBUG = false;
        this.ARM_ANIMATION_TIMESTAMP = 20;
        this.BLOCK_ITEMNAME_WHEN_NO_ANVIL = true;
        this.NMS_VERSION = "FIND";
        this.BLOCK_FAKE_PACKETS_ONLY_WHEN_ITEMS_NBT_BIGGER_THAN = 1000;
        this.VIAVERSION_INTEGRATION = true;
        this.VIAVERSION_MAX_CAPACITY = 5000;
        this.ALLOW_CREATIVE_INVENTORY_CLICK_WITHOUT_GAMEMODE = false;
        this.BLOCK_NULL_ADDRESS = true;
        this.NULL_ADDRESS_KICK = "&cYour login data is invalid! Please try relogging.";
    }
    
    public void reload(final File file) {
        this.load(file);
        this.save(file);
    }
    
    static {
        IMP = new Settings();
    }
    
    @Comment({ "Do not use '\\ n', use %nl%" })
    public static class MESSAGES
    {
        public String PREFIX;
        public String NOTIFICATION_PERMISSION;
        public String NOTIFICATION_MESSAGE;
        
        public MESSAGES() {
            this.PREFIX = "&cSpigotGuard &8>> ";
            this.NOTIFICATION_PERMISSION = "spigotguard.notification";
            this.NOTIFICATION_MESSAGE = "%nl%{PREFIX}&7Player &c{PLAYER}&7 tried to crash the server.%nl%{PREFIX}&7Packet: &c{PACKET}%nl%{PREFIX}&7Details: &c{DETAILS}%nl%{PREFIX}&7Read more about this crash attempt: &c/spigotguard {PLAYER}&7%nl%&7";
        }
    }
    
    public static class PAYLOAD_SETTINGS
    {
        @Comment({ "Max CustomPayload packet size" })
        public int MAX_CAPACITY;
        @Comment({ "Should we block sending payload (book) packets while player does not hold a book in a hand?" })
        public boolean FAIL_WHEN_NOT_HOLDING_BOOK;
        @Comment({ "Should we block sending payload with MC|BSign when no book was used?" })
        public boolean BLOCK_BOOK_SIGNING_WHEN_NO_BOOK_PLACED;
        
        public PAYLOAD_SETTINGS() {
            this.MAX_CAPACITY = 4800;
            this.FAIL_WHEN_NOT_HOLDING_BOOK = true;
            this.BLOCK_BOOK_SIGNING_WHEN_NO_BOOK_PLACED = true;
        }
    }
    
    @Comment({ "How should we kick player when he tries to crash the server?" })
    public static class KICK
    {
        @Comment({ "Available types:", "0 - Instant channel close, kick without any message", "1 - Kick with message (a bit slower because we have to send packet to player that he is kicked)" })
        public int TYPE;
        public String MESSAGE;
        
        public KICK() {
            this.TYPE = 0;
            this.MESSAGE = "&cSpigotGuard &8> &7You have been kicked for trying to &ccrash&7 the server.";
        }
    }
    
    public static class BOOK
    {
        public int MAX_PAGES;
        @Comment({ "Set it to higher value if stuff like <too large page> has false positives" })
        public int MAX_PAGE_SIZE;
        @Comment({ "Max page size without color codes" })
        public int MAX_STRIPPED_PAGE_SIZE;
        @Comment({ "Should we block BOOK_AND_QUILL and WRITTEN_BOOK on server? People will be kicked for using it" })
        public boolean BAN_BOOKS;
        @Comment({ "Max similar pages. Default: 4, Crash books very often use the same page text like 40 times." })
        public int MAX_SIMILAR_PAGES;
        @Comment({ "Limit of 2-byte chars. 2-byte chars are non-english chars like China chars, Emojis and similars." })
        public int MAX_2BYTE_CHARS;
        @Comment({ "Set it to true if your server does not use books very often", "It is fast, lightweight check that detects crash books very fast", "You could enable it if people do not even have access to book_and_quill and written_book on your server" })
        public boolean FAST_CHECK;
        @Comment({ "Max fast-check nbt item length" })
        public int FAST_CHECK_MAX_LENGTH;
        
        public BOOK() {
            this.MAX_PAGES = 50;
            this.MAX_PAGE_SIZE = 900;
            this.MAX_STRIPPED_PAGE_SIZE = 256;
            this.BAN_BOOKS = false;
            this.MAX_SIMILAR_PAGES = 4;
            this.MAX_2BYTE_CHARS = 15;
            this.FAST_CHECK = false;
            this.FAST_CHECK_MAX_LENGTH = 500;
        }
    }
    
    public static class OTHER_NBT
    {
        public int MAX_LISTS;
        public int MAX_LIST_SIZE;
        @Comment({ "Set it to higher value if stuff like <too large list length> has false positives" })
        public int MAX_LIST_CONTENT;
        public int MAX_KEYS;
        public int MAX_ARRAY_SIZE;
        public int FIREWORKS_CHARGE_LIMIT;
        public int FIREWORK_LIMIT;
        public int BANNER_LIMIT;
        public boolean SIMPLE_NBT_LIMIT;
        public int MAX_SIMPLE_NBT_LIMIT;
        
        public OTHER_NBT() {
            this.MAX_LISTS = 10;
            this.MAX_LIST_SIZE = 50;
            this.MAX_LIST_CONTENT = 900;
            this.MAX_KEYS = 20;
            this.MAX_ARRAY_SIZE = 50;
            this.FIREWORKS_CHARGE_LIMIT = 800;
            this.FIREWORK_LIMIT = 300;
            this.BANNER_LIMIT = 200;
            this.SIMPLE_NBT_LIMIT = true;
            this.MAX_SIMPLE_NBT_LIMIT = 10000;
        }
    }
    
    public static class POSITION_CHECKS
    {
        @Comment({ "Should we allow POSITION packets with Integer.MAX_VALUE values? Phase cheats usually use it! It can also lag a server a bit." })
        public boolean ALLOW_INVALID_MOVEMENT_V1;
        @Comment({ "Should we allow POSITION packets with Double.MAX_VALUE values? Phase cheats usually use it! It can also lag a server a bit." })
        public boolean ALLOW_INVALID_MOVEMENT_V2;
        public boolean ALLOW_INVALID_VEHICLE_MOVEMENT_V3;
        public boolean ALLOW_INVALID_VEHICLE_MOVEMENT_V4;
        public boolean ALLOW_INVALID_VEHICLE_MOVEMENT_V5;
        public int MAX_DISTANCE_BETWEEN_PLAYER_AND_BLOCK_POSITION;
        
        public POSITION_CHECKS() {
            this.ALLOW_INVALID_MOVEMENT_V1 = true;
            this.ALLOW_INVALID_MOVEMENT_V2 = false;
            this.ALLOW_INVALID_VEHICLE_MOVEMENT_V3 = false;
            this.ALLOW_INVALID_VEHICLE_MOVEMENT_V4 = false;
            this.ALLOW_INVALID_VEHICLE_MOVEMENT_V5 = false;
            this.MAX_DISTANCE_BETWEEN_PLAYER_AND_BLOCK_POSITION = 30;
        }
    }
    
    public static class SKULL_CHECKS
    {
        @Comment({ "Should we block head/skull textures that do not start with http://textures.minecraft.net ?" })
        public boolean BLOCK_INVALID_HEAD_TEXTURE;
        
        public SKULL_CHECKS() {
            this.BLOCK_INVALID_HEAD_TEXTURE = true;
        }
    }
    
    public static class PLACE_CHECKS
    {
        @Comment({ "Should player be able to send PacketPlayInBlockPlace with WRITTEN_BOOK and similars when he is not holding it? It is surely exploit." })
        public boolean BLOCK_PLACING_BOOK_WHEN_NOT_HOLDING_IT;
        @Comment({ "Should player be able to send PacketPlayInBlockPlace with FIREWORK and similars when he is not holding it? It is surely exploit." })
        public boolean BLOCK_PLACING_FIREWORK_WHEN_NOT_HOLDING_IT;
        @Comment({ "Should player be able to send PacketPlayInBlockPlace with BANNER and similars when he is not holding it? It is surely exploit." })
        public boolean BLOCK_PLACING_BANNER_WHEN_NOT_HOLDING_IT;
        
        public PLACE_CHECKS() {
            this.BLOCK_PLACING_BOOK_WHEN_NOT_HOLDING_IT = true;
            this.BLOCK_PLACING_FIREWORK_WHEN_NOT_HOLDING_IT = true;
            this.BLOCK_PLACING_BANNER_WHEN_NOT_HOLDING_IT = true;
        }
    }
    
    public static class PACKET_DECODER
    {
        public boolean ENABLED;
        @Comment({ "Max packet decoding size" })
        public int MAX_MAIN_SIZE;
        public int MAX_WINDOW_SIZE;
        public int MAX_PLACE_SIZE;
        public int MAX_CREATIVE_SIZE;
        public int MAX_PAYLOAD_SIZE;
        @Create
        public VIAVERSION VIAVERSION;
        
        public PACKET_DECODER() {
            this.ENABLED = true;
            this.MAX_MAIN_SIZE = 5000;
            this.MAX_WINDOW_SIZE = 1500;
            this.MAX_PLACE_SIZE = 1500;
            this.MAX_CREATIVE_SIZE = 5000;
            this.MAX_PAYLOAD_SIZE = 3000;
        }
        
        @Comment({ "Some changes when you use our ViaVersion fork" })
        public static class VIAVERSION
        {
            @Comment({ "FOR ViaVersion - Max packet size when there item inside the packet" })
            public int MAX_ITEM_SIZE;
            @Comment({ "Should we ONLY check packet size inside ViaVersion if the item is book?" })
            public boolean CHECK_ONLY_BOOKS;
            @Comment({ "Should we block (clear) every BOOK (written books, book_and_quill) packet, even if it does not exceed packet size limit?", "It could be very useful if you do not allow using/crafting books (written books, book and quill) on your server and want to block every exploit faster" })
            public boolean CLEAR_ALL_BOOKS;
            
            public VIAVERSION() {
                this.MAX_ITEM_SIZE = 2100;
                this.CHECK_ONLY_BOOKS = true;
                this.CLEAR_ALL_BOOKS = false;
            }
        }
    }
    
    public static class HACKER_BOOK
    {
        public boolean ENABLED;
        public String CONTENT;
        public String NEXT_PAGE;
        
        public HACKER_BOOK() {
            this.ENABLED = true;
            this.CONTENT = "&n&cHello, &9&l{PLAYER}&r!%nl%Looks like the last time you were here you got kicked for suspicious activity and we think you tried to crash the server. To see details please open next page.";
            this.NEXT_PAGE = "&eIf it is our mistake, please &econtact the staff.";
        }
    }
    
    public static class INVENTORIES
    {
        @Create
        public MAIN_INVENTORY MAIN_INVENTORY;
        @Create
        public RECENT_DETECTIONS_INVENTORY RECENT_DETECTIONS_INVENTORY;
        @Create
        public PLAYER_INFO_INVENTORY PLAYER_INFO_INVENTORY;
        
        public static class MAIN_INVENTORY
        {
            public String NAME;
            public int SIZE;
            @Create
            public RELOAD_ITEM RELOAD_ITEM;
            @Create
            public RECENT_DETECTIONS_ITEM RECENT_DETECTIONS_ITEM;
            
            public MAIN_INVENTORY() {
                this.NAME = "&cSpigotGuard management";
                this.SIZE = 27;
            }
            
            public static class RELOAD_ITEM
            {
                public String MATERIAL;
                public String NAME;
                public List<String> LORE;
                public int SLOT;
                
                public RELOAD_ITEM() {
                    this.MATERIAL = "STICK";
                    this.NAME = "&cReload configs";
                    this.LORE = Arrays.asList("&8&l&m>&r &7Click to reload configuration files");
                    this.SLOT = 14;
                }
            }
            
            public static class RECENT_DETECTIONS_ITEM
            {
                public String MATERIAL;
                public String NAME;
                public List<String> LORE;
                public int SLOT;
                
                public RECENT_DETECTIONS_ITEM() {
                    this.MATERIAL = "DIAMOND_CHESTPLATE";
                    this.NAME = "&cRecent detections";
                    this.LORE = Arrays.asList("&8&l&m>&r &7Click to open inventory with recent detections", "&8&l&m>&r &7You will see who tried to crash the server in last time!");
                    this.SLOT = 12;
                }
            }
        }
        
        public static class RECENT_DETECTIONS_INVENTORY
        {
            public String NAME;
            public int SIZE;
            @Create
            public RECENT_DETECTION_ITEM RECENT_DETECTION_ITEM;
            @Create
            public NO_DETECTIONS_ITEM NO_DETECTIONS_ITEM;
            
            public RECENT_DETECTIONS_INVENTORY() {
                this.NAME = "&cRecent detections";
                this.SIZE = 27;
            }
            
            public static class RECENT_DETECTION_ITEM
            {
                public String NAME;
                public List<String> LORE;
                
                public RECENT_DETECTION_ITEM() {
                    this.NAME = "&c{PLAYER-NAME}";
                    this.LORE = Arrays.asList(" &7Packet name: &c{PACKET}", " &7Details: &c{DETAILS}", " &7Time of crash attempt: &c{TIME}", " &7Player last seen at: &c{PLAYER-LAST-SEEN}", " ", "   &9&lHow to fix it, if it is false positive/detection?", " &7{HOW-TO-FIX-FALSE-POSITIVE}");
                }
            }
            
            public static class NO_DETECTIONS_ITEM
            {
                public String NAME;
                public List<String> LORE;
                
                public NO_DETECTIONS_ITEM() {
                    this.NAME = "&cNo detections found";
                    this.LORE = Arrays.asList(" &7We did not find any people that tried to crash the server :(", " &7Looks like your players are afraid to try crashers!");
                }
            }
        }
        
        public static class PLAYER_INFO_INVENTORY
        {
            public String NAME;
            public int SIZE;
            @Create
            public CRASH_ATTEMPT_ITEM CRASH_ATTEMPT_ITEM;
            
            public PLAYER_INFO_INVENTORY() {
                this.NAME = "&cCrash attempts of {PLAYER}";
                this.SIZE = 27;
            }
            
            public static class CRASH_ATTEMPT_ITEM
            {
                public String NAME;
                public List<String> LORE;
                
                public CRASH_ATTEMPT_ITEM() {
                    this.NAME = "&c{TIME}";
                    this.LORE = Arrays.asList(" &7Packet name: &c{PACKET}", " &7Details: &c{DETAILS}", " &7Time of crash attempt: &c{TIME}", " &7Player was last seen at: &c{LAST-SEEN}", " &7IP: &c{IP}", " ", "   &9&lHow to fix it, if it is false positive/detection?", " &7{HOW-TO-FIX-FALSE-POSITIVE}");
                }
            }
        }
    }
    
    @Comment({ "Database Setup" })
    public static class SQL
    {
        @Comment({ "After how many days to remove players from the database, which have been on server and no longer entered. Use 0 or less to stop" })
        public int PURGE_TIME;
        @Comment({ "Inform console about purging" })
        public boolean PURGE_CONSOLE_INFO;
        @Comment({ "Settings for mysql" })
        public String HOSTNAME;
        public int PORT;
        public String USER;
        public String PASSWORD;
        public String DATABASE;
        @Comment({ "Available types: sqlite & mysql" })
        public String STORAGE_TYPE;
        
        public SQL() {
            this.PURGE_TIME = 10;
            this.PURGE_CONSOLE_INFO = true;
            this.HOSTNAME = "localhost";
            this.PORT = 3306;
            this.USER = "user";
            this.PASSWORD = "password";
            this.DATABASE = "database";
            this.STORAGE_TYPE = "sqlite";
        }
    }
}
