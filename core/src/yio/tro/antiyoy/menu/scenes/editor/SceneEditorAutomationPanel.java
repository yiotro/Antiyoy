package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;

public class SceneEditorAutomationPanel extends AbstractEditorPanel{


    private ButtonYio basePanel;


    public SceneEditorAutomationPanel(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        createBasePanel();

        createIcon(512, 0, "menu/editor/expansion_icon.png", EditorReactions.rbEditorExpandProvinces);
        createIcon(513, 1, "menu/editor/palm_auto.png", EditorReactions.rbEditorExpandTrees);
        createIcon(514, 2, "menu/editor/house_auto.png", EditorReactions.rbEditorPlaceCapitalsOrFarms);
        createIcon(515, 3, "menu/editor/tower_auto.png", EditorReactions.rbEditorPlaceRandomTowers);
        createIcon(516, 4, "menu/editor/scissors.png", EditorReactions.rbEditorCutExcessStuff);

        spawnAllButtons();
    }


    private void createIcon(int id, int place, String texturePath, Reaction rb) {
        ButtonYio iconButton = buttonFactory.getButton(generateSquare(place * 0.07 / YioGdxGame.screenRatio, 0.07, 0.07), id, null);
        menuControllerYio.loadButtonOnce(iconButton, texturePath);
        iconButton.setReaction(rb);
    }


    private void createBasePanel() {
        basePanel = buttonFactory.getButton(generateRectangle(0, 0.07, 1, 0.07), 510, null);
        menuControllerYio.loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);
    }


    private void spawnAllButtons() {
        for (int i = 510; i <= 519; i++) {
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
        destroyByIndex(510, 519);
    }


    @Override
    public boolean isCurrentlyOpened() {
        return basePanel != null && basePanel.appearFactor.get() == 1;
    }
}
