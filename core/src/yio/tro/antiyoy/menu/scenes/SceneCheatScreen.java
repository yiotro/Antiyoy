package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;

public class SceneCheatScreen extends AbstractScene{

    public SceneCheatScreen(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, true, true);

        createButton(532, 0.62, "Unlock levels", ReactBehavior.rbUnlockLevels);
        createButton(537, 0.52, "Show FPS", ReactBehavior.rbShowFps);
//        createButton(533, 0.46, "choose_game_mode_tutorial", ReactBehavior.rbTutorialIndex);
//        createButton(534, 0.38, "choose_game_mode_campaign", ReactBehavior.rbCampaignMenu).disableTouchAnimation();
//        createButton(535, 0.3, "choose_game_mode_load", ReactBehavior.rbLoadGame).disableTouchAnimation();

        menuControllerYio.spawnBackButton(536, ReactBehavior.rbMainMenu);

        menuControllerYio.endMenuCreation();
    }


    private ButtonYio createButton(int id, double y, String key, ReactBehavior reactBehavior) {
        ButtonYio button = buttonFactory.getButton(generateRectangle(0.1, y, 0.8, 0.08), id, getString(key));
        button.setReactBehavior(reactBehavior);
        button.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        return button;
    }
}
