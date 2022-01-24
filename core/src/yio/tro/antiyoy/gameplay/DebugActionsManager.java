package yio.tro.antiyoy.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Clipboard;
import yio.tro.antiyoy.KeyboardManager;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.ai.AbstractAi;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.diplomacy.*;
import yio.tro.antiyoy.gameplay.name_generator.CityNameGenerator;
import yio.tro.antiyoy.gameplay.replays.ReplaySaveSystem;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.Yio;

import java.util.ArrayList;
import java.util.Map;

public class DebugActionsManager {

    private final GameController gameController;


    public DebugActionsManager(GameController gameController) {
        this.gameController = gameController;
    }


    public void debugActions() {
        Scenes.sceneAoArticle.create();
    }


    private void doTestTimeToStringConversion() {
        System.out.println();
        System.out.println("DebugActionsManager.doTestTimeToStringConversion");
        for (int i = 0; i < 10; i++) {
            int value = YioGdxGame.random.nextInt(999999);
            System.out.println(value + " -> " + Yio.convertTimeToUnderstandableString(value));
        }
    }


    private void doShowTutorialTip() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Test");
        Scenes.sceneTutorialTip.createTutorialTip(strings);
    }


    private void doShowCameraInConsole() {
        System.out.println();
        System.out.println("DebugActionsManager.doShowCameraInConsole");
        System.out.println("Camera: " + gameController.cameraController.encode());
    }


    public void doDetectAiFractionsBug() {
        ArrayList<AbstractAi> aiList = gameController.getAiList();
        for (int i = 0; i < aiList.size(); i++) {
            AbstractAi abstractAi = aiList.get(i);
            if (i != abstractAi.getFraction()) {
                System.out.println();
                System.out.println("DebugActionsManager.doDetectAiFractionsBug: problem detected");
                System.out.println("i = " + i);
                System.out.println("abstractAi.getFraction() = " + abstractAi.getFraction());
                return;
            }
        }
    }


    public void doDetectIntersectingProvincesBug() {
        System.out.println();
        System.out.println("DebugActionsManager.doDetectIntersectingProvincesBug");
        ArrayList<Province> provinces = gameController.fieldManager.provinces;
        for (int i = 0; i < provinces.size(); i++) {
            Province a = provinces.get(i);
            if (a.getFraction() != 0) continue;
            for (int j = i + 1; j < provinces.size(); j++) {
                Province b = provinces.get(j);
                if (b.getFraction() != 0) continue;
                if (a.intersects(b)) {
                    System.out.println("Detected problem");
                    System.out.println("a = " + a);
                    System.out.println("b = " + b);
                    return;
                }
            }
        }
        System.out.println("No problems detected");
    }


    private void doTestDebts() {
        System.out.println();
        System.out.println("DebugActionsManager.doTestDebts");
        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
        DiplomaticEntity entity1 = diplomacyManager.getEntity(0);
        DiplomaticEntity entity2 = diplomacyManager.getEntity(1);
        diplomacyManager.changeDebt(entity1, entity2, 12);
    }


    private void doCoverWholeMapInFarms() {
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (activeHex.isNeutral()) continue;
            if (!activeHex.isEmpty()) continue;
            activeHex.setObjectInside(Obj.FARM);
        }
    }


    private void doTestCompactMoneyAlgorithm() {
        System.out.println();
        System.out.println("DebugActionsManager.doTestCompactMoneyAlgorithm");
        System.out.println("Yio.getCompactMoneyString(0) = " + Yio.getCompactMoneyString(0));
        System.out.println("Yio.getCompactMoneyString(15) = " + Yio.getCompactMoneyString(15));
        System.out.println("Yio.getCompactMoneyString(600) = " + Yio.getCompactMoneyString(600));
        System.out.println("Yio.getCompactMoneyString(1500) = " + Yio.getCompactMoneyString(1500));
        System.out.println("Yio.getCompactMoneyString(-100) = " + Yio.getCompactMoneyString(-100));
        System.out.println("Yio.getCompactMoneyString(-1500) = " + Yio.getCompactMoneyString(-1500));
        System.out.println("Yio.getCompactMoneyString(50000) = " + Yio.getCompactMoneyString(50000));
        System.out.println("Yio.getCompactMoneyString(1000000) = " + Yio.getCompactMoneyString(1000000));
        System.out.println("Yio.getCompactMoneyString(2500000) = " + Yio.getCompactMoneyString(2500000));
        System.out.println("Yio.getCompactMoneyString(-2500000) = " + Yio.getCompactMoneyString(-2500000));
        System.out.println("Yio.getCompactMoneyString(999999) = " + Yio.getCompactMoneyString(999999));
        System.out.println("Yio.getCompactMoneyString(999999999) = " + Yio.getCompactMoneyString(999999999));
    }


    private void doGiveFirstFractionMoney() {
        for (Province province : gameController.fieldManager.provinces) {
            if (province.getFraction() != 0) continue;
            province.money += 1000;
        }
    }


    private void doShowProvinceNames() {
        System.out.println();
        System.out.println("DebugActionsManager.doShowProvinceNames");
        for (Province province : gameController.fieldManager.provinces) {
            System.out.println("- " + province.name);
        }
    }


    private void doShowSelectedProvinceIncome() {
        System.out.println();
        System.out.println("DebugActionsManager.doShowSelectedProvinceIncome");
        Province selectedProvince = gameController.fieldManager.selectedProvince;
        System.out.println("selectedProvince = " + selectedProvince);
        System.out.println("selectedProvince.getBalance() = " + selectedProvince.getProfit());
        System.out.println("selectedProvince.getIncome() = " + selectedProvince.getIncome());
        System.out.println("selectedProvince.getDotations() = " + selectedProvince.getDotations());
        System.out.println("selectedProvince.getTaxes() = " + selectedProvince.getTaxes());
        System.out.println("selectedProvince.getTowerTaxes() = " + selectedProvince.getTowerTaxes());
        System.out.println("selectedProvince.getUnitsTaxes() = " + selectedProvince.getUnitsTaxes());
    }


    public void doSaveFullLevelToClipboard() {
        String prefsName = "yio.tro.antiyoy.debug_prefs";
        gameController.gameSaver.saveGame(prefsName);

        Preferences preferences = Gdx.app.getPreferences(prefsName);
        Map<String, ?> stringMap = preferences.get();
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, ?> entry : stringMap.entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        String result = builder.toString();

        System.out.println();
        System.out.println("DebugActionsManager.doSaveFullLevelToClipboard");
        System.out.println(result);

        Clipboard clipboard = Gdx.app.getClipboard();
        clipboard.setContents(result);
    }


    private void doSendSomeMailsToPlayer() {
        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
        DiplomaticEntity humanEntity = diplomacyManager.getEntity(0);
        DiplomaticEntity aiEntity = diplomacyManager.getEntity(1);
        for (int i = 0; i < 10; i++) {
            DiplomaticMessage diplomaticMessage = diplomacyManager.log.addMessage(DipMessageType.message, aiEntity, humanEntity);
            if (diplomaticMessage == null) continue;
            diplomaticMessage.setArg1("Debug message");
        }
    }


    private void doEnableTmEditProvinces() {
        //
    }


    private void doShowIncomeGraph() {
        Scenes.sceneIncomeGraph.create();
    }


    private void doShowEntityNames() {
        System.out.println();
        System.out.println("DebugActionsManager.doShowEntityNames");
        for (DiplomaticEntity entity : gameController.fieldManager.diplomacyManager.entities) {
            System.out.println("entity = " + entity);
        }
    }


    private void doShowDipMessage() {
        Scenes.sceneDipMessage.showMessage("Debug", "Some random message");
    }


    private void doShowEditorProvinces() {
        gameController.levelEditorManager.editorProvinceManager.showProvincesInConsole();
    }


    private void doForceException() {
        Yio.forceException();
    }


    private void doKillRandomFriend() {
        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
        DiplomaticEntity mainEntity = diplomacyManager.getMainEntity();

        DiplomaticEntity friend = null;
        for (DiplomaticEntity entity : diplomacyManager.entities) {
            if (entity == mainEntity) continue;
            if (mainEntity.getRelation(entity) != DiplomaticRelation.FRIEND) continue;

            friend = entity;
            break;
        }

        if (friend == null) return;

        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (activeHex.fraction != friend.fraction) continue;

            gameController.fieldManager.setHexFraction(activeHex, GameRules.NEUTRAL_FRACTION);
        }
    }


    private void doTestAreaSelectionMode() {
        gameController.fieldManager.diplomacyManager.enableAreaSelectionMode(-1);
    }


    private void doForceDiplomaticLoss() {
        if (!GameRules.diplomacyEnabled) return;

        DiplomaticEntity randomAiDiplomaticEntity = getRandomAiDiplomaticEntity();
        if (randomAiDiplomaticEntity == null) return;

        doForceWinForDiplomaticEntity(randomAiDiplomaticEntity);
        Scenes.sceneNotification.show("Forced diplomatic loss");
    }


    private void doForceWinForDiplomaticEntity(DiplomaticEntity winner) {
        for (DiplomaticEntity entity : getDiplomacyManager().entities) {
            if (!entity.alive) continue;
            if (entity == winner) continue;
            if (entity.getRelation(winner) == DiplomaticRelation.FRIEND) continue;

            getDiplomacyManager().setRelation(entity, winner, DiplomaticRelation.FRIEND);
        }
    }


    private DiplomaticEntity getRandomAiDiplomaticEntity() {
        DiplomacyManager diplomacyManager = getDiplomacyManager();
        for (DiplomaticEntity entity : diplomacyManager.entities) {
            if (entity.isHuman()) continue;
            if (!entity.alive) continue;
            return entity;
        }
        return null;
    }


    private void doForceLevelSkipAvailability() {
        gameController.skipLevelManager.forceSkipAvailability();
        Scenes.sceneNotification.show("Level skip forced");
    }


    public void doShiftFractionsInEditorMode() {
        gameController.colorsManager.doShiftFractionsInEditorMode();
    }


    private void doShowKeyboard() {
        KeyboardManager.getInstance().apply("Debug", new AbstractKbReaction() {
            @Override
            public void onInputFromKeyboardReceived(String input) {
                System.out.println("input = " + input);
            }
        });
    }


    private void doShowSuspiciousStuff() {
        System.out.println();
        System.out.println("DebugActionsManager.doShowSuspiciousStuff");
        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;

        ArrayList<DiplomaticCooldown> cooldowns = diplomacyManager.cooldowns;
        System.out.println("cooldowns.size() = " + cooldowns.size());

        gameController.snapshotManager.showInConsole();
    }


    private void doTestStringBuilder() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            builder.append(i).append(" ");
        }

        String one = builder.toString();

        builder.setLength(0);
        ;
        for (int i = 0; i < 5; i++) {
            builder.append(3 * i).append(" ");
        }

        String two = builder.toString();

        System.out.println();
        System.out.println("DebugActionsManager.doTestStringBuilder");
        System.out.println("one = " + one);
        System.out.println("two = " + two);
    }


    private void doTakeSnapshot() {
        gameController.takeSnapshot();
    }


    private DiplomacyManager getDiplomacyManager() {
        return gameController.fieldManager.diplomacyManager;
    }


    private void doShowDiplomaticCooldownsInConsole() {
        getDiplomacyManager().showCooldownsInConsole(gameController.turn);
    }


    private void doShowGameRules() {
        System.out.println();
        System.out.println("DebugActionsManager.doShowGameRules");
        System.out.println("GameRules.campaignMode = " + GameRules.campaignMode);
        System.out.println("CampaignProgressManager.getInstance().currentLevelIndex = " + CampaignProgressManager.getInstance().currentLevelIndex);
        System.out.println("GameRules.slayRules = " + GameRules.slayRules);
        System.out.println("GameRules.userLevelMode = " + GameRules.userLevelMode);
        System.out.println("GameRules.editorFog = " + GameRules.editorFog);
        System.out.println("GameRules.editorDiplomacy = " + GameRules.editorDiplomacy);
    }


    private void doShowDiplomaticContracts() {
        getDiplomacyManager().showContractsInConsole(0);
    }


    private void doGenerateMultipleCityNames() {
        System.out.println();
        System.out.println("DebugActionsManager.generateMultipleCityNames");

        CityNameGenerator instance = CityNameGenerator.getInstance();
        FieldManager fieldManager = gameController.fieldManager;
        ArrayList<Hex> activeHexes = fieldManager.activeHexes;
        for (int i = 0; i < 10; i++) {
            Hex randomHex = activeHexes.get(YioGdxGame.random.nextInt(activeHexes.size()));
            String name = instance.generateName(randomHex);
            System.out.println("- " + name);
        }

        ArrayList<String> allNames = new ArrayList<>();
        for (Hex activeHex : activeHexes) {
            allNames.add(instance.generateName(activeHex));
        }

        int duplicates = 0;
        boolean hasDuplicates = false;
        for (int i = 0; i < allNames.size(); i++) {
            for (int j = i + 1; j < allNames.size(); j++) {
                if (allNames.get(i).equals(allNames.get(j))) {
                    duplicates++;
                }
            }
        }
        hasDuplicates = (duplicates > 0);

        System.out.println("hasDuplicates = " + hasDuplicates);
        if (hasDuplicates) {
            System.out.println("duplicates = " + duplicates);
        }
    }


    private void doShowNotification() {
        Scenes.sceneNotification.show("debug notification");
    }


    private void doShowSnapshotsInConsole() {
        gameController.snapshotManager.showInConsole();
    }


    private void doShowRuleset() {
        System.out.println();
        System.out.println("DebugActionsManager.doShowRuleset");
        System.out.println("GameRules.slayRules = " + GameRules.slayRules);
        String simpleName = gameController.ruleset.getClass().getSimpleName();
        System.out.println("simpleName = " + simpleName);
    }


    private void checkIfSomeProvincesAreDoubledInList() {
        ArrayList<Province> provinces = gameController.fieldManager.provinces;
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


    private void doSaveReplay() {
        ReplaySaveSystem instance = ReplaySaveSystem.getInstance();
        instance.saveReplay(gameController.replayManager.getReplay());
        Scenes.sceneNotification.show("Debug replay saved");
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
        for (Province province : gameController.fieldManager.provinces) {
            province.money += 1000;
        }
    }


    private void doShowColorStuff() {
        gameController.colorsManager.doShowInConsole();
    }


    private void doForceWin() {
        doCaptureRandomHexes();
    }


    private void doCaptureRandomHexes() {
        Scenes.sceneCheatsMenu.rbCaptureHexes.perform(null);
    }


    private boolean hasAtLeastOnePlayerHexNearby(Hex hex) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (adjacentHex == null) continue;
            if (!adjacentHex.active) continue;
            if (!adjacentHex.sameFraction(0)) continue;

            return true;
        }

        return false;
    }

}