package yio.tro.antiyoy.gameplay.user_levels;

import yio.tro.antiyoy.gameplay.user_levels.AbstractUserLevel;

public class UlevKingdomsPt1 extends AbstractUserLevel{

    @Override
    public String getLevelCode() {
        return "antiyoy_level_code#level_size:4#general:4 1 11#map_name:Slot 0#editor_info:2 true false #land:26 16 7 1,25 18 1 0,25 17 1 1,26 17 7 1,26 18 7 0,27 18 7 0,27 17 7 1,27 16 7 1,28 18 7 0,29 18 7 0,29 17 7 0,30 17 7 0,30 16 7 0,29 16 7 0,28 16 7 1,30 15 7 0,29 15 3 3,28 15 3 0,27 15 7 1,26 15 7 0,25 16 7 0,24 18 1 0,24 17 7 0,31 11 7 0,30 12 7 0,31 12 7 0,30 11 7 0,29 12 7 1,29 11 7 1,28 12 7 1,28 11 7 1,27 12 4 1,27 11 4 0,25 13 4 0,26 13 4 3,26 12 4 0,29 13 3 0,28 14 3 0,28 13 7 0,27 14 7 0,31 16 7 0,31 17 7 0,32 16 7 1,32 17 7 0,34 15 8 0,33 14 7 1,33 15 7 1,34 14 8 0,33 16 7 1,34 16 8 1,33 17 7 1,34 17 8 3,33 18 8 0,32 18 7 0,32 15 7 1,23 18 1 0,23 17 7 1,22 17 7 1,22 16 7 1,21 17 7 1,20 19 7 0,21 18 7 1,22 18 7 1,22 19 1 1,21 19 7 0,20 18 5 0,20 17 5 1,21 16 7 1,19 18 5 0,18 20 5 0,19 19 5 0,18 19 5 3,23 19 1 6,#units:#provinces:25@18@1@The Colonizers@150,29@15@4@Oneroi Empire@25,27@12@5@United Tribes@25,34@15@3@Northern Tribes@25,20@18@2@New Kyoto @25,#relations:#messages:Salutations, Governor!@The King has sent you a task@We must unite this forsaken island! Or else the tribes will attack us!@The King has given us fair amount of gold for campaigns@Now go, Governor! Make our empire proud!@#goal:def 0# ";
    }


    @Override
    public String getMapName() {
        return "Kingdoms pt1";
    }


    @Override
    public String getAuthor() {
        return "SCP Admin Guy";
    }
}
