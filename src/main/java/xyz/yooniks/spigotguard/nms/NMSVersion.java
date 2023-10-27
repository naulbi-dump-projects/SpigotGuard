package xyz.yooniks.spigotguard.nms;

public enum NMSVersion
{
    ONE_DOT_SEVEN_R4("v1_7", -1), 
    ONE_DOT_EIGHT_R3("v1_8", 0), 
    ONE_DOT_NINE_R2("v1_9", 1), 
    ONE_DOT_TVELVE_R1("v1_12", 2), 
    ONE_DOT_THIRTEEN("v1_13", 3), 
    ONE_DOT_FOURTEEN("v1_14", 4), 
    ONE_DOT_FIVETEEN("v1_15", 5), 
    ONE_DOT_SIXTEEN("v1_16", 6), 
    ONE_DOT_SIXTEEN_R2("v1_16_R2", 7), 
    UNSUPPORTED("unsupported", 8);
    
    String name;
    int version;
    
    private NMSVersion(final String name, final int version) {
        this.name = name;
        this.version = version;
    }
    
    public static NMSVersion find(final String version) {
        NMSVersion last = null;
        for (final NMSVersion nms : values()) {
            if (version.contains(nms.name)) {
                last = nms;
            }
        }
        if (last == null) {
            last = NMSVersion.UNSUPPORTED;
        }
        return last;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public String getName() {
        return this.name;
    }
}
