package yio.tro.antiyoy.gameplay.user_levels.pack;

import yio.tro.antiyoy.gameplay.user_levels.AbstractLegacyUserLevel;
import yio.tro.antiyoy.gameplay.user_levels.AbstractUserLevel;

public class UlevNotStalemate extends AbstractUserLevel {

    @Override
    public String getLevelCode() {
        return "antiyoy_level_code#level_size:4#general:3 1 11#map_name:Слот 51#editor_info:1 false false #land:23 18 7 3,24 13 6 0,23 14 6 3,18 16 5 0,19 15 5 3,20 14 4 0,21 13 4 3,19 16 3 6,20 16 3 3,21 15 2 0,20 15 2 3,21 14 1 3,22 14 1 0,23 13 0 0,22 13 0 3,#units:24 13 1 false,18 16 1 false,20 14 1 false,21 15 1 false,22 14 1 false,23 13 1 true,#provinces:24@13@1@Сетаипой@10,18@16@2@Еброро@10,20@14@3@Кекре@10,19@16@4@Макпе@10,21@15@5@Окоорг@10,21@14@6@Омопа@10,23@13@7@Екено@25,#relations:#messages:#";
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
