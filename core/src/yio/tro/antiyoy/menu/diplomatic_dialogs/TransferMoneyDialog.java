package yio.tro.antiyoy.menu.diplomatic_dialogs;

import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class TransferMoneyDialog extends AbstractDiplomaticDialog{

    DiplomaticEntity sender, recipient;


    public TransferMoneyDialog(MenuControllerYio menuControllerYio) {
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


    public void setEntities(DiplomaticEntity sender, DiplomaticEntity recipient) {
        this.sender = sender;
        this.recipient = recipient;

        updateAll();
        updateTagFraction();
    }


    private void updateTagFraction() {
        setTagFraction(recipient.fraction);
    }


    @Override
    protected void updateTagPosition() {
        tagPosition.x = viewPosition.x + leftOffset;
        tagPosition.width = viewPosition.width - 2 * leftOffset;
        tagPosition.y = viewPosition.y + viewPosition.height - topOffset - titleOffset - lineOffset + lineOffset / 4;
        tagPosition.height = lineOffset;
    }


    @Override
    protected void makeLabels() {
        if (sender == null) return;
        if (recipient == null) return;

        LanguagesManager instance = LanguagesManager.getInstance();
        float y = (float) (position.height - topOffset);

        addLabel(instance.getString("gift"), Fonts.gameFont, leftOffset, y);
        y -= titleOffset;

        addLabel(instance.getString("state") + ": " + recipient.capitalName, Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;
    }


    @Override
    protected void onYesButtonPressed() {
        if (appearFactor.getGravity() < 0) return;
        performMoneyTransfer();

        Scenes.sceneTransferMoneyDialog.hide();
    }


    private void performMoneyTransfer() {
        DiplomacyManager diplomacyManager = menuControllerYio.yioGdxGame.gameController.fieldManager.diplomacyManager;
        int currentRunnerIndex = Scenes.sceneTransferMoneyDialog.moneySlider.getValueIndex();
        int value = Scenes.sceneTransferMoneyDialog.convertSliderIndexIntoMoneyValue(currentRunnerIndex);
        diplomacyManager.transferMoney(sender, recipient, value);
    }


    @Override
    protected void onUserClickedOutside() {
        Scenes.sceneTransferMoneyDialog.hide();
    }


    @Override
    public boolean isInSingleButtonMode() {
        return true;
    }


    @Override
    protected void onNoButtonPressed() {

    }


    @Override
    public boolean areButtonsEnabled() {
        return true;
    }
}
