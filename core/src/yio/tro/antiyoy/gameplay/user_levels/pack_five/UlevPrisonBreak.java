package yio.tro.antiyoy.gameplay.user_levels.pack_five;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.user_levels.AbstractUserLevel;

public class UlevPrisonBreak extends AbstractUserLevel{

    @Override
    public String getFullLevelString() {
        return "4 4 1 7/14 10 6 0 0 0 10#11 17 6 0 0 0 10#13 17 6 0 2 0 10#20 9 6 0 0 0 10#14 17 6 0 0 0 10#15 17 6 0 0 0 10#16 17 6 0 0 0 10#27 17 6 0 0 0 10#28 17 6 0 2 0 10#26 17 6 0 0 0 10#25 17 6 0 0 0 10#32 9 6 0 2 0 10#30 9 6 0 0 0 10#31 9 6 0 0 0 10#29 9 6 0 0 0 10#26 19 6 0 0 0 10#31 15 6 0 0 0 10#33 11 6 0 0 0 10#17 25 0 3 0 0 10#17 24 0 0 1 0 10#19 25 0 3 0 0 10#19 24 0 0 1 0 10#21 21 0 3 0 0 10#20 22 0 0 1 0 10#21 25 0 3 0 0 10#21 24 0 0 1 0 10#23 21 0 3 0 0 10#22 22 0 0 1 0 10#23 25 0 3 0 0 10#23 24 0 0 1 0 10#25 21 0 3 0 0 10#24 22 0 0 1 0 10#12 17 6 0 2 0 10#10 23 6 6 0 0 10#11 23 6 0 0 0 10#12 23 6 0 1 0 10#13 23 6 0 0 0 10#14 23 6 0 1 0 10#15 23 6 0 0 0 10#16 23 6 0 1 0 10#25 23 6 6 0 0 10#24 23 6 0 1 0 10#23 23 6 0 0 0 10#22 23 6 0 1 0 10#21 23 6 0 0 0 10#20 23 6 0 1 0 10#19 23 6 0 0 0 10#18 23 6 0 1 0 10#17 23 6 0 0 0 10#18 22 0 0 0 0 10#19 21 0 3 0 0 10#19 20 0 4 0 0 10#18 21 0 0 1 0 10#30 16 6 0 0 0 10#29 17 6 0 2 0 10#28 18 6 0 0 0 10#27 19 6 0 2 0 10#27 18 6 0 0 0 10#26 18 6 7 0 0 10#25 19 6 0 0 0 10#25 18 6 0 0 0 10#24 19 6 0 0 0 10#24 18 6 0 0 0 10#23 19 6 0 0 0 10#27 5 0 3 0 0 10#29 5 0 3 0 0 10#29 4 0 0 1 0 10#32 3 6 0 1 0 10#33 3 6 0 0 0 10#30 3 6 0 1 0 10#31 3 6 0 0 0 10#29 3 6 0 0 0 10#28 3 6 0 1 0 10#27 3 6 0 0 0 10#26 3 6 0 1 0 10#25 3 6 0 0 0 10#24 3 6 0 1 0 10#23 3 6 0 0 0 10#22 3 6 0 1 0 10#35 1 0 3 0 0 10#34 2 0 0 1 0 10#33 1 0 3 0 0 10#32 2 0 0 1 0 10#31 1 0 3 0 0 10#30 2 0 0 1 0 10#29 1 0 3 0 0 10#28 2 0 0 1 0 10#27 4 0 0 0 0 10#26 5 0 0 1 0 10#26 6 0 4 0 0 10#25 7 6 0 0 0 10#26 7 6 0 0 0 10#31 7 6 0 0 0 10#30 7 6 0 0 0 10#30 11 0 6 0 0 10#28 15 0 6 0 0 10#31 11 0 6 0 0 10#31 12 0 6 0 0 10#30 14 0 6 0 0 10#29 15 0 6 0 0 10#29 14 0 6 0 0 10#30 12 0 6 0 0 10#30 13 0 6 0 0 10#29 13 0 3 0 0 10#29 7 6 0 0 0 10#28 7 6 0 0 0 10#29 8 6 0 0 0 10#22 19 6 0 0 0 10#17 19 6 0 0 0 10#16 19 6 0 0 0 10#16 18 6 0 0 0 10#23 7 6 0 0 0 10#22 7 6 0 0 0 10#21 8 6 0 0 0 10#14 14 0 6 0 0 10#15 12 0 6 0 0 10#14 15 0 6 0 0 10#15 13 0 6 0 0 10#16 11 0 6 0 0 10#15 15 0 6 0 0 10#17 11 0 6 0 0 10#15 14 0 6 0 0 10#16 12 0 6 0 0 10#16 13 0 3 0 0 10#24 11 0 0 2 0 10#23 12 0 3 0 0 10#23 11 0 0 2 0 10#22 12 0 0 1 0 10#24 12 0 0 1 0 10#23 13 7 7 0 0 10#22 13 7 7 0 0 10#23 14 0 0 1 0 10#22 14 0 3 0 0 10#21 14 0 0 1 0 10#22 15 0 0 2 0 10#21 15 0 0 2 0 10#25 12 0 7 0 0 10#26 12 6 4 0 0 10#24 14 0 7 0 0 10#25 14 6 4 0 0 10#21 12 0 7 0 0 10#20 12 6 4 0 0 10#24 10 0 7 0 0 10#25 9 6 0 0 0 10#24 9 6 0 0 0 10#20 14 0 7 0 0 10#19 14 6 4 0 0 10#21 16 0 7 0 0 10#21 17 6 0 0 0 10#20 17 6 0 0 0 10#26 9 6 0 0 0 10#27 9 6 0 0 0 10#27 10 6 0 0 0 10#27 11 6 0 0 0 10#27 12 6 0 0 0 10#27 13 6 0 0 0 10#26 13 6 0 0 0 10#26 14 6 0 0 0 10#25 15 6 0 0 0 10#24 16 6 0 0 0 10#23 17 6 0 0 0 10#22 17 6 0 0 0 10#19 17 6 0 0 0 10#18 17 6 0 0 0 10#18 16 6 0 0 0 10#18 15 6 0 0 0 10#18 14 6 0 0 0 10#18 13 6 0 0 0 10#19 13 6 0 0 0 10#19 12 6 0 0 0 10#20 11 6 0 0 0 10#21 10 6 0 0 0 10#22 9 6 0 0 0 10#23 9 6 0 0 0 10#27 8 6 0 0 0 10#26 8 6 0 0 0 10#25 8 6 4 0 0 10#24 8 6 0 0 0 10#23 8 6 0 0 0 10#22 18 6 0 0 0 10#21 18 6 0 0 0 10#20 18 6 4 0 0 10#19 18 6 0 0 0 10#18 18 6 0 0 0 10#22 8 6 7 0 0 10#21 9 6 0 0 0 10#20 10 6 0 0 0 10#19 11 6 0 0 0 10#18 12 6 0 0 0 10#17 13 0 4 0 0 10#17 14 6 0 0 0 10#17 15 6 0 0 0 10#17 16 6 0 0 0 10#17 17 6 0 0 0 10#17 18 6 7 0 0 10#28 8 6 7 0 0 10#28 9 6 0 0 0 10#28 10 6 0 0 0 10#28 11 6 0 0 0 10#28 12 6 0 0 0 10#28 13 0 4 0 0 10#26 15 6 0 0 0 10#27 14 6 0 0 0 10#25 16 6 0 0 0 10#24 17 6 0 0 0 10#23 18 6 7 0 0 10#19 19 6 0 0 0 10#20 19 6 0 0 0 10#31 4 0 0 1 0 10#31 5 0 3 0 0 10#30 8 6 0 0 0 10#31 8 6 7 0 0 10#32 7 6 0 0 0 10#32 8 6 0 0 0 10#33 7 6 0 2 0 10#33 8 6 0 0 0 10#33 4 0 0 1 0 10#33 5 0 3 0 0 10#21 3 6 0 0 0 10#34 3 6 0 1 0 10#18 3 6 6 0 0 10#33 9 6 0 2 0 10#33 10 6 0 0 0 10#32 13 6 3 0 0 10#33 12 6 0 4 0 10#32 14 6 0 4 0 10#20 8 6 0 0 0 10#21 7 6 0 0 0 10#19 8 6 7 0 0 10#20 7 6 0 0 0 10#18 8 6 0 0 0 10#19 7 6 0 0 0 10#17 8 6 0 0 0 10#18 7 6 0 2 0 10#16 8 6 0 0 0 10#17 7 6 0 2 0 10#15 19 6 0 0 0 10#15 18 6 0 0 0 10#14 19 6 0 0 0 10#14 18 6 7 0 0 10#13 19 6 0 0 0 10#12 19 6 0 2 0 10#13 18 6 0 0 0 10#12 18 6 0 0 0 10#10 19 6 0 0 0 10#11 18 6 0 0 0 10#11 19 6 0 2 0 10#18 4 6 6 0 0 10#12 15 6 0 2 0 10#11 16 6 0 0 0 10#9 23 6 6 0 0 10#10 20 6 0 0 0 10#9 21 6 0 0 0 10#9 22 6 6 0 0 10#10 21 6 0 0 0 10#34 7 6 0 2 0 10#34 8 6 0 0 0 10#34 9 6 0 0 0 10#34 10 6 0 0 0 10#34 11 6 0 0 0 10#34 12 6 6 0 0 10#34 13 6 7 0 0 10#33 14 6 6 0 0 10#32 15 6 0 0 0 10#31 16 6 0 0 0 10#30 17 6 0 0 0 10#29 18 6 0 0 0 10#28 19 6 0 2 0 10#35 4 6 6 0 0 10#35 3 6 6 0 0 10#35 5 6 0 0 0 10#35 6 6 0 0 0 10#36 3 6 6 0 0 10#36 4 6 6 0 0 10#36 5 6 0 0 0 10#36 7 6 6 0 0 10#36 6 6 0 0 0 10#36 2 6 6 0 0 10#37 2 6 6 0 0 10#37 3 6 6 0 0 10#37 4 6 6 0 0 10#37 5 6 6 0 0 10#37 6 6 6 0 0 10#35 7 6 0 0 0 10#35 8 6 6 0 0 10#35 9 6 6 0 0 10#35 10 6 6 0 0 10#35 11 6 6 0 0 10#35 12 6 6 0 0 10#35 13 6 6 0 0 10#34 14 6 6 0 0 10#33 15 6 6 0 0 10#32 16 6 6 0 0 10#31 17 6 6 0 0 10#30 18 6 6 0 0 10#29 19 6 0 0 0 10#28 20 6 0 0 0 10#19 9 6 0 0 0 10#18 9 6 0 0 0 10#17 9 6 0 2 0 10#16 9 6 0 2 0 10#30 19 6 6 0 0 10#28 21 6 0 0 0 10#29 20 6 0 0 0 10#27 22 6 6 0 0 10#27 21 6 0 0 0 10#26 23 6 6 0 0 10#26 22 6 6 0 0 10#27 23 6 6 0 0 10#26 24 6 6 0 0 10#25 24 6 6 0 0 10#28 22 6 6 0 0 10#29 21 6 6 0 0 10#30 20 6 6 0 0 10#19 4 6 6 0 0 10#20 3 6 6 0 0 10#19 3 6 6 0 0 10#20 2 6 6 0 0 10#19 2 6 6 0 0 10#16 5 6 6 0 0 10#17 4 6 6 0 0 10#25 4 0 0 1 0 10#24 5 0 3 0 0 10#26 2 0 0 1 0 10#26 1 0 3 0 0 10#23 4 0 0 1 0 10#22 5 0 3 0 0 10#24 2 0 0 1 0 10#24 1 0 3 0 0 10#21 4 0 0 1 0 10#20 5 0 3 0 0 10#22 2 0 0 1 0 10#22 1 0 3 0 0 10#16 22 0 0 1 0 10#16 21 0 3 0 0 10#15 24 0 0 1 0 10#14 25 0 3 0 0 10#14 22 0 0 1 0 10#14 21 0 3 0 0 10#13 24 0 0 1 0 10#12 25 0 3 0 0 10#12 22 0 0 1 0 10#12 21 0 3 0 0 10#11 24 0 0 1 0 10#10 25 0 3 0 0 10#17 5 6 0 0 0 10#18 5 6 0 0 0 10#17 6 6 0 0 0 10#16 7 6 0 0 0 10#16 6 6 0 0 0 10#15 6 6 6 0 0 10#15 7 6 6 0 0 10#15 8 6 6 0 0 10#15 10 6 0 0 0 10#15 9 6 0 0 0 10#14 11 6 0 2 0 10#13 13 6 0 0 0 10#13 12 6 0 4 0 10#12 14 6 0 4 0 10#14 9 6 6 0 0 10#13 10 6 6 0 0 10#13 11 6 0 0 0 10#12 11 6 6 0 0 10#11 13 6 7 0 0 10#11 14 6 6 0 0 10#12 13 6 6 0 0 10#12 12 6 6 0 0 10#11 12 6 6 0 0 10#10 13 6 6 0 0 10#10 14 6 6 0 0 10#11 15 6 0 0 0 10#10 15 6 6 0 0 10#10 17 6 6 0 0 10#10 16 6 6 0 0 10#9 20 6 0 0 0 10#9 19 6 6 0 0 10#10 22 6 6 0 0 10#9 24 6 6 0 0 10#8 24 6 6 0 0 10#8 23 6 6 0 0 10#8 22 6 6 0 0 10#8 21 6 6 0 0 10#8 20 6 6 0 0 10#10 18 6 6 0 0 10#12 16 6 0 0 0 10#33 13 6 6 0 0 10";
    }


    @Override
    public void onLevelLoaded(GameController gameController) {
        setProvinceMoney(gameController, 15, 14, 200);
        setProvinceMoney(gameController, 29, 14, 200);

        for (Province province : gameController.fieldController.provinces) {
            if (province.getColor() != 0) continue;

            for (Hex hex : province.hexList) {
                if (!hex.containsUnit()) continue;

                hex.unit.setReadyToMove(true);
                hex.unit.startJumping();
            }
        }
    }


    @Override
    public int getColorOffset() {
        return 6;
    }


    @Override
    public String getMapName() {
        return "Prison Break";
    }


    @Override
    public String getAuthor() {
        return "Likai Kuroi";
    }


    @Override
    public String getKey() {
        return "prison_break";
    }
}