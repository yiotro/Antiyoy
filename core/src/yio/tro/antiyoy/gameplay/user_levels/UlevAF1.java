package yio.tro.antiyoy.gameplay.user_levels;

import yio.tro.antiyoy.gameplay.user_levels.AbstractLegacyUserLevel;

public class UlevAF1 extends AbstractLegacyUserLevel {

    @Override
    public String getFullLevelString() {
        return "4 4 1 5/23 13 4 0 0 0 10#21 12 0 0 0 0 10#25 9 3 0 0 0 10#24 10 3 0 0 0 10#24 11 3 3 0 0 10#25 12 4 3 0 0 10#24 13 4 6 0 0 10#23 12 4 0 0 0 10#20 13 0 0 0 0 10#21 13 4 0 0 0 10#22 9 3 0 0 0 10#23 9 3 0 0 0 10#23 10 3 0 0 0 10#23 11 3 0 0 0 10#22 13 4 0 0 0 10#22 12 4 0 0 0 10#22 11 0 6 0 0 10#21 11 0 3 0 0 10";
    }


    @Override
    public int getColorOffset() {
        return 6;
    }


    @Override
    public String getMapName() {
        return "AF1";
    }


    @Override
    public String getAuthor() {
        return "loks GG";
    }


    @Override
    public String getKey() {
        return "af1";
    }
}
