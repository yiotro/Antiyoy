package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class SceneBleentoroRelease extends AbstractScene{

    ButtonYio panel, okButton;


    public SceneBleentoroRelease(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, false, true);

        panel = buttonFactory.getButton(generateRectangle(0.05, 0.33, 0.9, 0.34), 830, null);
        panel.cleatText();
        panel.addManyLines(menuControllerYio.getArrayListFromString(LanguagesManager.getInstance().getString("bleentoro_release")));
        while (panel.textLines.size() < 8) {
            panel.addTextLine(" ");
        }
        menuControllerYio.buttonRenderer.renderButton(panel);
        panel.setTouchable(false);
        panel.setAnimation(Animation.from_center);

        okButton = buttonFactory.getButton(generateRectangle(0.55, 0.33, 0.4, 0.05), 831, LanguagesManager.getInstance().getString("end_game_ok"));
        okButton.setTouchOffset(0.1f * GraphicsYio.width);
        okButton.setShadow(false);
        okButton.setReaction(new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneMainMenu.create();
            }
        });
        okButton.setAnimation(Animation.from_center);

        menuControllerYio.endMenuCreation();
    }
}
