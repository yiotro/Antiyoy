package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.ios.IosCheckMyGamesElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;

public class SceneIosCheckMyGames extends AbstractScene{

    public SceneIosCheckMyGames(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);
        menuControllerYio.spawnBackButton(17820, getBackReaction());

        IosCheckMyGamesElement iosCheckMyGamesElement = new IosCheckMyGamesElement(menuControllerYio);
        iosCheckMyGamesElement.setPosition(generateRectangle(0.1, 0.1, 0.8, 0.7));
        iosCheckMyGamesElement.setAnimation(Animation.from_center);
        menuControllerYio.addElementToScene(iosCheckMyGamesElement);
        iosCheckMyGamesElement.appear();

        menuControllerYio.endMenuCreation();
    }


    private Reaction getBackReaction() {
        return new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneMainMenu.create();
            }
        };
    }
}
