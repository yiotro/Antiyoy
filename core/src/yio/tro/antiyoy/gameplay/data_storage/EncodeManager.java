package yio.tro.antiyoy.gameplay.data_storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Clipboard;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.editor.EditorSaveSystem;
import yio.tro.antiyoy.gameplay.editor.LevelEditorManager;
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
        encodeCoalitions();
        encodeMessages();
        encodeGoal();
        encodeRealMoney();

        builder.append("#");
        return builder.toString();
    }


    private void encodeCoalitions() {
        startSection("coalitions");
        builder.append(gameController.levelEditorManager.coalitionsManager.encode());
    }


    private void encodeRealMoney() {
        startSection("real_money");
        for (Province province : fieldManager.provinces) {
            Hex capital = province.getCapital();
            builder.append(capital.index1)
                    .append(" ")
                    .append(capital.index2)
                    .append(" ")
                    .append(province.money)
                    .append(",");
        }
    }


    private void encodeGoal() {
        startSection("goal");
        builder.append(gameController.finishGameManager.encode());
    }


    private void encodeMessages() {
        startSection("messages");
        builder.append(gameController.messagesManager.encode());
    }


    private void encodeRelations() {
        startSection("relations");
        builder.append(gameController.levelEditorManager.editorRelationsManager.encode());
    }


    private void encodeProvinces() {
        startSection("provinces");
        builder.append(gameController.levelEditorManager.editorProvinceManager.encode());
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
        builder.append(GameRules.diplomaticRelationsLocked).append(" ");
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

        return delta > LevelEditorManager.MAX_ACCEPTABLE_DELTA;
    }


    private void startSection(String name) {
        builder.append("#").append(name).append(":");
    }
}
