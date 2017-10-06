package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.SoundControllerYio;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneBuildButtons extends AbstractScene{


    public SceneBuildButtons(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        ButtonYio unitButton = menuControllerYio.getButtonById(39);
        if (unitButton == null) { // init
            unitButton = buttonFactory.getButton(generateSquare(0.57, 0, 0.13 * YioGdxGame.screenRatio), 39, null);
            unitButton.setReactBehavior(ReactBehavior.rbBuildUnit);
            unitButton.setAnimType(ButtonYio.ANIM_DOWN);
            unitButton.enableRectangularMask();
        }
        loadUnitButtonTexture(unitButton);
        unitButton.setTouchable(true);
        unitButton.setTouchOffset(0.05f * GraphicsYio.width);
        unitButton.appearFactor.beginSpawning(3, 2);

        ButtonYio towerButton = menuControllerYio.getButtonById(38);
        if (towerButton == null) { // init
            towerButton = buttonFactory.getButton(generateSquare(0.30, 0, 0.13 * YioGdxGame.screenRatio), 38, null);
            towerButton.setReactBehavior(ReactBehavior.rbBuildSolidObject);
            towerButton.setAnimType(ButtonYio.ANIM_DOWN);
            towerButton.enableRectangularMask();
        }
        loadBuildObjectButton(towerButton);
        towerButton.setTouchable(true);
        towerButton.setTouchOffset(0.05f * GraphicsYio.width);
        towerButton.appearFactor.beginSpawning(3, 2);

        ButtonYio coinButton = menuControllerYio.getButtonById(37);
        if (coinButton == null) { // init
            coinButton = buttonFactory.getButton(generateSquare(0, 0.93, 0.07), 37, null);
            coinButton.setAnimType(ButtonYio.ANIM_UP);
            coinButton.setPressSound(SoundControllerYio.soundCoin);
            coinButton.enableRectangularMask();
            coinButton.disableTouchAnimation();
        }
        loadCoinButtonTexture(coinButton);
        coinButton.appearFactor.beginSpawning(3, 2);
        coinButton.setTouchable(true);
        coinButton.setReactBehavior(ReactBehavior.rbShowColorStats);
    }


    void loadBuildObjectButton(ButtonYio objectButton) {
        if (GameRules.slayRules) {
            loadTowerButtonTexture(objectButton);
        } else {
            loadFarmButtonTexture(objectButton);
        }
    }


    void loadFarmButtonTexture(ButtonYio farmButton) {
        if (Settings.isShroomArtsEnabled()) {
            menuControllerYio.loadButtonOnce(farmButton, "skins/ant/field_elements/house.png");
            return;
        }

        menuControllerYio.loadButtonOnce(farmButton, "field_elements/house.png");
    }


    void loadTowerButtonTexture(ButtonYio towerButton) {
        if (Settings.isShroomArtsEnabled()) {
            menuControllerYio.loadButtonOnce(towerButton, "skins/ant/field_elements/tower.png");
            return;
        }

        menuControllerYio.loadButtonOnce(towerButton, "field_elements/tower.png");
    }


    void loadUnitButtonTexture(ButtonYio unitButton) {
        if (Settings.isShroomArtsEnabled()) {
            menuControllerYio.loadButtonOnce(unitButton, "skins/ant/field_elements/man0.png");
            return;
        }

        menuControllerYio.loadButtonOnce(unitButton, "field_elements/man0.png");
    }


    void loadCoinButtonTexture(ButtonYio coinButton) {
        if (Settings.isShroomArtsEnabled()) {
            menuControllerYio.loadButtonOnce(coinButton, "skins/ant/coin.png");
            return;
        }

        menuControllerYio.loadButtonOnce(coinButton, "coin.png");
    }


    public void hide() {
        menuControllerYio.destroyButton(39);
        menuControllerYio.destroyButton(38);
        menuControllerYio.destroyButton(37);
        menuControllerYio.getYioGdxGame().gameController.selectionController.getSelMoneyFactor().beginDestroying(2, 8);
    }
}