package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SettingsManager {

    static SettingsManager instance = null;
    YioGdxGame yioGdxGame;
    public static boolean askToEndTurn;
    public static boolean autosave;
    public static boolean longTapToMove;
    public static boolean soundEnabled;
    public static float sensitivity;
    public static boolean waterTextureEnabled;
    public static int skinIndex;
    public static boolean replaysEnabled;
    public static boolean fastConstructionEnabled;
    public static boolean musicEnabled;
    public static boolean leftHandMode;
    public static boolean resumeButtonEnabled;
    public static boolean cityNamesEnabled;
    public static boolean fullScreenMode;
    public static boolean nativeKeyboard;


    public static void initialize() {
        instance = null;
    }


    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }

        return instance;
    }


    public void setYioGdxGame(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
    }


    public void loadAllSettings() {
        loadMainSettings();
        loadMoreSettings();
    }


    private void loadMoreSettings() {
        Preferences prefs = getPrefs();

        skinIndex = prefs.getInteger("skin", 0);

        setSensitivity(prefs.getInteger("sensitivity", 6));

        longTapToMove = prefs.getBoolean("long_tap_to_move", true);
        waterTextureEnabled = prefs.getBoolean("water_texture", false);
        replaysEnabled = true;
        fastConstructionEnabled = prefs.getBoolean("fast_construction", false);
        leftHandMode = prefs.getBoolean("left_hand_mode", false);
        resumeButtonEnabled = prefs.getBoolean("resume_button", getDefaultValueForResumeButtonOption());
        fullScreenMode = prefs.getBoolean("full_screen", false);
        nativeKeyboard = prefs.getBoolean("native_keyboard", true);
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


    private boolean getDefaultValueForResumeButtonOption() {
        return YioGdxGame.IOS;

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
        prefs.putBoolean("water_texture", waterTextureEnabled);
        prefs.putBoolean("long_tap_to_move", longTapToMove);
        prefs.putBoolean("fast_construction", fastConstructionEnabled);
        prefs.putBoolean("left_hand_mode", leftHandMode);
        prefs.putBoolean("resume_button", resumeButtonEnabled);
        prefs.putBoolean("full_screen", fullScreenMode);
        prefs.putBoolean("native_keyboard", nativeKeyboard);

        prefs.flush();
    }


    public void setSkin(int index) {
        if (index == skinIndex) return;

        skinIndex = index;
        yioGdxGame.skinManager.onSkinChanged();
    }


    private int convertToInteger(boolean value) {
        if (value) {
            return 1;
        }
        return 0;
    }
}
