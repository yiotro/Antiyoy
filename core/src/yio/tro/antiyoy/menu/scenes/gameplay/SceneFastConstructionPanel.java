package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.SoundControllerYio;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.fast_construction.FastConstructionPanel;

public class SceneFastConstructionPanel extends AbstractGameplayScene {

    public FastConstructionPanel fastConstructionPanel;


    public SceneFastConstructionPanel(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        fastConstructionPanel = null;
    }


    @Override
    public void create() {
        checkToCreateFastConstructionPanel();

        fastConstructionPanel.appear();
        createCoinButton();
    }


    private void createCoinButton() {
        ButtonYio coinButton = menuControllerYio.getButtonById(610);

        if (coinButton == null) { // init
            coinButton = buttonFactory.getButton(generateSquare(0, 0.93, 0.07), 610, null);
            coinButton.setAnimation(Animation.UP);
            coinButton.setPressSound(SoundControllerYio.soundCoin);
            coinButton.enableRectangularMask();
            coinButton.disableTouchAnimation();
        }

        loadCoinButtonTexture(coinButton);
        coinButton.appearFactor.appear(3, 2);
        coinButton.setTouchable(true);
        coinButton.setReaction(Reaction.rbShowColorStats);
    }


    public void checkToReappear() {
        if (!Settings.fastConstruction) return;
        if (fastConstructionPanel.getFactor().get() == 1) return;

        create();
    }


    void loadCoinButtonTexture(ButtonYio coinButton) {
        if (Settings.isShroomArtsEnabled()) {
            menuControllerYio.loadButtonOnce(coinButton, "skins/ant/coin.png");
            return;
        }

        menuControllerYio.loadButtonOnce(coinButton, "coin.png");
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
