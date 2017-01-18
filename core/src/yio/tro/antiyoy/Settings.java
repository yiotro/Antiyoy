package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.GameView;
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
        if (soundIndex == 0) Settings.SOUND = false;
        else Settings.SOUND = true;
        menuControllerYio.getCheckButtonById(5).setChecked(Settings.SOUND);

        // skin
        int skin = prefs.getInteger("skin", 0);
        gameView.loadSkin(skin);
        float slSkinValue = (float) skin / 2f;
        menuControllerYio.sliders.get(5).setRunnerValue(slSkinValue);

        // interface. Number of save slots
        Settings.interface_type = prefs.getInteger("interface", 0);
        menuControllerYio.getCheckButtonById(2).setChecked(Settings.interface_type == 1);

        // autosave
        int AS = prefs.getInteger("autosave", 0);
        Settings.autosave = false;
        if (AS == 1) Settings.autosave = true;
        menuControllerYio.getCheckButtonById(1).setChecked(Settings.autosave);

        // sensitivity
        int sensitivity = prefs.getInteger("sensitivity", 6);
        menuControllerYio.sliders.get(9).setRunnerValueByIndex(sensitivity);
        Settings.sensitivity = Math.max(0.1f, menuControllerYio.sliders.get(9).runnerValue);

        // ask to end turn
        int ATET = prefs.getInteger("ask_to_end_turn", 0);
        Settings.ask_to_end_turn = (ATET == 1);
//        menuControllerYio.sliders.get(8).setRunnerValue(ATET);
        menuControllerYio.getCheckButtonById(3).setChecked(Settings.ask_to_end_turn);

        // show city names
        int cityNames = prefs.getInteger("city_names", 0);
        gameController.setCityNamesEnabled(cityNames);
        menuControllerYio.getCheckButtonById(4).setChecked(cityNames == 1);

        // camera offset
        int camOffsetIndex = prefs.getInteger("camera_offset", 2);
        gameController.cameraController.cameraOffset = 0.05f * GraphicsYio.width * camOffsetIndex;
        menuControllerYio.sliders.get(6).setRunnerValueByIndex(camOffsetIndex);

        // turns limit
        Settings.turns_limit = prefs.getBoolean("turns_limit", true);
        menuControllerYio.getCheckButtonById(6).setChecked(Settings.turns_limit);

        // long tap to move
        Settings.long_tap_to_move = prefs.getBoolean("long_tap_to_move", true);
        CheckButtonYio checkButtonById = menuControllerYio.getCheckButtonById(7);
        if (checkButtonById != null) {
            checkButtonById.setChecked(Settings.long_tap_to_move);
        }

        // water texture
        Settings.waterTexture = prefs.getBoolean("water_texture", false);
        gameView.loadBackgroundTexture();
        CheckButtonYio chkWaterTexture = menuControllerYio.getCheckButtonById(10);
        if (chkWaterTexture != null) {
            chkWaterTexture.setChecked(Settings.waterTexture);
        }

        menuControllerYio.sliders.get(5).updateValueString();
        menuControllerYio.sliders.get(6).updateValueString();
        menuControllerYio.sliders.get(9).updateValueString();
    }


    public void saveSettings() {
        Preferences prefs = Gdx.app.getPreferences("settings");
        prefs.putInteger("sound", boolToInteger(menuControllerYio.getCheckButtonById(5).isChecked()));
        prefs.putInteger("skin", menuControllerYio.sliders.get(5).getCurrentRunnerIndex());
        prefs.putInteger("interface", boolToInteger(menuControllerYio.getCheckButtonById(2).isChecked())); // slot number
        prefs.putInteger("autosave", boolToInteger(menuControllerYio.getCheckButtonById(1).isChecked()));
        prefs.putInteger("ask_to_end_turn", boolToInteger(menuControllerYio.getCheckButtonById(3).isChecked()));
        prefs.putInteger("sensitivity", menuControllerYio.sliders.get(9).getCurrentRunnerIndex());
        prefs.putInteger("city_names", boolToInteger(menuControllerYio.getCheckButtonById(4).isChecked()));
        prefs.putInteger("camera_offset", menuControllerYio.sliders.get(6).getCurrentRunnerIndex());
        prefs.putBoolean("turns_limit", menuControllerYio.getCheckButtonById(6).isChecked());
        prefs.putBoolean("long_tap_to_move", menuControllerYio.getCheckButtonById(7).isChecked());
        CheckButtonYio chkWaterTexture = menuControllerYio.getCheckButtonById(10);
        if (chkWaterTexture != null) {
            prefs.putBoolean("water_texture", chkWaterTexture.isChecked());
        }
        prefs.flush();
    }


    private int boolToInteger(boolean b) {
        if (b) return 1;
        return 0;
    }
}
