package xyz.yooniks.spigotguard.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateHelper {
  private static final DateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
  
  public static String getDate(Date paramDate) {
    return SIMPLE_DATE_FORMAT.format(paramDate);
  }
}