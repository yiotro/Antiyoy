package yio.tro.antiyoy.menu.diplomatic_dialogs;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticContract;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticMessage;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.Yio;

public class FriendshipDialog extends AbstractDiplomaticDialog {

    DiplomaticEntity sender, recipient;
    DiplomaticMessage message;


    public FriendshipDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        reset();
    }


    protected void reset() {
        sender = null;
        recipient = null;
        message = null;
    }


    @Override
    protected void onAppear() {
        super.onAppear();

        reset();
    }


    public void setValues(DiplomaticEntity sender, DiplomaticEntity recipient, DiplomaticMessage message) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;

        updateAll();
        updateTagFraction();
    }


    private void updateTagFraction() {
        int fraction = sender.fraction;
        if (sender.isMain()) {
            fraction = recipient.fraction;
        }

        setTagFraction(fraction);
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

        addLabel(instance.getString("treaty_of_friendship"), Fonts.gameFont, leftOffset, y);
        y -= titleOffset;

        String capitalName = sender.capitalName;
        if (sender.isMain()) {
            capitalName = recipient.capitalName;
        }
        addLabel(instance.getString("state") + ": " + capitalName, Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("dotations") + ": " + getDotationsValue(), Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("duration") + ": " + DiplomaticContract.DURATION_FRIEND, Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;
    }


    protected int getDotationsValue() {
        if (message != null && message.arg1 != null && Yio.isNumeric(message.arg1)) {
            return -Integer.valueOf(message.arg1);
        }

        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
        int dotations = diplomacyManager.calculateDotationsForFriendship(sender, recipient);

        if (recipient.isMain()) {
            dotations *= -1;
        }

        return dotations;
    }


    @Override
    protected void onYesButtonPressed() {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
        diplomacyManager.requestedFriendship(sender, recipient);

        if (message != null && message.arg1 != null) {
            DiplomaticContract contract = diplomacyManager.getContract(DiplomaticContract.TYPE_FRIENDSHIP, sender, recipient);
            if (contract != null) {
                contract.setDotations(Integer.valueOf(message.arg1));
            }
        }

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
