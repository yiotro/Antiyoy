package yio.tro.antiyoy.stuff;

import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateYio implements ReusableYio, Comparable<DateYio> {

    public int day;
    public int month;
    public int year;


    public DateYio() {
        reset();
    }


    @Override
    public void reset() {
        day = -1;
        month = -1;
        year = -1;
    }


    public void copyFrom(DateYio src) {
        day = src.day;
        month = src.month;
        year = src.year;
    }


    public void applyCurrentDay() {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
    }


    @Override
    public String toString() {
        String dayString = getPrefix(day) + day;
        String monthString = getPrefix(month) + month;

        return dayString + "." + monthString + "." + year;
    }


    public boolean equalsMonth(DateYio dateYio) {
        return year == dateYio.year && month == dateYio.month;
    }


    private String getPrefix(int value) {
        if (value < 10) {
            return "0";
        }

        return "";
    }


    public void loadFromString(String src) {
        String[] split = src.split("\\.");
        day = Integer.valueOf(split[0]);
        month = Integer.valueOf(split[1]);
        year = Integer.valueOf(split[2]);
    }


    @Override
    public int compareTo(DateYio o) {
        if (year != o.year) return o.year - year;
        if (month != o.month) return o.month - month;
        return o.day - day;
    }
}

