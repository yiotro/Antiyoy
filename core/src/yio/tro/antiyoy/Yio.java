package yio.tro.antiyoy;

import java.util.ArrayList;
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


    public static float radianToDegree(double angle) {
        return (float) (180 / Math.PI * angle);
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


    public static String convertObjectToString(Object object) {
        String s = object.toString();
        return s.substring(s.indexOf("@"));
    }


    public static double getRandomAngle() {
        return 2d * Math.PI * YioGdxGame.random.nextDouble();
    }
}
