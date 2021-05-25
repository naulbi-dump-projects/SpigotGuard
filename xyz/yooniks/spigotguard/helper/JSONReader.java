package xyz.yooniks.spigotguard.helper;

import java.net.*;
import java.nio.charset.*;
import java.io.*;
import org.json.*;

public class JSONReader
{
    private static String readAll(final Reader rd) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char)cp);
        }
        return sb.toString();
    }
    
    public static JSONObject readJsonFromUrl(final String url) throws IOException, JSONException {
        final InputStream is = new URL(url).openStream();
        try {
            final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            final String jsonText = readAll(rd);
            final JSONObject json = new JSONObject(jsonText);
            return json;
        }
        finally {
            is.close();
        }
    }
}
