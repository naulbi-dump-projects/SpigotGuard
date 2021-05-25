package xyz.yooniks.spigotguard.listener;

import org.bukkit.event.block.*;
import xyz.yooniks.spigotguard.*;
import java.util.logging.*;
import xyz.yooniks.spigotguard.logger.*;
import java.nio.charset.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;

public class SignChangeListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSignChange(final SignChangeEvent event) {
        SpigotGuardPlugin.getInstance().getSpigotGuardClassLoaded().getSerializable();
        final Player player = event.getPlayer();
        for (final String line : event.getLines()) {
            if (line.length() >= 46) {
                event.setCancelled(true);
                SpigotGuardLogger.log(Level.INFO, player.getName() + " -> Too long sign line!", new Object[0]);
                return;
            }
            if (line.getBytes(StandardCharsets.UTF_8).length > 34) {
                event.setCancelled(true);
                SpigotGuardLogger.log(Level.INFO, player.getName() + " -> Too many bytes in sign line!", new Object[0]);
                return;
            }
        }
    }
}
