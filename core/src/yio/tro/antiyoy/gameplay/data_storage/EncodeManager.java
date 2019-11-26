package yio.tro.antiyoy.gameplay.data_storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Clipboard;
import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.LevelSize;
import yio.tro.antiyoy.gameplay.editor.EditorSaveSystem;
import yio.tro.antiyoy.gameplay.editor.LevelEditor;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class EncodeManager {

    GameController gameController;
    private StringBuilder builder;
    private FieldManager fieldManager;


    public EncodeManager(GameController gameController) {
        this.gameController = gameController;
    }


    public String performToClipboard() {
        String result = perform();

        Clipboard clipboard = Gdx.app.getClipboard();
        clipboard.setContents(result);

        return result;
    }


    public String perform() {
        builder = new StringBuilder();
        fieldManager = gameController.fieldManager;

        addWatermark();
        encodeLevelSize();
        encodeGeneralInfo();
        encodeMapName();
        encodeEditorInfo();
        encodeLand();
        encodeUnits();
        encodeProvinces();
        encodeRelations();
        encodeMessages();

        builder.append("#");
        return builder.toString();
    }


    private void encodeMessages() {
        startSection("messages");
        builder.append(gameController.messagesManager.encode());
    }


    private void encodeRelations() {
        startSection("relations");
        builder.append(gameController.levelEditor.editorRelationsManager.encode());
    }


    private void encodeProvinces() {
        startSection("provinces");
        builder.append(gameController.levelEditor.editorProvinceManager.encode());
    }


    private void encodeMapName() {
        startSection("map_name");
        Preferences preferences = getPreferences();
        int slotNumber = GameRules.editorSlotNumber;
        String savedNameString = preferences.getString("slot" + slotNumber + ":name");
        String name = LanguagesManager.getInstance().getString("slot") + " " + slotNumber;
        if (savedNameString.length() > 0) {
            name = savedNameString;
        }
        builder.append(name);
    }


    private Preferences getPreferences() {
        return Gdx.app.getPreferences(EditorSaveSystem.EDITOR_PREFS);
    }


    private void encodeEditorInfo() {
        startSection("editor_info");
        builder.append(GameRules.editorChosenColor).append(" ");
        builder.append(GameRules.editorDiplomacy).append(" ");
        builder.append(GameRules.editorFog).append(" ");
    }


    private void encodeUnits() {
        startSection("units");
        for (Hex activeHex : fieldManager.activeHexes) {
            if (!activeHex.hasUnit()) continue;
            builder.append(activeHex.unit.encode()).append(",");
        }
    }


    private void encodeLand() {
        startSection("land");
        builder.append(fieldManager.encode());
    }


    private void encodeGeneralInfo() {
        startSection("general");
        builder.append(GameRules.difficulty).append(" ");
        builder.append(gameController.playersNumber).append(" ");
        builder.append(GameRules.fractionsQuantity);
    }


    private void encodeLevelSize() {
        startSection("level_size");
        builder.append(gameController.levelSizeManager.levelSize);
    }


    private void addWatermark() {
        builder.append("antiyoy_level_code");
    }


    public boolean isCurrentLevelTooBig() {
        if (gameController.levelSizeManager.levelSize == LevelSize.HUGE) return true;

        float min = -1;
        float max = -1;
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (min == -1 || activeHex.pos.y < min) {
                min = activeHex.pos.y;
            }
            if (max == -1 || activeHex.pos.y > max) {
                max = activeHex.pos.y;
            }
        }

        float delta = max - min;
        delta /= fieldManager.hexStep1;
        delta += 1;

        return delta > LevelEditor.MAX_ACCEPTABLE_DELTA;
    }


    private void startSection(String name) {
        builder.append("#").append(name).append(":");
    }
}
