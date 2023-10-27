package xyz.yooniks.spigotguard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class FastAsyncWorldEditFix {
  public boolean isLoaded() {
    false;
    return (Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") != null);
  }
  
  public void fixConfig() {
    File file = new File("plugins/FastAsyncWorldEdit", "commands.yml");
    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    List list = yamlConfiguration.getStringList("BrushOptionsCommands.targetoffset.aliases");
    if (list.size() > 0) {
      SpigotGuardPlugin.getInstance().getLogger().warning("Found FastAsyncWorldEdit crash command bug and fixed it, a restart may be required to reload the changes in FAWE!");
      yamlConfiguration.set("BrushOptionsCommands.targetoffset.aliases", new ArrayList());
      yamlConfiguration.set("UtilityCommands./calc.aliases", new ArrayList());
    } 
    try {
      yamlConfiguration.save(file);
      false;
    } catch (IOException iOException) {
      SpigotGuardPlugin.getInstance().getLogger().warning("Could not override Fawe config! " + iOException.getMessage());
    } 
  }
}


/* Location:              X:\SpigotGuard v6.4.2\spigotguard-v6.4.2 Fully-deobf.jar!\xyz\yooniks\spigotguard\FastAsyncWorldEditFix.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */