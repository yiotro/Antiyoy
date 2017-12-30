package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.scenes.AbstractScene;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneSurrenderDialog extends AbstractScene {


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
        winButton.setReaction(Reaction.rbWinGame);
        winButton.setAnimation(Animation.FIXED_DOWN);
        winButton.appearFactor.appear(3, 1);
        winButton.disableTouchAnimation();

        ButtonYio okButton = menuControllerYio.getButtonById(53);
        okButton.setPosition(generateRectangle(0.5, 0.1, 0.5, 0.05));
        okButton.setTextLine(getString("continue"));
        okButton.setReaction(Reaction.rbRefuseEarlyGameEnd);
        okButton.setAnimation(Animation.FIXED_DOWN);
        okButton.disableTouchAnimation();
        menuControllerYio.getButtonRenderer().renderButton(okButton);
    }
}