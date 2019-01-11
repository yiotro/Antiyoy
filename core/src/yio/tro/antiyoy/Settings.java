package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.game_view.GameView;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class Settings {

    static Settings instance = null;
    YioGdxGame yioGdxGame;
    public static boolean askToEndTurn = false;
    public static boolean autosave;
    public static boolean longTapToMove;
    public static boolean soundEnabled = true;
    public static float sensitivity;
    public static boolean waterTextureChosen;
    private MenuControllerYio menuControllerYio;
    private GameView gameView;
    private GameController gameController;
    public static int skinIndex;
    public static boolean replaysEnabled;
    public static boolean fastConstructionEnabled;
    public static boolean musicEnabled;
    public static boolean leftHandMode;
    public static boolean resumeButtonEnabled;
    public static boolean cityNamesEnabled;
    public static boolean fullScreenMode;


    public static void initialize() {
        instance = null;
    }


    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }

        return instance;
    }


    public void setYioGdxGame(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        menuControllerYio = yioGdxGame.menuControllerYio;
        gameView = yioGdxGame.gameView;
        gameController = yioGdxGame.gameController;
    }


    public void loadAllSettings() {
        loadMainSettings();
        loadMoreSettings();
    }


    private void loadMoreSettings() {
        Preferences prefs = getPrefs();

        skinIndex = prefs.getInteger("skin", 0);
        gameView.loadSkin(skinIndex);

        setSensitivity(prefs.getInteger("sensitivity", 6));

        longTapToMove = prefs.getBoolean("long_tap_to_move", true);
        waterTextureChosen = prefs.getBoolean("water_texture", false);
        replaysEnabled = prefs.getBoolean("replays_enabled", true);
        fastConstructionEnabled = prefs.getBoolean("fast_construction", false);
        leftHandMode = prefs.getBoolean("left_hand_mode", false);
        resumeButtonEnabled = prefs.getBoolean("resume_button", getResumeButtonDefaultValue());
        fullScreenMode = prefs.getBoolean("full_screen", false);
    }


    public void setSensitivity(int sliderIndex) {
        sensitivity = Math.max(0.1f, sliderIndex / 6f);
    }


    private void loadMainSettings() {
        Preferences prefs = getPrefs();

        autosave = convertToBoolean(prefs.getInteger("autosave", 1));
        musicEnabled = prefs.getBoolean("music", false);
        askToEndTurn = convertToBoolean(prefs.getInteger("ask_to_end_turn", 0));
        cityNamesEnabled = convertToBoolean(prefs.getInteger("city_names", 0));
        soundEnabled = convertToBoolean(prefs.getInteger("sound", 0));

        MusicManager.getInstance().onMusicStatusChanged();
    }


    private Preferences getPrefs() {
        return Gdx.app.getPreferences("settings");
    }


    private boolean convertToBoolean(int value) {
        return value == 1;
    }


    private boolean getResumeButtonDefaultValue() {
        if (YioGdxGame.IOS) return true;

        return false;
    }


    public boolean saveMainSettings() {
        Preferences prefs = getPrefs();

        prefs.putInteger("sound", convertToInteger(soundEnabled));
        prefs.putInteger("autosave", convertToInteger(autosave));
        prefs.putInteger("ask_to_end_turn", convertToInteger(askToEndTurn));
        prefs.putInteger("city_names", convertToInteger(cityNamesEnabled));
        prefs.putBoolean("music", musicEnabled);

        prefs.flush();

        return false;
    }


    public void saveMoreSettings() {
        Preferences prefs = getPrefs();

        prefs.putInteger("skin", skinIndex);
        prefs.putInteger("sensitivity", (int) (sensitivity * 6));
        prefs.putBoolean("water_texture", waterTextureChosen);
        prefs.putBoolean("long_tap_to_move", longTapToMove);
        prefs.putBoolean("replays_enabled", replaysEnabled);
        prefs.putBoolean("fast_construction", fastConstructionEnabled);
        prefs.putBoolean("left_hand_mode", leftHandMode);
        prefs.putBoolean("resume_button", resumeButtonEnabled);
        prefs.putBoolean("full_screen", fullScreenMode);

        prefs.flush();
    }


    public void setSkin(int index) {
        if (index == skinIndex) return;

        skinIndex = index;
        Scenes.sceneSelectionOverlay.onSkinChanged();
    }


    public static boolean isShroomArtsEnabled() {
        return skinIndex == 3;
    }


    private int convertToInteger(boolean value) {
        if (value) {
            return 1;
        }
        return 0;
    }
}
