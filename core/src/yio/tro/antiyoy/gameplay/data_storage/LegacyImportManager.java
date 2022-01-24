package yio.tro.antiyoy.gameplay.data_storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Clipboard;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.loading.LoadingType;
import yio.tro.antiyoy.stuff.Yio;

import java.util.StringTokenizer;

public class LegacyImportManager {

    GameController gameController;
    private LoadingParameters parameters;


    public LegacyImportManager(GameController gameController) {
        this.gameController = gameController;
    }


    public void importLevelFromClipboard() {
        String fromClipboard = "";

        Clipboard clipboard = Gdx.app.getClipboard();
        fromClipboard = clipboard.getContents();
        if (!isValidLevelString(fromClipboard)) return;

        importLevel(fromClipboard);
    }


    public void importLevel(String fullLevel) {
        parameters = LoadingParameters.getInstance();
        parameters.loadingType = LoadingType.editor_load;
        applyFullLevel(parameters, fullLevel);
        parameters.colorOffset = 0;
        LoadingManager.getInstance().startGame(parameters);
    }


    public void applyFullLevel(LoadingParameters parameters, String fullLevel) {
        this.parameters = parameters;
        int delimiterChar = fullLevel.indexOf("/");
        String basicInfo;
        int[] basicInfoValues = new int[4];

        if (delimiterChar < 0) { // empty slot
            return;
        }

        basicInfo = fullLevel.substring(0, delimiterChar);
        StringTokenizer stringTokenizer = new StringTokenizer(basicInfo, " ");
        int i = 0;

        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            if (Yio.isNumeric(token)) {
                basicInfoValues[i] = Integer.valueOf(token);
            }
            i++;
        }

        parameters.activeHexes = fullLevel.substring(delimiterChar + 1);
        parameters.playersNumber = basicInfoValues[2];
        parameters.fractionsQuantity = basicInfoValues[3];
        parameters.levelSize = basicInfoValues[1];
        parameters.difficulty = basicInfoValues[0];
    }


    private boolean isValidLevelString(String fullLevel) {
        if (fullLevel == null) return false;
        if (!fullLevel.contains("/")) return false;
        if (!fullLevel.contains("#")) return false;
        if (fullLevel.length() < 10) return false;
        return true;
    }
}
