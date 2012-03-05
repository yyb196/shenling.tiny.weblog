package shenling.weblog.utils;

import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-2 下午8:27
 */
@Component("StringUtils")
public class StringUtils {
//    private static final String pattern = "yyyy-MM-dd HH:mm:ss.SSS+8";

    public String[] split(String s, String sp) {
        if (s == null) {
            return new String[0];
        }
        return s.split("\\s*" + Pattern.quote(sp) + "\\s*");
    }
    
    public String escapseUrl(String url){
        return url.replace("_", "%5F");
    }

    public boolean isBlank(String s) {
        if (s == null) {
            return true;
        } else if (s.trim().length() == 0) {
            return true;
        }
        return false;
    }

    public String date2Str(Date date) {
        if (date == null) {
            return "";
        }

        DateFormat formater = getDateFormater();
        return formater.format(date);
    }

    public Date str2Date(String s) {
        if (s == null) {
            return null;
        }
        DateFormat formater = getDateFormater();
        try {
            return formater.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws ParseException {
        Date time = new Date();
        System.out.println(time);
        //获得日期+时间
        DateFormat gmt08Formatter = getDateFormater();
        // 获得格式化后的东八区时间
        String gmt08DateTime = gmt08Formatter.format(time);
        System.out.println(gmt08DateTime);
        System.out.println(gmt08Formatter.parse(gmt08DateTime));
    }

    private static DateFormat getDateFormater() {
        DateFormat gmt08Formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                DateFormat.MEDIUM, Locale.SIMPLIFIED_CHINESE);
        //获得时间
        //                DateFormat gmt08Formatter=DateFormat.getTimeInstance(0);
        TimeZone timezone = TimeZone.getTimeZone("GMT+08:00");
        gmt08Formatter.setTimeZone(timezone);
        return gmt08Formatter;
    }
}
