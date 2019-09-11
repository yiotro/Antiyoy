package yio.tro.antiyoy.gameplay.user_levels;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.rules.GameRules;

public abstract class AbstractUserLevel extends AbstractLegacyUserLevel{


    private String mapName;
    private int playersNumber;


    @Override
    public String getFullLevelString() {
        return null;
    }


    public abstract String getLevelCode();


    public String getMapNameFromLevelCode() {
        return mapName;
    }


    public abstract String getMapName();


    public abstract String getKey();


    public abstract String getAuthor();


    @Override
    public boolean isSinglePlayer() {
        return playersNumber == 1;
    }


    @Override
    public boolean isMultiplayer() {
        return playersNumber > 1;
    }


    @Override
    protected void onGameControllerSet() {
        super.onGameControllerSet();
        String levelCode = getLevelCode();
        mapName = getDecodeManager().extractMapName(levelCode);
        playersNumber = getDecodeManager().extractPlayersNumber(levelCode);
    }
}
