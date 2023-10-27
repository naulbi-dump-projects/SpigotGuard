package xyz.yooniks.spigotguard.helper;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class CustomBookOpenEvent extends Event implements Cancellable {
  private static final HandlerList handlers = new HandlerList();
  
  private Hand hand;
  
  private boolean cancelled;
  
  private ItemStack book;
  
  private final Player player;
  
  public Player getPlayer() {
    return this.player;
  }
  
  public ItemStack getBook() {
    return this.book;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public void setBook(ItemStack paramItemStack) {
    this.book = paramItemStack;
  }
  
  public Hand getHand() {
    return this.hand;
  }
  
  public HandlerList getHandlers() {
    return handlers;
  }
  
  public CustomBookOpenEvent(Player paramPlayer, ItemStack paramItemStack, boolean paramBoolean) {
    this.player = paramPlayer;
    this.book = paramItemStack;
    if (paramBoolean) {
      false;
      if (false)
        throw null; 
    } else {
    
    } 
    this.hand = Hand.MAIN_HAND;
  }
  
  public void setHand(Hand paramHand) {
    this.hand = paramHand;
  }
  
  public void setCancelled(boolean paramBoolean) {
    this.cancelled = paramBoolean;
  }
  
  public static HandlerList getHandlerList() {
    return handlers;
  }
  
  public enum Hand {
    MAIN_HAND, OFF_HAND;
    
    private static final Hand[] $VALUES = new Hand[] { MAIN_HAND, OFF_HAND };
  }
}