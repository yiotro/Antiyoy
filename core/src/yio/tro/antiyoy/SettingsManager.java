package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SettingsManager {

    static SettingsManager instance = null;
    YioGdxGame yioGdxGame;
    public static boolean cautiosEndTurnEnabled;
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
    public static boolean useCityNamesList;
    public static boolean automaticTransition;


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
        loadCityNamesOptions();
    }


    private void loadMoreSettings() {
        Preferences prefs = getPrefs();

        skinIndex = prefs.getInteger("skin", 0);

        setSensitivity(prefs.getInteger("sensitivity", 6));

        longTapToMove = prefs.getBoolean("long_tap_to_move", true);
        waterTextureEnabled = prefs.getBoolean("water_texture", false);
        replaysEnabled = true;
        leftHandMode = prefs.getBoolean("left_hand_mode", false);
        resumeButtonEnabled = prefs.getBoolean("resume_button", getDefaultValueForResumeButtonOption());
        fullScreenMode = prefs.getBoolean("full_screen", false);
        nativeKeyboard = true;
        automaticTransition = prefs.getBoolean("automatic_transition", false);
    }


    public void setSensitivity(int sliderIndex) {
        sensitivity = Math.max(0.1f, sliderIndex / 6f);
    }


    private void loadMainSettings() {
        Preferences prefs = getPrefs();

        autosave = convertToBoolean(prefs.getInteger("autosave", 1));
        musicEnabled = prefs.getBoolean("music", false);
        cautiosEndTurnEnabled = prefs.getBoolean("cautious_end_turn", false);
        soundEnabled = convertToBoolean(prefs.getInteger("sound", 0));
        fastConstructionEnabled = prefs.getBoolean("fast_construction", false);

        MusicManager.getInstance().onMusicStatusChanged();
    }


    private void loadCityNamesOptions() {
        Preferences prefs = getPrefs();
        cityNamesEnabled = convertToBoolean(prefs.getInteger("city_names", 0));
        useCityNamesList = prefs.getBoolean("use_city_names_list", true);
    }


    private Preferences getPrefs() {
        return Gdx.app.getPreferences("settings");
    }


    private boolean convertToBoolean(int value) {
        return value == 1;
    }


    private boolean getDefaultValueForResumeButtonOption() {
        return YioGdxGame.platformType == PlatformType.ios;

    }


    public boolean saveMainSettings() {
        Preferences prefs = getPrefs();

        prefs.putInteger("sound", convertToInteger(soundEnabled));
        prefs.putInteger("autosave", convertToInteger(autosave));
        prefs.putBoolean("cautious_end_turn", cautiosEndTurnEnabled);
        prefs.putBoolean("music", musicEnabled);
        prefs.putBoolean("fast_construction", fastConstructionEnabled);

        prefs.flush();

        return false;
    }


    public void saveCityNamesOptions() {
        Preferences prefs = getPrefs();
        prefs.putInteger("city_names", convertToInteger(cityNamesEnabled));
        prefs.putBoolean("use_city_names_list", useCityNamesList);
        prefs.flush();
    }


    public void saveMoreSettings() {
        Preferences prefs = getPrefs();

        prefs.putInteger("skin", skinIndex);
        prefs.putInteger("sensitivity", (int) (sensitivity * 6));
        prefs.putBoolean("water_texture", waterTextureEnabled);
        prefs.putBoolean("long_tap_to_move", longTapToMove);
        prefs.putBoolean("left_hand_mode", leftHandMode);
        prefs.putBoolean("resume_button", resumeButtonEnabled);
        prefs.putBoolean("full_screen", fullScreenMode);
        prefs.putBoolean("automatic_transition", automaticTransition);

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
