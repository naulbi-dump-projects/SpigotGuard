package xyz.yooniks.spigotguard.classloader;

import java.io.Serializable;

public class SpigotGuardClassLoaded implements Serializable {
  static final long serialVersionUID = 423116223121231L;
  
  private final Serializable serializable;
  
  public Serializable getSerializable() {
    return this.serializable;
  }
  
  public SpigotGuardClassLoaded(Serializable paramSerializable) {
    this.serializable = paramSerializable;
  }
}