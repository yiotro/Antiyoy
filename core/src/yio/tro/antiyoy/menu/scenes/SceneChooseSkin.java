package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.customizable_list.*;
import yio.tro.antiyoy.menu.scenes.gameplay.AbstractModalScene;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneChooseSkin extends AbstractScene {


    private CustomizableListYio customizableListYio;
    String keys[];
    private Reaction rbBack;
    int tempCounter;


    public SceneChooseSkin(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        customizableListYio = null;
        initReactions();
        initKeys();
    }


    private void initKeys() {
        keys = new String[]{
                "original",
                "points",
                "grid",
                "skin_shroomarts",
                "Domchi",
                "Jannes Peters",
        };
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);
        menuControllerYio.spawnBackButton(260, rbBack);
        createInternals();
        menuControllerYio.endMenuCreation();
    }


    private void createInternals() {
        initList();
        customizableListYio.appear();
    }


    private void initReactions() {
        rbBack = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneMoreSettings.create();
            }
        };
    }


    private void initList() {
        if (customizableListYio != null) return;

        customizableListYio = new CustomizableListYio(menuControllerYio);
        customizableListYio.setPosition(generateRectangle(0.1, 0.15, 0.8, 0.6));
        customizableListYio.setAnimation(Animation.from_center);
        customizableListYio.setScrollingEnabled(false);
        menuControllerYio.addElementToScene(customizableListYio);

        TitleListItem titleListItem = new TitleListItem();
        titleListItem.setTitle(getString("skin"));
        customizableListYio.addItem(titleListItem);

        tempCounter = 0;
        for (String key : keys) {
            addSkinItem(key);
        }
    }


    private void addSkinItem(String key) {
        SkinListItem skinListItem = new SkinListItem();
        skinListItem.setSkinInfo(tempCounter, key);
        customizableListYio.addItem(skinListItem);
        tempCounter++;
    }


}
