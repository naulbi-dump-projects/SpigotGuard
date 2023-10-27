package xyz.yooniks.spigotguard.helper;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import xyz.yooniks.spigotguard.config.Settings;

public class MessageBuilder {
  private String text;
  
  public static MessageBuilder newBuilder(String paramString) {
    return new MessageBuilder(paramString);
  }
  
  public MessageBuilder stripped() {
    return withField("%nl%", "\n");
  }
  
  public MessageBuilder(String paramString) {
    this.text = paramString;
  }
  
  public MessageBuilder coloured() {
    this.text = ChatColor.translateAlternateColorCodes('&', this.text);
    return withField(">>", "»");
  }
  
  public MessageBuilder withField(String paramString1, String paramString2) {
    this.text = StringUtils.replace(this.text, paramString1, paramString2);
    return this;
  }
  
  public MessageBuilder prefix() {
    return withField("{PREFIX}", Settings.IMP.MESSAGES.PREFIX);
  }
  
  public String toString() {
    return this.text;
  }
}