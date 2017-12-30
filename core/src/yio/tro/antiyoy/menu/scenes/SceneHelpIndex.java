package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneHelpIndex extends AbstractScene{


    private int topicsNumber;
    private double tHeight;
    private double top;


    public SceneHelpIndex(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().setGamePaused(true);

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        topicsNumber = 6;
        tHeight = 0.08;
        top = 0.63;
        createBasePanel();


        double y = top - tHeight;
        createTopicButton(127, y, "help_about_rules", Reaction.rbArticleRules);
        y -= tHeight;

        createTopicButton(122, y, "help_about_units", Reaction.rbArticleUnits);
        y -= tHeight;

        createTopicButton(123, y, "help_about_trees", Reaction.rbArticleTrees);
        y -= tHeight;

        createTopicButton(124, y, "help_about_towers", Reaction.rbArticleTowers);
        y -= tHeight;

        createTopicButton(125, y, "help_about_money", Reaction.rbArticleMoney);
        y -= tHeight;

        createTopicButton(126, y, "help_about_tactics", Reaction.rbArticleTactics);
        y -= tHeight;


        menuControllerYio.spawnBackButton(129, Reaction.rbMainMenu);

        menuControllerYio.endMenuCreation();
    }


    private void createBasePanel() {
        double height = 0.1 + topicsNumber * tHeight;
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.1, top - topicsNumber * tHeight, 0.8, height), 120, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("help") + ":");
            for (int i = 0; i < 7; i++) {
                basePanel.addTextLine(" ");
            }
            menuControllerYio.getButtonRenderer().renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.FROM_CENTER);
    }


    private void createTopicButton(int id, double y, String key, Reaction reaction) {
        ButtonYio topicButton = buttonFactory.getButton(generateRectangle(0.1, y, 0.8, 0.08), id, getString(key));
        topicButton.setReaction(reaction);
        topicButton.setShadow(false);
        topicButton.setAnimation(Animation.FROM_CENTER);
    }
}