package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.LanguagesManager;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneSurrenderDialog extends AbstractScene{


    public SceneSurrenderDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        Scenes.sceneTutorialTip.createTutorialTip(menuControllerYio.getArrayListFromString(LanguagesManager.getInstance().getString("win_or_continue")));
        addWinButtonToTutorialTip();
    }


    public void addWinButtonToTutorialTip() {
        ButtonYio winButton = buttonFactory.getButton(generateRectangle(0, 0.1, 0.5, 0.05), 54, null);
        winButton.setTextLine(getString("win_game"));
        menuControllerYio.getButtonRenderer().renderButton(winButton);
        winButton.setShadow(false);
        winButton.setReactBehavior(ReactBehavior.rbWinGame);
        winButton.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        winButton.factorModel.beginSpawning(3, 1);

        ButtonYio okButton = menuControllerYio.getButtonById(53);
        okButton.setPosition(generateRectangle(0.5, 0.1, 0.5, 0.05));
        okButton.setTextLine(getString("continue"));
        okButton.setReactBehavior(ReactBehavior.rbRefuseEarlyGameEnd);
        menuControllerYio.getButtonRenderer().renderButton(okButton);
    }
}