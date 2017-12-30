package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;

public class SceneEditorHexPanel extends AbstractEditorPanel{


    private ButtonYio basePanel;


    public SceneEditorHexPanel(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        createBasePanel();
        createCancelButton();
        createFilterButton();
        createHexButtons();

        spawnAllButtons();
    }


    private void spawnAllButtons() {
        for (int i = 12350; i < 12359; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            if (buttonYio == null) continue;
            buttonYio.appearFactor.appear(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);
            buttonYio.enableRectangularMask();
            buttonYio.disableTouchAnimation();
            buttonYio.setAnimation(Animation.DOWN);
        }

        for (int i = 150; i <= 158; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            buttonYio.appearFactor.appear(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);
            buttonYio.enableRectangularMask();
            buttonYio.disableTouchAnimation();
            buttonYio.setAnimation(Animation.DOWN);
        }
    }


    private void createHexButtons() {
        ButtonYio hexButton;
        double curVerPos = 0.21;
        double curHorPos = 0.07;
        for (int i = 0; i < 9; i++) {
            if (i == 4) {
                curVerPos = 0.14;
                curHorPos = 0;
            }
            hexButton = buttonFactory.getButton(generateSquare((curHorPos) / YioGdxGame.screenRatio, curVerPos, 0.07), 150 + i, null);
            curHorPos += 0.07;
            hexButton.setReaction(EditorReactions.rbInputModeHex);
            loadHexButtonTexture(hexButton, i);
        }
    }


    private void loadHexButtonTexture(ButtonYio hexButton, int i) {
        switch (i) {
            case 0:
                menuControllerYio.loadButtonOnce(hexButton, "hex_green.png");
                break;
            case 1:
                menuControllerYio.loadButtonOnce(hexButton, "hex_red.png");
                break;
            case 2:
                menuControllerYio.loadButtonOnce(hexButton, "hex_blue.png");
                break;
            case 3:
                menuControllerYio.loadButtonOnce(hexButton, "hex_cyan.png");
                break;
            case 4:
                menuControllerYio.loadButtonOnce(hexButton, "hex_yellow.png");
                break;
            case 5:
                menuControllerYio.loadButtonOnce(hexButton, "hex_color1.png");
                break;
            case 6:
                menuControllerYio.loadButtonOnce(hexButton, "hex_color2.png");
                break;
            case 7:
                menuControllerYio.loadButtonOnce(hexButton, "hex_color3.png");
                break;
            case 8:
                menuControllerYio.loadButtonOnce(hexButton, "random_hex.png");
                break;
        }
    }


    private void createFilterButton() {
        ButtonYio filterButton = buttonFactory.getButton(generateRectangle(0, 0.08, 0.5, 0.05), 12353, null);
        filterButton.setReaction(Reaction.rbSwitchFilterOnlyLand);
        menuControllerYio.getYioGdxGame().gameController.getLevelEditor().updateFilterOnlyLandButton();
    }


    private void createCancelButton() {
        ButtonYio cancelButton = buttonFactory.getButton(generateSquare(0, 0.21, 0.07), 12350, null);
        menuControllerYio.loadButtonOnce(cancelButton, "cancel_icon.png");
        cancelButton.setReaction(EditorReactions.rbInputModeDelete);
    }


    private void createBasePanel() {
        basePanel = buttonFactory.getButton(generateRectangle(0, 0.07, 1, 0.21), 12352, null);
        menuControllerYio.loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);
    }


    @Override
    public void hide() {
        for (int i = 150; i <= 158; i++) {
            menuControllerYio.destroyButton(i);
        }
        for (int i = 12350; i < 12359; i++) {
            menuControllerYio.destroyButton(i);
        }
    }


    @Override
    public boolean isCurrentlyOpened() {
        return basePanel != null && basePanel.appearFactor.get() == 1;
    }
}