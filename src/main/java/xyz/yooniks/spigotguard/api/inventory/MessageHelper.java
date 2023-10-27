package xyz.yooniks.spigotguard.api.inventory;

import org.bukkit.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class MessageHelper
{
    private MessageHelper() {
    }
    
    public static String colored(final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    public static List<String> colored(final List<String> texts) {
        return texts.stream().map((Function<? super Object, ?>)MessageHelper::colored).collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
    }
}
