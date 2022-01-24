package yio.tro.antiyoy.gameplay.data_storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Clipboard;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.loading.LoadingType;

public class ImportManager {

    GameController gameController;
    private final DecodeManager decodeManager;


    public ImportManager(GameController gameController) {
        this.gameController = gameController;
        decodeManager = gameController.decodeManager;
    }


    public void launchGameFromClipboard() {
        Clipboard clipboard = Gdx.app.getClipboard();
        String levelCode = clipboard.getContents();
        if (checkForLegacyImport(levelCode)) return;
        if (!decodeManager.isValidLevelCode(levelCode)) return;

        launchGame(LoadingType.editor_import, levelCode);
    }


    private boolean checkForLegacyImport(String levelCode) {
        if (levelCode == null) return false;
        if (levelCode.contains("antiyoy_level_code")) return false;
        if (levelCode.contains("http")) return false;
        if (!levelCode.contains("/")) return false;
        if (!levelCode.contains("#")) return false;

        gameController.gameSaver.legacyImportManager.importLevel(levelCode);
        return true;
    }


    public void launchGame(LoadingType loadingType, String levelCode) {
        launchGame(loadingType, levelCode, null);
    }


    public void launchGame(LoadingType loadingType, String levelCode, String ulKey) {
        LoadingParameters instance = LoadingParameters.getInstance();
        decodeManager.setSource(levelCode);
        instance.loadingType = loadingType;
        instance.ulKey = ulKey;
        instance.levelSize = decodeManager.extractLevelSize(levelCode);
        instance.editorProvincesData = decodeManager.getSection("provinces");
        instance.editorRelationsData = decodeManager.getSection("relations");

        // this doesn't really matter
        instance.playersNumber = 1;
        instance.fractionsQuantity = 5;
        instance.colorOffset = 0;
        instance.difficulty = 1;
        instance.levelCode = levelCode;

        int slotNumber = gameController.editorSaveSystem.getNewSlotNumber();
        LoadingManager.getInstance().startGame(instance);

        gameController.editorSaveSystem.onLevelImported(levelCode, slotNumber);
    }
}
