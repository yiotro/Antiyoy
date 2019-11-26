package yio.tro.antiyoy.gameplay.editor;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.MoveZoneDetection;
import yio.tro.antiyoy.gameplay.Obj;

import java.util.ArrayList;

public class EditorAutomationManager {

    private final LevelEditor levelEditor;
    ArrayList<Hex> ignoredHices;


    public EditorAutomationManager(LevelEditor levelEditor) {
        this.levelEditor = levelEditor;

        ignoredHices = new ArrayList<>();
    }


    public void expandProvinces() {
        Hex adjacentHex;
        int objectInside;

        for (Hex activeHex : getFieldController().activeHexes) {
            if (activeHex.isNeutral()) continue;

            for (int i = 0; i < 6; i++) {
                adjacentHex = activeHex.getAdjacentHex(i);
                if (adjacentHex == null) continue;
                if (!adjacentHex.active) continue;
                if (!adjacentHex.isNeutral()) continue;
                if (YioGdxGame.random.nextDouble() > 0.1) continue;

                objectInside = adjacentHex.objectInside;
                getFieldController().setHexFraction(adjacentHex, activeHex.fraction);
                if (objectInside > 0) {
                    levelEditor.placeObject(adjacentHex, objectInside);
                }
            }
        }

        checkToRemoveDoubleCapitals();
    }


    private FieldManager getFieldController() {
        return levelEditor.gameController.fieldManager;
    }


    void checkToRemoveDoubleCapitals() {
        ignoredHices.clear();
        ArrayList<Hex> hices;

        for (Hex activeHex : getFieldController().activeHexes) {
            if (activeHex.isNeutral()) continue;
            if (ignoredHices.contains(activeHex)) continue;

            hices = levelEditor.detectorProvince.detectProvince(activeHex);
            tagHicesAsIgnored(hices);

            int number = howManyCapitalsInProvince(hices);
            if (number > 1) {
                removeAllCapitalsExceptOne(hices);
            }
        }
    }


    void removeAllCapitalsExceptOne(ArrayList<Hex> hices) {
        boolean foundCapital = false;
        for (Hex hice : hices) {
            if (hice.objectInside != Obj.TOWN) continue;

            if (!foundCapital) {
                foundCapital = true;
                continue;
            }

            levelEditor.gameController.cleanOutHex(hice);
        }
    }


    int howManyCapitalsInProvince(ArrayList<Hex> hices) {
        int c = 0;
        for (Hex hice : hices) {
            if (hice.objectInside == Obj.TOWN) {
                c++;
            }
        }

        return c;
    }


    public void expandTrees() {
        getFieldController().expandTrees();
    }


    public void placeCapitalsOrFarms() {
        ArrayList<Hex> hices;
        ignoredHices.clear();

        for (Hex activeHex : getFieldController().activeHexes) {
            if (activeHex.isNeutral()) continue;
            if (activeHex.numberOfFriendlyHexesNearby() == 0) continue;
            if (ignoredHices.contains(activeHex)) continue;

            hices = levelEditor.detectorProvince.detectProvince(activeHex);
            tagHicesAsIgnored(hices);

            if (provinceHasCapital(hices)) {
                buildSomeRandomFarms(hices);
            } else {
                placeCapitalInRandomPlace(hices);
            }
        }
    }


    void tagHicesAsIgnored(ArrayList<Hex> hices) {
        for (Hex hex : hices) {
            ignoredHices.listIterator().add(hex);
        }
    }


    void buildSomeRandomFarms(ArrayList<Hex> hices) {
        for (Hex hex : hices) {
            if (hex.containsObject()) continue;
            if (!MoveZoneDetection.canBuildFarmOnHex(hex)) continue;
            if (YioGdxGame.random.nextDouble() > 0.2) continue;

            levelEditor.placeObject(hex, Obj.FARM);
        }
    }


    void placeCapitalInRandomPlace(ArrayList<Hex> hices) {
        int size = hices.size();
        int index = YioGdxGame.random.nextInt(size);
        Hex hex = hices.get(index);

        levelEditor.placeObject(hex, Obj.TOWN);
    }


    boolean provinceHasCapital(ArrayList<Hex> hices) {
        for (Hex hice : hices) {
            if (hice.objectInside == Obj.TOWN) {
                return true;
            }
        }

        return false;
    }


    public void placeRandomTowers() {
        for (Hex activeHex : getFieldController().activeHexes) {
            if (isHexGoodForRandomTower(activeHex)) {
                levelEditor.placeObject(activeHex, Obj.TOWER);
            }
        }
    }


    boolean isHexGoodForRandomTower(Hex hex) {
        if (!hex.active) return false;
        if (hex.isNeutral()) return false;
        if (!hex.isFree()) return false;
        if (!hasProvinceNearby(hex)) return false;

        return getPredictedDefenseGainByNewTower(hex) >= 3;
    }


    boolean hasProvinceNearby(Hex hex) {
        Hex adj1, adj2;
        for (int i = 0; i < 6; i++) {
            adj1 = hex.getAdjacentHex(i);
            if (!adj1.active) continue;

            for (int j = 0; j < 6; j++) {
                adj2 = adj1.getAdjacentHex(j);
                if (adj2.active && adj2.fraction != hex.fraction) {
                    return true;
                }
            }
        }

        return false;
    }


    protected int getPredictedDefenseGainByNewTower(Hex hex) {
        int c = 0;

        if (hex.active && !hex.isDefendedByTower()) c++;

        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.getAdjacentHex(i);
            if (adjHex.active && hex.sameFraction(adjHex) && !adjHex.isDefendedByTower()) c++;
            if (adjHex.containsTower()) c--;
        }

        return c;
    }


    public void cutExcessStuff() {
        for (Hex activeHex : getFieldController().activeHexes) {
            if (activeHex.containsTree()) {
                checkToCutExcessTree(activeHex);
                continue;
            }

            if (activeHex.objectInside == Obj.FARM) {
                checkToCutExcessFarm(activeHex);
                continue;
            }
        }
    }


    private void checkToCutExcessFarm(Hex activeHex) {
        if (YioGdxGame.random.nextDouble() > 0.1) return;

        getFieldController().cleanOutHex(activeHex);
    }


    private void checkToCutExcessTree(Hex activeHex) {
        if (YioGdxGame.random.nextDouble() > 0.2) return;

        int numberOfAdjacentTrees = getNumberOfAdjacentTrees(activeHex);
        if (numberOfAdjacentTrees == 0) return;

        getFieldController().cleanOutHex(activeHex);
    }


    private int getNumberOfAdjacentTrees(Hex hex) {
        int c = 0;

        Hex adjacentHex;
        for (int i = 0; i < 6; i++) {
            adjacentHex = hex.getAdjacentHex(i);
            if (!adjacentHex.active) continue;

            if (adjacentHex.containsTree()) {
                c++;
            }
        }

        return c;
    }
}