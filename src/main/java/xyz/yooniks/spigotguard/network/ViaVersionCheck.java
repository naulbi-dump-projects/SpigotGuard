package xyz.yooniks.spigotguard.network;

import com.viaversion.viaversion.ViaVersionPlugin;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.ViaVersionPlugin;

public class ViaVersionCheck {
  private static boolean viaVersionOld = false;
  
  private static boolean viaVersionNew = false;
  
  static {
    try {
      viaVersionOld = true;
      false;
    } catch (Exception exception) {
      viaVersionOld = false;
    } 
    try {
      viaVersionNew = true;
      false;
    } catch (Exception exception) {
      viaVersionNew = false;
    } 
  }
  
  public static boolean shouldChangeCompressor(Player paramPlayer) {
    if (viaVersionOld)
      try {
        int i = ViaVersionPlugin.getInstance().getApi().getPlayerVersion(paramPlayer);
        System.out.println("[SG-Old VV] Player " + paramPlayer.getName() + " version: " + i);
        false;
        return (i == 47);
      } catch (Exception exception) {
        exception.printStackTrace();
      }  
    if (viaVersionNew)
      try {
        int i = ViaVersionPlugin.getInstance().getApi().getPlayerVersion(paramPlayer);
        System.out.println("[SG-New VV] Player " + paramPlayer.getName() + " version: " + i);
        false;
        return (i == 47);
      } catch (Exception exception) {
        exception.printStackTrace();
      }  
    return false;
  }
}