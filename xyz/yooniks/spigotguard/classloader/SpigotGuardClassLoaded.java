package xyz.yooniks.spigotguard.classloader;

import java.io.*;

public class SpigotGuardClassLoaded implements Serializable
{
    static final long serialVersionUID = 423116223121231L;
    private final Serializable serializable;
    
    public SpigotGuardClassLoaded(final Serializable serializable) {
        this.serializable = serializable;
    }
    
    public Serializable getSerializable() {
        return this.serializable;
    }
}
