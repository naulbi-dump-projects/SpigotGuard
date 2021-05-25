package xyz.yooniks.spigotguard.helper;

import org.apache.commons.lang.*;
import org.bukkit.*;
import xyz.yooniks.spigotguard.config.*;

public class MessageBuilder
{
    private String text;
    
    public MessageBuilder(final String text) {
        this.text = text;
    }
    
    public static MessageBuilder newBuilder(final String text) {
        return new MessageBuilder(text);
    }
    
    public MessageBuilder withField(final String field, final String value) {
        this.text = StringUtils.replace(this.text, field, value);
        return this;
    }
    
    public MessageBuilder coloured() {
        this.text = ChatColor.translateAlternateColorCodes('&', this.text);
        return this.withField(">>", "»");
    }
    
    public MessageBuilder stripped() {
        return this.withField("%nl%", "\n");
    }
    
    public MessageBuilder prefix() {
        return this.withField("{PREFIX}", Settings.IMP.MESSAGES.PREFIX);
    }
    
    @Override
    public String toString() {
        return this.text;
    }
}
