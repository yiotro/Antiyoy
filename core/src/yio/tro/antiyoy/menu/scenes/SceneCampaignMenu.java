package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneCampaignMenu extends AbstractScene{


    public SceneCampaignMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, true, true);

        menuControllerYio.spawnBackButton(20, ReactBehavior.rbChooseGameModeMenu);
        menuControllerYio.getButtonById(20).setTouchable(true);

        ButtonYio moreOptionsButton = buttonFactory.getButton(generateRectangle(0.75, 0.9, 0.2, 0.07), 21, getString("..."));
        moreOptionsButton.setReactBehavior(ReactBehavior.rbMoreCampaignOptions);
        moreOptionsButton.setAnimType(ButtonYio.ANIM_UP);
        moreOptionsButton.disableTouchAnimation();

        menuControllerYio.getLevelSelector().appear();

        menuControllerYio.endMenuCreation();
    }
}