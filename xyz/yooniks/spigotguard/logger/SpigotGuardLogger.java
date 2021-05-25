package xyz.yooniks.spigotguard.logger;

import java.util.logging.*;
import xyz.yooniks.spigotguard.*;

public class SpigotGuardLogger
{
    private static final Logger LOGGER;
    
    public static void log(final Level level, final String message, final Object... params) {
        SpigotGuardLogger.LOGGER.log(level, message, params);
    }
    
    public static void exception(final String message, final Exception ex) {
        SpigotGuardLogger.LOGGER.log(Level.WARNING, message, ex);
    }
    
    static {
        LOGGER = ((SpigotGuardPlugin)SpigotGuardPlugin.getPlugin((Class)SpigotGuardPlugin.class)).getLogger();
    }
}
