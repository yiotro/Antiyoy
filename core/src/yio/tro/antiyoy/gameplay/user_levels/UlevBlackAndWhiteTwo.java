package yio.tro.antiyoy.gameplay.user_levels;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.user_levels.AbstractLegacyUserLevel;

public class UlevBlackAndWhiteTwo extends AbstractLegacyUserLevel {

    @Override
    public String getFullLevelString() {
        return "3 4 1 2/22 15 1 6 0 0 10#23 15 1 6 0 0 10#22 16 1 6 0 0 10#21 16 1 4 0 0 10#22 14 1 4 0 0 10#23 13 1 6 0 0 10#23 14 1 6 0 0 10#22 13 1 6 0 0 10#21 14 1 0 4 0 10#20 15 1 3 0 0 10#21 15 1 6 0 0 10#20 16 1 0 4 0 10#20 17 1 6 0 0 10#21 17 1 6 0 0 10#24 13 0 0 0 0 10#24 12 0 0 0 0 10#23 12 1 0 0 0 10#21 12 1 0 0 0 10#21 13 1 6 0 0 10#22 12 1 0 0 0 10#20 13 1 6 0 0 10#20 14 1 6 0 0 10#19 15 1 7 0 0 10#19 16 1 6 0 0 10#18 17 1 6 0 0 10#19 17 1 6 0 0 10#19 18 1 0 0 0 10#20 18 1 0 0 0 10#21 18 0 0 0 0 10#22 18 0 0 0 0 10#22 17 0 0 0 0 10#23 17 0 0 0 0 10#23 16 0 0 0 0 10#24 15 0 0 0 0 10#24 14 0 0 0 0 10#25 14 0 0 0 0 10#25 15 0 3 0 0 10#25 13 0 0 0 0 10#26 13 0 0 0 0 10#27 13 0 0 0 0 10#28 13 0 0 0 0 10#28 14 0 0 0 0 10#28 15 0 0 0 0 10#28 16 0 0 0 0 10#27 17 0 0 0 0 10#26 18 0 0 0 0 10#25 18 0 0 0 0 10#25 17 0 0 0 0 10#24 17 0 0 0 0 10#25 16 0 0 0 0 10#24 16 0 0 0 0 10#27 14 0 0 0 0 10#27 15 0 0 0 0 10#27 16 0 0 0 0 10#26 17 0 0 0 0 10#26 16 0 0 0 0 10#26 15 0 0 0 0 10#26 14 0 0 0 0 10#27 12 0 0 0 0 10#28 12 0 0 0 0 10#25 12 0 0 0 0 10#26 12 0 0 0 0 10#18 18 1 0 0 0 10#18 16 1 6 0 0 10#19 14 1 6 0 0 10#23 18 0 0 0 0 10#24 18 0 0 0 0 10#29 15 0 0 0 0 10#29 14 0 0 0 0 10#29 13 0 0 0 0 10#29 12 0 0 0 0 10#18 15 1 6 0 0 10";
    }


    @Override
    public String getMapName() {
        return "Black & White 2";
    }


    @Override
    public String getAuthor() {
        return "jukzi";
    }


    @Override
    public String getKey() {
        return "black_white_2";
    }


    @Override
    public void onLevelLoaded(GameController gameController) {
        super.onLevelLoaded(gameController);
        setProvinceMoney(gameController, 25, 16, 250);
    }
}
