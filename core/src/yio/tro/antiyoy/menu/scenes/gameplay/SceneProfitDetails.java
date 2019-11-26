package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.customizable_list.*;

public class SceneProfitDetails extends AbstractModalScene{


    private Reaction rbClose;
    CustomizableListYio customizableListYio;


    public SceneProfitDetails(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        customizableListYio = null;
    }


    private void initReactions() {
        rbClose = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                hide();
            }
        };
    }


    @Override
    public void create() {
        initReactions();
        createInvisibleCloseButton(rbClose);
        createList();
        loadValues();
    }


    private void loadValues() {
        for (AbstractCustomListItem item : customizableListYio.items) {
            if (item instanceof ProfitDetailItem) {
                ((ProfitDetailItem) item).updateValue();
            }
        }
    }


    private void createList() {
        initList();
        customizableListYio.appear();
    }


    private void initList() {
        if (customizableListYio != null) return;

        customizableListYio = new CustomizableListYio(menuControllerYio);
        customizableListYio.setPosition(generateRectangle(0.1, 0.10, 0.8, 0.5));
        customizableListYio.setAnimation(Animation.down);
        customizableListYio.setScrollingEnabled(false);
        menuControllerYio.addElementToScene(customizableListYio);

        TitleListItem titleListItem = new TitleListItem();
        titleListItem.setTitle(getString("profit"));
        customizableListYio.addItem(titleListItem);

        for (IncomeType incomeType : IncomeType.values()) {
            ProfitDetailItem profitDetailItem = new ProfitDetailItem();
            profitDetailItem.setIncomeType(incomeType);
            if (customizableListYio.items.size() % 2 == 1) {
                profitDetailItem.setHighlightEnabled(true);
            }
            customizableListYio.addItem(profitDetailItem);
        }
    }


    @Override
    public void hide() {
        destroyByIndex(73182730, 73182739);
        if (customizableListYio != null) {
            customizableListYio.destroy();
        }
    }
}
