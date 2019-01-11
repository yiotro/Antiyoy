package yio.tro.antiyoy.menu.scenes.gameplay;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneSelectionOverlay extends AbstractGameplayScene {


    private ButtonYio unitButton;
    private ButtonYio towerButton;
    private ButtonYio coinButton;
    private ButtonYio diplomacyButton;
    TextureRegion flagNormal, flagRed;


    public SceneSelectionOverlay(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        flagNormal = GraphicsYio.loadTextureRegion("diplomacy/flag.png", true);
        flagRed = GraphicsYio.loadTextureRegion("diplomacy/flag_red.png", true);
    }


    @Override
    public void create() {
        createUnitButton();
        createTowerButton();
        createCoinButton();
        createDiplomacyButton();
    }


    private void createDiplomacyButton() {
        diplomacyButton = menuControllerYio.getButtonById(36);
        if (diplomacyButton == null) { // init
            diplomacyButton = buttonFactory.getButton(generateSquare(0, 0.1, 0.07), 36, null);
            diplomacyButton.setAnimation(Animation.LEFT);
            diplomacyButton.enableRectangularMask();
            diplomacyButton.disableTouchAnimation();
            diplomacyButton.setTouchOffset(0.05f * GraphicsYio.width);
            diplomacyButton.setIgnorePauseResume(true);
            diplomacyButton.setReaction(new Reaction() {
                @Override
                public void perform(ButtonYio buttonYio) {
                    getGameController(buttonYio).fieldController.diplomacyManager.onDiplomacyButtonPressed();
                }
            });
        }
        updateDiplomacyFlagTexture();
        diplomacyButton.appearFactor.appear(3, 2);
        diplomacyButton.setTouchable(GameRules.diplomacyEnabled);

        if (!GameRules.diplomacyEnabled) {
            diplomacyButton.destroy();
        }
    }


    private void updateDiplomacyFlagTexture() {
        DiplomacyManager diplomacyManager = menuControllerYio.yioGdxGame.gameController.fieldController.diplomacyManager;
        if (diplomacyManager.log.hasSomethingToRead()) {
            diplomacyButton.setTexture(flagRed);
        } else {
            diplomacyButton.setTexture(flagNormal);
        }
    }


    private void createCoinButton() {
        // important: there is another coin button on SceneFastConstruction
        coinButton = menuControllerYio.getButtonById(37);
        if (coinButton == null) { // init
            coinButton = buttonFactory.getButton(generateSquare(0, 0.93, 0.07), 37, null);
            coinButton.setAnimation(Animation.UP);
            coinButton.setPressSound(SoundManagerYio.soundCoin);
            coinButton.enableRectangularMask();
            coinButton.disableTouchAnimation();
        }
        loadCoinButtonTexture();
        coinButton.appearFactor.appear(3, 2);
        coinButton.setTouchable(true);
        coinButton.setReaction(Reaction.rbShowColorStats);
    }


    private void createTowerButton() {
        towerButton = menuControllerYio.getButtonById(38);
        if (towerButton == null) { // init
            towerButton = buttonFactory.getButton(generateSquare(0.30, 0, 0.13 * YioGdxGame.screenRatio), 38, null);
            towerButton.setReaction(Reaction.rbBuildSolidObject);
            towerButton.setAnimation(Animation.DOWN);
            towerButton.enableRectangularMask();
        }
        loadBuildObjectButton();
        towerButton.setTouchable(true);
        towerButton.setTouchOffset(0.05f * GraphicsYio.width);
        towerButton.appearFactor.appear(3, 2);
    }


    private void createUnitButton() {
        unitButton = menuControllerYio.getButtonById(39);
        if (unitButton == null) { // init
            unitButton = buttonFactory.getButton(generateSquare(0.57, 0, 0.13 * YioGdxGame.screenRatio), 39, null);
            unitButton.setReaction(Reaction.rbBuildUnit);
            unitButton.setAnimation(Animation.DOWN);
            unitButton.enableRectangularMask();
        }
        loadUnitButtonTexture();
        unitButton.setTouchable(true);
        unitButton.setTouchOffset(0.05f * GraphicsYio.width);
        unitButton.appearFactor.appear(3, 2);
    }


    void loadBuildObjectButton() {
        if (GameRules.slayRules) {
            loadTowerButtonTexture();
        } else {
            loadFarmButtonTexture();
        }
    }


    public void onSkinChanged() {
        if (coinButton != null) {
            coinButton.resetTexture();
        }
        if (unitButton != null) {
            unitButton.resetTexture();
        }
        if (towerButton != null) {
            towerButton.resetTexture();
        }
    }


    void loadFarmButtonTexture() {
        if (Settings.isShroomArtsEnabled()) {
            menuControllerYio.loadButtonOnce(towerButton, "skins/ant/field_elements/house.png");
            return;
        }

        menuControllerYio.loadButtonOnce(towerButton, "field_elements/house.png");
    }


    void loadTowerButtonTexture() {
        if (Settings.isShroomArtsEnabled()) {
            menuControllerYio.loadButtonOnce(towerButton, "skins/ant/field_elements/tower.png");
            return;
        }

        menuControllerYio.loadButtonOnce(towerButton, "field_elements/tower.png");
    }


    void loadUnitButtonTexture() {
        if (Settings.isShroomArtsEnabled()) {
            menuControllerYio.loadButtonOnce(unitButton, "skins/ant/field_elements/man0.png");
            return;
        }

        menuControllerYio.loadButtonOnce(unitButton, "field_elements/man0.png");
    }


    void loadCoinButtonTexture() {
        if (Settings.isShroomArtsEnabled()) {
            menuControllerYio.loadButtonOnce(coinButton, "skins/ant/coin.png");
            return;
        }

        menuControllerYio.loadButtonOnce(coinButton, "coin.png");
    }


    @Override
    public void hide() {
        menuControllerYio.destroyButton(39);
        menuControllerYio.destroyButton(38);
        menuControllerYio.destroyButton(37);
        menuControllerYio.destroyButton(36);
        menuControllerYio.getYioGdxGame().gameController.selectionController.getSelMoneyFactor().destroy(2, 8);
    }
}