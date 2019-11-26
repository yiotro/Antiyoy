package yio.tro.antiyoy.menu.diplomatic_dialogs;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticContract;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticRelation;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class ConfirmDislikeDialog extends AbstractDiplomaticDialog {

    DiplomaticEntity selectedEntity;


    public ConfirmDislikeDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    protected void makeLabels() {
        LanguagesManager instance = LanguagesManager.getInstance();
        float y = (float) (position.height - topOffset);

        DiplomacyManager diplomacyManager = getDiplomacyManager();
        DiplomaticEntity mainEntity = diplomacyManager.getMainEntity();
        int relation = mainEntity.getRelation(selectedEntity);

        String messageKey;
        switch (relation) {
            default:
                messageKey = "-";
                break;
            case DiplomaticRelation.FRIEND:
                messageKey = "confirm_cancel_friendship";
                break;
            case DiplomaticRelation.NEUTRAL:
                messageKey = "confirm_start_war";
                break;
            case DiplomaticRelation.ENEMY:
                messageKey = "-";
                break;
        }

        addLabel(instance.getString(messageKey), Fonts.gameFont, leftOffset, y);

        if (relation == DiplomaticRelation.FRIEND) {
            addTraitorFineLabel();
        }
    }


    private void addTraitorFineLabel() {
        DiplomacyManager diplomacyManager = getDiplomacyManager();
        int finePerTurn = diplomacyManager.calculateTraitorFine(diplomacyManager.getMainEntity());
        int fine = finePerTurn * DiplomaticContract.DURATION_TRAITOR;

        float y = (float) (position.height - topOffset - 0.05f * GraphicsYio.height);
        addLabel(LanguagesManager.getInstance().getString("fine") + ": " + fine, Fonts.smallerMenuFont, leftOffset, y);
    }


    @Override
    protected void onYesButtonPressed() {
        DiplomacyManager diplomacyManager = getDiplomacyManager();
        diplomacyManager.onUserRequestedToMakeRelationsWorse(selectedEntity);

        Scenes.sceneConfirmDislike.hide();
    }


    private DiplomacyManager getDiplomacyManager() {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        return gameController.fieldManager.diplomacyManager;
    }


    @Override
    protected void onNoButtonPressed() {
        Scenes.sceneConfirmDislike.hide();
    }


    @Override
    public boolean areButtonsEnabled() {
        return true;
    }


    public void setSelectedEntity(DiplomaticEntity selectedEntity) {
        this.selectedEntity = selectedEntity;

        updateAll();
    }
}
