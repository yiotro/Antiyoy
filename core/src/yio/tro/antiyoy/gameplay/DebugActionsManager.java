package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.gameplay.replays.ReplaySaveSystem;
import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.ArrayList;

public class DebugActionsManager {

    private final GameController gameController;


    public DebugActionsManager(GameController gameController) {
        this.gameController = gameController;
    }


    public void debugActions() {
//        doShowActiveHexesString();
//        doCaptureRandomHexes();
        //
        doShowReplayManager();
    }


    private void doShowRuleset() {
        System.out.println();
        System.out.println("DebugActionsManager.doShowRuleset");
        System.out.println("GameRules.slayRules = " + GameRules.slayRules);
        String simpleName = gameController.ruleset.getClass().getSimpleName();
        System.out.println("simpleName = " + simpleName);
    }


    private void checkIfSomeProvincesAreDoubledInList() {
        ArrayList<Province> provinces = gameController.fieldController.provinces;
        for (int i = 0; i < provinces.size(); i++) {
            for (int j = 0; j < provinces.size(); j++) {
                Province A = provinces.get(i);
                Province B = provinces.get(j);
                if (i != j && A.equals(B)) {
                    System.out.println("found shit!");
                }
            }
        }
    }


    private void doReplaySystemStuff() {
        ReplaySaveSystem instance = ReplaySaveSystem.getInstance();
        instance.clearKeys();

        instance.saveReplay(gameController.replayManager.getReplay());
    }


    private void doShowReplayManager() {
        gameController.replayManager.showInConsole();
    }


    private void doShowSnapshots() {
        SnapshotManager snapshotManager = gameController.snapshotManager;
        snapshotManager.showInConsole();
    }


    private void doShowStatistics() {
        gameController.matchStatistics.showInConsole();
    }


    private void doGiveEverybodyLotOfMoney() {
        for (Province province : gameController.fieldController.provinces) {
            province.money += 1000;
        }
    }


    private void doShowAllProvincesMoney() {
        System.out.println("DebugActionsManager.doShowAllProvincesMoney:");
        for (Province province : gameController.fieldController.provinces) {
            String colorName = gameController.fieldController.getColorName(province.getColor());
            System.out.println(colorName + ": " + province.money + " + " + province.getIncome());
        }
        System.out.println();
    }


    private void doShowColorStuff() {
        System.out.println();
        System.out.println("FieldController.NEUTRAL_LANDS_INDEX = " + FieldController.NEUTRAL_LANDS_INDEX);
        System.out.println("colorIndexViewOffset = " + gameController.colorIndexViewOffset);
        System.out.println("GameRules.colorNumber = " + GameRules.colorNumber);
        for (int i = 0; i < GameRules.colorNumber; i++) {
            int colorIndexWithOffset = gameController.ruleset.getColorIndexWithOffset(i);
            System.out.println(i + " -> " + colorIndexWithOffset);
        }
    }


    private void doCaptureRandomHexes() {
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (gameController.getRandom().nextDouble() > 0.5) {
                gameController.fieldController.setHexColor(activeHex, 0);
            }
        }
    }


    private void doShowActiveHexesString() {
        System.out.println("" + gameController.getGameSaver().getActiveHexesString());
    }
}