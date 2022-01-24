package yio.tro.antiyoy.gameplay.tests;

import yio.tro.antiyoy.PlatformType;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.ai.Difficulty;
import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.data_storage.DecodeManager;
import yio.tro.antiyoy.gameplay.loading.LoadingType;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class TestRestoreLevelState extends AbstractTest{

    @Override
    public String getName() {
        return "Restore state";
    }


    private String getLevelStateString() {
        // press 'O' in game to retrieve level state string
        return "1.01 1.61 1.89/antiyoy_level_code#level_size:4#general:5 0 5#map_name:Слот -1#editor_info:1 false false false #land:47 3 3 3,47 2 3 6,46 4 3 6,46 2 0 0,45 3 0 0,45 2 0 4,44 8 0 0,44 6 0 0,44 3 0 0,44 2 0 0,43 15 3 3,43 14 3 6,43 8 0 0,43 6 0 0,43 4 0 0,43 3 0 0,43 2 0 0,42 15 3 6,42 8 0 0,42 7 0 0,42 5 0 0,42 4 0 0,42 2 0 0,41 16 3 0,41 8 0 0,41 7 0 0,41 6 0 0,41 5 0 0,41 4 0 0,41 3 0 0,40 17 3 0,40 15 0 0,40 14 0 0,40 12 0 0,40 9 0 0,40 8 0 0,40 7 0 4,40 6 0 0,40 5 0 0,40 4 0 0,40 3 0 0,40 2 0 0,39 21 2 0,39 19 3 0,39 18 3 4,39 17 0 0,39 16 0 0,39 15 0 0,39 14 0 0,39 12 0 0,39 9 0 4,39 8 0 0,39 7 0 0,39 6 0 0,39 5 0 0,39 4 0 0,39 2 0 0,38 21 2 0,38 18 0 0,38 17 0 0,38 16 0 0,38 14 0 0,38 12 0 0,38 10 0 0,38 8 0 0,38 7 0 0,38 6 0 0,38 5 0 0,38 4 0 0,37 22 2 0,37 21 2 0,37 20 2 0,37 19 2 0,37 18 2 0,37 17 0 0,37 16 0 0,37 15 0 0,37 14 0 0,37 13 0 0,37 11 0 4,37 10 0 0,37 9 0 4,37 8 0 0,37 7 0 0,37 6 0 0,37 5 0 0,37 4 0 0,37 3 0 6,37 2 0 6,36 23 2 0,36 21 2 0,36 20 2 4,36 19 2 0,36 18 2 0,36 17 0 0,36 16 0 0,36 15 0 0,36 14 0 0,36 13 0 0,36 12 0 0,36 11 0 0,36 10 0 0,36 9 0 0,36 8 0 0,36 7 0 0,36 6 0 0,36 5 0 0,36 4 0 7,36 2 0 6,35 24 2 0,35 23 2 0,35 22 2 0,35 20 2 0,35 19 2 0,35 18 2 4,35 17 0 0,35 16 0 0,35 15 0 0,35 14 0 4,35 13 0 0,35 12 0 4,35 11 0 0,35 10 0 0,35 9 0 4,35 8 0 0,35 7 0 4,35 6 0 0,35 5 0 0,35 4 0 0,35 3 0 6,35 2 0 6,34 22 2 0,34 20 2 4,34 19 2 0,34 18 2 0,34 16 0 4,34 15 0 0,34 14 0 0,34 13 0 0,34 12 0 0,34 11 0 0,34 10 0 0,34 9 0 0,34 8 0 0,34 7 0 0,34 6 0 0,34 5 0 0,34 4 0 0,34 3 0 6,34 2 0 6,33 22 2 0,33 20 2 0,33 18 2 4,33 16 0 0,33 14 0 0,33 13 0 0,33 12 0 0,33 11 0 4,33 10 0 0,33 9 0 0,33 8 0 0,33 7 0 0,33 6 0 0,33 5 0 0,33 4 0 0,33 3 0 3,33 2 0 6,32 22 2 0,32 21 2 4,32 20 2 0,32 18 2 0,32 16 0 0,32 15 0 0,32 14 0 0,32 13 0 4,32 12 0 0,32 11 0 0,32 10 0 4,32 9 0 0,32 7 0 0,32 6 0 0,32 5 0 0,32 4 0 6,32 3 0 6,31 23 2 0,31 22 2 0,31 20 2 0,31 19 2 0,31 16 0 4,31 15 0 0,31 14 0 0,31 13 0 0,31 12 0 0,31 11 0 0,31 10 0 0,31 9 0 0,31 7 0 0,31 6 0 0,31 5 0 0,31 4 0 6,30 20 2 0,30 19 2 6,30 17 0 0,30 16 0 0,30 15 0 0,30 12 0 0,30 11 0 4,30 10 0 0,30 9 0 4,30 8 0 0,30 7 0 6,30 6 0 6,30 5 0 6,29 20 2 6,29 18 0 0,29 17 0 0,29 16 0 0,29 15 0 0,29 14 0 4,29 13 0 0,29 12 0 0,29 11 0 0,29 10 0 0,29 9 0 0,29 8 0 4,29 7 0 6,29 6 0 6,29 5 0 6,29 4 0 6,29 3 0 6,28 21 2 6,28 18 0 0,28 17 0 0,28 16 0 0,28 15 0 0,28 14 0 0,28 13 0 0,28 12 0 0,28 5 0 6,28 4 0 6,27 22 2 6,27 21 2 4,27 20 2 0,27 19 4 4,27 18 2 0,27 17 0 4,27 16 0 0,27 15 0 0,27 14 0 4,27 13 0 0,27 12 0 0,26 23 2 6,26 21 2 0,26 20 4 3,26 19 2 4,26 18 2 0,26 17 4 4,26 16 4 3,26 14 0 0,26 13 0 0,26 12 0 0,26 11 0 0,25 24 2 6,25 23 2 0,25 22 2 0,25 21 2 4,25 20 2 0,25 19 2 0,25 17 4 6,25 16 0 0,25 15 0 0,25 12 0 0,24 25 2 6,24 23 2 0,24 22 2 0,24 21 2 0,24 20 2 0,24 19 2 0,24 16 0 0,24 14 0 0,24 13 0 0,24 12 0 0,23 25 2 6,23 24 2 0,23 23 2 0,23 22 2 0,23 21 2 0,23 20 2 4,23 19 2 0,23 15 0 0,23 12 0 0,22 25 2 6,22 24 2 0,22 23 2 4,22 21 2 0,22 19 2 0,21 25 2 6,21 23 2 6,21 19 2 0,20 25 2 6,20 23 2 6,19 25 2 6,19 24 2 3,19 23 2 6,18 23 2 6,#units:40 17 1 false,40 15 3 true,40 14 1 true,40 12 1 true,39 17 3 true,39 12 4 true,38 18 3 true,38 14 1 true,37 14 2 true,36 18 4 false,35 17 3 true,35 15 3 true,27 20 2 false,27 18 4 false,26 18 4 false,#provinces:#relations:#coalitions:temporary#messages:#goal:def 0#real_money:47 3 173,26 20 7,26 16 67,43 15 3,19 24 21,33 3 140,#";
    }


    @Override
    protected void execute() {
        String levelStateString = getLevelStateString();
        int indexOf = levelStateString.indexOf("/");
        String cameraState = levelStateString.substring(0, indexOf);
        String levelCode = levelStateString.substring(indexOf + 1);

        DebugFlags.testMode = false;
        gameController.importManager.launchGame(LoadingType.user_level, levelCode, null);
        gameController.cameraController.decode(cameraState);
        gameController.yioGdxGame.gameView.updateAnimationTexture();
        applyRealMoney(levelCode);
        DebugFlags.testMode = true;
        GameRules.aiOnlyMode = true;
        Scenes.sceneGameOverlay.create();

        if (YioGdxGame.platformType == PlatformType.pc) {
            DebugFlags.showAiData = true;
            DebugFlags.closerLookMode = true;
            DebugFlags.showDetailedAiMasterInfo = true;
        }

        prepareAiForMasterOnlyMatch(gameController);
    }


    public static void prepareAiForMasterOnlyMatch(GameController gameController) {
        int[] array = new int[GameRules.fractionsQuantity];
        for (int i = 0; i < array.length; i++) {
            array[i] = Difficulty.MASTER;
        }
        gameController.aiFactory.createCustomAiList(array);
    }


    private void applyRealMoney(String levelCode) {
        DecodeManager decodeManager = gameController.decodeManager;
        decodeManager.setSource(levelCode);
        decodeManager.applyRealMoney();
    }

}
