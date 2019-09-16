package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class SceneHelpIndex extends AbstractScene{


    private int topicsNumber;
    private double tHeight;
    private double top;
    private Reaction rbDiplomacy1;
    private Reaction rbDiplomacy2;
    private Reaction rbMyGames;
    private double y;
    private ButtonYio basePanel;


    public SceneHelpIndex(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        rbMyGames = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                createMyGamesArticle();
            }
        };

        rbDiplomacy1 = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneArticle.create("article_diplomacy_1", Reaction.rbHelpIndex, 18);
            }
        };

        rbDiplomacy2 = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneArticle.create("article_diplomacy_2", Reaction.rbHelpIndex, 18);
            }
        };
    }


    public static void createMyGamesArticle() {
        Scenes.sceneMyGames.create();
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().setGamePaused(true);

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        topicsNumber = 9;
        tHeight = 0.07;
        top = 0.7;
        createBasePanel();

        y = top - tHeight;
        createTopicButton(127, "help_about_rules", Reaction.rbArticleRules);
        createTopicButton(122, "help_about_units", Reaction.rbArticleUnits);
        createTopicButton(123, "help_about_trees", Reaction.rbArticleTrees);
        createTopicButton(124, "help_about_towers", Reaction.rbArticleTowers);
        createTopicButton(125, "help_about_money", Reaction.rbArticleMoney);
        createTopicButton(126, "help_about_tactics", Reaction.rbArticleTactics);
        createTopicButton(931, getString("diplomacy") + " " + 1, rbDiplomacy1);
        createTopicButton(932, getString("diplomacy") + " " + 2, rbDiplomacy2);
        createTopicButton(933, "my_games", rbMyGames);

        menuControllerYio.spawnBackButton(129, Reaction.rbMainMenu);

        menuControllerYio.endMenuCreation();
    }


    private void createBasePanel() {
        double height = 0.1 + topicsNumber * tHeight;
        basePanel = buttonFactory.getButton(generateRectangle(0.1, top - topicsNumber * tHeight, 0.8, height), 120, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("help") + ":");
            for (int i = 0; i < 1 + topicsNumber; i++) {
                basePanel.addTextLine(" ");
            }
            menuControllerYio.getButtonRenderer().renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.from_center);
    }


    private void createTopicButton(int id, String key, Reaction reaction) {
        ButtonYio topicButton = buttonFactory.getButton(generateRectangle(0.1, y, 0.8, tHeight), id, getString(key));
        topicButton.setReaction(reaction);
        topicButton.setShadow(false);
        topicButton.setVisualHook(basePanel);
        topicButton.setAnimation(Animation.from_center);

        y -= tHeight;
    }
}