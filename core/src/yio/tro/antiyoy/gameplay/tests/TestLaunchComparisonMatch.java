package yio.tro.antiyoy.gameplay.tests;

import yio.tro.antiyoy.PlatformType;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.ai.Difficulty;
import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.LevelSize;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.loading.LoadingType;

public class TestLaunchComparisonMatch extends AbstractTest{

    @Override
    public String getName() {
        return "Launch comparison";
    }


    @Override
    protected void execute() {
        LoadingParameters instance = LoadingParameters.getInstance();

        instance.loadingType = LoadingType.skirmish;
        instance.levelSize = LevelSize.MEDIUM;
        instance.playersNumber = 0;
        instance.fractionsQuantity = 5;
        instance.difficulty = 5;
        instance.colorOffset = 0;
        instance.slayRules = false;
        instance.fogOfWar = false;
        instance.diplomacy = false;
        instance.genProvinces = 1;
        instance.treesPercentageIndex = 2;

        DebugFlags.testMode = false;
        LoadingManager.getInstance().startGame(instance);
        gameController.cameraController.setTargetZoomToMax();
        DebugFlags.testMode = true;

        if (YioGdxGame.platformType == PlatformType.pc) {
            DebugFlags.showAiData = true;
        }

        prepareAiForMatch(gameController);
    }


    public static void prepareAiForMatch(GameController gameController) {
        gameController.aiFactory.createCustomAiList(new int[]{
                Difficulty.MASTER,
                Difficulty.BALANCER,
                Difficulty.BALANCER,
                Difficulty.BALANCER,
                Difficulty.BALANCER,
        });
    }
}
