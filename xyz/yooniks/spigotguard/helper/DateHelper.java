package xyz.yooniks.spigotguard.helper;

import java.util.*;
import java.text.*;

public final class DateHelper
{
    private static final DateFormat SIMPLE_DATE_FORMAT;
    
    private DateHelper() {
    }
    
    public static String getDate(final Date time) {
        return DateHelper.SIMPLE_DATE_FORMAT.format(time);
    }
    
    static {
        SIMPLE_DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    }
}
