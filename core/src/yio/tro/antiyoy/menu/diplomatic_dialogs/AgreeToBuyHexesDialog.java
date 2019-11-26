package yio.tro.antiyoy.menu.diplomatic_dialogs;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.touch_mode.TouchMode;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.PointYio;

import java.util.ArrayList;

public class AgreeToBuyHexesDialog extends AbstractDiplomaticDialog{

    DiplomaticEntity sender, recipient;
    ArrayList<Hex> hexesToBuy;
    int price;
    PointYio tempPoint;


    public AgreeToBuyHexesDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        hexesToBuy = new ArrayList<>();
        price = 0;
        tempPoint = new PointYio();
        resetEntities();
    }


    protected void resetEntities() {
        sender = null;
        recipient = null;
    }


    @Override
    protected void onAppear() {
        super.onAppear();
    }


    public void setData(DiplomaticEntity recipient, ArrayList<Hex> hexesToBuy, int price) {
        resetEntities();
        this.recipient = recipient;
        this.price = price;

        Hex firstHex = hexesToBuy.get(0);
        sender = getDiplomacyManager().getEntity(firstHex.fraction);

        this.hexesToBuy.clear();
        this.hexesToBuy.addAll(hexesToBuy);

        updateAll();
        updateTagFraction();
    }


    private void updateTagFraction() {
        setTagFraction(sender.fraction);
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

        addLabel(instance.getString("confirm_buy_hexes"), Fonts.gameFont, leftOffset, y);
        y -= titleOffset;

        addLabel(instance.getString("state") + ": " + sender.capitalName, Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("quantity") + ": " + hexesToBuy.size(), Fonts.smallerMenuFont, leftOffset, y);
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
        for (Hex hex : hexesToBuy) {
            tempPoint.x += hex.pos.x;
            tempPoint.y += hex.pos.y;
        }
        tempPoint.x /= hexesToBuy.size();
        tempPoint.y /= hexesToBuy.size();
    }


    @Override
    protected void onCustomActionButtonPressed(AcButton acButton) {
        if (acButton.actionType == AcActionType.show) {
            Scenes.sceneAgreeToBuyHexes.hide();
            Scenes.sceneDiplomaticLog.hide();
            GameController gameController = menuControllerYio.yioGdxGame.gameController;
            gameController.setTouchMode(TouchMode.tmShowChosenHexes);
            TouchMode.tmShowChosenHexes.highlightHexList(hexesToBuy);
            TouchMode.tmShowChosenHexes.setParentScene(Scenes.sceneAgreeToBuyHexes);
            updateTempPointAsGeometricalCenter();
            gameController.cameraController.focusOnPoint(tempPoint);
        }
    }


    private DiplomacyManager getDiplomacyManager() {
        return menuControllerYio.yioGdxGame.gameController.fieldManager.diplomacyManager;
    }


    @Override
    protected void onYesButtonPressed() {
        getDiplomacyManager().applyHexPurchase(recipient, sender, hexesToBuy, price);
        destroy();
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
