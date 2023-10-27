package xyz.yooniks.spigotguard.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import xyz.yooniks.spigotguard.event.ExploitDetails;
import xyz.yooniks.spigotguard.network.PacketInjector;

public class User implements Serializable {
  private final List<ExploitDetails> attempts = new ArrayList<>();
  
  private final Map<String, Integer> packetsSent;
  
  private PacketInjector packetInjector;
  
  private String ip;
  
  private String name;
  
  private boolean injectedPacketDecoder = false;
  
  private final UUID id;
  
  private long lastJoin;
  
  public PacketInjector getPacketInjector() {
    return this.packetInjector;
  }
  
  public UUID getId() {
    return this.id;
  }
  
  public int increaseAndGetReceivedPackets(String paramString) {
    return ((Integer)this.packetsSent.getOrDefault(paramString, Integer.valueOf(0))).intValue();
  }
  
  public void setInjectedPacketDecoder(boolean paramBoolean) {
    this.injectedPacketDecoder = paramBoolean;
  }
  
  public void setPacketInjector(PacketInjector paramPacketInjector) {
    this.packetInjector = paramPacketInjector;
  }
  
  public void setLastJoin(long paramLong) {
    this.lastJoin = paramLong;
  }
  
  public String getIp() {
    return this.ip;
  }
  
  public void sortAttempts() {
    this.attempts.sort(Comparator.comparing(User::lambda$sortAttempts$0));
  }
  
  public String getName() {
    return this.name;
  }
  
  public long getLastJoin() {
    return this.lastJoin;
  }
  
  public void addAttempts(List<ExploitDetails> paramList) {}
  
  public void setName(String paramString) {
    this.name = paramString;
  }
  
  public void cleanup() {
    this.packetsSent.clear();
  }
  
  public boolean isInjectedPacketDecoder() {
    return this.injectedPacketDecoder;
  }
  
  public void setIp(String paramString) {
    this.ip = paramString;
  }
  
  public User(String paramString1, String paramString2, UUID paramUUID) {
    this.name = paramString1;
    this.ip = paramString2;
    this.id = paramUUID;
    this.packetsSent = new HashMap<>();
  }
  
  public List<ExploitDetails> getAttempts() {
    return new ArrayList<>(this.attempts);
  }
  
  private static Date lambda$sortAttempts$0(ExploitDetails paramExploitDetails) {
    return new Date(paramExploitDetails.getTime());
  }
  
  public Map<String, Integer> getPacketsSent() {
    return this.packetsSent;
  }
}