package yio.tro.antiyoy.menu.scenes.gameplay;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.gameplay.skins.SkinManager;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.income_view.MoneyViewElement;
import yio.tro.antiyoy.menu.income_view.MveBehavior;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneSelectionOverlay extends AbstractModalScene {


    private ButtonYio unitButton;
    private ButtonYio towerButton;
    private ButtonYio diplomacyButton;
    TextureRegion flagNormal;
    private final TextureRegion mailIconTexture;
    private ButtonYio logButton;


    public SceneSelectionOverlay(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        flagNormal = GraphicsYio.loadTextureRegion("diplomacy/flag.png", true);
        mailIconTexture = GraphicsYio.loadTextureRegion("diplomacy/mail_icon.png", true);
    }


    @Override
    public void create() {
        createUnitButton();
        createTowerButton();
        createDiplomacyButton();
        createDiplomaticLogButton();
    }


    private void createDiplomaticLogButton() {
        logButton = menuControllerYio.getButtonById(33);
        initLogButton();
        logButton.appearFactor.appear(3, 2);
        logButton.setTouchable(GameRules.diplomacyEnabled);

        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
        boolean hasSomethingToRead = diplomacyManager.log.hasSomethingToRead();
        if (!GameRules.diplomacyEnabled || !hasSomethingToRead) {
            logButton.destroy();
        }
    }


    private void initLogButton() {
        if (logButton != null) return;

        logButton = buttonFactory.getButton(generateSquare(0, 0.2, 0.07), 33, null);
        logButton.setAnimation(Animation.left);
        logButton.enableRectangularMask();
        logButton.setTouchOffset(0.05f * GraphicsYio.width);
        logButton.setIgnorePauseResume(true);
        logButton.setTexture(mailIconTexture);
        logButton.setReaction(new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                getGameController(buttonYio).fieldManager.diplomacyManager.onDiplomaticLogButtonPressed();
            }
        });
    }


    private void createDiplomacyButton() {
        diplomacyButton = menuControllerYio.getButtonById(36);
        initDiplomacyButton();
        updateDiplomacyFlagTexture();
        diplomacyButton.appearFactor.appear(3, 2);
        diplomacyButton.setTouchable(GameRules.diplomacyEnabled);

        if (!GameRules.diplomacyEnabled) {
            diplomacyButton.destroy();
        }
    }


    private void initDiplomacyButton() {
        if (diplomacyButton != null) return;

        diplomacyButton = buttonFactory.getButton(generateSquare(0, 0.1, 0.07), 36, null);
        diplomacyButton.setAnimation(Animation.left);
        diplomacyButton.enableRectangularMask();
        diplomacyButton.setTouchOffset(0.05f * GraphicsYio.width);
        diplomacyButton.setIgnorePauseResume(true);
        diplomacyButton.setReaction(new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                getGameController(buttonYio).fieldManager.diplomacyManager.onDiplomacyButtonPressed();
            }
        });
    }


    private void updateDiplomacyFlagTexture() {
        diplomacyButton.setTexture(flagNormal);
    }


    private void createTowerButton() {
        towerButton = menuControllerYio.getButtonById(38);
        if (towerButton == null) { // init
            towerButton = buttonFactory.getButton(generateSquare(0.30, 0, 0.13 * YioGdxGame.screenRatio), 38, null);
            towerButton.setReaction(Reaction.rbBuildSolidObject);
            towerButton.setAnimation(Animation.down);
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
            unitButton.setAnimation(Animation.down);
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
        if (unitButton != null) {
            unitButton.resetTexture();
        }
        if (towerButton != null) {
            towerButton.resetTexture();
        }
    }


    void loadFarmButtonTexture() {
        menuControllerYio.loadButtonOnce(towerButton, getSkinManager().getFarmTexturePath());
    }


    void loadTowerButtonTexture() {
        menuControllerYio.loadButtonOnce(towerButton, getSkinManager().getTowerTexturePath());
    }


    void loadUnitButtonTexture() {
        menuControllerYio.loadButtonOnce(unitButton, getSkinManager().getPeasantTexturePath());
    }


    private SkinManager getSkinManager() {
        return menuControllerYio.yioGdxGame.skinManager;
    }


    @Override
    public void hide() {
        destroyByIndex(33, 39);

        GameController gameController = menuControllerYio.getYioGdxGame().gameController;
        FactorYio selMoneyFactor = gameController.selectionManager.getSelMoneyFactor();
        selMoneyFactor.destroy(2, 8);
    }
}