package yio.tro.antiyoy.gameplay.user_levels;

import yio.tro.antiyoy.gameplay.user_levels.AbstractLegacyUserLevel;
import yio.tro.antiyoy.gameplay.user_levels.AbstractUserLevel;

public class UlevNotStalemate extends AbstractUserLevel {

    @Override
    public String getLevelCode() {
        return "antiyoy_level_code#level_size:1#general:0 1 11#map_name:Slot 8#editor_info:7 false false #land:30 6 6 0,29 7 6 3,31 11 7 0,29 6 0 0,28 6 0 3,28 7 1 0,27 7 1 3,27 8 2 0,26 8 2 3,26 9 3 3,25 9 3 6,27 6 4 3,26 7 4 0,25 8 5 3,24 9 5 0,#units:30 6 1 false,29 6 1 true,28 7 1 false,27 8 1 false,26 7 1 false,24 9 1 false,#provinces:30@6@7@Paimode@10,29@6@6@Rairtesk@10,28@7@5@Raipmo@10,27@8@4@Bokamo@10,26@9@3@Bobrot@10,27@6@2@Dekaipai@10,25@8@1@Tobro@10,#relations:#messages:#";
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
