package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;

public class SceneMyGames extends AbstractScene {

    private ButtonYio infoPanel;


    public SceneMyGames(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, true, false);
        menuControllerYio.spawnBackButton(490, Reaction.rbHelpIndex);

        createInfoPanel();
        createInternalButton(492, 0, "Achikaps", "https://play.google.com/store/apps/details?id=yio.tro.achikaps");
        createInternalButton(493, 1, "Bleentoro", "https://play.google.com/store/apps/details?id=yio.tro.bleentoro");
        createInternalButton(494, 2, "Shmatoosto", "https://play.google.com/store/apps/details?id=yio.tro.shmatoosto");
        createInternalButton(495, 3, "Opacha-mda", "https://play.google.com/store/apps/details?id=yio.tro.opacha");

        menuControllerYio.endMenuCreation();
    }


    void createInternalButton(int id, int oIndex, String key, String url) {
        double bh = 0.055;
        ButtonYio button = buttonFactory.getButton(generateRectangle(0.2, 0.55 - bh * oIndex, 0.6, bh), id, getString(key));
        button.setAnimation(Animation.from_center);
        button.appearFactor.appear(2, 1.5);
        button.setVisualHook(infoPanel);
        button.setReaction(new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Gdx.net.openURI(url);
            }
        });
    }


    private void createInfoPanel() {
        infoPanel = buttonFactory.getButton(generateRectangle(0.05, 0.1, 0.9, 0.7), 491, null);
        infoPanel.cleatText();
        ArrayList<String> list = menuControllerYio.getArrayListFromString(LanguagesManager.getInstance().getString("article_my_games"));
        infoPanel.addManyLines(list);
        int lines = 18;
        int addedEmptyLines = lines - list.size();
        for (int i = 0; i < addedEmptyLines; i++) {
            infoPanel.addTextLine(" ");
        }
        menuControllerYio.getButtonRenderer().renderButton(infoPanel);

        infoPanel.setTouchable(false);
        infoPanel.setAnimation(Animation.from_center);
        infoPanel.appearFactor.appear(2, 1.5);
    }
}
