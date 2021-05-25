package xyz.upperlevel.spigot.book;

import org.bukkit.event.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;

public class CustomBookOpenEvent extends Event implements Cancellable
{
    private static final HandlerList handlers;
    private final Player player;
    private boolean cancelled;
    private Hand hand;
    private ItemStack book;
    
    public CustomBookOpenEvent(final Player player, final ItemStack book, final boolean offHand) {
        this.player = player;
        this.book = book;
        this.hand = (offHand ? Hand.OFF_HAND : Hand.MAIN_HAND);
    }
    
    public static HandlerList getHandlerList() {
        return CustomBookOpenEvent.handlers;
    }
    
    public HandlerList getHandlers() {
        return CustomBookOpenEvent.handlers;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public Hand getHand() {
        return this.hand;
    }
    
    public void setHand(final Hand hand) {
        this.hand = hand;
    }
    
    public ItemStack getBook() {
        return this.book;
    }
    
    public void setBook(final ItemStack book) {
        this.book = book;
    }
    
    static {
        handlers = new HandlerList();
    }
    
    public enum Hand
    {
        MAIN_HAND, 
        OFF_HAND;
    }
}
