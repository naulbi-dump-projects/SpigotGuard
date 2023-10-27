package xyz.yooniks.spigotguard.nms;

public enum NMSVersion {
  ONE_DOT_SEVEN_R4("v1_7", -1),
  ONE_DOT_EIGHT_R3("v1_8", 0),
  ONE_DOT_NINE_R2("v1_9", 1),
  ONE_DOT_TVELVE_R1("v1_12", 2),
  ONE_DOT_THIRTEEN("v1_13", 3),
  ONE_DOT_FOURTEEN("v1_14", 4),
  ONE_DOT_FIVETEEN("v1_15", 5),
  ONE_DOT_SIXTEEN("v1_16", 6),
  ONE_DOT_SIXTEEN_R2("v1_16_R2", 7),
  ONE_DOT_SIXTEEN_R3("v1_16_R3", 8),
  UNSUPPORTED("unsupported", 8);
  
  String name;
  
  int version;
  
  private static final NMSVersion[] $VALUES = new NMSVersion[] { 
      ONE_DOT_SEVEN_R4, ONE_DOT_EIGHT_R3, ONE_DOT_NINE_R2, ONE_DOT_TVELVE_R1, ONE_DOT_THIRTEEN, ONE_DOT_FOURTEEN, ONE_DOT_FIVETEEN, ONE_DOT_SIXTEEN, ONE_DOT_SIXTEEN_R2, ONE_DOT_SIXTEEN_R3, 
      UNSUPPORTED };
  
  NMSVersion(String paramString1, int paramInt1) {
    this.name = paramString1;
    this.version = paramInt1;
  }
  
  public static NMSVersion find(String paramString) {
    NMSVersion nMSVersion = null;
    NMSVersion[] arrayOfNMSVersion = values();
    int i = arrayOfNMSVersion.length;
    byte b = 0;
    while (b < i) {
      NMSVersion nMSVersion1 = arrayOfNMSVersion[b];
      if (paramString.contains(nMSVersion1.name))
        nMSVersion = nMSVersion1; 
      b++;
      false;
    } 
    if (nMSVersion == null)
      nMSVersion = UNSUPPORTED; 
    return nMSVersion;
  }
  
  public int getVersion() {
    return this.version;
  }
  
  public String getName() {
    return this.name;
  }
}