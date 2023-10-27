package xyz.yooniks.spigotguard.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public final class URIHelper {
  public static String readContent(URL var0) throws Exception {
    StringBuilder var1 = new StringBuilder();
    URLConnection var2 = var0.openConnection();
    var2.setConnectTimeout(7500);
    var2.setReadTimeout(7500);
    BufferedReader var3 = new BufferedReader(new InputStreamReader(var2.getInputStream()));

    String var4;
    while((var4 = var3.readLine()) != null) {
      var1.append(var4);
    }

    var3.close();
    return String.valueOf(var1);
  }

  private URIHelper() {
  }
}
