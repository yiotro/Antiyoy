package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneTutorialIndex extends AbstractScene{


    public SceneTutorialIndex(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        createBasePanel();

        createTopicButton(202, 0.53, "help", ReactBehavior.rbHelpIndex);
        createTopicButton(203, 0.45, "normal_rules", ReactBehavior.rbTutorialGeneric);
        createTopicButton(204, 0.37, "slay_rules", ReactBehavior.rbTutorialSlay);

        menuControllerYio.spawnBackButton(209, ReactBehavior.rbChooseGameModeMenu);

        menuControllerYio.endMenuCreation();
    }


    private void createTopicButton(int id, double y, String key, ReactBehavior reactBehavior) {
        ButtonYio topicButton = buttonFactory.getButton(generateRectangle(0.05, y, 0.9, 0.08), id, getString(key));
        topicButton.setReactBehavior(reactBehavior);
        topicButton.setShadow(false);
        topicButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
    }


    private void createBasePanel() {
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.37, 0.9, 0.34), 200, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("choose_game_mode_tutorial") + ":");
            for (int i = 0; i < 5; i++) {
                basePanel.addTextLine(" ");
            }
            menuControllerYio.getButtonRenderer().renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);
    }
}