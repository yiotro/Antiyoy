package yio.tro.antiyoy.stuff;

public class TimeMeasureYio {

    private static long time1;


    public static void begin() {
        time1 = System.currentTimeMillis();
    }


    public static long apply(String message) {
        long resultTime = System.currentTimeMillis() - time1;
        Yio.syncSay(message + ": " + resultTime);
        begin();
        return resultTime;
    }


    public static long apply() {
        return apply("Time taken");
    }

}
