package xyz.yooniks.spigotguard.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public final class BookUtil {
  private static final boolean canTranslateDirectly;
  
  static {
    boolean bool = true;
    try {
      false;
    } catch (NoSuchMethodError noSuchMethodError) {
      bool = false;
    } 
    canTranslateDirectly = bool;
  }
  
  public static void openPlayer(Player paramPlayer, ItemStack paramItemStack) {
    CustomBookOpenEvent customBookOpenEvent = new CustomBookOpenEvent(paramPlayer, paramItemStack, false);
    Bukkit.getPluginManager().callEvent(customBookOpenEvent);
  }
  
  public static BookBuilder writtenBook() {
    return new BookBuilder(new ItemStack(Material.WRITTEN_BOOK));
  }
  
  public static interface ClickAction {
    String value();
    
    ClickEvent.Action action();
    
    static ClickAction openUrl(String param1String) {
      if (param1String.startsWith("http://") || param1String.startsWith("https://"))
        return new SimpleClickAction(ClickEvent.Action.OPEN_URL, param1String); 
      throw new IllegalArgumentException("Invalid url: \"" + param1String + "\", it should start with http:// or https://");
    }
    
    static ClickAction runCommand(String param1String) {
      return new SimpleClickAction(ClickEvent.Action.RUN_COMMAND, param1String);
    }
    
    @Deprecated
    static ClickAction suggestCommand(String param1String) {
      return new SimpleClickAction(ClickEvent.Action.SUGGEST_COMMAND, param1String);
    }
    
    public static class SimpleClickAction implements ClickAction {
      private final ClickEvent.Action action;
      
      private final String value;
      
      public String value() {
        return this.value;
      }
      
      public ClickEvent.Action action() {
        return this.action;
      }
      
      public SimpleClickAction(ClickEvent.Action param2Action, String param2String) {
        this.action = param2Action;
        this.value = param2String;
      }
    }
  }
  
  public static class PageBuilder {
    private final List<BaseComponent> text = new ArrayList<>();
    
    public BaseComponent[] build() {
      return this.text.<BaseComponent>toArray(new BaseComponent[0]);
    }
    
    public PageBuilder add(Collection<BaseComponent> param1Collection) {
      return this;
    }
    
    public PageBuilder newLine() {
      return this;
    }
    
    public static PageBuilder of(BaseComponent... var0) {
      PageBuilder var1 = new PageBuilder();
      BaseComponent[] var2 = var0;
      int var3 = var0.length;

      boolean var10001;
      for(int var4 = 0; var4 < var3; var10001 = false) {
        BaseComponent var5 = var2[var4];
        var1.add(var5);
        ++var4;
      }

      return var1;
    }
    
    public PageBuilder add(String param1String) {
      return this;
    }
    
    public PageBuilder add(BaseComponent param1BaseComponent) {
      return this;
    }
    
    public static PageBuilder of(String param1String) {
      return (new PageBuilder()).add(param1String);
    }
    
    public static PageBuilder of(BaseComponent param1BaseComponent) {
      return (new PageBuilder()).add(param1BaseComponent);
    }
    
    public PageBuilder add(BaseComponent... param1VarArgs) {
      return this;
    }
  }
  
  public static interface HoverAction {
    static HoverAction showText(String param1String) {
      return new SimpleHoverAction(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { (BaseComponent)new TextComponent(param1String) });
    }
    
    static HoverAction showItem(ItemStack param1ItemStack) {
      return new SimpleHoverAction(HoverEvent.Action.SHOW_ITEM, NmsBookHelper.itemToComponents(param1ItemStack));
    }
    
    static HoverAction showItem(BaseComponent... param1VarArgs) {
      return new SimpleHoverAction(HoverEvent.Action.SHOW_ITEM, param1VarArgs);
    }
    
    static HoverAction showStatistic(String param1String) {
      return new SimpleHoverAction(HoverEvent.Action.SHOW_ACHIEVEMENT, new BaseComponent[] { (BaseComponent)new TextComponent("statistic." + param1String) });
    }
    
    BaseComponent[] value();
    
    static HoverAction showText(BaseComponent... param1VarArgs) {
      return new SimpleHoverAction(HoverEvent.Action.SHOW_TEXT, param1VarArgs);
    }
    
    HoverEvent.Action action();
    
    public static class SimpleHoverAction implements HoverAction {
      private final HoverEvent.Action action;
      
      private final BaseComponent[] value;
      
      public BaseComponent[] value() {
        return this.value;
      }
      
      public SimpleHoverAction(HoverEvent.Action param2Action, BaseComponent... param2VarArgs) {
        this.action = param2Action;
        this.value = param2VarArgs;
      }
      
      public HoverEvent.Action action() {
        return this.action;
      }
    }
  }
  
  public static class BookBuilder {
    private final BookMeta meta;
    
    private final ItemStack book;
    
    public BookBuilder pagesRaw(List<String> param1List) {
      this.meta.setPages(param1List);
      return this;
    }
    
    public BookBuilder pages(List<BaseComponent[]> param1List) {
      NmsBookHelper.setPages(this.meta, param1List.<BaseComponent[]>toArray(new BaseComponent[0][]));
      return this;
    }
    
    public BookBuilder pagesRaw(String... param1VarArgs) {
      this.meta.setPages(param1VarArgs);
      return this;
    }
    
    public BookBuilder(ItemStack param1ItemStack) {
      this.book = param1ItemStack;
      this.meta = (BookMeta)param1ItemStack.getItemMeta();
    }
    
    public BookBuilder title(String param1String) {
      return this;
    }
    
    public BookBuilder author(String param1String) {
      this.meta.setAuthor(param1String);
      return this;
    }
    
    public ItemStack build() {
      return this.book;
    }
    
    public BookBuilder pages(BaseComponent[]... param1VarArgs) {
      NmsBookHelper.setPages(this.meta, param1VarArgs);
      return this;
    }
  }
  
  public static class TextBuilder {
    private String text = "";
    
    private BookUtil.ClickAction onClick = null;
    
    private BookUtil.HoverAction onHover = null;
    
    private ChatColor[] style;
    
    private ChatColor color = ChatColor.BLACK;
    
    public TextBuilder style(ChatColor... param1VarArgs) {
      ChatColor[] arrayOfChatColor = param1VarArgs;
      int i = arrayOfChatColor.length;
      byte b = 0;
      while (b < i) {
        ChatColor chatColor = arrayOfChatColor[b];
        if (!chatColor.isFormat())
          throw new IllegalArgumentException("Argument isn't a style!"); 
        b++;
        false;
      } 
      this.style = param1VarArgs;
      return this;
    }
    
    public TextBuilder onClick(BookUtil.ClickAction param1ClickAction) {
      this.onClick = param1ClickAction;
      return this;
    }
    
    public static TextBuilder of(String param1String) {
      return (new TextBuilder()).text(param1String);
    }
    
    public TextBuilder color(ChatColor param1ChatColor) {
      if (param1ChatColor != null && !param1ChatColor.isColor())
        throw new IllegalArgumentException("Argument isn't a color!"); 
      this.color = param1ChatColor;
      return this;
    }
    
    public TextBuilder text(String param1String) {
      this.text = param1String;
      return this;
    }
    
    public BaseComponent build() {
      TextComponent textComponent = new TextComponent(this.text);
      if (this.onClick != null)
        textComponent.setClickEvent(new ClickEvent(this.onClick.action(), this.onClick.value())); 
      if (this.onHover != null)
        textComponent.setHoverEvent(new HoverEvent(this.onHover.action(), this.onHover.value())); 
      if (this.color != null)
        if (BookUtil.canTranslateDirectly) {
          textComponent.setColor(this.color.asBungee());
        } else {
          textComponent.setColor(ChatColor.getByChar(this.color.getChar()));
        }  
      if (this.style != null) {
        ChatColor[] arrayOfChatColor = this.style;
        int i = arrayOfChatColor.length;
        byte b = 0;
        while (b < i) {
          ChatColor chatColor = arrayOfChatColor[b];
          switch (chatColor) {
            case MAGIC:
              textComponent.setObfuscated(Boolean.valueOf(true));
              break;
            case BOLD:
              textComponent.setBold(Boolean.valueOf(true));
              break;
            case STRIKETHROUGH:
              textComponent.setStrikethrough(Boolean.valueOf(true));
              break;
            case UNDERLINE:
              textComponent.setUnderlined(Boolean.valueOf(true));
              break;
            case ITALIC:
              textComponent.setItalic(Boolean.valueOf(true));
              break;
          } 
          b++;
          false;
        } 
      } 
      return (BaseComponent)textComponent;
    }
    
    public void setStyle(ChatColor[] param1ArrayOfChatColor) {
      this.style = param1ArrayOfChatColor;
    }
    
    public TextBuilder onHover(BookUtil.HoverAction param1HoverAction) {
      this.onHover = param1HoverAction;
      return this;
    }
  }
}