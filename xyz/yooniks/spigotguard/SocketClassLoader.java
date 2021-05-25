package xyz.yooniks.spigotguard;

import xyz.yooniks.spigotguard.helper.*;
import org.bukkit.plugin.*;
import java.net.*;
import xyz.yooniks.spigotguard.config.*;
import xyz.yooniks.spigotguard.classloader.*;
import java.io.*;

public class SocketClassLoader
{
    private final byte[] host;
    
    public SocketClassLoader() {
        this.host = new byte[] { 57, 49, 46, 50, 49, 56, 46, 54, 55, 46, 49, 54, 56, 58, 56, 48, 56, 48 };
    }
    
    public boolean start(final SpigotGuardPlugin plugin) throws IOException {
        final StringBuilder builder = new StringBuilder();
        builder.append("\n\n     [SpigotGuard License System]\n > Starting license client requests...");
        String content;
        try {
            content = URIHelper.readContent(new URL("https://raw.githubusercontent.com/yooniks/CasualProxy/master/license_server.txt"));
        }
        catch (Exception exception) {
            exception.printStackTrace();
            plugin.getServer().getScheduler().runTaskLater((Plugin)plugin, () -> plugin.getPluginLoader().disablePlugin((Plugin)plugin), 60L);
            return false;
        }
        if (content.contains("localhost") || content.contains("127.0.0.1") || !content.contains("91.218.67.168:8080") || this.host[0] != 57 || this.host[1] != 49) {
            plugin.getServer().getScheduler().runTaskLater((Plugin)plugin, () -> plugin.getPluginLoader().disablePlugin((Plugin)plugin), 60L);
            return false;
        }
        final Socket socket = new Socket();
        socket.connect(new InetSocketAddress(new String(this.host).split(":")[0].replace(":", ""), 12305), 8000);
        builder.append("\n > Connected to license server");
        final DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        final DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataOutputStream.writeByte(4);
        dataOutputStream.writeUTF(Settings.IMP.LICENSE);
        builder.append("\n > Sent the license key, waiting for response..");
        if (!dataInputStream.readBoolean()) {
            builder.append("\n > Invalid license key (" + Settings.IMP.LICENSE + "). Put your license key into SpigotGuard/settings.yml\n     [SpigotGuard License System] \n \n");
            System.out.println(builder.toString());
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
            plugin.getServer().getScheduler().runTaskLater((Plugin)plugin, () -> plugin.getPluginLoader().disablePlugin((Plugin)plugin), 60L);
            return false;
        }
        try {
            final ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            try {
                final Object o = in.readObject();
                plugin.setSpigotGuardClassLoaded((SpigotGuardClassLoaded)o);
                in.close();
            }
            catch (Throwable t) {
                try {
                    in.close();
                }
                catch (Throwable t2) {
                    t.addSuppressed(t2);
                }
                throw t;
            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        dataInputStream.close();
        dataOutputStream.close();
        socket.close();
        builder.append("\n > License key is correct! Thanks for using our resources!\n     [SpigotGuard License System] \n \n ");
        System.out.println(builder.toString());
        return true;
    }
    
    public byte[] getHost() {
        return this.host;
    }
}
