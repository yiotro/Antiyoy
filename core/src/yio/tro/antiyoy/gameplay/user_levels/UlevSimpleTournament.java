package yio.tro.antiyoy.gameplay.user_levels;

public class UlevSimpleTournament extends AbstractUserLevel{

    @Override
    public String getLevelCode() {
        return "antiyoy_level_code#level_size:2#general:3 1 11#map_name:Simple tournament#editor_info:0 false false true #land:27 10 10 3,24 16 4 3,26 18 9 3,32 15 1 3,31 8 6 3,34 11 0 3,31 9 6 0,31 10 7 0,33 11 0 0,32 11 7 0,27 17 9 0,28 16 7 0,31 15 1 0,30 15 7 0,25 15 4 0,26 14 7 0,27 11 10 0,27 12 7 0,29 15 7 4,29 14 7 0,31 11 7 4,30 12 7 0,29 13 7 7,28 13 7 0,27 13 7 4,#units:#provinces:27@10@1@Бомбемск@10,24@16@2@Аибнасе@10,26@18@3@Доддомск@10,32@15@4@Дартой@10,31@8@5@Помеке@10,34@11@6@Покоой@10,#relations:#coalitions:temporary#messages:#goal:destroy_everyone 0#real_money:#";
    }


    @Override
    public String getMapName() {
        return "Simple tournament";
    }


    @Override
    public String getAuthor() {
        return "leomovskii";
    }
}
