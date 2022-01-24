package yio.tro.antiyoy.gameplay.editor;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.data_storage.DecodeManager;
import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;
import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.ArrayList;

public class EditorCoalitionsManager implements EncodeableYio {

    LevelEditorManager levelEditorManager;
    public ArrayList<Coalition> coalitions;


    public EditorCoalitionsManager(LevelEditorManager levelEditorManager) {
        this.levelEditorManager = levelEditorManager;
        coalitions = new ArrayList<>();
    }


    public void defaultValues() {
        coalitions.clear();
    }


    public void onEndCreation() {
        
    }


    public void onLevelImported(String levelCode) {
        DecodeManager decodeManager = getGameController().decodeManager;
        decodeManager.setSource(levelCode);
        String relationsSection = decodeManager.getSection("coalitions");
        if (relationsSection == null) return;

        decode(relationsSection);
    }


    private GameController getGameController() {
        return levelEditorManager.gameController;
    }


    @Override
    public String encode() {
        return "temporary";
    }


    @Override
    public void decode(String source) {

    }


    public void checkToApplyData() {
        if (!GameRules.diplomacyEnabled) return;


    }
}
