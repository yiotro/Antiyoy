package yio.tro.antiyoy.menu.diplomatic_dialogs;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.diplomacy.*;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.menu.scenes.gameplay.choose_entity.IDipEntityReceiver;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class PrepareForAttackPropositionDialog extends AbstractDiplomaticDialog implements IDipEntityReceiver{

    DiplomaticEntity sender, recipient;
    DiplomaticEntity target;


    public PrepareForAttackPropositionDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        resetEntities();
    }


    protected void resetEntities() {
        sender = null;
        recipient = null;
        target = null;
    }


    @Override
    protected void onAppear() {
        super.onAppear();
        resetEntities();
    }


    public void setEntities(DiplomaticEntity sender, DiplomaticEntity recipient) {
        this.sender = sender;
        this.recipient = recipient;
        target = sender;

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

        addLabel(instance.getString("attack_proposition"), Fonts.gameFont, leftOffset, y);
        y -= titleOffset;

        addLabel(instance.getString("aggressor") + ": " + recipient.capitalName, Fonts.smallerMenuFont, leftOffset, y);
        y -= 4 * lineOffset;

        addLabel(instance.getString("target") + ": " + getTargetString(), Fonts.smallerMenuFont, leftOffset, y);
    }


    private String getTargetString() {
        if (target != null) {
            return target.capitalName;
        }

        return "...";
    }


    @Override
    protected void makeCustomButtons() {
        tempRectangle.width = GraphicsYio.width / 2;
        tempRectangle.height = 0.05f * GraphicsYio.height;
        tempRectangle.x = GraphicsYio.width / 2 - tempRectangle.width / 2;
        tempRectangle.y = 1.6f * buttonHeight;
        addButton(LanguagesManager.getInstance().getString("choose"), AcActionType.choose, tempRectangle);
    }


    @Override
    protected void onCustomActionButtonPressed(AcButton acButton) {
        if (acButton.actionType == AcActionType.choose) {
            Scenes.sceneChooseDiplomaticEntity.create();
            Scenes.sceneChooseDiplomaticEntity.setiDipEntityReceiver(this);
            Scenes.sceneChooseDiplomaticEntity.loadValues();
        }
    }


    @Override
    protected void onYesButtonPressed() {
        if (appearFactor.getGravity() < 0) return;
        apply();
        Scenes.scenePrepareForAttackProposition.hide();
    }


    private void apply() {
        if (target == null) return;

        int price = Scenes.scenePrepareForAttackProposition.getCurrentChosenMoneyValue();
        int fraction = target.fraction;

        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
        DiplomaticLog log = diplomacyManager.log;
        DiplomaticMessage message = log.addMessage(DipMessageType.attack_proposition, sender, recipient);
        message.setArg1("" + price);
        message.setArg2("" + fraction);
    }


    @Override
    protected void onUserClickedOutside() {
        Scenes.scenePrepareForAttackProposition.hide();
    }


    @Override
    protected void onNoButtonPressed() {

    }


    @Override
    public boolean isInSingleButtonMode() {
        return true;
    }


    public void setTarget(DiplomaticEntity target) {
        this.target = target;
    }


    @Override
    public boolean areButtonsEnabled() {
        return true;
    }


    @Override
    public void onDiplomaticEntityChosen(DiplomaticEntity entity) {
        target = entity;
        updateAll();
        updateTagFraction();
        move();
    }


    @Override
    public boolean canDiplomaticEntityBeChosen(DiplomaticEntity entity) {
        return entity != recipient;
    }
}
