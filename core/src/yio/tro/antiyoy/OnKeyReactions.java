package yio.tro.antiyoy;

import com.badlogic.gdx.Input;
import yio.tro.antiyoy.ai.AbstractAi;
import yio.tro.antiyoy.ai.master.AiMaster;
import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.gameplay.tests.TestRestoreLevelState;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.fast_construction.FastConstructionPanel;
import yio.tro.antiyoy.menu.keyboard.BasicKeyboardElement;
import yio.tro.antiyoy.menu.keyboard.NativeKeyboardElement;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class OnKeyReactions {

    private final YioGdxGame yioGdxGame;


    public OnKeyReactions(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
    }


    boolean onKeyPressed(int keycode) {
        if (checkForBasicKeyboardElement(keycode)) return true;
        if (checkForNativeKeyboardElement(keycode)) return true;

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
            case Input.Keys.NUM_9:
                onEditLevelButtonPressed();
                break;
            case Input.Keys.NUM_0:
                yioGdxGame.setGamePaused(true);
                yioGdxGame.gameView.destroy();
                Scenes.sceneDebugTests.create();
                break;
            case Input.Keys.C:
                openCheatScreen();
                break;
            case Input.Keys.S:
                yioGdxGame.menuControllerYio.specialActionController.perform();
                break;
            case Input.Keys.T:
                Scenes.sceneTestScreen.create();
                break;
            case Input.Keys.L:
                yioGdxGame.saveSystem.loadTopSlot();
                break;
            case Input.Keys.I:
                yioGdxGame.gameController.importManager.launchGameFromClipboard();
                break;
            case Input.Keys.X:
                Scenes.sceneCampaignMenu.create();
                yioGdxGame.setGamePaused(true);
                break;
            case Input.Keys.K:
                yioGdxGame.gameController.editorSaveSystem.loadTopSlot();
                break;
            case Input.Keys.U:
                Scenes.sceneUserLevels.create();
                yioGdxGame.setGamePaused(true);
                break;
            case Input.Keys.Q:
                yioGdxGame.pressButtonIfVisible(145);
                break;
            case Input.Keys.P:
                DebugFlags.showAiData = !DebugFlags.showAiData;
                break;
            case Input.Keys.O:
                doExportAiMasterState();
                break;
        }

        checkFastConstructionPanel(keycode);

        return false;
    }


    private void doExportAiMasterState() {
        if (yioGdxGame.gamePaused) {
            TestRestoreLevelState test = new TestRestoreLevelState();
            test.setGameController(yioGdxGame.gameController);
            test.perform();
            return;
        }

        for (AbstractAi abstractAi : yioGdxGame.gameController.getAiList()) {
            if (abstractAi instanceof AiMaster) {
                ((AiMaster) abstractAi).exportLastStateStringToClipboard();
                break;
            }
        }
    }


    private boolean checkForBasicKeyboardElement(int keycode) {
        BasicKeyboardElement basicKeyboardElement = Scenes.sceneBasicKeyboard.basicKeyboardElement;
        if (basicKeyboardElement == null) return false;
        if (basicKeyboardElement.getFactor().get() < 0.2) return false;

        return basicKeyboardElement.onPcKeyPressed(keycode);
    }


    private boolean checkForNativeKeyboardElement(int keycode) {
        NativeKeyboardElement keyboardElement = Scenes.sceneNativeKeyboard.nativeKeyboardElement;
        if (keyboardElement == null) return false;
        if (keyboardElement.getFactor().get() < 0.2) return false;

        keyboardElement.onPcKeyPressed(keycode);
        return true;
    }


    private void checkFastConstructionPanel(int keycode) {
        FastConstructionPanel fastConstructionPanel = Scenes.sceneFastConstructionPanel.fastConstructionPanel;
        if (fastConstructionPanel == null) return;

        fastConstructionPanel.onKeyPressed(keycode);
    }


    private void onEnterPressed() {
        pressIfVisible(Scenes.sceneMainMenu.playButton);
        pressIfVisible(Scenes.sceneChooseGameMode.skirmishButton);
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
        if (!yioGdxGame.gamePaused) {
            yioGdxGame.pressButtonIfVisible(38);
        }
    }


    private void onBuildUnitButtonPressed() {
        if (yioGdxGame.gameController.isInEditorMode()) {
            yioGdxGame.pressButtonIfVisible(142);
            return;
        }

        if (!yioGdxGame.gamePaused) {
            yioGdxGame.pressButtonIfVisible(39);
        }
    }


    private void onSpaceButtonPressed() {
        if (yioGdxGame.gamePaused) return;
        if (yioGdxGame.pressButtonIfVisible(53)) return; // close tutorial tip
        Scenes.sceneGameOverlay.onSpaceButtonPressed();
    }


    private void onBackButtonPressed() {
        if (yioGdxGame == null) return;
        if (yioGdxGame.menuControllerYio == null) return;

        if (!yioGdxGame.gamePaused) {
            pressIfVisible(Scenes.sceneAiOnlyOverlay.inGameMenuButton);
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