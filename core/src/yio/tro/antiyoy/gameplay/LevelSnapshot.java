package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyInfoCondensed;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.replays.Replay;
import yio.tro.antiyoy.gameplay.replays.ReplayManager;
import yio.tro.antiyoy.gameplay.replays.actions.RepAction;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.TimeMeasureYio;

import java.util.ArrayList;
import java.util.ListIterator;


public class LevelSnapshot {

    private final GameController gameController;
    private Hex[][] fieldCopy;
    private ArrayList<Province> provincesCopy;
    private ArrayList<Hex> activeHexesCopy;
    private Hex selectionHex;
    private int fWidth;
    private int fHeight;
    private MatchStatistics matchStatistics;
    private ArrayList<RepAction> replayBuffer;
    private String diplomacyInfo;
    private String namings;
    boolean used;


    public LevelSnapshot(GameController gameController) {
        this.gameController = gameController;

        fieldCopy = null;
        provincesCopy = new ArrayList<>();
        activeHexesCopy = new ArrayList<>();
        matchStatistics = new MatchStatistics();
        if (SettingsManager.replaysEnabled) {
            replayBuffer = new ArrayList<>();
        }

        used = false;
        diplomacyInfo = null;
        namings = null;
    }


    void defaultValues() {
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                fieldCopy[i][j] = null;
            }
        }

        provincesCopy.clear();
        activeHexesCopy.clear();
        selectionHex = null;
        matchStatistics.defaultValues();
        if (replayBuffer != null) {
            replayBuffer.clear();
        }

        fWidth = -1;
        fHeight = -1;
        diplomacyInfo = null;
        namings = null;
    }


    public void take() {
        used = true;
        updateSelectionHex();

        updateMetrics();
        updateFieldCopy();
        updateProvincesCopy();
        updateActiveHexesCopy();
        updateMatchStatistics();
        updateReplayBuffer();
        updateDiplomacyInfo();
    }


    private void updateDiplomacyInfo() {
        if (!GameRules.diplomacyEnabled) return;

        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
        DiplomacyInfoCondensed instance = DiplomacyInfoCondensed.getInstance();
        instance.update(diplomacyManager);

        diplomacyInfo = instance.getFull();
    }


    private void updateReplayBuffer() {
        if (!SettingsManager.replaysEnabled) return;

        replayBuffer.clear();
        ReplayManager replayManager = gameController.replayManager;
        Replay replay = replayManager.getReplay();
        for (RepAction repAction : replay.buffer) {
            replayBuffer.add(repAction);
        }
    }


    private void updateMatchStatistics() {
        matchStatistics.copyFrom(gameController.matchStatistics);
    }


    private void updateActiveHexesCopy() {
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            activeHexesCopy.add(activeHex.getSnapshotCopy());
        }
    }


    private void updateProvincesCopy() {
        for (Province province : gameController.fieldManager.provinces) {
            provincesCopy.add(province.getSnapshotCopy());
        }
    }


    private void updateFieldCopy() {
        checkToCreateFieldCopyMatrix();
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                fieldCopy[i][j] = gameController.fieldManager.field[i][j].getSnapshotCopy();
            }
        }
    }


    private void checkToCreateFieldCopyMatrix() {
        if (fieldCopy != null) return;

        fieldCopy = new Hex[fWidth][fHeight];
    }


    private void updateMetrics() {
        fWidth = gameController.fieldManager.fWidth;
        fHeight = gameController.fieldManager.fHeight;
    }


    private void updateSelectionHex() {
        selectionHex = null;

        if (!gameController.selectionManager.isSomethingSelected()) return;

        Province selectedProvince = gameController.fieldManager.selectedProvince;
        if (selectedProvince == null) return;

        selectionHex = selectedProvince.hexList.get(0);
    }


    private void cleanOutEveryHexInField() {
        gameController.fieldManager.cleanOutAllHexesInField();
    }


    private Hex getHexByCopy(Hex copy) {
        return gameController.fieldManager.field[copy.index1][copy.index2];
    }


    public void recreate() {
        gameController.fieldManager.clearField();
        cleanOutEveryHexInField();
        gameController.fieldManager.clearAnims();

        recreateField();
        recreateActiveHexes();

        gameController.fieldManager.detectProvinces();
        recreateProvinces();

        recreateSelection();
        recreateStatistics();
        recreateReplayBuffer();
        recreateDiplomacy();

        gameController.addAnimHex(gameController.fieldManager.field[0][0]);
        gameController.updateWholeCache = true;
    }


    private void recreateDiplomacy() {
        if (!GameRules.diplomacyEnabled) return;
        if (diplomacyInfo == null) return;

        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
        DiplomacyInfoCondensed instance = DiplomacyInfoCondensed.getInstance();
        instance.setFull(diplomacyInfo);
        instance.apply(diplomacyManager);
        diplomacyManager.updateAllAliveStatuses();
    }


    private void recreateReplayBuffer() {
        if (!SettingsManager.replaysEnabled) return;
        if (GameRules.replayMode) return;

        gameController.replayManager.getReplay().recreateBufferFromSnapshot(replayBuffer);
    }


    private void recreateStatistics() {
        gameController.matchStatistics.copyFrom(matchStatistics);
    }


    private void recreateSelection() {
        gameController.selectionManager.deselectAll();

        if (selectionHex != null) {
            gameController.selectAdjacentHexes(selectionHex);
        }
    }


    private void recreateProvinces() {
        for (Province copy : provincesCopy) {
            Province province = gameController.findProvinceCopy(copy);
            if (province == null) {
//                province.money = 999;
//                province.name = "Bugged province";
                System.out.println();
                System.out.println("Problem in level snapshot.");
                System.out.println("Wasn't been able to find province by hex. Color = " + copy.getFraction());
                System.out.println("copy.getCapital() = " + copy.getCapital());
            } else {
                province.money = copy.money;
                province.updateName();
            }
        }
    }


    private void recreateActiveHexes() {
        ListIterator iterator = gameController.fieldManager.activeHexes.listIterator();
        for (Hex hex : activeHexesCopy) {
            iterator.add(getHexByCopy(hex));
        }
    }


    private void recreateField() {
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                recreateSingleHex(i, j);
            }
        }
    }


    private void recreateSingleHex(int i, int j) {
        Hex currHex = gameController.fieldManager.field[i][j];
        if (!currHex.active) return;

        if (!currHex.sameFraction(fieldCopy[i][j])) {
            currHex.fraction = fieldCopy[i][j].fraction;
            gameController.addAnimHex(currHex);
        }

        if (currHex.selected != fieldCopy[i][j].selected) {
            currHex.selected = fieldCopy[i][j].selected;
            if (!currHex.selected) {
                currHex.selectionFactor.setValues(0, 0);
            }
        }

        if (fieldCopy[i][j].containsObject()) {
            gameController.addSolidObject(currHex, fieldCopy[i][j].objectInside);
        }

        if (fieldCopy[i][j].containsUnit()) {
            Unit copyUnit = fieldCopy[i][j].unit;
            Unit newUnit = gameController.addUnit(currHex, copyUnit.strength);
            if (copyUnit.isReadyToMove() && gameController.isUnitValidForMovement(newUnit)) {
                currHex.unit.setReadyToMove(true);
                currHex.unit.startJumping();
            } else {
                currHex.unit.setReadyToMove(false);
                currHex.unit.stopJumping();
            }
        }
    }


    public void reset() {
        used = false;

        defaultValues();
    }
}
