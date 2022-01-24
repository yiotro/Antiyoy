package yio.tro.antiyoy.gameplay.loading;

import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyInfoCondensed;
import yio.tro.antiyoy.gameplay.replays.Replay;
import yio.tro.antiyoy.gameplay.rules.GameRules;

public class LoadingParameters {

    private static LoadingParameters instance = null;

    public LoadingType loadingType;
    public int levelSize;
    public int playersNumber;
    public int fractionsQuantity;
    public int difficulty;
    public int colorOffset;
    public boolean slayRules;
    public int campaignLevelIndex;
    public String activeHexes;
    public int turn;
    public Replay replay;
    public boolean fogOfWar;
    public boolean diplomacy;
    public DiplomacyInfoCondensed diplomacyInfo;
    public boolean userLevelMode;
    public String ulKey;
    public boolean editorColorFixApplied;
    public String levelCode;
    public String editorProvincesData;
    public String editorRelationsData;
    public String editorCoalitionsData;
    public String preparedMessagesData;
    public String goalData;
    public int genProvinces;
    public int treesPercentageIndex;
    public boolean diplomaticRelationsLocked;


    void defaultValues() {
        loadingType = null;
        levelSize = -1;
        playersNumber = -1;
        fractionsQuantity = -1;
        difficulty = -1;
        colorOffset = -1;
        slayRules = false;
        campaignLevelIndex = YioGdxGame.random.nextInt();
        activeHexes = "";
        turn = -1;
        replay = null;
        diplomacyInfo = null;
        fogOfWar = false;
        diplomacy = false;
        ulKey = null;
        editorColorFixApplied = false;
        levelCode = "";
        editorProvincesData = "";
        editorRelationsData = "";
        editorCoalitionsData = "";
        preparedMessagesData = "";
        goalData = "";
        genProvinces = 0;
        treesPercentageIndex = 2;
        diplomaticRelationsLocked = false;
    }


    public void copyFrom(LoadingParameters src) {
        loadingType = src.loadingType;
        levelSize = src.levelSize;
        playersNumber = src.playersNumber;
        fractionsQuantity = src.fractionsQuantity;
        difficulty = src.difficulty;
        colorOffset = src.colorOffset;
        slayRules = src.slayRules;
        campaignLevelIndex = src.campaignLevelIndex;
        activeHexes = src.activeHexes;
        turn = src.turn;
        replay = src.replay;
        fogOfWar = src.fogOfWar;
        diplomacy = src.diplomacy;
        diplomacyInfo = src.diplomacyInfo;
        userLevelMode = src.userLevelMode;
        ulKey = src.ulKey;
        editorColorFixApplied = src.editorColorFixApplied;
        levelCode = src.levelCode;
        editorProvincesData = src.editorProvincesData;
        editorRelationsData = src.editorRelationsData;
        preparedMessagesData = src.preparedMessagesData;
        genProvinces = src.genProvinces;
        treesPercentageIndex = src.treesPercentageIndex;
        goalData = src.goalData;
        diplomaticRelationsLocked = src.diplomaticRelationsLocked;
        editorCoalitionsData = src.editorCoalitionsData;
    }


    public static void initialize() {
        instance = null;
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
        System.out.println("loadMode = " + loadingType);
        System.out.println("activeHexes = " + activeHexes);
        System.out.println("levelSize = " + levelSize);
        System.out.println("playersNumber = " + playersNumber);
        System.out.println("fractionsQuantity = " + fractionsQuantity);
        System.out.println("difficulty = " + difficulty);
        System.out.println("colorOffset = " + colorOffset);
        System.out.println("slayRules = " + slayRules);
        System.out.println("campaignLevelIndex = " + campaignLevelIndex);
        System.out.println("turn = " + turn);
        System.out.println("replay = " + replay);
        System.out.println("fogOfWar = " + fogOfWar);
        System.out.println("diplomacy = " + diplomacy);
        System.out.println("userLevelMode = " + userLevelMode);
        System.out.println("ulKey = " + ulKey);
        System.out.println("editorColorFixApplied = " + editorColorFixApplied);
        System.out.println("levelCode = " + levelCode);
        System.out.println("editorProvincesData = " + editorProvincesData);
        System.out.println("editorRelationsData = " + editorRelationsData);
        System.out.println("preparedMessagesData = " + preparedMessagesData);
        System.out.println("genProvinces = " + genProvinces);
        System.out.println("treesPercentageIndex = " + treesPercentageIndex);
        System.out.println("goalData = " + goalData);
        System.out.println("diplomaticRelationsLocked = " + diplomaticRelationsLocked);
        System.out.println("editorCoalitionsData = " + editorCoalitionsData);

        System.out.println();
    }


    public void loadBasicInfo(Preferences prefs) {
        turn = prefs.getInteger("save_turn");
        playersNumber = prefs.getInteger("save_player_number");
        fractionsQuantity = prefs.getInteger("save_color_number");
        if (fractionsQuantity > GameRules.MAX_FRACTIONS_QUANTITY) {
            fractionsQuantity = GameRules.MAX_FRACTIONS_QUANTITY;
        }
        levelSize = prefs.getInteger("save_level_size");
        difficulty = prefs.getInteger("save_difficulty");
        GameRules.campaignMode = prefs.getBoolean("save_campaign_mode");
        campaignLevelIndex = prefs.getInteger("save_current_level");
        colorOffset = prefs.getInteger("save_color_offset", 0);
        slayRules = prefs.getBoolean("slay_rules", true);
        fogOfWar = prefs.getBoolean("fog_of_war", false);
        diplomacy = prefs.getBoolean("diplomacy", false);
        userLevelMode = prefs.getBoolean("user_level_mode", false);
        ulKey = prefs.getString("ul_key", null);
        editorColorFixApplied = prefs.getBoolean("editor_color_fix_applied", false);
        diplomaticRelationsLocked = prefs.getBoolean("lock_relations", false);
    }
}
