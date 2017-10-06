package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.SoundControllerYio;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneGameOverlay extends AbstractScene{


    public SceneGameOverlay(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        if (GameRules.inEditorMode) {
            Scenes.sceneEditorInstruments.create();
            return;
        }

        if (GameRules.aiOnlyMode) {
            Scenes.sceneReplayOverlay.create();
            return;
        }

        menuControllerYio.beginMenuCreation();

        ButtonYio inGameMenuButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.93, 0.07), 30, null);
        menuControllerYio.loadButtonOnce(inGameMenuButton, "menu_icon.png");
        inGameMenuButton.setReactBehavior(ReactBehavior.rbPauseMenu);
        inGameMenuButton.setAnimType(ButtonYio.ANIM_UP);
        inGameMenuButton.enableRectangularMask();
        inGameMenuButton.disableTouchAnimation();

        ButtonYio endTurnButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0, 0.07), 31, null);
        menuControllerYio.loadButtonOnce(endTurnButton, "end_turn.png");
        endTurnButton.setReactBehavior(ReactBehavior.rbEndTurn);
        endTurnButton.setAnimType(ButtonYio.ANIM_DOWN);
        endTurnButton.enableRectangularMask();
        endTurnButton.disableTouchAnimation();
        endTurnButton.setPressSound(SoundControllerYio.soundEndTurn);

        ButtonYio undoButton = buttonFactory.getButton(generateSquare(0, 0, 0.07), 32, null);
        menuControllerYio.loadButtonOnce(undoButton, "undo.png");
        undoButton.setReactBehavior(ReactBehavior.rbUndo);
        undoButton.setAnimType(ButtonYio.ANIM_DOWN);
        undoButton.enableRectangularMask();
        undoButton.setTouchOffset(0.08f * GraphicsYio.width);
        undoButton.disableTouchAnimation();

        menuControllerYio.endMenuCreation();
    }
}