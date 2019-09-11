package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.fast_construction.FastConstructionPanel;

public class SceneFastConstructionPanel extends AbstractModalScene {

    public FastConstructionPanel fastConstructionPanel;
    private ButtonYio coinButton;


    public SceneFastConstructionPanel(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        coinButton = null;
        fastConstructionPanel = null;
    }


    @Override
    public void create() {
        checkToCreateFastConstructionPanel();

        fastConstructionPanel.appear();
        createCoinButton();
    }


    private void createCoinButton() {
        coinButton = menuControllerYio.getButtonById(610);

        if (coinButton == null) { // init
            coinButton = buttonFactory.getButton(generateSquare(0, 0.93, 0.07), 610, null);
            coinButton.setAnimation(Animation.up);
            coinButton.setPressSound(SoundManagerYio.soundCoin);
            coinButton.enableRectangularMask();
        }

        loadCoinButtonTexture(coinButton);
        coinButton.appearFactor.appear(3, 2);
        coinButton.setTouchable(true);
        coinButton.setReaction(Reaction.rbShowColorStats);
    }


    public void onSkinChanged() {
        if (coinButton != null) {
            coinButton.resetTexture();
        }
    }


    public void checkToReappear() {
        if (!SettingsManager.fastConstructionEnabled) return;
        if (fastConstructionPanel.getFactor().get() == 1) return;

        create();
    }


    void loadCoinButtonTexture(ButtonYio coinButton) {
        menuControllerYio.loadButtonOnce(coinButton, menuControllerYio.yioGdxGame.skinManager.getCoinTexturePath());
    }


    private void checkToCreateFastConstructionPanel() {
        if (fastConstructionPanel != null) return;

        fastConstructionPanel = new FastConstructionPanel(menuControllerYio, -1);
        menuControllerYio.addElementToScene(fastConstructionPanel);
    }


    @Override
    public void hide() {
        menuControllerYio.destroyButton(610);

        if (fastConstructionPanel != null) {
            fastConstructionPanel.destroy();
        }
    }
}
