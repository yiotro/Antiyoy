package yio.tro.antiyoy.gameplay.tests;

import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.ai.Difficulty;
import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.gameplay.LevelSize;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.loading.LoadingType;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.Yio;

import java.util.ArrayList;

public class TestAiComparison extends AbstractTest {

    int results[];


    @Override
    public String getName() {
        return "AI Comparison";
    }


    @Override
    protected void execute() {
        System.out.println();
        System.out.println("Test started: " + quantity);
        results = new int[getPlayersNumber()];

        for (int i = 0; i < quantity; i++) {
            showStep(i);
            launchMatch();
            prepareAiForMatch();
            simulateMatch();
            if (DebugFlags.testWinner == -1) continue;

            results[DebugFlags.testWinner]++;
        }

        finish();
    }


    private void prepareAiForMatch() {
        gameController.aiFactory.createCustomAiList(new int[]{
                Difficulty.BALANCER,
                Difficulty.BALANCER,
                Difficulty.BALANCER,
                Difficulty.BALANCER,
                Difficulty.BALANCER,
        });
//        gameController.getAiList().set(0, new AiBalancerGenericRestored(gameController, 0));
    }


    protected void showStep(int i) {
        if (i == 0) return;
        if (i % 100 != 0) return;

        if (i % 1000 != 0) {
            System.out.println("step " + i);
        } else {
            System.out.println("step " + i + "     =========   ");
        }
    }


    private void finish() {
        disableTestMode();
        showResultsInConsole();
        gameController.yioGdxGame.setGamePaused(true);
        gameController.yioGdxGame.gameView.destroy();
        Scenes.sceneTestResults.create();
        ArrayList<String> resultsArray = getResultsArray();
        resultsArray.add(0, "Results:");
        Scenes.sceneTestResults.renderResults(resultsArray);

        if (quantity > 100) {
            SoundManagerYio.playSound(SoundManagerYio.soundHoldToMarch);
        }
    }


    private void showResultsInConsole() {
        System.out.println();
        System.out.println("TestAiComparison.showResultsInConsole");
        System.out.println("Time passed: " + getPassedTime());
        for (String s : getResultsArray()) {
            System.out.println(s);
        }
    }


    private ArrayList<String> getResultsArray() {
        ArrayList<String> list = new ArrayList<>();

        int minResult = getMinResult();
        float ratio = 1;
        for (int i = 0; i < results.length; i++) {
            if (minResult > 0) {
                ratio = (float) results[i] / (float) minResult;
            }
            String fractionString = i + "";
            list.add(fractionString + ": " + results[i] + " (" + Yio.roundUp(ratio, 2) + ")");
        }

        return list;
    }


    private int getMinResult() {
        int min = -1;

        for (int result : results) {
            if (min == -1 || result < min) {
                min = result;
            }
        }

        return min;
    }


    private void simulateMatch() {
        gameController.yioGdxGame.gamePaused = false;
        DebugFlags.testWinner = -1;

        int c = results.length * 200;
        while (DebugFlags.testWinner == -1) {
            gameController.move();

            c--;
            if (c == 0) break;
        }
    }


    private int getPlayersNumber() {
        return 5;
    }


    private void launchMatch() {
        LoadingParameters instance = LoadingParameters.getInstance();

        instance.loadingType = LoadingType.skirmish;
        instance.levelSize = LevelSize.MEDIUM;
        instance.playersNumber = 0;
        instance.fractionsQuantity = results.length;
        instance.difficulty = Difficulty.BALANCER;
        instance.colorOffset = 0;
        instance.slayRules = false;
        instance.fogOfWar = false;
        instance.diplomacy = false;
        instance.genProvinces = 0;
        instance.treesPercentageIndex = 2;

        LoadingManager.getInstance().startGame(instance);
    }
}
