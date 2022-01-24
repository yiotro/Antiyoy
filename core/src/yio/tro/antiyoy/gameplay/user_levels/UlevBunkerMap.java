package yio.tro.antiyoy.gameplay.user_levels;

import yio.tro.antiyoy.gameplay.user_levels.AbstractLegacyUserLevel;

public class UlevBunkerMap extends AbstractLegacyUserLevel {

    @Override
    public String getFullLevelString() {
        return "4 4 1 7/14 12 0 0 0 0 10#15 12 0 0 0 0 10#16 11 0 0 0 0 10#17 11 0 0 0 0 10#16 13 0 0 0 0 10#15 16 1 3 0 0 10#16 15 1 0 0 0 10#17 15 1 0 0 0 10#18 14 1 0 0 0 10#18 15 1 0 0 0 10#17 16 1 0 0 0 10#16 16 1 0 0 0 10#19 8 2 0 0 0 10#20 8 2 0 0 0 10#20 9 2 0 0 0 10#21 9 2 0 0 0 10#21 10 2 0 0 0 10#21 11 2 0 0 0 10#20 11 2 0 0 0 10#20 10 2 3 0 0 10#19 9 2 0 0 0 10#18 10 7 0 0 0 10#16 14 7 0 0 0 10#17 12 0 3 0 0 10";
    }


    @Override
    public String getMapName() {
        return "Bunker map";
    }


    @Override
    public String getAuthor() {
        return "iwantthetopbunk";
    }


    @Override
    public String getKey() {
        return "bunker_map";
    }
}
