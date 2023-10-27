package xyz.yooniks.spigotguard.limitation;

public class LimitablePacket {
  private final int limit;
  
  private final boolean cancelOnly;
  
  public LimitablePacket(int paramInt, boolean paramBoolean) {
    this.limit = paramInt;
    this.cancelOnly = paramBoolean;
  }
  
  public boolean isCancelOnly() {
    return this.cancelOnly;
  }
  
  public int getLimit() {
    return this.limit;
  }
}


/* Location:              X:\SpigotGuard v6.4.2\spigotguard-v6.4.2 Fully-deobf.jar!\xyz\yooniks\spigotguard\limitation\LimitablePacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */