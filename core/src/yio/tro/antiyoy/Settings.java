package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.game_view.GameView;
import yio.tro.antiyoy.menu.CheckButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class Settings {

    static Settings instance = null;
    YioGdxGame yioGdxGame;
    public static final int INTERFACE_SIMPLE = 0;
    public static final int INTERFACE_COMPLICATED = 1;
    public static int interface_type = 0;
    public static boolean ask_to_end_turn = false;
    public static boolean autosave;
    public static boolean turns_limit;
    public static boolean long_tap_to_move;
    public static boolean SOUND = true;
    public static float sensitivity;
    public static boolean waterTexture;
    private MenuControllerYio menuControllerYio;
    private GameView gameView;
    private GameController gameController;
    public static int skinIndex;


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
        if (soundIndex == 0) SOUND = false;
        else SOUND = true;
        menuControllerYio.getCheckButtonById(5).setChecked(SOUND);

        // skin
        skinIndex = prefs.getInteger("skin", 0);
        gameView.loadSkin(skinIndex);
        menuControllerYio.sliders.get(5).setRunnerValueByIndex(skinIndex);

        // interface. Number of save slots
        interface_type = prefs.getInteger("interface", 1);
        menuControllerYio.getCheckButtonById(2).setChecked(interface_type == 1);

        // autosave
        int AS = prefs.getInteger("autosave", 0);
        autosave = false;
        if (AS == 1) autosave = true;
        menuControllerYio.getCheckButtonById(1).setChecked(autosave);

        // sensitivity
        sensitivity = prefs.getInteger("sensitivity", 6);
        menuControllerYio.sliders.get(9).setRunnerValueByIndex((int) sensitivity);
        sensitivity = Math.max(0.1f, menuControllerYio.sliders.get(9).runnerValue);

        // ask to end turn
        int ATET = prefs.getInteger("ask_to_end_turn", 0);
        ask_to_end_turn = (ATET == 1);
//        menuControllerYio.sliders.get(8).setRunnerValue(ATET);
        menuControllerYio.getCheckButtonById(3).setChecked(ask_to_end_turn);

        // show city names
        int cityNames = prefs.getInteger("city_names", 0);
        gameController.setCityNamesEnabled(cityNames);
        menuControllerYio.getCheckButtonById(4).setChecked(cityNames == 1);

        // turns limit
        turns_limit = prefs.getBoolean("turns_limit", true);
        menuControllerYio.getCheckButtonById(6).setChecked(turns_limit);

        // long tap to move
        long_tap_to_move = prefs.getBoolean("long_tap_to_move", true);
        CheckButtonYio checkButtonById = menuControllerYio.getCheckButtonById(7);
        if (checkButtonById != null) {
            checkButtonById.setChecked(long_tap_to_move);
        }

        // water texture
        waterTexture = prefs.getBoolean("water_texture", false);
        gameView.loadBackgroundTexture();
        CheckButtonYio chkWaterTexture = menuControllerYio.getCheckButtonById(10);
        if (chkWaterTexture != null) {
            chkWaterTexture.setChecked(waterTexture);
        }

        menuControllerYio.sliders.get(5).updateValueString();
//        menuControllerYio.sliders.get(6).updateValueString();
        menuControllerYio.sliders.get(9).updateValueString();
    }


    public boolean saveSettings() {
        Preferences prefs = Gdx.app.getPreferences("settings");
        boolean needToRestart = false;

        prefs.putInteger("sound", boolToInteger(menuControllerYio.getCheckButtonById(5).isChecked()));
        saveSkin(prefs);
        prefs.putInteger("interface", boolToInteger(menuControllerYio.getCheckButtonById(2).isChecked())); // slot number
        prefs.putInteger("autosave", boolToInteger(menuControllerYio.getCheckButtonById(1).isChecked()));
        prefs.putInteger("ask_to_end_turn", boolToInteger(menuControllerYio.getCheckButtonById(3).isChecked()));
        prefs.putInteger("sensitivity", menuControllerYio.sliders.get(9).getCurrentRunnerIndex());
        prefs.putInteger("city_names", boolToInteger(menuControllerYio.getCheckButtonById(4).isChecked()));
//        prefs.putInteger("camera_offset", menuControllerYio.sliders.get(6).getCurrentRunnerIndex());
        prefs.putBoolean("turns_limit", menuControllerYio.getCheckButtonById(6).isChecked());
        prefs.putBoolean("long_tap_to_move", menuControllerYio.getCheckButtonById(7).isChecked());
        saveWaterTexture(prefs);

        prefs.flush();
        return needToRestart;
    }


    private void saveWaterTexture(Preferences prefs) {
        CheckButtonYio chkWaterTexture = menuControllerYio.getCheckButtonById(10);
        if (chkWaterTexture != null) {
            prefs.putBoolean("water_texture", chkWaterTexture.isChecked());
        }
    }


    private boolean saveSkin(Preferences prefs) {
        int lastIndex = skinIndex;
        skinIndex = menuControllerYio.sliders.get(5).getCurrentRunnerIndex();
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
