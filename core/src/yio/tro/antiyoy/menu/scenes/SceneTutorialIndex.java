package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;

public class SceneTutorialIndex extends AbstractScene{


    public SceneTutorialIndex(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        createBasePanel();

        createTopicButton(202, 0.53, "help", Reaction.rbHelpIndex);
        createTopicButton(203, 0.45, "normal_rules", Reaction.rbTutorialGeneric);
        createTopicButton(204, 0.37, "slay_rules", Reaction.rbTutorialSlay);

        menuControllerYio.spawnBackButton(209, Reaction.rbChooseGameModeMenu);

        menuControllerYio.endMenuCreation();
    }


    private void createTopicButton(int id, double y, String key, Reaction reaction) {
        ButtonYio topicButton = buttonFactory.getButton(generateRectangle(0.05, y, 0.9, 0.08), id, getString(key));
        topicButton.setReaction(reaction);
        topicButton.setShadow(false);
        topicButton.setAnimation(Animation.from_center);
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
        basePanel.setAnimation(Animation.from_center);
    }
}