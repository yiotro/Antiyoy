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

    DiplomaticEntity sender, recipient;


    public FriendshipDialog(MenuControllerYio menuControllerYio) {
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
        updateTagColor();
    }


    private void updateTagColor() {
        int color = sender.color;
        if (sender.isMain()) {
            color = recipient.color;
        }

        setTagColor(color);
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
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        DiplomacyManager diplomacyManager = gameController.fieldController.diplomacyManager;
        int dotations = diplomacyManager.calculateDotationsForFriendship(sender, recipient);

        if (recipient.isMain()) {
            dotations *= -1;
        }

        return dotations;
    }


    @Override
    protected void onYesButtonPressed() {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        DiplomacyManager diplomacyManager = gameController.fieldController.diplomacyManager;
        diplomacyManager.requestedFriendship(sender, recipient);

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
