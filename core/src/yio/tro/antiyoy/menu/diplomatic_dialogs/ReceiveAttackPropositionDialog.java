package yio.tro.antiyoy.menu.diplomatic_dialogs;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticRelation;
import yio.tro.antiyoy.gameplay.touch_mode.TouchMode;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.PointYio;

public class ReceiveAttackPropositionDialog extends AbstractDiplomaticDialog {

    DiplomaticEntity sender, recipient;
    DiplomaticEntity target;
    int price;
    PointYio tempPoint;
    Province targetProvince;


    public ReceiveAttackPropositionDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        price = 0;
        tempPoint = new PointYio();
        targetProvince = null;
        resetEntities();
    }


    protected void resetEntities() {
        sender = null;
        recipient = null;
        target = null;
    }


    public void setData(DiplomaticEntity sender, DiplomaticEntity recipient, DiplomaticEntity target, int price) {
        this.sender = sender;
        this.recipient = recipient;
        this.target = target;
        this.price = price;

        YioGdxGame yioGdxGame = menuControllerYio.yioGdxGame;
        GameController gameController = yioGdxGame.gameController;
        FieldManager fieldManager = gameController.fieldManager;
        targetProvince = fieldManager.getBiggestProvince(target.fraction);

        updateAll();
        updateTagFraction();
    }


    private void updateTagFraction() {
        setTagFraction(target.fraction);
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

        addLabel(instance.getString("target") + ": " + target.capitalName, Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("price") + ": $" + price, Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;
    }


    @Override
    protected void makeCustomButtons() {
        tempRectangle.width = GraphicsYio.width / 2;
        tempRectangle.height = 0.05f * GraphicsYio.height;
        tempRectangle.x = GraphicsYio.width / 2 - tempRectangle.width / 2;
        tempRectangle.y = 1.5f * buttonHeight;
        addButton(LanguagesManager.getInstance().getString("show"), AcActionType.show, tempRectangle);
    }


    public void updateTempPointAsGeometricalCenter() {
        tempPoint.reset();
        for (Hex hex : targetProvince.hexList) {
            tempPoint.x += hex.pos.x;
            tempPoint.y += hex.pos.y;
        }
        tempPoint.x /= targetProvince.hexList.size();
        tempPoint.y /= targetProvince.hexList.size();
    }


    @Override
    protected void onCustomActionButtonPressed(AcButton acButton) {
        if (acButton.actionType == AcActionType.show && targetProvince != null) {
            Scenes.sceneReceiveAttackPropositionDialog.hide();
            GameController gameController = menuControllerYio.yioGdxGame.gameController;
            gameController.setTouchMode(TouchMode.tmShowChosenHexes);
            TouchMode.tmShowChosenHexes.highlightHexList(targetProvince.hexList);
            TouchMode.tmShowChosenHexes.setParentScene(Scenes.sceneReceiveAttackPropositionDialog);
            updateTempPointAsGeometricalCenter();
            gameController.cameraController.focusOnPoint(tempPoint);
        }
    }


    private DiplomacyManager getDiplomacyManager() {
        return menuControllerYio.yioGdxGame.gameController.fieldManager.diplomacyManager;
    }


    @Override
    protected void onYesButtonPressed() {
        apply();
        destroy();
    }


    private void apply() {
        if (sender.getStateFullMoney() < price) {
            Scenes.sceneNotification.show("buyer_not_enough_money");
            return;
        }

        DiplomacyManager diplomacyManager = getDiplomacyManager();
        diplomacyManager.transferMoney(sender, recipient, price);

        diplomacyManager.onEntityRequestedToMakeRelationsWorse(recipient, target);
        if (recipient.getRelation(target) != DiplomaticRelation.ENEMY) {
            diplomacyManager.onEntityRequestedToMakeRelationsWorse(recipient, target);
        }
    }


    @Override
    protected void onNoButtonPressed() {
        destroy();
    }


    @Override
    public boolean areButtonsEnabled() {
        return true;
    }
}
