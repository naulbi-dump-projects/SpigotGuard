package xyz.yooniks.spigotguard.user;

import java.io.*;
import xyz.yooniks.spigotguard.event.*;
import xyz.yooniks.spigotguard.network.*;
import java.util.*;

public class User implements Serializable
{
    private final UUID id;
    private final Map<String, Integer> packetsSent;
    private final List<ExploitDetails> attempts;
    private String name;
    private String ip;
    private PacketInjector packetInjector;
    private long lastJoin;
    private boolean injectedPacketDecoder;
    
    public User(final String name, final String ip, final UUID id) {
        this.attempts = new ArrayList<ExploitDetails>();
        this.injectedPacketDecoder = false;
        this.name = name;
        this.ip = ip;
        this.id = id;
        this.packetsSent = new HashMap<String, Integer>();
    }
    
    public boolean isInjectedPacketDecoder() {
        return this.injectedPacketDecoder;
    }
    
    public void setInjectedPacketDecoder(final boolean injectedPacketDecoder) {
        this.injectedPacketDecoder = injectedPacketDecoder;
    }
    
    public String getIp() {
        return this.ip;
    }
    
    public void setIp(final String ip) {
        this.ip = ip;
    }
    
    public UUID getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public PacketInjector getPacketInjector() {
        return this.packetInjector;
    }
    
    public void setPacketInjector(final PacketInjector packetInjector) {
        this.packetInjector = packetInjector;
    }
    
    public int increaseAndGetReceivedPackets(final String packet) {
        final int currentPackets = this.packetsSent.getOrDefault(packet, 0);
        this.packetsSent.put(packet, currentPackets + 1);
        return currentPackets;
    }
    
    public void addAttempts(final List<ExploitDetails> attempts) {
        this.attempts.addAll(attempts);
    }
    
    public Map<String, Integer> getPacketsSent() {
        return this.packetsSent;
    }
    
    public List<ExploitDetails> getAttempts() {
        return new ArrayList<ExploitDetails>(this.attempts);
    }
    
    public void sortAttempts() {
        this.attempts.sort(Comparator.comparing(o -> new Date(o.getTime())));
    }
    
    public long getLastJoin() {
        return this.lastJoin;
    }
    
    public void setLastJoin(final long lastJoin) {
        this.lastJoin = lastJoin;
    }
    
    public void cleanup() {
        this.packetsSent.clear();
    }
}
