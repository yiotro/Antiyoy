package yio.tro.antiyoy.gameplay.user_levels;

public abstract class AbstractUserLevel {


    public abstract String getFullLevelString();


    public abstract String getMapName();


    public abstract String getAuthor();


    public abstract String getKey();


    public int getColorOffset() {
        return 0;
    }


    public boolean getFogOfWar() {
        return false;
    }


    public boolean isHistorical() {
        return false;
    }

}
