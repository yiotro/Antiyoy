package yio.tro.antiyoy;

import com.badlogic.gdx.Input;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class OnKeyActions {

    private final YioGdxGame yioGdxGame;


    public OnKeyActions(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
    }


    boolean onKeyPressed(int keycode) {
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

        if (keycode == Input.Keys.SPACE) {
            onSpaceButtonPressed();
        }

        if (keycode == Input.Keys.NUM_1) {
            onBuildUnitButtonPressed();
        }

        if (keycode == Input.Keys.NUM_2) {
            onBuildObjectButtonPressed();
        }

        if (keycode == Input.Keys.D) {
            onDebugButtonPressed();
        }

        if (keycode == Input.Keys.Z) {
            yioGdxGame.gameController.cameraController.setTargetZoomLevel(0.9f);
        }

        if (keycode == Input.Keys.NUM_0) {
            onEditLevelButtonPressed();
        }

        if (keycode == Input.Keys.C) {
            openCheatScreen();
        }

        return false;
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

        Scenes.sceneCheatScreen.create();
    }


    private void onEditLevelButtonPressed() {
        yioGdxGame.gameController.getLevelEditor().launchEditCampaignLevelMode();
    }


    private void onDebugButtonPressed() {
        if (!yioGdxGame.gamePaused) yioGdxGame.gameController.debugActions();
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
        if (!yioGdxGame.gamePaused) {
            ButtonYio pauseButton = yioGdxGame.menuControllerYio.getButtonById(30);
            if (pauseButton != null && pauseButton.isVisible()) pauseButton.press();
            else yioGdxGame.menuControllerYio.getButtonById(140).press();
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