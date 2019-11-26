package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.skins.SkinManager;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.income_view.MoneyViewElement;
import yio.tro.antiyoy.menu.income_view.MveBehavior;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class SceneFinances extends AbstractModalScene{

    public ButtonYio coinButton;
    MoneyViewElement profitViewElement;
    MoneyViewElement balanceViewElement;


    public SceneFinances(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        profitViewElement = null;
        balanceViewElement = null;
        coinButton = null;
    }


    @Override
    public void create() {
        if (isAlreadyShown()) return;

        createCoinButton();
        createProfitViewElement();
        createBalanceViewElement();
    }


    private boolean isAlreadyShown() {
        if (coinButton == null) return false;
        if (coinButton.getFactor().get() != 1) return false;
        if (coinButton.getFactor().getGravity() <= 0) return false;
        return true;
    }


    private void createCoinButton() {
        // important: there is another coin button in SceneAiOnlyOverlay

        coinButton = menuControllerYio.getButtonById(37);
        if (coinButton == null) { // init
            coinButton = buttonFactory.getButton(generateSquare(0, 0.93, 0.07), 650, null);
            coinButton.setAnimation(Animation.up);
            coinButton.setPressSound(SoundManagerYio.soundCoin);
            coinButton.enableRectangularMask();
        }
        loadCoinButtonTexture();
        coinButton.appearFactor.appear(3, 2);
        coinButton.setTouchable(true);
        coinButton.setReaction(Reaction.rbShowIncomeGraph);
    }


    public void onSkinChanged() {
        if (coinButton != null) {
            coinButton.resetTexture();
        }
    }


    void loadCoinButtonTexture() {
        menuControllerYio.loadButtonOnce(coinButton, getSkinManager().getCoinTexturePath());
    }


    private SkinManager getSkinManager() {
        return menuControllerYio.yioGdxGame.skinManager;
    }


    private void createBalanceViewElement() {
        initBalanceViewElement();
        balanceViewElement.appear();
    }


    private void initBalanceViewElement() {
        if (balanceViewElement != null) return;

        balanceViewElement = new MoneyViewElement(menuControllerYio);
        balanceViewElement.setPosition(generateRectangle(0.12, 0.935, 0.2, 0.06));
        balanceViewElement.setAnimation(Animation.up);
        balanceViewElement.setBehavior(getBalanceViewBehavior());
        balanceViewElement.setReaction(getBalanceViewReaction());
        menuControllerYio.addElementToScene(balanceViewElement);
    }


    private Reaction getBalanceViewReaction() {
        return new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneIncomeGraph.create();
            }
        };
    }


    private MveBehavior getBalanceViewBehavior() {
        return new MveBehavior() {
            @Override
            public int getTitleValue() {
                GameController gameController = menuControllerYio.yioGdxGame.gameController;
                Province selectedProvince = gameController.fieldManager.selectedProvince;
                if (selectedProvince == null) return 0;
                return selectedProvince.money;
            }
        };
    }


    private void createProfitViewElement() {
        initProfitViewElement();
        profitViewElement.appear();
    }


    private void initProfitViewElement() {
        if (profitViewElement != null) return;

        profitViewElement = new MoneyViewElement(menuControllerYio);
        profitViewElement.setPosition(generateRectangle(0.4, 0.935, 0.2, 0.06));
        profitViewElement.setAnimation(Animation.up);
        profitViewElement.setCentered(true);
        profitViewElement.setPlusNeeded(true);
        profitViewElement.setBehavior(getProfitViewBehavior());
        profitViewElement.setReaction(getProfitViewReaction());
        menuControllerYio.addElementToScene(profitViewElement);
    }


    private Reaction getProfitViewReaction() {
        return new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneProfitDetails.create();
            }
        };
    }


    private MveBehavior getProfitViewBehavior() {
        return new MveBehavior() {
            @Override
            public int getTitleValue() {
                GameController gameController = menuControllerYio.yioGdxGame.gameController;
                Province selectedProvince = gameController.fieldManager.selectedProvince;
                if (selectedProvince == null) return 0;
                return selectedProvince.getProfit();
            }
        };
    }


    @Override
    public void hide() {
        if (profitViewElement != null) {
            profitViewElement.destroy();
        }

        if (balanceViewElement != null) {
            balanceViewElement.destroy();
        }

        if (coinButton != null) {
            coinButton.destroy();
            coinButton.appearFactor.destroy(3, 2);
        }
    }
}
