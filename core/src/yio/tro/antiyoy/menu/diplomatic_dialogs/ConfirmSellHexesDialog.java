package yio.tro.antiyoy.menu.diplomatic_dialogs;

import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;

public class ConfirmSellHexesDialog extends AbstractDiplomaticDialog{

    DiplomaticEntity sender, recipient;
    ArrayList<Hex> hexesToBuy;
    int price;


    public ConfirmSellHexesDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        hexesToBuy = new ArrayList<>();
        price = 0;
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


    public void setData(DiplomaticEntity sender, ArrayList<Hex> hexesToBuy, int price) {
        this.sender = sender;
        this.price = price;

        Hex firstHex = hexesToBuy.get(0);
        recipient = getDiplomacyManager().getEntity(firstHex.colorIndex);

        this.hexesToBuy.clear();
        this.hexesToBuy.addAll(hexesToBuy);

        updateAll();
        updateTagColor();
    }


    private void updateTagColor() {
        setTagColor(sender.color);
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

        addLabel(instance.getString("confirm_sell_hexes"), Fonts.gameFont, leftOffset, y);
        y -= titleOffset;

        addLabel(instance.getString("state") + ": " + sender.capitalName, Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("quantity") + ": " + hexesToBuy.size(), Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;

        addLabel(instance.getString("price") + ": $" + price, Fonts.smallerMenuFont, leftOffset, y);
        y -= lineOffset;
    }


    private DiplomacyManager getDiplomacyManager() {
        return menuControllerYio.yioGdxGame.gameController.fieldController.diplomacyManager;
    }


    @Override
    protected void onYesButtonPressed() {
        getDiplomacyManager().applyHexPurchase(sender, recipient, hexesToBuy, price);
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
