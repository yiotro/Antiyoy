package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.EndTurnButtonElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.AbstractScene;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneGameOverlay extends AbstractScene {


    public EndTurnButtonElement endTurnButtonElement;


    public SceneGameOverlay(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        endTurnButtonElement = null;
    }


    @Override
    public void create() {
        if (GameRules.inEditorMode) {
            Scenes.sceneEditorOverlay.create();
            return;
        }

        if (GameRules.aiOnlyMode) {
            Scenes.sceneAiOnlyOverlay.create();
            return;
        }

        menuControllerYio.beginMenuCreation();

        ButtonYio inGameMenuButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.93, 0.07), 30, null);
        menuControllerYio.loadButtonOnce(inGameMenuButton, "menu_icon.png");
        inGameMenuButton.setReaction(Reaction.rbPauseMenu);
        inGameMenuButton.setAnimation(Animation.up);
        inGameMenuButton.enableRectangularMask();

        createEndTurnButton();

        ButtonYio undoButton = buttonFactory.getButton(generateSquare(0, 0, 0.07), 32, null);
        menuControllerYio.loadButtonOnce(undoButton, "undo.png");
        undoButton.setReaction(Reaction.rbUndo);
        undoButton.setAnimation(Animation.down);
        undoButton.enableRectangularMask();
        undoButton.setTouchOffset(0.08f * GraphicsYio.width);

        menuControllerYio.endMenuCreation();
    }


    private void createEndTurnButton() {
        initEndTurnButton();
        endTurnButtonElement.appear();
    }


    private void initEndTurnButton() {
        if (endTurnButtonElement != null) return;
        endTurnButtonElement = new EndTurnButtonElement(menuControllerYio);
        endTurnButtonElement.setPosition(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0, 0.07));
        endTurnButtonElement.setAnimation(Animation.down);
        menuControllerYio.addElementToScene(endTurnButtonElement);
    }


    public void onSpaceButtonPressed() {
        if (endTurnButtonElement == null) return;
        endTurnButtonElement.onSpaceButtonPressed();
    }


    private void createEndTurnButtonOld() {
        ButtonYio endTurnButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0, 0.07), 31, null);
        menuControllerYio.loadButtonOnce(endTurnButton, "end_turn.png");
        endTurnButton.setReaction(Reaction.rbEndTurn);
        endTurnButton.setAnimation(Animation.down);
        endTurnButton.enableRectangularMask();
        endTurnButton.setPressSound(SoundManagerYio.soundEndTurn);
    }
}