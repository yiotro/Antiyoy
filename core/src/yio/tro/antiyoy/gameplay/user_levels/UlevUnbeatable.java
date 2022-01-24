package yio.tro.antiyoy.gameplay.user_levels;

import yio.tro.antiyoy.gameplay.user_levels.AbstractLegacyUserLevel;

public class UlevUnbeatable extends AbstractLegacyUserLevel {

    @Override
    public String getFullLevelString() {
        return "4 4 1 7/9 13 7 2 0 0 10#8 15 7 2 0 0 10#8 14 7 4 0 0 10#9 14 0 0 2 1 10#10 14 0 0 0 0 10#14 14 1 3 0 0 10#15 13 1 4 0 0 10#11 14 0 2 0 0 10#11 15 0 2 0 0 10#12 15 0 2 0 0 10#13 14 0 3 0 0 10#12 14 0 7 0 0 10#13 13 0 2 0 0 10#12 13 0 2 0 0 10#14 15 1 6 0 0 10";
    }


    @Override
    public String getMapName() {
        return "Unbeatable";
    }


    @Override
    public String getAuthor() {
        return "Raay";
    }


    @Override
    public String getKey() {
        return "unbeatable";
    }
}
