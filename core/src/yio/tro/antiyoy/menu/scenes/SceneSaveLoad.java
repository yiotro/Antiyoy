package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.save_slot_selector.SaveSlotSelector;

public class SceneSaveLoad extends AbstractScene{


    private ButtonYio backButton;
    public SaveSlotSelector slotSelector;
    private ButtonYio replaysButton;


    public SceneSaveLoad(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        slotSelector = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        backButton = menuControllerYio.spawnBackButton(580, null); // reaction will be set later

        checkToCreateSlotSelector();
        slotSelector.appear();

        menuControllerYio.endMenuCreation();
    }


    private void createReplaysButton() {
        replaysButton = buttonFactory.getButton(generateRectangle(0.55, 0.9, 0.4, 0.07), 581, getString("replays"));
        replaysButton.setReaction(Reaction.rbReplaysMenu);
        replaysButton.setAnimation(Animation.up);
    }


    private void checkToCreateSlotSelector() {
        if (slotSelector != null) return;

        slotSelector = new SaveSlotSelector(menuControllerYio, -1);
        slotSelector.setPosition(generateRectangle(0.05, 0.07, 0.9, 0.75));

        menuControllerYio.addElementToScene(slotSelector);
    }


    public boolean getOperationType() {
        if (slotSelector == null) return false;

        return slotSelector.getOperationType();
    }


    public void setOperationType(boolean load) {
        slotSelector.setOperationType(load);

        if (load) {
            backButton.setReaction(Reaction.rbChooseGameModeMenu);
            createReplaysButton();
        } else {
            backButton.setReaction(Reaction.rbPauseMenu);
        }
    }
}
