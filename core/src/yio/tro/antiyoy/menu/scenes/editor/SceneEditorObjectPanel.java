package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;

public class SceneEditorObjectPanel extends AbstractEditorPanel{


    private ButtonYio basePanel;


    public SceneEditorObjectPanel(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        basePanel = buttonFactory.getButton(generateRectangle(0, 0.07, 1, 0.07), 160, null);
        menuControllerYio.loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);

        ButtonYio cancelButton = buttonFactory.getButton(generateSquare(0, 0.07, 0.07), 162, null);
        menuControllerYio.loadButtonOnce(cancelButton, "cancel_icon.png");
        cancelButton.setReaction(EditorReactions.rbInputModeSetObject);

//        ButtonYio hideButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.07, 0.07), 161, null);
//        menuControllerYio.loadButtonOnce(hideButton, "hide_panel.png");
//        hideButton.setReaction(EditorReactions.rbHideObjectPanel);

        ButtonYio objectButton;
        for (int i = 0; i < 6; i++) {
            objectButton = buttonFactory.getButton(generateSquare((0.07 + 0.07 * i) / YioGdxGame.screenRatio, 0.07, 0.07), 163 + i, null);
            objectButton.setReaction(EditorReactions.rbInputModeSetObject);
            switch (i) {
                case 0:
                    menuControllerYio.loadButtonOnce(objectButton, "field_elements/pine_low.png");
                    break;
                case 1:
                    menuControllerYio.loadButtonOnce(objectButton, "field_elements/palm_low.png");
                    break;
                case 2:
                    menuControllerYio.loadButtonOnce(objectButton, "field_elements/house_low.png");
                    break;
                case 3:
                    menuControllerYio.loadButtonOnce(objectButton, "field_elements/tower_low.png");
                    break;
                case 4:
                    menuControllerYio.loadButtonOnce(objectButton, "field_elements/man0_low.png");
                    break;
                case 5:
                    menuControllerYio.loadButtonOnce(objectButton, "field_elements/man1_low.png");
                    break;
            }
        }

        for (int i = 160; i <= 168; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            if (buttonYio == null) continue;

            buttonYio.appearFactor.appear(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);
            buttonYio.enableRectangularMask();
            buttonYio.disableTouchAnimation();
            buttonYio.setAnimation(Animation.DOWN);
        }
    }


    @Override
    public void hide() {
        for (int i = 160; i <= 168; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            if (buttonYio == null) continue;
            buttonYio.destroy();
        }
    }


    @Override
    public boolean isCurrentlyOpened() {
        return basePanel != null && basePanel.appearFactor.get() == 1;
    }
}