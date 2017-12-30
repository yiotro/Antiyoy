package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.game_view.GameView;
import yio.tro.antiyoy.menu.CheckButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.menu.slider.SliderYio;

public class Settings {

    static Settings instance = null;
    YioGdxGame yioGdxGame;
    public static boolean ask_to_end_turn = false;
    public static boolean autosave;
    public static boolean long_tap_to_move;
    public static boolean soundEnabled = true;
    public static float sensitivity;
    public static boolean waterTexture;
    private MenuControllerYio menuControllerYio;
    private GameView gameView;
    private GameController gameController;
    public static int skinIndex;
    public static boolean replaysEnabled;
    public static boolean fastConstruction;
    public static boolean musicEnabled;
    public static boolean leftHandMode;


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


    public void loadSettings() {
        Preferences prefs = Gdx.app.getPreferences("settings");

        // sound
        int soundIndex = prefs.getInteger("sound", 0);
        if (soundIndex == 0) soundEnabled = false;
        else soundEnabled = true;
        menuControllerYio.getCheckButtonById(5).setChecked(soundEnabled);

        // skin
        skinIndex = prefs.getInteger("skin", 0);
        gameView.loadSkin(skinIndex);

        // autosave
        int AS = prefs.getInteger("autosave", 0);
        autosave = false;
        if (AS == 1) autosave = true;
        menuControllerYio.getCheckButtonById(1).setChecked(autosave);

        // sensitivity
        sensitivity = prefs.getInteger("sensitivity", 6);
        sensitivity = Math.max(0.1f, sensitivity / 6f);

        // ask to end turn
        int ATET = prefs.getInteger("ask_to_end_turn", 0);
        ask_to_end_turn = (ATET == 1);
//        menuControllerYio.sliders.get(8).setRunnerValue(ATET);
        menuControllerYio.getCheckButtonById(3).setChecked(ask_to_end_turn);

        // show city names
        int cityNames = prefs.getInteger("city_names", 0);
        gameController.setCityNamesEnabled(cityNames);
        menuControllerYio.getCheckButtonById(4).setChecked(cityNames == 1);

        // long tap to move
        long_tap_to_move = prefs.getBoolean("long_tap_to_move", true);
        applyCheckButtonIfNotNull(menuControllerYio.getCheckButtonById(7), long_tap_to_move);

        // water texture
        waterTexture = prefs.getBoolean("water_texture", false);
        applyCheckButtonIfNotNull(menuControllerYio.getCheckButtonById(10), waterTexture);
        gameView.loadBackgroundTexture();

        replaysEnabled = prefs.getBoolean("replays_enabled", true);
        applyCheckButtonIfNotNull(menuControllerYio.getCheckButtonById(8), replaysEnabled);

        fastConstruction = prefs.getBoolean("fast_construction", false);
        applyCheckButtonIfNotNull(menuControllerYio.getCheckButtonById(9), fastConstruction);

        musicEnabled = prefs.getBoolean("music", true);
        applyCheckButtonIfNotNull(menuControllerYio.getCheckButtonById(2), musicEnabled);

        leftHandMode = prefs.getBoolean("left_hand_mode", false);
        applyCheckButtonIfNotNull(menuControllerYio.getCheckButtonById(12), leftHandMode);

        MusicManager.getInstance().onMusicStatusChanged();
    }


    public boolean saveSettings() {
        Preferences prefs = Gdx.app.getPreferences("settings");

        prefs.putInteger("sound", boolToInteger(menuControllerYio.getCheckButtonById(5).isChecked()));
        prefs.putInteger("autosave", boolToInteger(menuControllerYio.getCheckButtonById(1).isChecked()));
        prefs.putInteger("ask_to_end_turn", boolToInteger(menuControllerYio.getCheckButtonById(3).isChecked()));
        prefs.putInteger("city_names", boolToInteger(menuControllerYio.getCheckButtonById(4).isChecked()));
        prefs.putBoolean("music", menuControllerYio.getCheckButtonById(2).isChecked());

        prefs.flush();

        MusicManager.getInstance().onMusicStatusChanged();

        return false;
    }


    public void saveMoreSettings() {
        Preferences prefs = Gdx.app.getPreferences("settings");

        saveSkin(prefs);
        prefs.putInteger("sensitivity", Scenes.sceneMoreSettingsMenu.sensitivitySlider.getCurrentRunnerIndex());
        saveWaterTexture(prefs);
        prefs.putBoolean("long_tap_to_move", menuControllerYio.getCheckButtonById(7).isChecked());
        prefs.putBoolean("replays_enabled", menuControllerYio.getCheckButtonById(8).isChecked());
        prefs.putBoolean("fast_construction", menuControllerYio.getCheckButtonById(9).isChecked());
        prefs.putBoolean("left_hand_mode", menuControllerYio.getCheckButtonById(12).isChecked());

        MusicManager.getInstance().onMusicStatusChanged();

        prefs.flush();
    }


    private void applyCheckButtonIfNotNull(CheckButtonYio checkButtonYio, boolean value) {
        if (checkButtonYio == null) return;

        checkButtonYio.setChecked(value);
    }


    private void saveWaterTexture(Preferences prefs) {
        CheckButtonYio chkWaterTexture = menuControllerYio.getCheckButtonById(10);
        if (chkWaterTexture != null) {
            prefs.putBoolean("water_texture", chkWaterTexture.isChecked());
        }
    }


    private boolean saveSkin(Preferences prefs) {
        int lastIndex = skinIndex;
        SliderYio skinSlider = Scenes.sceneMoreSettingsMenu.skinSlider;
        skinIndex = skinSlider.getCurrentRunnerIndex();
        prefs.putInteger("skin", skinIndex);

        // shroom arts
        if (lastIndex != skinIndex && (lastIndex == 3 || skinIndex == 3)) {
            return true; // restart app
        }

        return false;
    }


    public static boolean isShroomArtsEnabled() {
        return skinIndex == 3;
    }


    private int boolToInteger(boolean b) {
        if (b) return 1;
        return 0;
    }
}
