package yio.tro.antiyoy.menu.diplomatic_dialogs;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticContract;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class FriendshipDialog extends AbstractDiplomaticDialog {

    DiplomaticEntity selectedEntity, mainEntity;


    public FriendshipDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        resetEntities();
    }


    protected void resetEntities() {
        selectedEntity = null;
        mainEntity = null;
    }


    @Override
    protected void onAppear() {
        super.onAppear();

        resetEntities();
    }


    public void setEntities(DiplomaticEntity mainEntity, DiplomaticEntity selectedEntity) {
        this.mainEntity = mainEntity;
        this.selectedEntity = selectedEntity;

        updateAll();
    }


    @Override
    protected void makeLabels() {
        if (selectedEntity == null) return;
        if (mainEntity == null) return;

        LanguagesManager instance = LanguagesManager.getInstance();
        float y = (float) (position.height - topOffset);

        addLabel(instance.getString("treaty_of_friendship"), Fonts.gameFont, leftOffset, y);
        y -= titleOffset;

        addLabel(instance.getString("state") + ": " + selectedEntity.capitalName, Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("dotations") + ": " + getDotationsValue(), Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("duration") + ": " + DiplomaticContract.DURATION_FRIEND, Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;
    }


    protected int getDotationsValue() {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        DiplomacyManager diplomacyManager = gameController.fieldController.diplomacyManager;
        int dotations = diplomacyManager.calculateDotationsForFriendship(mainEntity, selectedEntity);
        return dotations;
    }


    @Override
    protected void onYesButtonPressed() {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        DiplomacyManager diplomacyManager = gameController.fieldController.diplomacyManager;
        diplomacyManager.onUserRequestedFriendship(selectedEntity);

        Scenes.sceneFriendshipDialog.hide();
    }


    @Override
    protected void onNoButtonPressed() {
        Scenes.sceneFriendshipDialog.hide();
    }


    @Override
    public boolean areButtonsEnabled() {
        return true;
    }
}
