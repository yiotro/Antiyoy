package yio.tro.antiyoy.menu.diplomatic_dialogs;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticContract;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticRelation;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class DipInfoDialog extends AbstractDiplomaticDialog{

    DiplomaticEntity selectedEntity;
    DiplomaticEntity mainEntity;


    public DipInfoDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        selectedEntity = null;
        mainEntity = null;
    }


    @Override
    protected void makeLabels() {
        if (selectedEntity == null) return;

        LanguagesManager instance = LanguagesManager.getInstance();
        float y = (float) (position.height - topOffset);

        addLabel(selectedEntity.capitalName, Fonts.gameFont, leftOffset, y);
        y -= titleOffset;

        addLabel(instance.getString("friends") + ": " + selectedEntity.getNumberOfFriends(), Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("mutual_friends") + ": " + selectedEntity.getNumberOfMutualFriends(mainEntity), Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        int relation = mainEntity.getRelation(selectedEntity);
        if (relation == DiplomaticRelation.FRIEND) {
            addLabel(instance.getString("dotations") + ": " + getDotationsString(), Fonts.smallerMenuFont, leftOffset, y);
            y -= lineOffset;

            addLabel(instance.getString("duration") + ": " + getDurationValue(), Fonts.smallerMenuFont, leftOffset, y);
            y -= lineOffset;
        }

        DiplomacyManager diplomacyManager = getDiplomacyManager();
        DiplomaticContract contract = diplomacyManager.findContract(DiplomaticContract.TYPE_PIECE, mainEntity, selectedEntity);
        if (contract != null) {
            addLabel(instance.getString("reparations") + ": " + getReparations(contract), Fonts.smallerMenuFont, leftOffset, y);
            y -= lineOffset;

            addLabel(instance.getString("duration") + ": " + getDurationValue(), Fonts.smallerMenuFont, leftOffset, y);
            y -= lineOffset;
        }
    }


    private String getReparations(DiplomaticContract contract) {
        return contract.getDotationsStringFromEntityPerspective(mainEntity);
    }


    private int getDurationValue() {
        DiplomacyManager diplomacyManager = getDiplomacyManager();
        DiplomaticContract contract = diplomacyManager.findContract(-1, mainEntity, selectedEntity);
        if (contract == null) return 0;

        return contract.getExpireCountDown();
    }


    private DiplomacyManager getDiplomacyManager() {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        return gameController.fieldManager.diplomacyManager;
    }


    private String getDotationsString() {
        DiplomacyManager diplomacyManager = getDiplomacyManager();
        DiplomaticContract contract = diplomacyManager.findContract(DiplomaticContract.TYPE_FRIENDSHIP, mainEntity, selectedEntity);
        if (contract == null) return "0";

        return contract.getDotationsStringFromEntityPerspective(mainEntity);
    }


    public void setEntities(DiplomaticEntity mainEntity, DiplomaticEntity selectedEntity) {
        this.mainEntity = mainEntity;
        this.selectedEntity = selectedEntity;

        updateAll();
    }


    @Override
    protected void onYesButtonPressed() {

    }


    @Override
    protected void onNoButtonPressed() {

    }


    @Override
    public boolean areButtonsEnabled() {
        return false;
    }
}
