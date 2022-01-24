package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.KeyboardManager;
import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.gameplay.name_generator.CustomCityNamesManager;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.CheckButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.customizable_list.*;
import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneCityNames extends AbstractScene{


    private Reaction rbBack;
    private ButtonYio topLabel;
    private CheckButtonYio chkCityNames;
    private CheckButtonYio chkUseList;
    CustomizableListYio customizableListYio;


    public SceneCityNames(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        chkCityNames = null;
        customizableListYio = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        initReactions();
        menuControllerYio.spawnBackButton(660, rbBack);
        createTopLabel();
        createChecks();
        createList();

        loadValues();
        menuControllerYio.endMenuCreation();
    }


    private void createList() {
        initList();
        customizableListYio.appear();
        customizableListYio.appearFactor.appear(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);
    }


    private void initList() {
        if (customizableListYio != null) return;

        customizableListYio = new CustomizableListYio(menuControllerYio);
        customizableListYio.setPosition(generateRectangle(0.05, 0.05, 0.9, 0.6));
        customizableListYio.setAnimation(Animation.from_center);
        customizableListYio.setDestroyParameters(MenuControllerYio.DESTROY_ANIM, MenuControllerYio.DESTROY_SPEED);
        menuControllerYio.addElementToScene(customizableListYio);
    }


    private void loadValues() {
        chkCityNames.setChecked(SettingsManager.cityNamesEnabled);
        chkUseList.setChecked(SettingsManager.useCityNamesList);
        loadList();
    }


    private void loadList() {
        customizableListYio.clearItems();

        TitleListItem titleListItem = new TitleListItem();
        titleListItem.setTitle(getString("city_names"));
        titleListItem.setFont(Fonts.gameFont);
        customizableListYio.addItem(titleListItem);

        CustomCityNamesManager customCityNamesManager = CustomCityNamesManager.getInstance();
        for (String name : customCityNamesManager.getNames()) {
            ScrollListItem scrollListItem = new ScrollListItem();
            scrollListItem.setKey(customizableListYio.items.size() + "");
            scrollListItem.setTitle(name);
            scrollListItem.setCentered(true);
            scrollListItem.setHeight(0.07f * GraphicsYio.height);
            scrollListItem.setClickReaction(getNameItemReaction());
            customizableListYio.addItem(scrollListItem);
        }

        ScrollListItem additionItem = new ScrollListItem();
        additionItem.setTitle("+");
        additionItem.setCentered(true);
        additionItem.setHeight(0.07f * GraphicsYio.height);
        additionItem.setClickReaction(getAdditionItemReaction());
        customizableListYio.addItem(additionItem);
    }


    private SliReaction getAdditionItemReaction() {
        return new SliReaction() {
            @Override
            public void apply(AbstractCustomListItem item) {
                onAdditionItemClicked(item);
            }
        };
    }


    private SliReaction getNameItemReaction() {
        return new SliReaction() {
            @Override
            public void apply(AbstractCustomListItem item) {
                onNameItemClicked(item);
            }
        };
    }


    private void onAdditionItemClicked(AbstractCustomListItem item) {
        KeyboardManager.getInstance().apply(new AbstractKbReaction() {
            @Override
            public void onInputFromKeyboardReceived(String input) {
                if (input.length() == 0) return;
                CustomCityNamesManager.getInstance().addName(input);
                loadValues();
            }
        });
    }


    private void onNameItemClicked(AbstractCustomListItem item) {
        ScrollListItem scrollListItem = (ScrollListItem) item;
        final String string = scrollListItem.title.string;
        KeyboardManager.getInstance().apply(string, new AbstractKbReaction() {
            @Override
            public void onInputFromKeyboardReceived(String input) {
                if (input.length() == 0) {
                    CustomCityNamesManager.getInstance().removeName(string);
                    loadValues();
                    return;
                }
                CustomCityNamesManager.getInstance().changeName(string, input);
                loadValues();
            }
        });
    }


    private void createChecks() {
        initChecks();
        chkCityNames.appear();
        chkUseList.appear();
    }


    private void initChecks() {
        if (chkCityNames != null) return;

        chkCityNames = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkCityNames.setParent(topLabel);
        chkCityNames.alignTop(0.01);
        chkCityNames.setTitle(getString("city_names"));

        chkUseList = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkUseList.setParent(topLabel);
        chkUseList.alignUnderPreviousElement();
        chkUseList.setTitle(getString("use_list"));
    }


    private void createTopLabel() {
        topLabel = buttonFactory.getButton(generateRectangle(0.05, 0.7, 0.9, 0.16), 661, " ");
        topLabel.setTouchable(false);
        topLabel.setAnimation(Animation.from_center);
    }


    private void initReactions() {
        rbBack = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                applyValues();
                Scenes.sceneMoreSettings.create();
            }
        };
    }


    void applyValues() {
        SettingsManager.cityNamesEnabled = chkCityNames.isChecked();
        SettingsManager.useCityNamesList = chkUseList.isChecked();
        SettingsManager.getInstance().saveCityNamesOptions();
    }
}
