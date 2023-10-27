package xyz.yooniks.spigotguard.limitation;

public class LimitablePacket
{
    private final int limit;
    private final boolean cancelOnly;
    
    public LimitablePacket(final int limit, final boolean cancelOnly) {
        this.limit = limit;
        this.cancelOnly = cancelOnly;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    public boolean isCancelOnly() {
        return this.cancelOnly;
    }
}
