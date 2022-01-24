package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.gameplay.GoalType;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.customizable_list.AbstractCustomListItem;
import yio.tro.antiyoy.menu.customizable_list.CustomizableListYio;
import yio.tro.antiyoy.menu.customizable_list.ScrollListItem;
import yio.tro.antiyoy.menu.customizable_list.SliReaction;
import yio.tro.antiyoy.menu.scenes.gameplay.AbstractModalScene;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class SceneChooseGoalType extends AbstractModalScene{

    IGoalChoiceListener goalChoiceListener;
    private Reaction rbClose;
    CustomizableListYio customizableListYio;
    private SliReaction clickReaction;


    public SceneChooseGoalType(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        initReactions();
        customizableListYio = null;
    }


    private void initReactions() {
        clickReaction = new SliReaction() {
            @Override
            public void apply(AbstractCustomListItem item) {
                onItemClicked((ScrollListItem) item);
            }
        };
        rbClose = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                hide();
            }
        };
    }


    @Override
    public void create() {
        createInvisibleCloseButton(rbClose);
        createCustomizableList();
    }


    private void createCustomizableList() {
        initCustomList();
        customizableListYio.appear();
    }


    private void initCustomList() {
        if (customizableListYio != null) return;

        customizableListYio = new CustomizableListYio(menuControllerYio);
        customizableListYio.setAnimation(Animation.down);
        customizableListYio.setPosition(generateRectangle(0.1, 0.08, 0.8, 0.42));
        menuControllerYio.addElementToScene(customizableListYio);

        loadValues();
    }


    private void loadValues() {
        customizableListYio.clearItems();

        for (GoalType goalType : GoalType.values()) {
            ScrollListItem scrollListItem = new ScrollListItem();
            scrollListItem.setKey("" + goalType);
            scrollListItem.setTitle(getItemTitle(goalType));
            scrollListItem.setHeight(0.06f * GraphicsYio.height);
            scrollListItem.setClickReaction(clickReaction);
            customizableListYio.addItem(scrollListItem);
        }
    }


    void onItemClicked(ScrollListItem scrollListItem) {
        GoalType goalType = GoalType.valueOf(scrollListItem.key);
        goalChoiceListener.onGoalTypeChosen(goalType);
        hide();
    }


    private String getItemTitle(GoalType goalType) {
        if (goalType == GoalType.def) {
            return LanguagesManager.getInstance().getString("default");
        }
        return LanguagesManager.getInstance().getString("" + goalType);
    }


    public void setGoalChoiceListener(IGoalChoiceListener goalChoiceListener) {
        this.goalChoiceListener = goalChoiceListener;
    }


    @Override
    public void hide() {
        if (invisibleCloseElement != null) {
            invisibleCloseElement.destroy();
        }
        if (customizableListYio != null) {
            customizableListYio.destroy();
        }
    }
}
