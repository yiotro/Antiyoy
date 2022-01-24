package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.StoreLinksYio;

public class SceneAoArticle extends AbstractScene{

    private ButtonYio panel;
    private ButtonYio loadButton;


    public SceneAoArticle(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, false, true);

        menuControllerYio.spawnBackButton(5470, getBackReaction());

        createPanel();
        createLoadButton();

        menuControllerYio.endMenuCreation();
    }


    private void createPanel() {
        panel = buttonFactory.getButton(generateRectangle(0.05, 0.07, 0.9, 0.76), 5471, null);
        panel.cleatText();
        panel.addManyLines(menuControllerYio.getArrayListFromString(LanguagesManager.getInstance().getString("antiyoy_online")));
        while (panel.textLines.size() < 18) {
            panel.addTextLine(" ");
        }
        menuControllerYio.buttonRenderer.renderButton(panel);
        panel.setTouchable(false);
        panel.setAnimation(Animation.from_center);
    }


    private void createLoadButton() {
        String string = LanguagesManager.getInstance().getString("choose_game_mode_load");
        loadButton = buttonFactory.getButton(generateRectangle(0.25, 0.09, 0.5, 0.05), 5474, string);
        loadButton.setTouchOffset(0.1f * GraphicsYio.width);
        loadButton.setShadow(false);
        loadButton.setReaction(getLoadReaction());
        loadButton.setAnimation(Animation.from_center);
    }


    private Reaction getLoadReaction() {
        return new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Gdx.net.openURI(StoreLinksYio.getInstance().getLink("onliyoy"));
            }
        };
    }



    private Reaction getBackReaction() {
        return new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneChooseGameMode.create();
            }
        };
    }
}
