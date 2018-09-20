package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;

public class SceneSecretScreen extends AbstractScene{


    private Reaction rbFireworks;
    double curY;
    private ButtonYio label;
    private Reaction rbCheats;


    public SceneSecretScreen(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        initReactions();
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, true, true);

        label = buttonFactory.getButton(generateRectangle(0.1, 0.55, 0.8, 0.3), 570, null);
        if (label.notRendered()) {
            label.cleatText();
            label.addTextLine("This is secret screen.");
            label.addTextLine("It's mostly used for debug purposes, but it also contains some stuff that can be useful for players.");
            label.addTextLine("For example, you can tap 'unlock levels' and you will be able to launch any campaign level.");
            menuControllerYio.buttonRenderer.renderButton(label);
        }
        label.setTouchable(false);
        label.setAnimation(Animation.UP);

        curY = 0.42;
        createButton(572, "Unlock levels", Reaction.rbUnlockLevels);
        createButton(573, "Show FPS", Reaction.rbShowFps);
        createButton(574, "Fireworks", rbFireworks);
        createButton(575, "Cheats on/off", rbCheats);

        menuControllerYio.spawnBackButton(579, Reaction.rbMainMenu);

        menuControllerYio.endMenuCreation();
    }


    private void initReactions() {
        rbFireworks = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneFireworks.create();
            }
        };

        rbCheats = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                if (!DebugFlags.cheatsEnabled) {
                    DebugFlags.cheatsEnabled = true;
                    Scenes.sceneNotification.showNotification("Cheats are now enabled");
                } else {
                    DebugFlags.cheatsEnabled = false;
                    Scenes.sceneNotification.showNotification("Cheats disabled");
                }
            }
        };
    }


    private ButtonYio createButton(int id, String key, Reaction reaction) {
        ButtonYio button = buttonFactory.getButton(generateRectangle(0.1, curY, 0.8, 0.07), id, getString(key));
        button.setReaction(reaction);
        button.setAnimation(Animation.DOWN);

        curY -= 0.09;

        return button;
    }
}
