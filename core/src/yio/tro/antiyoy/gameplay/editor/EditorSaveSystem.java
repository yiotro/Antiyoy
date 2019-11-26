package yio.tro.antiyoy.gameplay.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.ai.Difficulty;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.loading.LoadingType;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.color_picking.ColorHolderElement;

import java.util.ArrayList;
import java.util.Arrays;

public class EditorSaveSystem {

    public static final String EDITOR_PREFS = "editor";
    public static final String SLOT_NAME = "slot";
    GameController gameController;


    public EditorSaveSystem(GameController gameController) {
        this.gameController = gameController;
    }


    public void createNewLevel(int levelSize) {
        LoadingParameters instance = LoadingParameters.getInstance();
        instance.loadingType = LoadingType.editor_new;
        instance.levelSize = levelSize;
        instance.playersNumber = 1;
        instance.fractionsQuantity = 5;
        instance.colorOffset = 0;
        instance.difficulty = Difficulty.BALANCER;

        int slotNumber = getNewSlotNumber();
        LoadingManager.getInstance().startGame(instance);
        GameRules.editorChosenColor = 1; // green
        GameRules.editorFog = false;
        GameRules.editorDiplomacy = false;
        GameRules.editorSlotNumber = slotNumber;

        gameController.getLevelEditor().resetInputMode();
    }


    public void onLevelImported(String levelCode, int slotNumber) {
        GameRules.editorSlotNumber = slotNumber;
        gameController.levelEditor.onLevelImported(levelCode);
    }


    public int getNewSlotNumber() {
        int maxSlotNumber = -1;
        int index = 0;
        Preferences preferences = getPreferences();
        while (true) {
            String key = LevelEditor.SLOT_NAME + index;
            if (index > 8 && !preferences.contains(key)) break;
            if (isEmpty(key)) break;

            maxSlotNumber = index;
            index++;
        }

        return maxSlotNumber + 1;
    }


    public void playLevel(int slotNumber) {
        Preferences prefs = getPreferences();
        String fullLevel = prefs.getString(SLOT_NAME + slotNumber, "");
        if (fullLevel.length() < 10) return; // empty slot

        LoadingParameters instance = LoadingParameters.getInstance();
        instance.loadingType = LoadingType.editor_play;
        gameController.gameSaver.legacyImportManager.applyFullLevel(instance, fullLevel);
        GameRules.editorChosenColor = prefs.getInteger("chosen_color" + slotNumber);
        GameRules.editorFog = prefs.getBoolean("editor_fog" + slotNumber, false);
        instance.fogOfWar = GameRules.editorFog;
        checkToApplyPlayersNumberFix(instance);
        GameRules.editorDiplomacy = prefs.getBoolean("editor_diplomacy" + slotNumber, false);
        instance.diplomacy = GameRules.editorDiplomacy;
        instance.colorOffset = getColorOffsetForPlayLevelProcess();
        instance.editorProvincesData = gameController.levelEditor.editorProvinceManager.encode();
        instance.editorRelationsData = gameController.levelEditor.editorRelationsManager.encode();
        instance.preparedMessagesData = gameController.messagesManager.encode();

        LoadingManager.getInstance().startGame(instance);
    }


    private int getColorOffsetForPlayLevelProcess() {
        if (GameRules.editorChosenColor == 0) {
            return getRandomValidPlayerColor();
        }
        return ColorHolderElement.getColor(GameRules.editorChosenColor, GameRules.MAX_FRACTIONS_QUANTITY);
    }


    private int getRandomValidPlayerColor() {
        ArrayList<Integer> availableColors = new ArrayList<>();
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (activeHex.isNeutral()) continue;
            int color = activeHex.fraction; // in case of editor it's the same thing
            if (availableColors.contains(color)) continue;
            availableColors.add(color);
        }

        if (availableColors.size() == 0) return 0;

        int index = YioGdxGame.random.nextInt(availableColors.size());
        return availableColors.get(index);
    }


    private void checkToApplyPlayersNumberFix(LoadingParameters instance) {
        if (instance.playersNumber <= GameRules.NEUTRAL_FRACTION) return;
        instance.playersNumber++;
    }


    private boolean isEmpty(String key) {
        String fullLevelString = getPreferences().getString(key);
        return (fullLevelString.length() < 10);
    }


    public int getSlotNumberByKey(String key) {
        return Integer.valueOf(key.substring(4));
    }


    public void loadSlot(int slotNumber) {
        Preferences prefs = getPreferences();
        String fullLevel = prefs.getString(SLOT_NAME + slotNumber, "");

        LoadingParameters instance = LoadingParameters.getInstance();

        instance.loadingType = LoadingType.editor_load;
        gameController.gameSaver.legacyImportManager.applyFullLevel(instance, fullLevel);
        instance.colorOffset = 0;

        LoadingManager.getInstance().startGame(instance);
        GameRules.editorChosenColor = prefs.getInteger("chosen_color" + slotNumber);
        GameRules.editorFog = prefs.getBoolean("editor_fog" + slotNumber, false);
        GameRules.editorDiplomacy = prefs.getBoolean("editor_diplomacy" + slotNumber, false);
        GameRules.editorSlotNumber = slotNumber;
        String editorProvincesString = prefs.getString("editor_provinces" + slotNumber, "");
        if (editorProvincesString.length() > 4) {
            gameController.levelEditor.editorProvinceManager.decode(editorProvincesString);
        }
        String editorRelationsString = prefs.getString("editor_relations" + slotNumber, "");
        if (editorRelationsString.length() > 4) {
            gameController.levelEditor.editorRelationsManager.decode(editorRelationsString);
        }
        String preparedMessagesString = prefs.getString("prepared_messages" + slotNumber, "");
        if (preparedMessagesString.length() > 1) {
            gameController.messagesManager.decode(preparedMessagesString);
        }

        gameController.getLevelEditor().resetInputMode();
    }


    public void loadTopSlot() {
        int newSlotNumber = getNewSlotNumber();
        if (newSlotNumber < 2) return;
        loadSlot(newSlotNumber - 1);
    }


    public void saveSlot(int slotNumber) {
        String fullLevel = getFullLevelString();
        Preferences prefs = getPreferences();
        prefs.putString(SLOT_NAME + slotNumber, fullLevel);
        prefs.putInteger("chosen_color" + slotNumber, GameRules.editorChosenColor);
        prefs.putBoolean("editor_fog" + slotNumber, GameRules.editorFog);
        prefs.putBoolean("editor_diplomacy" + slotNumber, GameRules.editorDiplomacy);
        prefs.putString("editor_provinces" + slotNumber, gameController.levelEditor.editorProvinceManager.encode());
        prefs.putString("editor_relations" + slotNumber, gameController.levelEditor.editorRelationsManager.encode());
        prefs.putString("prepared_messages" + slotNumber, gameController.messagesManager.encode());
        prefs.flush();
    }


    public String getFullLevelString() {
        GameRules.setFractionsQuantity(gameController.getLevelEditor().countUpFractionsQuantity());
        return gameController.gameSaver.legacyExportManager.getFullLevelString();
    }


    private Preferences getPreferences() {
        return Gdx.app.getPreferences(EDITOR_PREFS);
    }
}
