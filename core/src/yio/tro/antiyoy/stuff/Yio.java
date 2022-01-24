package yio.tro.antiyoy.stuff;

import yio.tro.antiyoy.YioGdxGame;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;
import java.util.StringTokenizer;

public class Yio {

    public static double angle(double x1, double y1, double x2, double y2) {
        if (x1 == x2) {
            if (y2 > y1) return 0.5 * Math.PI;
            if (y2 < y1) return 1.5 * Math.PI;
            return 0;
        }
        if (x2 >= x1) return Math.atan((y2 - y1) / (x2 - x1));
        else return Math.PI + Math.atan((y2 - y1) / (x2 - x1));
    }


    static float maxElement(ArrayList<Float> list) {
        if (list.size() == 0) return 0;
        float max = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) > max) max = list.get(i);
        }
        return max;
    }


    public static double roundUp(double value, int length) {
        double d = Math.pow(10, length);
        value = value * d;
        int i = (int) (value + 0.45);
        return (double) i / d;
    }


    public static String convertTime(long time) {
        // time is in frames
        long seconds = time / 60;
        int min = 0;
        while (seconds >= 60) {
            min++;
            seconds -= 60;
        }
        String zero = "";
        if (seconds < 10) {
            zero = "0";
        }
        return min + ":" + zero + seconds;
    }


    public static String convertTimeToUnderstandableString(long time) {
        // time is in frames
        long seconds = time / 60;
        int minutes = 0;
        while (seconds >= 60) {
            minutes++;
            seconds -= 60;
        }
        int hours = 0;
        while (minutes >= 60) {
            hours++;
            minutes -= 60;
        }

        String hString = "";
        if (hours > 0) {
            hString = hours + LanguagesManager.getInstance().getString("hours_abbreviation") + " ";
        }
        String mString = "";
        if (minutes > 0 || hours > 0) {
            mString = convertToTwoDigitString(minutes) + LanguagesManager.getInstance().getString("minutes_abbreviation") + " ";
        }
        String sString = convertToTwoDigitString(seconds) + LanguagesManager.getInstance().getString("seconds_abbreviation");

        return hString + mString + sString;
    }


    public static String convertToTwoDigitString(long value) {
        if (value < 10) {
            return "0" + value;
        }
        return "" + value;
    }


    public static void removeByIterator(ArrayList<?> list, Object object) {
        ListIterator iterator = list.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next() == object) {
                iterator.remove();
                break;
            }
        }
    }


    public static void addByIterator(ArrayList<?> list, Object object) {
        ListIterator iterator = list.listIterator();
        iterator.add(object);
    }


    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }


    public static float fastDistance(double x1, double y1, double x2, double y2) {
        return (float) (Math.abs(x2 - x1) + Math.abs(y2 - y1));
    }


    public static float radianToDegree(double angle) {
        return (float) (180 / Math.PI * angle);
    }


    public static void forceException() {
        PointYio pointYio = null;
        pointYio.x = 0;
    }


    public static void printStackTrace() {
        try {
            throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<String> decodeStringToArrayList(String string, String delimiters) {
        ArrayList<String> res = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(string, delimiters);
        while (tokenizer.hasMoreTokens()) {
            res.add(tokenizer.nextToken());
        }
        return res;
    }


    public static void syncSay(String message) {
        synchronized (System.out) {
            System.out.println(message);
        }
    }


    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy  HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }


    public static boolean isNewYearNear() {
        DateYio dateYio = new DateYio();
        dateYio.applyCurrentDay();
        if (dateYio.month != 12) return false;
        if (dateYio.day < 28) return false;
        return true;
    }


    public static String getCompactMoneyString(int srcValue) {
        String prefix = "";
        if (srcValue < 0) {
            prefix = "-";
        }
        srcValue = Math.abs(srcValue);

        if (srcValue < 1000) {
            return prefix + srcValue;
        }

        float v;
        int iv;

        if (srcValue < 10000) {
            v = srcValue;
            v /= 1000;
            v = (float) Yio.roundUp(v, 1);
            return prefix + v + "k";
        }

        if (srcValue < 1000000) {
            iv = srcValue;
            iv /= 1000;
            return prefix + iv + "k";
        }

        if (srcValue < 10000000) {
            v = srcValue;
            v /= 1000000;
            v = (float) Yio.roundUp(v, 1);
            return prefix + v + "m";
        }

        iv = srcValue;
        iv /= 1000000;
        return prefix + iv + "m";
    }


    public static String convertObjectToString(Object object) {
        String s = object.toString();
        return s.substring(s.indexOf("@"));
    }


    public static double getRandomAngle() {
        return 2d * Math.PI * YioGdxGame.random.nextDouble();
    }


    public static boolean isNumeric(String str) {
        return str.length() != 0 && str.matches("-?\\d+(\\.\\d+)?");
    }


    public static String getDeltaMoneyString(int value) {
        if (value > 0) {
            return "+$" + value;
        }
        return "$" + value;
    }


    public static String getCapitalizedString(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
