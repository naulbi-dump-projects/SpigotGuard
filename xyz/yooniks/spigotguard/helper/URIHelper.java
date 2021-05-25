package xyz.yooniks.spigotguard.helper;

import java.io.*;
import java.net.*;

public final class URIHelper
{
    private URIHelper() {
    }
    
    public static String readContent(final URL url) throws Exception {
        final StringBuilder content = new StringBuilder();
        final URLConnection urlConnection = url.openConnection();
        urlConnection.setConnectTimeout(7500);
        urlConnection.setReadTimeout(7500);
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            content.append(line);
        }
        bufferedReader.close();
        return content.toString();
    }
}
