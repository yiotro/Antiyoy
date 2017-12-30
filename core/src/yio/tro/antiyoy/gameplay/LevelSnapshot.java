package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.gameplay.replays.Replay;
import yio.tro.antiyoy.gameplay.replays.ReplayManager;
import yio.tro.antiyoy.gameplay.replays.actions.RepAction;
import yio.tro.antiyoy.gameplay.rules.GameRules;

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
    boolean used;


    public LevelSnapshot(GameController gameController) {
        this.gameController = gameController;

        fieldCopy = null;
        provincesCopy = new ArrayList<>();
        activeHexesCopy = new ArrayList<>();
        matchStatistics = new MatchStatistics();
        if (Settings.replaysEnabled) {
            replayBuffer = new ArrayList<>();
        }

        used = false;
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
    }


    private void updateReplayBuffer() {
        if (!Settings.replaysEnabled) return;

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
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            activeHexesCopy.add(activeHex.getSnapshotCopy());
        }
    }


    private void updateProvincesCopy() {
        for (Province province : gameController.fieldController.provinces) {
            provincesCopy.add(province.getSnapshotCopy());
        }
    }


    private void updateFieldCopy() {
        checkToCreateFieldCopyMatrix();
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                fieldCopy[i][j] = gameController.fieldController.field[i][j].getSnapshotCopy();
            }
        }
    }


    private void checkToCreateFieldCopyMatrix() {
        if (fieldCopy != null) return;

        fieldCopy = new Hex[fWidth][fHeight];
    }


    private void updateMetrics() {
        fWidth = gameController.fieldController.fWidth;
        fHeight = gameController.fieldController.fHeight;
    }


    private void updateSelectionHex() {
        if (gameController.selectionController.isSomethingSelected()) {
            selectionHex = gameController.fieldController.selectedProvince.hexList.get(0);
        } else {
            selectionHex = null;
        }
    }


    private void cleanOutEveryHexInField() {
        gameController.fieldController.cleanOutAllHexesInField();
    }


    private Hex getHexByCopy(Hex copy) {
        return gameController.fieldController.field[copy.index1][copy.index2];
    }


    public void recreate() {
        gameController.fieldController.clearField();
        cleanOutEveryHexInField();
        gameController.fieldController.clearAnims();

        recreateField();
        recreateActiveHexes();

        gameController.fieldController.detectProvinces();
        recreateProvinces();

        recreateSelection();
        recreateStatistics();
        recreateReplayBuffer();

        gameController.addAnimHex(gameController.fieldController.field[0][0]);
        gameController.updateWholeCache = true;
    }


    private void recreateReplayBuffer() {
        if (!Settings.replaysEnabled) return;
        if (GameRules.replayMode) return;

        gameController.replayManager.getReplay().recreateBufferFromSnapshot(replayBuffer);
    }


    private void recreateStatistics() {
        gameController.matchStatistics.copyFrom(matchStatistics);
    }


    private void recreateSelection() {
        gameController.selectionController.deselectAll();

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
                System.out.println("Wasn't been able to find province by hex. Color = " + copy.getColor());
                System.out.println("copy.getCapital() = " + copy.getCapital());
            } else {
                province.money = copy.money;
                province.updateName();
            }
        }
    }


    private void recreateActiveHexes() {
        ListIterator iterator = gameController.fieldController.activeHexes.listIterator();
        for (Hex hex : activeHexesCopy) {
            iterator.add(getHexByCopy(hex));
        }
    }


    private void recreateField() {
        Hex currHex;
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {

                currHex = gameController.fieldController.field[i][j];
                if (!currHex.active) continue;

                if (!currHex.sameColor(fieldCopy[i][j])) {
                    currHex.colorIndex = fieldCopy[i][j].colorIndex;
                    gameController.addAnimHex(currHex);
                }

                if (currHex.selected != fieldCopy[i][j].selected) {
                    currHex.selected = fieldCopy[i][j].selected;
                    if (!currHex.selected) currHex.selectionFactor.setValues(0, 0);
                }

                if (fieldCopy[i][j].containsObject()) {
                    gameController.addSolidObject(currHex, fieldCopy[i][j].objectInside);
                }

                if (fieldCopy[i][j].containsUnit()) {
                    gameController.addUnit(currHex, fieldCopy[i][j].unit.strength);
                    if (fieldCopy[i][j].unit.isReadyToMove()) {
                        currHex.unit.setReadyToMove(true);
                        currHex.unit.startJumping();
                    } else {
                        currHex.unit.setReadyToMove(false);
                        currHex.unit.stopJumping();
                    }
                }
            }
        }
    }


    public void reset() {
        used = false;

        defaultValues();
    }
}
