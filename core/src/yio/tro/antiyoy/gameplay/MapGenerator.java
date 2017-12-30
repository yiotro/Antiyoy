package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.Yio;
import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;


public class MapGenerator {

    protected final GameController gameController;
    private final DetectorProvince detectorProvince;
    protected float boundWidth, boundHeight;
    protected int fWidth, fHeight, w, h;
    protected Hex[][] field;
    protected Random random;
    protected ArrayList<PointYio> islandCenters;
    static int SMALL_PROVINCE_SIZE = 4;
    protected ArrayList<Link> links;


    public MapGenerator(GameController gameController) {
        this.gameController = gameController;
        detectorProvince = new DetectorProvince();
    }


    protected void templateLoop() {
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {

            }
        }
    }


    protected void setValues(Random random, Hex field[][]) {
        boundWidth = gameController.boundWidth;
        boundHeight = gameController.boundHeight;
        fWidth = gameController.fieldController.fWidth;
        fHeight = gameController.fieldController.fHeight;
        w = (int) GraphicsYio.width;
        h = (int) GraphicsYio.height;
        SMALL_PROVINCE_SIZE = 5;
        this.random = random;
        this.field = field;
    }


    public void generateMap(Random random, Hex field[][]) {
        setValues(random, field);

        beginGeneration();

        createLand();
        removeSingleHoles();
        addTrees();
        balanceMap();

        endGeneration();
    }


    protected void balanceMap() {
        checkToFixNoPlayerProblem();

        if (GameRules.colorNumber < 4) {
            return; // to prevent infinite loop
        }

        spawnManySmallProvinces();
        cutProvincesToSmallSizes();
        achieveFairNumberOfProvincesForEveryPlayer();
        giveLastPlayersSlightAdvantage();
    }


    private void checkToFixNoPlayerProblem() {
        if (mapHasAtLeastOnePlayerProvince()) return;

        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (activeHex.colorIndex != 0) continue;

            for (int i = 0; i < 6; i++) {
                Hex adjacentHex = activeHex.getAdjacentHex(i);
                if (!adjacentHex.active) continue;

                adjacentHex.colorIndex = 0;
                break;
            }

            break;
        }
    }


    private boolean mapHasAtLeastOnePlayerProvince() {
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (activeHex.colorIndex != 0) continue;

            if (activeHex.numberOfFriendlyHexesNearby() > 0) {
                return true;
            }
        }

        return false;
    }


    protected void increaseProvince(ArrayList<Hex> provinceList, double power) {
        for (Hex hex : provinceList) {
            for (int i = 0; i < 6; i++) {
                Hex adjHex = hex.getAdjacentHex(i);
                if (adjHex.active && !adjHex.sameColor(hex) && random.nextDouble() < power) {
                    adjHex.colorIndex = hex.colorIndex;
                }
            }
        }
    }


    protected boolean hexHasEnemiesNear(Hex hex) {
        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.getAdjacentHex(i);
            if (adjHex.active && !adjHex.sameColor(hex)) return true;
        }
        return false;
    }


    protected void decreaseProvince(ArrayList<Hex> provinceList, double power) {
        for (Hex hex : provinceList) {
            if (hexHasEnemiesNear(hex) && random.nextDouble() < power) {
                hex.colorIndex = getRandomColor();
            }
        }
    }


    protected void giveDisadvantageToPlayer(int index, double power) {
        clearGenFlags();
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (!activeHex.genFlag && activeHex.sameColor(index)) {
                ArrayList<Hex> provinceList = detectorProvince.detectProvince(activeHex);
                tagProvince(provinceList);
                decreaseProvince(provinceList, power);
            }
        }
    }


    protected void giveAdvantageToPlayer(int index, double power) {
        clearGenFlags();
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (!activeHex.genFlag && activeHex.sameColor(index)) {
                ArrayList<Hex> provinceList = detectorProvince.detectProvince(activeHex);
                increaseProvince(provinceList, power);
                provinceList = detectorProvince.detectProvince(activeHex); // detect again because province increased
                tagProvince(provinceList);
            }
        }
    }


    protected void giveLastPlayersSlightAdvantage() {
        giveAdvantageToPlayer(GameRules.colorNumber - 1, 0.053); // last
        giveAdvantageToPlayer(GameRules.colorNumber - 2, 0.033);
        if (GameRules.colorNumber >= 5) {
            giveAdvantageToPlayer(2, 0.0165);
//            giveAdvantageToPlayer(1, 0.005);
        } else { // if color number == 4
            giveAdvantageToPlayer(1, 0.0065);
            giveAdvantageToPlayer(GameRules.colorNumber - 1, 0.01);
        }
        giveDisadvantageToPlayer(0, 0.048);

        // [ 156 154 159 177 154 ] - [ 207 183 217 199 194 ] - [ 221 193 181 211 194 ] - [ 198 190 211 198 203 ] - [ 202 204 209 207 178 ] - [ 204 176 203 209 208 ]
    }


    protected void spawnManySmallProvinces() {
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (activeHex.noProvincesNearby()) spawnProvince(activeHex, 2);
        }
    }


    protected void spawnProvince(Hex spawnHex, int startingPotential) {
        spawnHex.genPotential = startingPotential;
        ArrayList<Hex> propagationList = new ArrayList<Hex>();
        propagationList.add(spawnHex);
        while (propagationList.size() > 0) {
            Hex hex = propagationList.get(0);
            propagationList.remove(0);
            if (random.nextInt(startingPotential) > hex.genPotential) continue;
            hex.colorIndex = spawnHex.colorIndex;
            if (hex.genPotential == 0) continue;
            for (int i = 0; i < 6; i++) {
                Hex adjHex = hex.getAdjacentHex(i);
                if (!propagationList.contains(adjHex) && adjHex.active && adjHex.colorIndex != spawnHex.colorIndex) {
                    adjHex.genPotential = hex.genPotential - 1;
                    propagationList.add(adjHex);
                }
            }
        }
    }


    protected boolean atLeastOneProvinceIsTooBig() {
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (detectorProvince.detectProvince(activeHex).size() > SMALL_PROVINCE_SIZE) return true;
        }
        return false;
    }


    protected void cutProvincesToSmallSizes() {
        int loopLimit = 100;
        while (atLeastOneProvinceIsTooBig() && loopLimit > 0) {
            for (Hex activeHex : gameController.fieldController.activeHexes) {
                ArrayList<Hex> provinceList = detectorProvince.detectProvince(activeHex);
                if (provinceList.size() > SMALL_PROVINCE_SIZE)
                    reduceProvinceSize(provinceList);
            }
            loopLimit--;
        }
    }


    protected int getRandomColorExceptOne(int excludedColor) {
        while (true) {
            int color = getRandomColor();
            if (color != excludedColor) return color;
        }
    }


    protected Hex findHexToExcludeFromProvince(ArrayList<Hex> provinceList) {
        Hex resultHex = null;
        int minNumber = 0;
        for (int i = 0; i < provinceList.size(); i++) {
            Hex currHex = provinceList.get(i);
            int currNumber = currHex.numberOfFriendlyHexesNearby();
            if (resultHex == null || currNumber < minNumber) {
                minNumber = currNumber;
                resultHex = currHex;
            }
        }
        return resultHex;
    }


    protected void reduceProvinceSize(ArrayList<Hex> provinceList) {
        int provinceColor = provinceList.get(0).colorIndex;
        while (provinceList.size() > SMALL_PROVINCE_SIZE) {
            Hex hex = findHexToExcludeFromProvince(provinceList);
            provinceList.remove(hex);
            hex.colorIndex = getRandomColorExceptOne(provinceColor);
        }
    }


    protected void countProvinces(int numbers[]) {
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = 0;
        }
        clearGenFlags();
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (!activeHex.genFlag) {
                ArrayList<Hex> provinceList = detectorProvince.detectProvince(activeHex);
                if (provinceList.size() > 1) {
                    numbers[provinceList.get(0).colorIndex]++;
                    for (Hex hex : provinceList) {
                        hex.genFlag = true;
                    }
                }
            }
        }
    }


    protected int maxDifferenceInNumbers(int numbers[]) {
        int maxDifference = 0;
        for (int i = 0; i < numbers.length; i++) {
            for (int j = i + 1; j < numbers.length; j++) {
                int d = Math.abs(numbers[i] - numbers[j]);
                if (d > maxDifference) maxDifference = d;
            }
        }
        return maxDifference;
    }


    protected int indexOfMax(int numbers[]) {
        int indexMax = 0;
        int maxValue = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > maxValue) {
                indexMax = i;
                maxValue = numbers[i];
            }
        }
        return indexMax;
    }


    protected int indexOfMin(int numbers[]) {
        int indexMin = 0;
        int minValue = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] < minValue) {
                indexMin = i;
                minValue = numbers[i];
            }
        }
        return indexMin;
    }


    protected boolean provinceHasNeighbourWithColor(ArrayList<Hex> provinceList, int color) {
        for (Hex hex : provinceList) {
            for (int i = 0; i < 6; i++) {
                Hex adjHex = hex.getAdjacentHex(i);
                if (adjHex.active && adjHex.sameColor(color) && adjHex.numberOfFriendlyHexesNearby() > 0) return true;
            }
        }
        return false;
    }


    protected boolean tryToGiveAwayProvince(ArrayList<Hex> provinceList) {
        for (int i = 0; i < GameRules.colorNumber; i++) {
            if (i == provinceList.get(0).colorIndex) continue;
            if (!provinceHasNeighbourWithColor(provinceList, i)) {
                for (Hex hex : provinceList) {
                    hex.colorIndex = i;
                }
                return true;
            }
        }
        return false;
    }


    protected void tagProvince(ArrayList<Hex> provinceList) {
        for (Hex hex : provinceList) {
            hex.genFlag = true;
        }
    }


    protected boolean giveProvinceToSomeone(int giverIndex) {
        clearGenFlags();
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (activeHex.sameColor(giverIndex) && !activeHex.genFlag) {
                ArrayList<Hex> provinceList = detectorProvince.detectProvince(activeHex);
                if (provinceList.size() > 1) {
                    tagProvince(provinceList);
                    if (tryToGiveAwayProvince(provinceList)) return true;
                }
            }
        }
        return false;
    }


    protected void achieveFairNumberOfProvincesForEveryPlayer() {
        int numbers[] = new int[GameRules.colorNumber];
        countProvinces(numbers);
        int loopLimit = 50;
        while (maxDifferenceInNumbers(numbers) > 1 && loopLimit > 0) {
            int indexMax = indexOfMax(numbers);
            boolean gaveAway = giveProvinceToSomeone(indexMax);
            if (!gaveAway) break;
            countProvinces(numbers);
            loopLimit--;
        }
    }


    protected void centerLand() {
        Hex centerHex = getCenterHex();
        int utterLeft = centerHex.index1;
        int utterRight = centerHex.index1;
        int utterUp = centerHex.index2;
        int utterDown = centerHex.index2;
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (activeHex.index1 < utterLeft) utterLeft = activeHex.index1;
            if (activeHex.index1 > utterRight) utterRight = activeHex.index1;
            if (activeHex.index2 < utterDown) utterDown = activeHex.index2;
            if (activeHex.index2 > utterUp) utterUp = activeHex.index2;
        }
        int averageHorizontal = (utterLeft + utterRight) / 2;
        int averageVertical = (utterDown + utterUp) / 2;
        relocateMap(centerHex.index1 - averageHorizontal, centerHex.index2 - averageVertical);
    }


    protected void relocateMap(int deltaX, int deltaY) {
        clearGenFlags();

        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                field[i][j].genFlag = field[i][j].active;
                field[i][j].lastColorIndex = field[i][j].colorIndex;
            }
        }

        gameController.fieldController.clearActiveHexesList();
        clearHexes();

        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                int destX = i + deltaX;
                int destY = j + deltaY;
                if (destX >= 0 && destY >= 0 && destX < fWidth && deltaY < fHeight) {
                    if (field[i][j].genFlag) activateHex(field[destX][destY], field[i][j].lastColorIndex);
                } else {
                    deactivateHex(field[i][j]);
                }
            }
        }
    }


    protected boolean isGood() {
        return isLinked() && gameController.fieldController.activeHexes.size() > 0.3 * numberOfAvailableHexes();
    }


    protected int numberOfAvailableHexes() {
        int c = 0;
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                if (isHexInsideBounds(field[i][j])) c++;
            }
        }
        return c;
    }


    protected boolean isLinked() {
        clearGenFlags();

        Hex activeHex = findActiveHex();
        if (activeHex == null) return false;

        // flood
        ArrayList<Hex> tempList = new ArrayList<Hex>();
        tempList.add(activeHex);
        while (tempList.size() > 0) {
            Hex hex = tempList.get(0);
            tempList.remove(0);
            hex.genFlag = true;
            for (int i = 0; i < 6; i++) {
                Hex adjHex = hex.getAdjacentHex(i);
                if (adjHex.active && !adjHex.genFlag && !tempList.contains(adjHex)) {
                    tempList.add(adjHex);
                }
            }
        }

        // check if something is not tagged
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                if (field[i][j].active && !field[i][j].genFlag) return false;
            }
        }
        return true;
    }


    protected Hex findActiveHex() {
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                if (field[i][j].active) return field[i][j];
            }
        }
        return null;
    }


    protected void maybeDeactivateIfPossible(Hex hex) {
        if (!hex.active) return;
        if (random.nextDouble() > 0.8) return;
        int activeNearby = hex.numberOfActiveHexesNearby();
        if (activeNearby == 4) {
            deactivateHex(hex);
            return;
        }
    }


    protected void cutOffHexesOutsideOfBounds() {
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                if (field[i][j].active && !isHexInsideBounds(field[i][j])) {
                    deactivateHex(field[i][j]);
                    for (int k = 0; k < 6; k++) maybeDeactivateIfPossible(field[i][j].getAdjacentHex(k));
                }
            }
        }
    }


    protected void clearHexes() {
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                if (field[i][j].active) deactivateHex(field[i][j]);
            }
        }
    }


    protected void clearGenFlags() {
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                field[i][j].genFlag = false;
            }
        }
    }


    protected Hex getCenterHex() {
        return gameController.fieldController.getHexByPos(boundWidth / 2, boundHeight / 2);
    }


    protected double distanceFromCenterToCorners() {
        return Yio.distance(0, 0, boundWidth / 2, boundHeight / 2);
    }


    protected Hex getRandomHexNearCenter() {
        while (true) {
            double a = getRandomAngle();
            double r = random.nextDouble();
            r *= r;
            r *= distanceFromCenterToCorners();
            Hex hex = gameController.fieldController.getHexByPos(boundWidth / 2 + r * Math.cos(a), boundHeight / 2 + r * Math.sin(a));
            if (hex != null && isHexInsideBounds(hex)) return hex;
        }
    }


    protected double getRandomAngle() {
        return random.nextDouble() * 2d * Math.PI;
    }


    protected Hex getRandomHex() {
        while (true) {
            Hex hex = field[random.nextInt(fWidth)][random.nextInt(fHeight)];
            if (isHexInsideBounds(hex)) return hex;
        }
    }


    protected int getRandomColor() {
        return random.nextInt(GameRules.colorNumber);
    }


    protected boolean activateHex(Hex hex, int color) {
        if (hex.active) return false;
        hex.active = true;
        hex.setColorIndex(color);
        ListIterator activeIterator = gameController.fieldController.activeHexes.listIterator();
        activeIterator.add(hex);
        return true;
    }


    protected void deactivateHex(Hex hex) {
        hex.active = false;
        gameController.fieldController.activeHexes.remove(hex);
    }


    protected void spawnIsland(Hex startHex, int size) {
        clearGenFlags();
        startHex.genPotential = size;
        ArrayList<Hex> propagationList = new ArrayList<Hex>();
        propagationList.add(startHex);
        while (propagationList.size() > 0) {
            Hex hex = propagationList.get(0);
            propagationList.remove(0);
            hex.genFlag = true;
            if (random.nextInt(size) > hex.genPotential) continue;
            boolean activated = activateHex(hex, getRandomColor());
            if (hex.genPotential == 0 || !activated) continue;
            for (int i = 0; i < 6; i++) {
                Hex adjHex = hex.getAdjacentHex(i);
                if (!adjHex.genFlag && !adjHex.isEmptyHex() && !propagationList.contains(adjHex)) {
                    adjHex.genPotential = hex.genPotential - 1;
                    propagationList.add(adjHex);
                }
            }
        }
    }


    protected void uniteIslandsWithRoads() {
        for (int i = 0; i < islandCenters.size(); i++) {
            createRoadBetweenIslands(i, getClosestIslandIndex(i));
        }
    }


    protected void createRoadBetweenIslands(int islandOne, int islandTwo) {
        if (islandTwo == -1) return;
        PointYio startPoint = islandCenters.get(islandOne);
        PointYio endPoint = islandCenters.get(islandTwo);
        links.add(new Link(startPoint, endPoint));
        double distance = startPoint.distanceTo(endPoint);
        double delta = gameController.fieldController.hexSize / 2;
        double a = startPoint.angleTo(endPoint);
        int n = (int) (distance / delta);
        for (int i = 0; i < n; i++) {
            double currentX = startPoint.x + delta * i * Math.cos(a);
            double currentY = startPoint.y + delta * i * Math.sin(a);
            Hex hex = gameController.fieldController.getHexByPos(currentX, currentY);
            spawnIsland(hex, 2);
        }
    }


    protected boolean areIslandsAlreadyUnited(PointYio p1, PointYio p2) {
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).equals(p1, p2)) return true;
        }
        return false;
    }


    protected int getClosestIslandIndex(int searcherIslandIndex) {
        PointYio startPoint = islandCenters.get(searcherIslandIndex);
        int closestIslandIndex = -1;
        double minDistance = fWidth * fHeight, currentDistance;
        for (int i = 1; i < islandCenters.size(); i++) {
            if (i == searcherIslandIndex) continue;
            if (areIslandsAlreadyUnited(startPoint, islandCenters.get(i))) continue;
            currentDistance = startPoint.distanceTo(islandCenters.get(i));
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                closestIslandIndex = i;
            }
        }
        return closestIslandIndex;
    }


    protected void addTrees() {
        for (Hex activeHex : gameController.fieldController.activeHexes) {
            if (random.nextDouble() < 0.1 && !activeHex.containsObject()) {
                gameController.fieldController.spawnTree(activeHex);
            }
        }
    }


    protected void removeSingleHoles() {
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                if (!field[i][j].active && isHexInsideBounds(field[i][j]) && field[i][j].numberOfActiveHexesNearby() == 6) {
                    activateHex(field[i][j], getRandomColor());
                }
            }
        }
    }


    protected boolean isHexInsideBounds(Hex hex) {
        PointYio pos = hex.getPos();
        return pos.x > 0.1 * w && pos.x < boundWidth - 0.1 * w && pos.y > 0.15 * h && pos.y < boundHeight - 0.1 * h;
    }


    protected int numberOfIslandsByLevelSize() {
        switch (gameController.fieldController.levelSize) {
            default:
            case FieldController.SIZE_SMALL:
                return 2;
            case FieldController.SIZE_MEDIUM:
                return 4;
            case FieldController.SIZE_BIG:
                return 7;
        }
    }


    protected void createLand() {
        while (!isGood()) {
            clearHexes();
            int N = numberOfIslandsByLevelSize();
            for (int i = 0; i < N; i++) {
                Hex hex = getRandomHex();
                islandCenters.add(hex.getPos());
                spawnIsland(hex, 7);
            }
            uniteIslandsWithRoads();
            centerLand();
            cutOffHexesOutsideOfBounds();
        }
    }


    protected void endGeneration() {
        gameController.fieldController.emptyHex.active = false;
    }


    protected void beginGeneration() {
        gameController.fieldController.createFieldMatrix();
        islandCenters = new ArrayList<PointYio>();
        links = new ArrayList<Link>();
    }


    class Link {
        PointYio p1, p2;


        public Link(PointYio p1, PointYio p2) {
            this.p1 = p1;
            this.p2 = p2;
        }


        boolean equals(PointYio p1, PointYio p2) {
            return containsPoint(p1) && containsPoint(p2);
        }


        boolean containsPoint(PointYio p) {
            return p1 == p || p2 == p;
        }


        boolean equals(Link link) {
            return equals(link.p1, link.p2);
        }
    }
}
