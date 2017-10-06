package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class SceneSkirmishMenu extends AbstractScene {


    public ButtonYio startButton;


    public SceneSkirmishMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    public void saveSkirmishSettings() {
        // 3 - difficulty
        // 0 - map size
        // 1 - player number
        // 2 - color number
        Preferences prefs = Gdx.app.getPreferences("skirmish");
        prefs.putInteger("difficulty", menuControllerYio.getSliders().get(3).getCurrentRunnerIndex());
        prefs.putInteger("map_size", menuControllerYio.getSliders().get(0).getCurrentRunnerIndex());
        prefs.putInteger("player_number", menuControllerYio.getSliders().get(1).getCurrentRunnerIndex());
        prefs.putInteger("color_number", menuControllerYio.getSliders().get(2).getCurrentRunnerIndex());
        prefs.flush();
    }


    public void loadSkirmishSettings() {
        Preferences prefs = Gdx.app.getPreferences("skirmish");
        menuControllerYio.getSliders().get(3).setRunnerValueByIndex(prefs.getInteger("difficulty", 1));
        menuControllerYio.getSliders().get(0).setRunnerValueByIndex(prefs.getInteger("map_size", 1));
        menuControllerYio.getSliders().get(2).setRunnerValueByIndex(prefs.getInteger("color_number", 2));
        menuControllerYio.getSliders().get(1).setRunnerValueByIndex(prefs.getInteger("player_number", 1));

        menuControllerYio.loadMoreSkirmishOptions();
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, true, true);

        menuControllerYio.getSliders().get(3).appear();
        menuControllerYio.getSliders().get(3).setPos(0.15, 0.73, 0.7, 0);

        menuControllerYio.getSliders().get(0).appear();
        menuControllerYio.getSliders().get(0).setPos(0.15, 0.52, 0.7, 0);

        menuControllerYio.getSliders().get(1).appear();
        menuControllerYio.getSliders().get(1).setPos(0.15, 0.31, 0.7, 0);
        menuControllerYio.getSliders().get(1).setVerticalTouchOffset(0.06f * Gdx.graphics.getHeight());

        menuControllerYio.getSliders().get(2).appear();
        menuControllerYio.getSliders().get(2).setPos(0.15, 0.1, 0.7, 0);
        menuControllerYio.getSliders().get(2).setVerticalTouchOffset(0.06f * Gdx.graphics.getHeight());

        ButtonYio difficultyLabel = buttonFactory.getButton(generateRectangle(0.1, 0.67, 0.8, 0.2), 88, null);
        menuControllerYio.renderTextAndSomeEmptyLines(difficultyLabel, getString("difficulty"), 2);
        difficultyLabel.setTouchable(false);
        difficultyLabel.setAnimType(ButtonYio.ANIM_UP);

        ButtonYio mapSizeLabel = buttonFactory.getButton(generateRectangle(0.1, 0.46, 0.8, 0.2), 81, null);
        menuControllerYio.renderTextAndSomeEmptyLines(mapSizeLabel, getString("map_size"), 2);
        mapSizeLabel.setTouchable(false);
        mapSizeLabel.setAnimType(ButtonYio.ANIM_UP);

        ButtonYio playersLabel = buttonFactory.getButton(generateRectangle(0.1, 0.25, 0.8, 0.2), 84, null);
        menuControllerYio.renderTextAndSomeEmptyLines(playersLabel, getString("player_number"), 2);
        playersLabel.setTouchable(false);
        playersLabel.setAnimType(ButtonYio.ANIM_DOWN);

        ButtonYio colorsLabel = buttonFactory.getButton(generateRectangle(0.1, 0.04, 0.8, 0.2), 87, null);
        menuControllerYio.renderTextAndSomeEmptyLines(colorsLabel, getString("color_number"), 2);
        colorsLabel.setTouchable(false);
        colorsLabel.setAnimType(ButtonYio.ANIM_DOWN);

        menuControllerYio.getButtonById(88).appearFactor.beginSpawning(2, 1.5);
        menuControllerYio.getButtonById(81).appearFactor.beginSpawning(2, 1.5);
        menuControllerYio.getButtonById(84).appearFactor.beginSpawning(2, 1.5);
        menuControllerYio.getButtonById(87).appearFactor.beginSpawning(2, 1.5);

        menuControllerYio.spawnBackButton(80, ReactBehavior.rbBackFromSkirmish);
        menuControllerYio.getButtonById(80).setTouchable(true);

        startButton = buttonFactory.getButton(generateRectangle(0.55, 0.9, 0.4, 0.07), 83, getString("game_settings_start"));
        startButton.setReactBehavior(ReactBehavior.rbStartSkirmishGame);
        startButton.setAnimType(ButtonYio.ANIM_UP);
        startButton.disableTouchAnimation();

        ButtonYio moreButton = buttonFactory.getButton(generateRectangle(0.6, 0.2, 0.3, 0.04), 86, getString("more"));
        moreButton.setReactBehavior(ReactBehavior.rbMoreSkirmishOptions);
        moreButton.setAnimType(ButtonYio.ANIM_DOWN);
        moreButton.disableTouchAnimation();

        loadSkirmishSettings();

        menuControllerYio.endMenuCreation();
    }


    public static String getHumansString(int n) {
        LanguagesManager instance = LanguagesManager.getInstance();

        if (n == 0) {
            return instance.getString("ai_only");
        } else if (n == 1) {
            return instance.getString("single_player");
        } else {
            return instance.getString("multiplayer") + " " + (n) + "x";
        }
    }
}