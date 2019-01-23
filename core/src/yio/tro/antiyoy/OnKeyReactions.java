package yio.tro.antiyoy;

import com.badlogic.gdx.Input;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.fast_construction.FastConstructionPanel;
import yio.tro.antiyoy.menu.keyboard.KeyboardElement;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class OnKeyReactions {

    private final YioGdxGame yioGdxGame;


    public OnKeyReactions(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
    }


    boolean onKeyPressed(int keycode) {
        if (checkForKeyboardElement(keycode)) return true;

        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            onBackButtonPressed();
        }

        if (keycode == Input.Keys.ENTER) {
            onEnterPressed();
        }

        // wtf is this? :)
        if (keycode == Input.Keys.Q) {
            if (!yioGdxGame.gamePaused) {
                yioGdxGame.pressButtonIfVisible(32);
                yioGdxGame.pressButtonIfVisible(53); // skip tutorial tip
            }
        }

        switch (keycode) {
            case Input.Keys.SPACE:
                onSpaceButtonPressed();
                break;
            case Input.Keys.NUM_1:
                onBuildUnitButtonPressed();
                break;
            case Input.Keys.NUM_2:
                onBuildObjectButtonPressed();
                break;
            case Input.Keys.D:
                onDebugButtonPressed();
                break;
            case Input.Keys.Z:
                yioGdxGame.gameController.cameraController.setTargetZoomLevel(0.9f);
                break;
            case Input.Keys.NUM_0:
                onEditLevelButtonPressed();
                break;
            case Input.Keys.C:
                openCheatScreen();
                break;
            case Input.Keys.S:
                yioGdxGame.menuControllerYio.specialActionController.perform();
                break;
            case Input.Keys.L:
                yioGdxGame.saveSystem.loadTopSlot();
                break;
            case Input.Keys.I:
                yioGdxGame.gameController.getLevelEditor().importFromClipboardToExtraSlot();
                break;
            case Input.Keys.X:
                Scenes.sceneCampaignMenu.create();
                yioGdxGame.setGamePaused(true);
                break;
            case Input.Keys.U:
                Scenes.sceneUserLevels.create();
                yioGdxGame.setGamePaused(true);
                break;
        }

        checkFastConstructionPanel(keycode);

        return false;
    }


    private boolean checkForKeyboardElement(int keycode) {
        KeyboardElement keyboardElement = Scenes.sceneKeyboard.keyboardElement;
        if (keyboardElement == null) return false;
        if (keyboardElement.getFactor().get() < 0.2) return false;

        return keyboardElement.onPcKeyPressed(keycode);
    }


    private void checkFastConstructionPanel(int keycode) {
        FastConstructionPanel fastConstructionPanel = Scenes.sceneFastConstructionPanel.fastConstructionPanel;
        if (fastConstructionPanel == null) return;

        fastConstructionPanel.onKeyPressed(keycode);
    }


    private void onEnterPressed() {
        pressIfVisible(Scenes.sceneMainMenu.playButton);
        pressIfVisible(Scenes.sceneChoodeGameModeMenu.skirmishButton);
        pressIfVisible(Scenes.sceneSkirmishMenu.startButton);

        pressIfVisible(Scenes.scenePauseMenu.resumeButton);
    }


    private void pressIfVisible(ButtonYio buttonYio) {
        if (buttonYio == null) return;
        if (!buttonYio.isVisible()) return;

        buttonYio.press();
    }


    private void openCheatScreen() {
        yioGdxGame.setGamePaused(true);

        Scenes.sceneSecretScreen.create();
    }


    private void onEditLevelButtonPressed() {
        yioGdxGame.gameController.getLevelEditor().launchEditLevelMode();
    }


    private void onDebugButtonPressed() {
        yioGdxGame.gameController.debugActions();
    }


    private void onBuildObjectButtonPressed() {
        if (!yioGdxGame.gamePaused) yioGdxGame.pressButtonIfVisible(38);
    }


    private void onBuildUnitButtonPressed() {
        if (!yioGdxGame.gamePaused) yioGdxGame.pressButtonIfVisible(39);
    }


    private void onSpaceButtonPressed() {
        if (!yioGdxGame.gamePaused) {
            yioGdxGame.pressButtonIfVisible(31);
            yioGdxGame.pressButtonIfVisible(53); // close tip
        }
    }


    private void onBackButtonPressed() {
        if (yioGdxGame == null) return;
        if (yioGdxGame.menuControllerYio == null) return;

        if (!yioGdxGame.gamePaused) {
            pressIfVisible(Scenes.sceneReplayOverlay.inGameMenuButton);
            ButtonYio pauseButton = yioGdxGame.menuControllerYio.getButtonById(30);
            if (pauseButton != null && pauseButton.isVisible()) {
                pauseButton.press();
            } else {
                ButtonYio buttonById = yioGdxGame.menuControllerYio.getButtonById(140);
                if (buttonById != null) {
                    buttonById.press();
                }
            }
        } else {
            yioGdxGame.pressButtonIfVisible(42);
            yioGdxGame.pressButtonIfVisible(1);

            // back buttons
            for (Integer integer : yioGdxGame.backButtonIds) {
                yioGdxGame.pressButtonIfVisible(integer);
            }
        }
    }
}