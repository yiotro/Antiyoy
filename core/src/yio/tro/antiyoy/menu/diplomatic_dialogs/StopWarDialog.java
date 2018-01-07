package yio.tro.antiyoy.menu.diplomatic_dialogs;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticContract;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class StopWarDialog extends AbstractDiplomaticDialog {

    DiplomaticEntity selectedEntity;


    public StopWarDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        selectedEntity = null;
    }


    @Override
    protected void makeLabels() {
        if (selectedEntity == null) return;

        LanguagesManager instance = LanguagesManager.getInstance();
        float y = (float) (position.height - topOffset);

        addLabel(instance.getString("peace_treaty"), Fonts.gameFont, leftOffset, y);
        y -= titleOffset;

        addLabel(instance.getString("state") + ": " + selectedEntity.capitalName, Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("pay") + " (1x) : " + getPay(), Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("reparations") + ": " + getReparations(), Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("duration") + ": " + DiplomaticContract.DURATION_PIECE, Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;
    }


    private String getPay() {
        DiplomacyManager diplomacyManager = getDiplomacyManager();
        int pay = diplomacyManager.calculatePayToStopWar(diplomacyManager.getMainEntity(), selectedEntity);
        if (pay == 0) return "0";

        return "" + pay;
    }


    private String getReparations() {
        DiplomacyManager diplomacyManager = getDiplomacyManager();
        int reparations = diplomacyManager.calculateReparations(diplomacyManager.getMainEntity(), selectedEntity);
        if (reparations == 0) return "0";

        return "" + reparations;
    }


    @Override
    protected void onYesButtonPressed() {
        getDiplomacyManager().onUserRequestedToStopWar(selectedEntity);

        destroy();
    }


    private DiplomacyManager getDiplomacyManager() {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        return gameController.fieldController.diplomacyManager;
    }


    @Override
    protected void onNoButtonPressed() {
        destroy();
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
