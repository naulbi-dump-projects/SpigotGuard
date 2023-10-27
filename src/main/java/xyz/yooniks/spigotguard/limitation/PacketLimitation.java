package xyz.yooniks.spigotguard.limitation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class PacketLimitation {
  private final Map limitablePacketMap = new HashMap();

  public void initialize(FileConfiguration var1) {
    boolean var10001;
    for(Iterator var2 = var1.getConfigurationSection("packet-limitter.limits").getKeys(false).iterator(); var2.hasNext(); var10001 = false) {
      String var3 = (String)var2.next();
      ConfigurationSection var4 = var1.getConfigurationSection("packet-limitter.limits." + var3);
      int var5 = var4.getInt("limit");
      boolean var6 = var4.getBoolean("cancel-only");
      this.limitablePacketMap.put(var3, new LimitablePacket(var5, var6));
    }

  }

  public boolean isLimitable(String var1) {
    return this.limitablePacketMap.containsKey(var1);
  }

  public LimitablePacket getLimit(String var1) {
    return (LimitablePacket)this.limitablePacketMap.get(var1);
  }
}
