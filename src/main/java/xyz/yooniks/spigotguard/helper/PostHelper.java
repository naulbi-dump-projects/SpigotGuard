package xyz.yooniks.spigotguard.helper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PostHelper {
  public static String post(String var0, String var1) throws IOException {
    HttpURLConnection var2 = null;
    byte[] var3 = var1.getBytes(StandardCharsets.UTF_8);
    URL var4 = new URL(var0);
    var2 = (HttpURLConnection)var4.openConnection();
    var2.setDoOutput(true);
    var2.setRequestMethod("POST");
    var2.setRequestProperty("User-Agent", "Java client");
    var2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    DataOutputStream var5 = new DataOutputStream(var2.getOutputStream());

    boolean var10000;
    try {
      var5.write(var3);
    } catch (Throwable var11) {
      try {
        var5.close();
      } catch (Throwable var10) {
        var11.addSuppressed(var10);
        throw var11;
      }

      var10000 = false;
      throw var11;
    }

    var5.close();
    BufferedReader var6 = new BufferedReader(new InputStreamReader(var2.getInputStream()));

    StringBuilder var13;
    try {
      var13 = new StringBuilder();

      String var7;
      while((var7 = var6.readLine()) != null) {
        var13.append(var7);
      }
    } catch (Throwable var12) {
      try {
        var6.close();
      } catch (Throwable var9) {
        var12.addSuppressed(var9);
        throw var12;
      }

      var10000 = false;
      throw var12;
    }

    var6.close();
    return String.valueOf(var13);
  }
}
