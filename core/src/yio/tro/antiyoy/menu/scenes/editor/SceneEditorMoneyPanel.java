package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.scenes.AbstractScene;

public class SceneEditorMoneyPanel extends AbstractScene{

    public SceneEditorMoneyPanel(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        createBasePanel();

        createIcon(542, 0, "menu/editor/minus.png", ReactBehavior.rbNothing);
        createIcon(543, 1, "menu/editor/plus.png", ReactBehavior.rbNothing);

        menuControllerYio.yioGdxGame.gameController.getLevelEditor().showMoney = true;

        spawnAllButtons();
    }


    private void createIcon(int id, int place, String texturePath, ReactBehavior rb) {
        ButtonYio iconButton = buttonFactory.getButton(generateSquare(place * 0.07 / YioGdxGame.screenRatio, 0.07, 0.07), id, null);
        menuControllerYio.loadButtonOnce(iconButton, texturePath);
        iconButton.setReactBehavior(rb);
    }


    private void createBasePanel() {
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0, 0.07, 1, 0.07), 540, null);
        menuControllerYio.loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);
    }


    private void spawnAllButtons() {
        for (int i = 540; i <= 549; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            if (buttonYio == null) continue;

            buttonYio.appearFactor.beginSpawning(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);
            buttonYio.enableRectangularMask();
            buttonYio.disableTouchAnimation();
            buttonYio.setAnimType(ButtonYio.ANIM_DOWN);
        }
    }


    public void hide() {
        destroyByIndex(540, 549);
    }
}
