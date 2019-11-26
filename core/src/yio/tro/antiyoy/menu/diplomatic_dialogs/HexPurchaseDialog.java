package yio.tro.antiyoy.menu.diplomatic_dialogs;

import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;

public class HexPurchaseDialog extends AbstractDiplomaticDialog{

    DiplomaticEntity sender, recipient;
    ArrayList<Hex> hexesToBuy;


    public HexPurchaseDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        hexesToBuy = new ArrayList<>();
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


    public void setData(DiplomaticEntity sender, ArrayList<Hex> hexesToBuy) {
        this.sender = sender;

        Hex firstHex = hexesToBuy.get(0);
        recipient = getDiplomacyManager().getEntity(firstHex.fraction);

        this.hexesToBuy.clear();
        this.hexesToBuy.addAll(hexesToBuy);

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

        addLabel(instance.getString("hex_purchase"), Fonts.gameFont, leftOffset, y);
        y -= titleOffset;

        addLabel(instance.getString("state") + ": " + recipient.capitalName, Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("quantity") + ": " + hexesToBuy.size(), Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

    }


    private int getPrice() {
        return getDiplomacyManager().calculatePriceForHexes(hexesToBuy);
    }


    private DiplomacyManager getDiplomacyManager() {
        return menuControllerYio.yioGdxGame.gameController.fieldManager.diplomacyManager;
    }


    @Override
    protected void onYesButtonPressed() {
        int valueIndex = Scenes.sceneHexPurchaseDialog.moneySlider.getValueIndex();
        int moneyValue = Scenes.sceneHexPurchaseDialog.moneyValues[valueIndex];
        getDiplomacyManager().onEntityRequestedHexPurchase(
                sender,
                recipient,
                hexesToBuy,
                moneyValue
        );
        destroy();
    }


    @Override
    protected void onNoButtonPressed() {

    }


    @Override
    public boolean isInSingleButtonMode() {
        return true;
    }


    public ArrayList<Hex> getHexesToBuy() {
        return hexesToBuy;
    }


    @Override
    public boolean areButtonsEnabled() {
        return true;
    }
}
