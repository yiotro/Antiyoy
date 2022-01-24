package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.customizable_list.AbstractCustomListItem;
import yio.tro.antiyoy.menu.customizable_list.CustomizableListYio;
import yio.tro.antiyoy.menu.customizable_list.ScrollListItem;
import yio.tro.antiyoy.menu.customizable_list.SliReaction;
import yio.tro.antiyoy.menu.scenes.editor.SceneEditorOverlay;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.StoreLinksYio;
import yio.tro.antiyoy.stuff.Yio;

import java.util.ArrayList;
import java.util.Set;

public class SceneMyGames extends AbstractScene {

    private ButtonYio infoPanel;
    CustomizableListYio customizableListYio;


    public SceneMyGames(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        customizableListYio = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, true, false);
        menuControllerYio.spawnBackButton(490, Reaction.rbHelpIndex);

        createInfoPanel();
        createList();

        menuControllerYio.endMenuCreation();
    }


    private void createList() {
        initList();
        customizableListYio.appear();
    }


    private void initList() {
        if (customizableListYio != null) return;
        customizableListYio = new CustomizableListYio(menuControllerYio);
        customizableListYio.setAnimation(Animation.from_center);
        customizableListYio.setEmbeddedMode(true);
        customizableListYio.setPosition(generateRectangle(0.06, 0.06, 0.88, 0.63));
        menuControllerYio.addElementToScene(customizableListYio);
        loadValues();
    }


    private void loadValues() {
        StoreLinksYio instance = StoreLinksYio.getInstance();
        for (String key : instance.getKeys()) {
            if (key.equals("antiyoy")) continue;
            String capitalizedString = Yio.getCapitalizedString(key);
            String link = instance.getLink(key);
            addListItem(capitalizedString, link);
        }
    }


    private void addListItem(String key, final String url) {
        ScrollListItem scrollListItem = new ScrollListItem();
        scrollListItem.setKey(key);
        scrollListItem.setTitle(key);
        scrollListItem.setHeight(0.06f * GraphicsYio.height);
        scrollListItem.setClickReaction(new SliReaction() {
            @Override
            public void apply(AbstractCustomListItem item) {
                Gdx.net.openURI(url);
            }
        });
        customizableListYio.addItem(scrollListItem);
    }


    private void createInfoPanel() {
        infoPanel = buttonFactory.getButton(generateRectangle(0.05, 0.05, 0.9, 0.8), 491, null);
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
