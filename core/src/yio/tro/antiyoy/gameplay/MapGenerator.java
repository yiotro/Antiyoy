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
    private ArrayList<Hex> tempList;
    PointYio tempPoint;
    public static int treesPercentages[] = new int[]{0, 5, 10, 15, 25, 33, 50, 66, 75, 90, 95, 100};


    public MapGenerator(GameController gameController) {
        this.gameController = gameController;
        detectorProvince = new DetectorProvince();
        tempList = new ArrayList<>();
        tempPoint = new PointYio();
    }


    protected void setValues(Random random, Hex field[][]) {
        boundWidth = gameController.levelSizeManager.boundWidth;
        boundHeight = gameController.levelSizeManager.boundHeight;
        fWidth = gameController.fieldManager.fWidth;
        fHeight = gameController.fieldManager.fHeight;
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

        if (GameRules.fractionsQuantity < 4) {
            return; // to prevent infinite loop
        }

        spawnManySmallProvinces();
        cutProvincesToSmallSizes();
        achieveFairNumberOfProvincesForEveryPlayer();
        applyBalanceMeasures();
    }


    private void checkToFixNoPlayerProblem() {
        if (mapHasAtLeastOnePlayerProvince()) return;

        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (activeHex.fraction != 0) continue;

            for (int i = 0; i < 6; i++) {
                Hex adjacentHex = activeHex.getAdjacentHex(i);
                if (!adjacentHex.active) continue;

                adjacentHex.fraction = 0;
                break;
            }

            break;
        }
    }


    private boolean mapHasAtLeastOnePlayerProvince() {
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (activeHex.fraction != 0) continue;

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
                if (adjHex.active && !adjHex.sameFraction(hex) && random.nextDouble() < power) {
                    adjHex.fraction = hex.fraction;
                }
            }
        }
    }


    protected boolean hexHasEnemiesNear(Hex hex) {
        for (int i = 0; i < 6; i++) {
            Hex adjHex = hex.getAdjacentHex(i);
            if (adjHex.active && !adjHex.sameFraction(hex)) return true;
        }
        return false;
    }


    protected void decreaseProvince(ArrayList<Hex> provinceList, double power) {
        for (Hex hex : provinceList) {
            if (hexHasEnemiesNear(hex) && random.nextDouble() < power) {
                hex.fraction = getRandomFraction();
            }
        }
    }


    protected void giveDisadvantageToPlayer(int index, double power) {
        clearGenFlags();
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (!activeHex.genFlag && activeHex.sameFraction(index)) {
                ArrayList<Hex> provinceList = detectorProvince.detectProvince(activeHex);
                tagProvince(provinceList);
                decreaseProvince(provinceList, power);
            }
        }
    }


    protected void giveAdvantageToPlayer(int index, double power) {
        clearGenFlags();
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (!activeHex.genFlag && activeHex.sameFraction(index)) {
                ArrayList<Hex> provinceList = detectorProvince.detectProvince(activeHex);
                increaseProvince(provinceList, power);
                provinceList = detectorProvince.detectProvince(activeHex); // detect again because province increased
                tagProvince(provinceList);
            }
        }
    }


    protected void applyBalanceMeasures() {
        giveAdvantageToPlayer(GameRules.fractionsQuantity - 1, 0.053); // last
        giveAdvantageToPlayer(GameRules.fractionsQuantity - 2, 0.033);
        if (GameRules.fractionsQuantity >= 5) {
            giveAdvantageToPlayer(2, 0.0165);
        } else {
            giveAdvantageToPlayer(1, 0.0065);
            giveAdvantageToPlayer(GameRules.fractionsQuantity - 1, 0.01);
        }
        giveDisadvantageToPlayer(0, 0.048);

        // [ 156 154 159 177 154 ] - [ 207 183 217 199 194 ] - [ 221 193 181 211 194 ] - [ 198 190 211 198 203 ] - [ 202 204 209 207 178 ] - [ 204 176 203 209 208 ]
    }


    protected void spawnManySmallProvinces() {
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
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
            hex.fraction = spawnHex.fraction;
            if (hex.genPotential == 0) continue;
            for (int i = 0; i < 6; i++) {
                Hex adjHex = hex.getAdjacentHex(i);
                if (!propagationList.contains(adjHex) && adjHex.active && adjHex.fraction != spawnHex.fraction) {
                    adjHex.genPotential = hex.genPotential - 1;
                    propagationList.add(adjHex);
                }
            }
        }
    }


    protected boolean atLeastOneProvinceIsTooBig() {
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (detectorProvince.detectProvince(activeHex).size() > SMALL_PROVINCE_SIZE) return true;
        }
        return false;
    }


    protected void cutProvincesToSmallSizes() {
        int loopLimit = 100;
        while (atLeastOneProvinceIsTooBig() && loopLimit > 0) {
            for (Hex activeHex : gameController.fieldManager.activeHexes) {
                ArrayList<Hex> provinceList = detectorProvince.detectProvince(activeHex);
                if (provinceList.size() > SMALL_PROVINCE_SIZE)
                    reduceProvinceSize(provinceList);
            }
            loopLimit--;
        }
    }


    protected int getRandomFractionExceptOne(int excludedFraction) {
        while (true) {
            int fraction = getRandomFraction();
            if (fraction != excludedFraction) return fraction;
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
        int provinceFraction = provinceList.get(0).fraction;
        while (provinceList.size() > SMALL_PROVINCE_SIZE) {
            Hex hex = findHexToExcludeFromProvince(provinceList);
            provinceList.remove(hex);
            hex.fraction = getRandomFractionExceptOne(provinceFraction);
        }
    }


    protected void countProvinces(int numbers[]) {
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = 0;
        }
        clearGenFlags();
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (!activeHex.genFlag) {
                ArrayList<Hex> provinceList = detectorProvince.detectProvince(activeHex);
                if (provinceList.size() > 1) {
                    numbers[provinceList.get(0).fraction]++;
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


    protected boolean provinceHasNeighbourWithFraction(ArrayList<Hex> provinceList, int fraction) {
        for (Hex hex : provinceList) {
            for (int i = 0; i < 6; i++) {
                Hex adjHex = hex.getAdjacentHex(i);
                if (adjHex.active && adjHex.sameFraction(fraction) && adjHex.numberOfFriendlyHexesNearby() > 0) return true;
            }
        }
        return false;
    }


    protected boolean tryToGiveAwayProvince(ArrayList<Hex> provinceList) {
        for (int i = 0; i < GameRules.fractionsQuantity; i++) {
            if (i == provinceList.get(0).fraction) continue;
            if (!provinceHasNeighbourWithFraction(provinceList, i)) {
                for (Hex hex : provinceList) {
                    hex.fraction = i;
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
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (activeHex.sameFraction(giverIndex) && !activeHex.genFlag) {
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
        int numbers[] = new int[GameRules.fractionsQuantity];
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
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
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
                field[i][j].previousFraction = field[i][j].fraction;
            }
        }

        gameController.fieldManager.clearActiveHexesList();
        deactivateHexes();

        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                int destX = i + deltaX;
                int destY = j + deltaY;
                if (destX >= 0 && destY >= 0 && destX < fWidth && deltaY < fHeight) {
                    if (field[i][j].genFlag) activateHex(field[destX][destY], field[i][j].previousFraction);
                } else {
                    deactivateHex(field[i][j]);
                }
            }
        }
    }


    protected boolean isGood() {
        return isLinked() && gameController.fieldManager.activeHexes.size() > 0.25 * numberOfAvailableHexes();
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
        tempList.clear();
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


    protected void deactivateHexes() {
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                if (!field[i][j].active) continue;

                deactivateHex(field[i][j]);
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
        return gameController.fieldManager.getHexByPos(boundWidth / 2, boundHeight / 2);
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
            Hex hex = gameController.fieldManager.getHexByPos(boundWidth / 2 + r * Math.cos(a), boundHeight / 2 + r * Math.sin(a));
            if (hex != null && isHexInsideBounds(hex)) return hex;
        }
    }


    protected double getRandomAngle() {
        return random.nextDouble() * 2d * Math.PI;
    }


    protected Hex getRandomHexInsideBounds() {
        while (true) {
            Hex hex;
            if (gameController.levelSizeManager.levelSize <= LevelSize.MEDIUM) {
                hex = field[random.nextInt(fWidth)][random.nextInt(fHeight)];
            } else {
                tempPoint.set(boundWidth / 2, boundHeight / 2);
                tempPoint.relocateRadial(random.nextDouble() * random.nextDouble() * 0.5 * boundHeight, getRandomAngle());
                hex = gameController.fieldManager.getHexByPos(tempPoint.x, tempPoint.y);
            }

            if (!isHexInsideBounds(hex)) continue;

            return hex;
        }
    }


    protected int getRandomFraction() {
        if (GameRules.fractionsQuantity == 0) return -1;
        while (true) {
            int fraction = random.nextInt(GameRules.fractionsQuantity);
            if (fraction == GameRules.NEUTRAL_FRACTION) continue;
            return fraction;
        }
    }


    protected boolean activateHex(Hex hex, int fraction) {
        if (hex.active) return false;
        hex.active = true;
        hex.setFraction(fraction);
        ListIterator activeIterator = gameController.fieldManager.activeHexes.listIterator();
        activeIterator.add(hex);
        return true;
    }


    protected void deactivateHex(Hex hex) {
        hex.active = false;
        gameController.fieldManager.activeHexes.remove(hex);
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
            boolean activated = activateHex(hex, getRandomFraction());
            if (hex.genPotential == 0 || !activated) continue;
            for (int i = 0; i < 6; i++) {
                Hex adjHex = hex.getAdjacentHex(i);
                if (!adjHex.genFlag && !adjHex.isNullHex() && !propagationList.contains(adjHex)) {
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
        double delta = gameController.fieldManager.hexSize / 2;
        double a = startPoint.angleTo(endPoint);
        int n = (int) (distance / delta);
        for (int i = 0; i < n; i++) {
            double currentX = startPoint.x + delta * i * Math.cos(a);
            double currentY = startPoint.y + delta * i * Math.sin(a);
            Hex hex = gameController.fieldManager.getHexByPos(currentX, currentY);
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
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (random.nextDouble() >= GameRules.treesSpawnChance) continue;
            if (activeHex.containsObject()) continue;
            gameController.fieldManager.spawnTree(activeHex);
        }
    }


    protected void removeSingleHoles() {
        for (int i = 0; i < fWidth; i++) {
            for (int j = 0; j < fHeight; j++) {
                if (!field[i][j].active && isHexInsideBounds(field[i][j]) && field[i][j].numberOfActiveHexesNearby() == 6) {
                    activateHex(field[i][j], getRandomFraction());
                }
            }
        }
    }


    protected boolean isHexInsideBounds(Hex hex) {
        if (hex == null) return false;

        PointYio pos = hex.getPos();

        if (pos.x <= 0.1 * w) return false;
        if (pos.x >= boundWidth - 0.1 * w) return false;
        if (pos.y <= 0.15 * h) return false;
        if (pos.y >= boundHeight - 0.1 * h) return false;

        return true;
    }


    protected int numberOfIslandsByLevelSize() {
        switch (gameController.levelSizeManager.levelSize) {
            default:
            case LevelSize.SMALL:
                return 2;
            case LevelSize.MEDIUM:
                return 4;
            case LevelSize.BIG:
                return 20;
            case LevelSize.HUGE:
                return 35;
        }
    }


    protected void createLand() {
        while (!isGood()) {
            deactivateHexes();
            int N = numberOfIslandsByLevelSize();
            for (int i = 0; i < N; i++) {
                Hex hex = getRandomHexInsideBounds();
                islandCenters.add(hex.getPos());
                spawnIsland(hex, 7);
            }
            uniteIslandsWithRoads();
            centerLand();
            cutOffHexesOutsideOfBounds();
        }
    }


    protected void endGeneration() {
        gameController.fieldManager.nullHex.active = false;
    }


    protected void beginGeneration() {
        gameController.fieldManager.createFieldMatrix();
        islandCenters = new ArrayList<>();
        links = new ArrayList<>();
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
