package yio.tro.antiyoy.menu.customizable_list;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Obj;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.AbstractRenderCustomListItem;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.RenderableTextYio;

public class ProfitDetailItem extends AbstractCustomListItem{

    public IncomeType incomeType;
    public RenderableTextYio title;
    public RenderableTextYio value;
    public boolean highlightEnabled;


    @Override
    protected void initialize() {
        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        value = new RenderableTextYio();
        value.setFont(Fonts.smallerMenuFont);
        highlightEnabled = false;
    }


    @Override
    protected void move() {
        moveRenderableTextByDefault(title);

        value.centerVertical(viewPosition);
        value.position.x = (float) (viewPosition.x + viewPosition.width - 0.04f * GraphicsYio.width - value.width);
        value.updateBounds();
    }


    public void setIncomeType(IncomeType incomeType) {
        this.incomeType = incomeType;

        title.setString(LanguagesManager.getInstance().getString("" + incomeType));
        title.updateMetrics();
    }


    private String castInt(int x) {
        if (x > 0) {
            return "+" + x;
        }
        return "" + x;
    }


    public void updateValue() {
        value.setString(castInt(getIncomeValue(incomeType)));
        value.updateMetrics();
    }


    private int getIncomeValue(IncomeType incomeType) {
        MenuControllerYio menuControllerYio = customizableListYio.menuControllerYio;
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        Province selectedProvince = gameController.fieldManager.selectedProvince;

        switch (incomeType) {
            default:
                System.out.println("IncomeDetailItem.updateValue(): problem");
                return 0;
            case lands:
                return selectedProvince.hexList.size();
            case farms:
                return 4 * selectedProvince.countObjects(Obj.FARM);
            case diplomacy:
                return getDiplomacyIncome(selectedProvince);
            case trees:
                int treesQuantity = selectedProvince.countObjects(Obj.PINE) + selectedProvince.countObjects(Obj.PALM);
                return -1 * treesQuantity;
            case units:
                return -1 * selectedProvince.getUnitsTaxes();
            case towers:
                return -1 * selectedProvince.getTowerTaxes();
        }
    }


    private int getDiplomacyIncome(Province selectedProvince) {
        int fullIncome = selectedProvince.getProfit();
        int partialIncome = 0;
        for (IncomeType type : IncomeType.values()) {
            if (type == IncomeType.diplomacy) continue;
            partialIncome += getIncomeValue(type);
        }
        return fullIncome - partialIncome;
    }


    @Override
    protected double getWidth() {
        return getDefaultWidth();
    }


    @Override
    protected double getHeight() {
        return 0.07f * GraphicsYio.height;
    }


    @Override
    protected void onPositionChanged() {
        title.delta.x = 0.04f * GraphicsYio.width;
        title.delta.y = (float) (getHeight() / 2 + title.height / 2);
    }


    @Override
    protected void onClicked() {

    }


    @Override
    protected void onLongTapped() {

    }


    public void setHighlightEnabled(boolean highlightEnabled) {
        this.highlightEnabled = highlightEnabled;
    }


    @Override
    public AbstractRenderCustomListItem getRender() {
        return MenuRender.renderProfitDetailItem;
    }
}
