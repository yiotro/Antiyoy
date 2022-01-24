package yio.tro.antiyoy.gameplay.editor;

import yio.tro.antiyoy.gameplay.ColorsManager;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.data_storage.DecodeManager;
import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticRelation;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.scenes.Scenes;

import java.util.ArrayList;

public class EditorRelationsManager implements EncodeableYio{

    LevelEditorManager levelEditorManager;
    public ArrayList<EditorRelation> relations;


    public EditorRelationsManager(LevelEditorManager levelEditorManager) {
        this.levelEditorManager = levelEditorManager;
        relations = new ArrayList<>();
    }


    void defaultValues() {
        relations.clear();
    }


    public void onAddRelationRequested(int color1, int color2, int relation) {
        if (!isValid(color1, color2, relation)) return;

        EditorRelation editorRelation = new EditorRelation();
        editorRelation.color1 = color1;
        editorRelation.color2 = color2;
        editorRelation.relation = relation;
        relations.add(editorRelation);

        Scenes.sceneEditorEditRelation.hide();
        Scenes.sceneEditorDiplomacy.create();
    }


    public void deleteRelation(EditorRelation editorRelation) {
        relations.remove(editorRelation);
    }


    private boolean isValid(int color1, int color2, int relation) {
        if (color1 == -1) return false;
        if (color2 == -1) return false;
        if (color1 == color2) return false;
        if (relation == DiplomaticRelation.NEUTRAL) return false;
        return true;
    }


    void onEndCreation() {

    }


    public void onLevelImported(String levelCode) {
        DecodeManager decodeManager = getGameController().decodeManager;
        decodeManager.setSource(levelCode);
        String relationsSection = decodeManager.getSection("relations");
        if (relationsSection == null) return;

        decode(relationsSection);
    }


    @Override
    public String encode() {
        StringBuilder builder = new StringBuilder();
        for (EditorRelation relation : relations) {
            builder.append(relation.encode()).append(",");
        }
        return builder.toString();
    }


    @Override
    public void decode(String source) {
        relations.clear();
        for (String token : source.split(",")) {
            if (token.length() < 4) continue;
            EditorRelation editorRelation = new EditorRelation();
            editorRelation.decode(token);
            relations.add(editorRelation);
        }
    }


    private GameController getGameController() {
        return levelEditorManager.gameController;
    }


    public void checkToApplyData() {
        if (!GameRules.diplomacyEnabled) return;

        String source = getGameController().initialParameters.editorRelationsData;
        if (source == null) return;
        if (source.length() < 5) return;

        onEndCreation();
        decode(source);

        boolean lockBackup = GameRules.diplomaticRelationsLocked;
        GameRules.diplomaticRelationsLocked = false;

        DiplomacyManager diplomacyManager = getGameController().fieldManager.diplomacyManager;
        ColorsManager colorsManager = getGameController().colorsManager;
        for (EditorRelation relation : relations) {
            int fraction1 = colorsManager.getFractionByColor(relation.color1);
            DiplomaticEntity entity1 = diplomacyManager.getEntity(fraction1);
            if (entity1 == null) continue;

            int fraction2 = colorsManager.getFractionByColor(relation.color2);
            DiplomaticEntity entity2 = diplomacyManager.getEntity(fraction2);
            if (entity2 == null) continue;

            diplomacyManager.setRelation(entity1, entity2, relation.relation);
        }

        GameRules.diplomaticRelationsLocked = lockBackup;
    }
}
