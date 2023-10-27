package xyz.upperlevel.spigot.book;

import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.event.*;
import org.bukkit.*;
import net.md_5.bungee.api.chat.*;
import org.bukkit.inventory.meta.*;
import java.util.*;

public final class BookUtil
{
    private static final boolean canTranslateDirectly;
    
    public static void openPlayer(final Player p, final ItemStack book) {
        final CustomBookOpenEvent event = new CustomBookOpenEvent(p, book, false);
        Bukkit.getPluginManager().callEvent((Event)event);
        if (event.isCancelled()) {
            return;
        }
        p.closeInventory();
        final ItemStack hand = p.getItemInHand();
        p.setItemInHand(event.getBook());
        p.updateInventory();
        NmsBookHelper.openBook(p, event.getBook(), event.getHand() == CustomBookOpenEvent.Hand.OFF_HAND);
        p.setItemInHand(hand);
        p.updateInventory();
    }
    
    public static BookBuilder writtenBook() {
        return new BookBuilder(new ItemStack(Material.WRITTEN_BOOK));
    }
    
    static {
        boolean success = true;
        try {
            ChatColor.BLACK.asBungee();
        }
        catch (NoSuchMethodError e) {
            success = false;
        }
        canTranslateDirectly = success;
    }
    
    public interface ClickAction
    {
        default ClickAction runCommand(final String command) {
            return new SimpleClickAction(ClickEvent.Action.RUN_COMMAND, command);
        }
        
        @Deprecated
        default ClickAction suggestCommand(final String command) {
            return new SimpleClickAction(ClickEvent.Action.SUGGEST_COMMAND, command);
        }
        
        default ClickAction openUrl(final String url) {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return new SimpleClickAction(ClickEvent.Action.OPEN_URL, url);
            }
            throw new IllegalArgumentException("Invalid url: \"" + url + "\", it should start with http:// or https://");
        }
        
        ClickEvent.Action action();
        
        String value();
        
        public static class SimpleClickAction implements ClickAction
        {
            private final ClickEvent.Action action;
            private final String value;
            
            public SimpleClickAction(final ClickEvent.Action action, final String value) {
                this.action = action;
                this.value = value;
            }
            
            @Override
            public ClickEvent.Action action() {
                return this.action;
            }
            
            @Override
            public String value() {
                return this.value;
            }
        }
    }
    
    public interface HoverAction
    {
        default HoverAction showText(final BaseComponent... text) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_TEXT, text);
        }
        
        default HoverAction showText(final String text) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { (BaseComponent)new TextComponent(text) });
        }
        
        default HoverAction showItem(final BaseComponent... item) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ITEM, item);
        }
        
        default HoverAction showItem(final ItemStack item) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ITEM, NmsBookHelper.itemToComponents(item));
        }
        
        default HoverAction showStatistic(final String statisticId) {
            return new SimpleHoverAction(HoverEvent.Action.SHOW_ACHIEVEMENT, new BaseComponent[] { (BaseComponent)new TextComponent("statistic." + statisticId) });
        }
        
        HoverEvent.Action action();
        
        BaseComponent[] value();
        
        public static class SimpleHoverAction implements HoverAction
        {
            private final HoverEvent.Action action;
            private final BaseComponent[] value;
            
            public SimpleHoverAction(final HoverEvent.Action action, final BaseComponent... value) {
                this.action = action;
                this.value = value;
            }
            
            @Override
            public BaseComponent[] value() {
                return this.value;
            }
            
            @Override
            public HoverEvent.Action action() {
                return this.action;
            }
        }
    }
    
    public static class BookBuilder
    {
        private final BookMeta meta;
        private final ItemStack book;
        
        public BookBuilder(final ItemStack book) {
            this.book = book;
            this.meta = (BookMeta)book.getItemMeta();
        }
        
        public BookBuilder title(final String title) {
            this.meta.setTitle(title);
            return this;
        }
        
        public BookBuilder author(final String author) {
            this.meta.setAuthor(author);
            return this;
        }
        
        public BookBuilder pagesRaw(final String... pages) {
            this.meta.setPages(pages);
            return this;
        }
        
        public BookBuilder pagesRaw(final List<String> pages) {
            this.meta.setPages((List)pages);
            return this;
        }
        
        public BookBuilder pages(final BaseComponent[]... pages) {
            NmsBookHelper.setPages(this.meta, pages);
            return this;
        }
        
        public BookBuilder pages(final List<BaseComponent[]> pages) {
            NmsBookHelper.setPages(this.meta, pages.toArray(new BaseComponent[0][]));
            return this;
        }
        
        public ItemStack build() {
            this.book.setItemMeta((ItemMeta)this.meta);
            return this.book;
        }
    }
    
    public static class PageBuilder
    {
        private final List<BaseComponent> text;
        
        public PageBuilder() {
            this.text = new ArrayList<BaseComponent>();
        }
        
        public static PageBuilder of(final String text) {
            return new PageBuilder().add(text);
        }
        
        public static PageBuilder of(final BaseComponent text) {
            return new PageBuilder().add(text);
        }
        
        public static PageBuilder of(final BaseComponent... text) {
            final PageBuilder res = new PageBuilder();
            for (final BaseComponent b : text) {
                res.add(b);
            }
            return res;
        }
        
        public PageBuilder add(final String text) {
            this.text.add(TextBuilder.of(text).build());
            return this;
        }
        
        public PageBuilder add(final BaseComponent component) {
            this.text.add(component);
            return this;
        }
        
        public PageBuilder add(final BaseComponent... components) {
            this.text.addAll(Arrays.asList(components));
            return this;
        }
        
        public PageBuilder add(final Collection<BaseComponent> components) {
            this.text.addAll(components);
            return this;
        }
        
        public PageBuilder newLine() {
            this.text.add((BaseComponent)new TextComponent("\n"));
            return this;
        }
        
        public BaseComponent[] build() {
            return this.text.toArray(new BaseComponent[0]);
        }
    }
    
    public static class TextBuilder
    {
        private String text;
        private ClickAction onClick;
        private HoverAction onHover;
        private ChatColor color;
        private ChatColor[] style;
        
        public TextBuilder() {
            this.text = "";
            this.onClick = null;
            this.onHover = null;
            this.color = ChatColor.BLACK;
        }
        
        public static TextBuilder of(final String text) {
            return new TextBuilder().text(text);
        }
        
        public TextBuilder color(final ChatColor color) {
            if (color != null && !color.isColor()) {
                throw new IllegalArgumentException("Argument isn't a color!");
            }
            this.color = color;
            return this;
        }
        
        public void setStyle(final ChatColor[] style) {
            this.style = style;
        }
        
        public TextBuilder text(final String text) {
            this.text = text;
            return this;
        }
        
        public TextBuilder onClick(final ClickAction action) {
            this.onClick = action;
            return this;
        }
        
        public TextBuilder onHover(final HoverAction action) {
            this.onHover = action;
            return this;
        }
        
        public TextBuilder style(final ChatColor... style) {
            for (final ChatColor c : style) {
                if (!c.isFormat()) {
                    throw new IllegalArgumentException("Argument isn't a style!");
                }
            }
            this.style = style;
            return this;
        }
        
        public BaseComponent build() {
            final TextComponent res = new TextComponent(this.text);
            if (this.onClick != null) {
                res.setClickEvent(new ClickEvent(this.onClick.action(), this.onClick.value()));
            }
            if (this.onHover != null) {
                res.setHoverEvent(new HoverEvent(this.onHover.action(), this.onHover.value()));
            }
            if (this.color != null) {
                if (BookUtil.canTranslateDirectly) {
                    res.setColor(this.color.asBungee());
                }
                else {
                    res.setColor(net.md_5.bungee.api.ChatColor.getByChar(this.color.getChar()));
                }
            }
            if (this.style != null) {
                for (final ChatColor c : this.style) {
                    switch (c) {
                        case MAGIC: {
                            res.setObfuscated(Boolean.valueOf(true));
                            break;
                        }
                        case BOLD: {
                            res.setBold(Boolean.valueOf(true));
                            break;
                        }
                        case STRIKETHROUGH: {
                            res.setStrikethrough(Boolean.valueOf(true));
                            break;
                        }
                        case UNDERLINE: {
                            res.setUnderlined(Boolean.valueOf(true));
                            break;
                        }
                        case ITALIC: {
                            res.setItalic(Boolean.valueOf(true));
                            break;
                        }
                    }
                }
            }
            return (BaseComponent)res;
        }
    }
}
