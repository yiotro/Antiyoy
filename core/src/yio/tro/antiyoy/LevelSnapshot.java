package yio.tro.antiyoy;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by ivan on 06.11.2015.
 */
class LevelSnapshot {

    private final GameController gameController;
    private Hex[][] fieldCopy;
    private ArrayList<Province> provincesCopy;
    private ArrayList<Hex> activeHexesCopy;
    private Hex selectionHex;


    public LevelSnapshot(GameController gameController) {
        this.gameController = gameController;
    }


    void takeSnapshot() {
        if (gameController.isSomethingSelected()) {
            selectionHex = gameController.selectedProvince.hexList.get(0);
        } else selectionHex = null;

        fieldCopy = new Hex[gameController.fWidth][gameController.fHeight];
        for (int i = 0; i < gameController.fWidth; i++) {
            for (int j = 0; j < gameController.fHeight; j++) {
                fieldCopy[i][j] = gameController.field[i][j].getSnapshotCopy();
            }
        }

        provincesCopy = new ArrayList<Province>();
        for (Province province : gameController.provinces) {
            provincesCopy.add(province.getSnapshotCopy());
        }

        activeHexesCopy = new ArrayList<Hex>();
        for (Hex activeHex : gameController.activeHexes) {
            activeHexesCopy.add(activeHex.getSnapshotCopy());
        }
    }


    private void cleanOutEveryHexInField() {
        for (int i = 0; i < gameController.fWidth; i++) {
            for (int j = 0; j < gameController.fHeight; j++) {
                if (!gameController.field[i][j].active) continue;
                gameController.cleanOutHex(gameController.field[i][j]);
            }
        }
    }


    private Hex getHexByCopy(Hex copy) {
        return gameController.field[copy.index1][copy.index2];
    }


    void recreateSnapshot() {
        gameController.clearField();
        cleanOutEveryHexInField();
        gameController.clearAnims();

        Hex currHex;
        for (int i = 0; i < gameController.fWidth; i++) {
            for (int j = 0; j < gameController.fHeight; j++) {

                currHex = gameController.field[i][j];
                if (!currHex.active) continue;

                if (!currHex.sameColor(fieldCopy[i][j])) {
                    currHex.colorIndex = fieldCopy[i][j].colorIndex;
                    gameController.addAnimHex(currHex);
                }

                if (currHex.selected != fieldCopy[i][j].selected) {
                    currHex.selected = fieldCopy[i][j].selected;
                    if (!currHex.selected) currHex.selectionFactor.setValues(0, 0);
                }

                if (fieldCopy[i][j].containsSolidObject()) {
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

        ListIterator iterator = gameController.activeHexes.listIterator();
        for (Hex hex : activeHexesCopy) {
            iterator.add(getHexByCopy(hex));
        }

        gameController.detectProvinces();

        // gameController.provinces have to be exactly in the same order as provincesCopy
//        ComparatorProvince comparatorProvince = new ComparatorProvince();
//        Collections.sort(provincesCopy, comparatorProvince);
//        Collections.sort(gameController.provinces, comparatorProvince);
//        for (int i = 0; i < provincesCopy.size(); i++) {
//            gameController.provinces.get(i).money = provincesCopy.get(i).money;
//            if (gameController.provinces.get(i).hexList.size() != provincesCopy.get(i).hexList.size()) YioGdxGame.say("dasdgah");
//        }

        for (Province copy : provincesCopy) {
            Province province = gameController.findProvinceCopy(copy);
            if (province == null) {
//                province.money = 999;
//                province.name = "Bugged province";
                System.out.println("Problem in level snapshot.");
                System.out.println("Wasn't been able to find province by hex. Color = " + copy.getColor());
            } else {
                province.money = copy.money;
                province.updateName();
            }
        }

        gameController.deselectAll();
        if (selectionHex != null) {
            gameController.selectAdjacentHexes(selectionHex);
        }

        gameController.addAnimHex(gameController.field[0][0]);
        gameController.updateWholeCache = true;
    }
}
