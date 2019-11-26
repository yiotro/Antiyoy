package yio.tro.antiyoy.menu.diplomatic_dialogs;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticContract;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class StopWarDialog extends AbstractDiplomaticDialog {

    DiplomaticEntity sender, recipient;


    public StopWarDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        resetEntities();
    }


    protected void resetEntities() {
        sender = null;
        recipient = null;
    }


    @Override
    protected void onAppear() {
        super.onAppear();

        resetEntities();
    }


    @Override
    protected void makeLabels() {
        if (sender == null) return;
        if (recipient == null) return;

        LanguagesManager instance = LanguagesManager.getInstance();
        float y = (float) (position.height - topOffset);

        addLabel(instance.getString("peace_treaty"), Fonts.gameFont, leftOffset, y);
        y -= titleOffset;

        String capitalName = sender.capitalName;
        if (sender.isMain()) {
            capitalName = recipient.capitalName;
        }
        addLabel(instance.getString("state") + ": " + capitalName, Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("pay") + " (1x) : " + getPay(), Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("reparations") + ": " + getReparationsValue(), Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("duration") + ": " + DiplomaticContract.DURATION_PIECE, Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;
    }


    private String getPay() {
        DiplomacyManager diplomacyManager = getDiplomacyManager();
        int pay = diplomacyManager.calculatePayToStopWar(sender, recipient);
        if (pay == 0) return "0";

        return "" + pay;
    }


    private String getReparationsValue() {
        DiplomacyManager diplomacyManager = getDiplomacyManager();
        int reparations = diplomacyManager.calculateReparations(sender, recipient);
        if (reparations == 0) return "0";

        if (diplomacyManager.getMainEntity() == recipient) {
            reparations *= -1;
        }

        return "" + reparations;
    }


    @Override
    protected void onYesButtonPressed() {
        if (sender.isMain()) {
            getDiplomacyManager().onUserRequestedToStopWar(sender, recipient);
        } else {
            getDiplomacyManager().onEntityRequestedToStopWar(sender, recipient);
        }

        destroy();
    }


    private DiplomacyManager getDiplomacyManager() {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        return gameController.fieldManager.diplomacyManager;
    }


    @Override
    protected void onNoButtonPressed() {
        destroy();
    }


    @Override
    public boolean areButtonsEnabled() {
        return true;
    }


    public void setEntities(DiplomaticEntity sender, DiplomaticEntity recipient) {
        this.sender = sender;
        this.recipient = recipient;

        updateAll();
    }


}
