package xyz.yooniks.spigotguard.network;

import org.bukkit.entity.*;
import us.myles.ViaVersion.*;

public class ViaVersionCheck
{
    public static boolean shouldChangeCompressor(final Player player) {
        final int playerVersion = ViaVersionPlugin.getInstance().getApi().getPlayerVersion((Object)player);
        return playerVersion == 47;
    }
}
