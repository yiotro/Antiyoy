package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneColorStats extends AbstractScene{


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
        showPanel.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        showPanel.factorModel.beginSpawning(3, 1);

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0.6, 0.1, 0.4, 0.07), 56322, getString("end_game_ok"));
        okButton.setShadow(false);
        okButton.setReactBehavior(ReactBehavior.rbHideColorStats);
        okButton.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        okButton.factorModel.beginSpawning(3, 1);
    }


    public void hide() {
        menuControllerYio.getButtonById(30).setTouchable(true);
        menuControllerYio.getButtonById(31).setTouchable(true);
        menuControllerYio.getButtonById(32).setTouchable(true);

        menuControllerYio.getButtonById(56321).destroy();
        menuControllerYio.getButtonById(56321).factorModel.beginDestroying(1, 3);
        menuControllerYio.getButtonById(56322).destroy();
        menuControllerYio.getButtonById(56322).factorModel.beginDestroying(1, 3);
    }
}