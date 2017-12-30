package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneColorStats extends AbstractGameplayScene {


    public SceneColorStats(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.getYioGdxGame().gameController.selectionController.deselectAll();
        menuControllerYio.getButtonById(30).setTouchable(false);
        menuControllerYio.getButtonById(31).setTouchable(false);
        menuControllerYio.getButtonById(32).setTouchable(false);

        ButtonYio showPanel = buttonFactory.getButton(generateRectangle(0, 0.1, 1, 0.41), 56321, null);
        showPanel.setTouchable(false);
        showPanel.setAnimation(Animation.FIXED_DOWN);
        showPanel.appearFactor.appear(3, 1);

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0.6, 0.1, 0.4, 0.07), 56322, getString("end_game_ok"));
        okButton.setShadow(false);
        okButton.setReaction(Reaction.rbHideColorStats);
        okButton.setAnimation(Animation.FIXED_DOWN);
        okButton.appearFactor.appear(3, 1);
    }


    @Override
    public void hide() {
        menuControllerYio.getButtonById(30).setTouchable(true);
        menuControllerYio.getButtonById(31).setTouchable(true);
        menuControllerYio.getButtonById(32).setTouchable(true);

        menuControllerYio.getButtonById(56321).destroy();
        menuControllerYio.getButtonById(56321).appearFactor.destroy(1, 3);
        menuControllerYio.getButtonById(56322).destroy();
        menuControllerYio.getButtonById(56322).appearFactor.destroy(1, 3);
    }
}