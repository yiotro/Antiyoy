package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneIncomeGraph extends AbstractGameplayScene {


    public ButtonYio showPanel;
    private Reaction rbHide;
    private ButtonYio okButton;
    private ButtonYio closeButton;


    public SceneIncomeGraph(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        initReactions();
    }


    private void initReactions() {
        rbHide = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                hide();
            }
        };
    }


    @Override
    public void create() {
        menuControllerYio.getYioGdxGame().gameController.selectionController.deselectAll();

        createCloseButton();
        createShowPanel();
    }


    private void createCloseButton() {
        closeButton = buttonFactory.getButton(generateRectangle(0, 0, 1, 1), 56323, null);
        if (closeButton.notRendered()) {
            closeButton.loadTexture("pixels/transparent_black_pixel.png");
        }
        closeButton.setReaction(rbHide);
        closeButton.setSelectionRenderable(false);
        closeButton.setAnimation(Animation.NONE);
    }


    private void createShowPanel() {
        showPanel = buttonFactory.getButton(generateRectangle(0, 0.15, 1, 0.41), 56321, null);
        showPanel.setTouchable(false);
        showPanel.setAnimation(Animation.FIXED_DOWN);
    }


    @Override
    public void hide() {
        destroyByIndex(56321, 56329);
    }
}