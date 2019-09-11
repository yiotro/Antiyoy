package yio.tro.antiyoy.gameplay.user_levels.pack;

import yio.tro.antiyoy.gameplay.user_levels.AbstractLegacyUserLevel;

public class UlevNotStalemate extends AbstractLegacyUserLevel {

    @Override
    public String getFullLevelString() {
        return "3 4 1 7/23 18 7 3 0 0 10#24 13 6 0 1 0 10#23 14 6 3 0 0 10#18 16 5 0 1 0 10#19 15 5 3 0 0 10#20 14 4 0 1 0 10#21 13 4 3 0 0 10#19 16 3 6 0 0 10#20 16 3 3 0 0 10#21 15 2 0 1 0 10#20 15 2 3 0 0 10#21 14 1 3 0 0 10#22 14 1 0 1 0 10#23 13 0 0 1 1 10#22 13 0 3 0 0 10";
    }


    @Override
    public String getMapName() {
        return "Not a stalemate";
    }


    @Override
    public String getAuthor() {
        return "frost";
    }


    @Override
    public String getKey() {
        return "not_a_stalemate";
    }
}
