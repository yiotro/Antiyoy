package yio.tro.antiyoy.gameplay.loading;

import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.replays.RepSlot;
import yio.tro.antiyoy.gameplay.replays.Replay;
import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.Random;
import java.util.StringTokenizer;

public class LoadingParameters {

    private static LoadingParameters instance = null;

    public static final int MODE_TUTORIAL = 0;
    public static final int MODE_SKIRMISH = 1;
    public static final int MODE_CAMPAIGN_CUSTOM = 2;
    public static final int MODE_LOAD_GAME = 3;
    public static final int MODE_EDITOR_LOAD = 4;
    public static final int MODE_EDITOR_PLAY = 5;
    public static final int MODE_CAMPAIGN_RANDOM = 6;
    public static final int MODE_EDITOR_NEW = 7;
    public static final int MODE_LOAD_REPLAY = 8;

    public int mode;
    public int levelSize;
    public int playersNumber;
    public int colorNumber;
    public int difficulty;
    public int colorOffset;
    public boolean slayRules;
    public int campaignLevelIndex;
    public String activeHexes;
    public int turn;
    public Replay replay;


    void defaultValues() {
        mode = -1;
        levelSize = -1;
        playersNumber = -1;
        colorNumber = -1;
        difficulty = -1;
        colorOffset = -1;
        slayRules = false;
        campaignLevelIndex = YioGdxGame.random.nextInt();
        activeHexes = "";
        turn = -1;
        replay = null;
    }


    public void copyFrom(LoadingParameters src) {
        mode = src.mode;
        levelSize = src.levelSize;
        playersNumber = src.playersNumber;
        colorNumber = src.colorNumber;
        difficulty = src.difficulty;
        colorOffset = src.colorOffset;
        slayRules = src.slayRules;
        campaignLevelIndex = src.campaignLevelIndex;
        activeHexes = src.activeHexes;
        turn = src.turn;
        replay = src.replay;
    }


    public static LoadingParameters getInstance() {
        if (instance == null) {
            instance = new LoadingParameters();
        }

        instance.defaultValues();
        return instance;
    }


    public void showInConsole() {
        System.out.println();
        System.out.println("Parameters:");
        System.out.println("loadMode = " + mode);
        System.out.println("levelSize = " + levelSize);
        System.out.println("playersNumber = " + playersNumber);
        System.out.println("colorNumber = " + colorNumber);
        System.out.println("difficulty = " + difficulty);
        System.out.println("colorOffset = " + colorOffset);
        System.out.println("slayRules = " + slayRules);
        System.out.println("campaignLevelIndex = " + campaignLevelIndex);
        System.out.println("activeHexes = " + activeHexes);
        System.out.println("turn = " + turn);
        System.out.println("replay = " + replay);

        System.out.println();
    }


    public void applyFullLevel(String fullLevel) {
        int delimiterChar = fullLevel.indexOf("/");
        String basicInfo;
        int basicInfoValues[] = new int[4];

        if (delimiterChar < 0) { // empty slot
            return;
        }

        basicInfo = fullLevel.substring(0, delimiterChar);
        StringTokenizer stringTokenizer = new StringTokenizer(basicInfo, " ");
        int i = 0;

        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            basicInfoValues[i] = Integer.valueOf(token);
            i++;
        }

        activeHexes = fullLevel.substring(delimiterChar + 1, fullLevel.length());
        playersNumber = basicInfoValues[2];
        colorNumber = basicInfoValues[3];
        levelSize = basicInfoValues[1];
        difficulty = basicInfoValues[0];
    }


    public void applyPrefs(Preferences prefs) {
        turn = prefs.getInteger("save_turn");
        playersNumber = prefs.getInteger("save_player_number");
        colorNumber = prefs.getInteger("save_color_number");
        if (colorNumber > FieldController.NEUTRAL_LANDS_INDEX) {
            colorNumber = FieldController.NEUTRAL_LANDS_INDEX;
        }
        levelSize = prefs.getInteger("save_level_size");
        difficulty = prefs.getInteger("save_difficulty");
        GameRules.campaignMode = prefs.getBoolean("save_campaign_mode");
        campaignLevelIndex = prefs.getInteger("save_current_level");
        colorOffset = prefs.getInteger("save_color_offset", 0);
        slayRules = prefs.getBoolean("slay_rules", true);
    }
}
