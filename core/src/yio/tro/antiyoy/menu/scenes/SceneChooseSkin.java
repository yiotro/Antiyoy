package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.gameplay.skins.SkinType;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.customizable_list.*;
import yio.tro.antiyoy.menu.scenes.gameplay.AbstractModalScene;
import yio.tro.antiyoy.stuff.AtlasLoader;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneChooseSkin extends AbstractScene {


    private CustomizableListYio customizableListYio;
    private Reaction rbBack;
    private AtlasLoader atlasLoader;


    public SceneChooseSkin(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        customizableListYio = null;
        initAtlasLoader();
        initReactions();
    }


    private void initAtlasLoader() {
        String path = "skins/preview/";
        atlasLoader = new AtlasLoader(path + "atlas_texture.png", path + "atlas_structure.txt", true);
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
        customizableListYio.setPosition(generateRectangle(0.05, 0.05, 0.9, 0.8));
        customizableListYio.setAnimation(Animation.from_center);
        menuControllerYio.addElementToScene(customizableListYio);

        TitleListItem titleListItem = new TitleListItem();
        titleListItem.setTitle(getString("skin"));
        customizableListYio.addItem(titleListItem);

        boolean darken = true;
        for (SkinType skinType : SkinType.values()) {
            addSkinItem(skinType, darken);
            darken = !darken;
        }
    }


    private void addSkinItem(SkinType skinType, boolean darken) {
        SkinListItem skinListItem = new SkinListItem(atlasLoader);
        skinListItem.setSkinInfo(skinType);
        skinListItem.setDarken(darken);
        customizableListYio.addItem(skinListItem);
    }


}
