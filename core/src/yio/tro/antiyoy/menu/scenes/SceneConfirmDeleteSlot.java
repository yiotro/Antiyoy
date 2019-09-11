package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;

public class SceneConfirmDeleteSlot extends AbstractScene{


    private ButtonYio yesButton;
    private ButtonYio basePanel;
    private ButtonYio noButton;
    private Reaction rbNo;
    private Reaction currentYesReaction;
    private Reaction rbYes;


    public SceneConfirmDeleteSlot(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        rbNo = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneSaveLoad.create();
                Scenes.sceneSaveLoad.setOperationType(Scenes.sceneSaveLoad.getOperationType());
            }
        };

        rbYes = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                // this reaction can be switched to another
                Scenes.sceneSaveLoad.slotSelector.deleteTargetItem();
                Scenes.sceneSaveLoad.create();
                Scenes.sceneSaveLoad.setOperationType(Scenes.sceneSaveLoad.getOperationType());
                Scenes.sceneSaveLoad.slotSelector.updateAll();
            }
        };

        currentYesReaction = null;
        yesButton = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.9, 0.15), 850, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("confirm_delete_slot"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            menuControllerYio.getButtonRenderer().renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.from_center);

        yesButton = buttonFactory.getButton(generateRectangle(0.5, 0.4, 0.45, 0.05), 851, getString("yes"));
        yesButton.setReaction(rbYes);
        yesButton.setShadow(false);
        yesButton.setAnimation(Animation.from_center);

        noButton = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.45, 0.05), 852, getString("no"));
        noButton.setReaction(rbNo);
        noButton.setShadow(false);
        noButton.setAnimation(Animation.from_center);

        menuControllerYio.endMenuCreation();
    }


    public void setCurrentYesReaction(Reaction currentYesReaction) {
        this.currentYesReaction = currentYesReaction;

        if (yesButton != null) {
            yesButton.setReaction(currentYesReaction);
        }
    }
}
