package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.SingleMessages;
import yio.tro.antiyoy.menu.diplomacy_element.DeIcon;
import yio.tro.antiyoy.menu.diplomacy_element.DiplomacyElement;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.Yio;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;
import java.util.Map;

public class DiplomacyManager {

    FieldController fieldController;
    public ArrayList<DiplomaticEntity> entities;
    ObjectPoolYio<DiplomaticEntity> poolEntities;
    public ArrayList<DiplomaticContract> contracts;
    ObjectPoolYio<DiplomaticContract> poolContracts;


    public DiplomacyManager(FieldController fieldController) {
        this.fieldController = fieldController;

        entities = new ArrayList<>();
        contracts = new ArrayList<>();

        initPools();
    }


    private void initPools() {
        poolEntities = new ObjectPoolYio<DiplomaticEntity>() {
            @Override
            public DiplomaticEntity makeNewObject() {
                return new DiplomaticEntity(DiplomacyManager.this);
            }
        };

        poolContracts = new ObjectPoolYio<DiplomaticContract>() {
            @Override
            public DiplomaticContract makeNewObject() {
                return new DiplomaticContract();
            }
        };
    }


    public void onEndCreation() {
        if (!GameRules.diplomacyEnabled) return;

        updateEntities();
        clearContracts();

        DiplomacyElement diplomacyElement = Scenes.sceneDiplomacy.diplomacyElement;
        if (diplomacyElement != null) {
            diplomacyElement.updateAll();
        }
    }


    private void clearContracts() {
        for (DiplomaticContract contract : contracts) {
            poolContracts.add(contract);
        }

        contracts.clear();
    }


    public void checkForSingleMessage() {
        if (SingleMessages.diplomacyWinConditions) return;

        SingleMessages.diplomacyWinConditions = true;
        SingleMessages.save();

        // diplomacy panel should be under this dialog
        Scenes.sceneDiplomacy.create();
        Scenes.sceneDiplomacy.hide();

        Scenes.sceneDipMessage.create();
        Scenes.sceneDipMessage.dialog.setMessage("win_conditions", "diplomatic_win_conditions");
    }


    void onEntityDied(DiplomaticEntity deadEntity) {
        dropEntityRelationsToDefault(deadEntity);
        cancelContractsWithEntity(deadEntity);
    }


    private void dropEntityRelationsToDefault(DiplomaticEntity deadEntity) {
        for (DiplomaticEntity entity : entities) {
            if (entity == deadEntity) continue;

            makeNeutral(deadEntity, entity);
        }
    }


    private void cancelContractsWithEntity(DiplomaticEntity deadEntity) {
        for (int i = contracts.size() - 1; i >= 0; i--) {
            DiplomaticContract diplomaticContract = contracts.get(i);
            if (!diplomaticContract.contains(deadEntity)) continue;

            removeContract(diplomaticContract);
        }
    }


    private void updateEntities() {
        clearEntities();

        for (int color = 0; color < GameRules.colorNumber; color++) {
            DiplomaticEntity next = poolEntities.getNext();

            next.setColor(color);
            next.updateCapitalName();
            next.setHuman(fieldController.gameController.isPlayerTurn(color));

            entities.add(next);
        }

        initEntityRelations();
//        randomizeRelations();
    }


    private void randomizeRelations() {
        int size = entities.size();

        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                double randomValue = YioGdxGame.random.nextDouble();

                if (randomValue < 0.4) {
                    makeFriends(entities.get(i), entities.get(j));
                } else if (randomValue < 0.55) {
                    makeEnemies(entities.get(i), entities.get(j));
                }
            }
        }
    }


    private void initEntityRelations() {
        for (DiplomaticEntity entity : entities) {
            entity.initRelations();
        }
    }


    private void clearEntities() {
        for (DiplomaticEntity entity : entities) {
            poolEntities.add(entity);
        }

        entities.clear();
    }


    public int getDiplomaticWinner() {
        if (!isThereAtLeastOneDiplomaticWinner()) return -1;

        DiplomaticEntity bestEntity = null;
        for (DiplomaticEntity entity : entities) {
            if (!entity.hasOnlyFriends()) continue;

            if (bestEntity == null || entity.getNumberOfLands() > bestEntity.getNumberOfLands()) {
                bestEntity = entity;
            }
        }

        return bestEntity.color;
    }


    boolean isThereAtLeastOneDiplomaticWinner() {
        for (DiplomaticEntity entity : entities) {
            if (entity.hasOnlyFriends()) {
                return true;
            }
        }

        return false;
    }


    public void onUserClickedContextIcon(int selectedColor, int action) {
        DiplomaticEntity mainEntity = getMainEntity();
        DiplomaticEntity selectedEntity = getEntity(selectedColor);

        switch (action) {
            case DeIcon.ACTION_LIKE:
                requestBetterRelations(mainEntity, selectedEntity);
                break;
            case DeIcon.ACTION_DISLIKE:
                Scenes.sceneConfirmDislike.create();
                Scenes.sceneConfirmDislike.dialog.setSelectedEntity(selectedEntity);
                break;
            case DeIcon.ACTION_BLACK_MARK:
                Scenes.sceneConfirmBlackMarkDialog.create();
                Scenes.sceneConfirmBlackMarkDialog.dialog.setSelectedEntity(selectedEntity);
                break;
            case DeIcon.ACTION_INFO:
                Scenes.sceneDipInfoDialog.create();
                Scenes.sceneDipInfoDialog.dialog.setEntities(mainEntity, selectedEntity);
                break;
        }
    }


    public void onUserRequestedBlackMark(DiplomaticEntity selectedEntity) {
        makeBlackMarked(getMainEntity(), selectedEntity);
    }


    public void makeBlackMarked(DiplomaticEntity initiator, DiplomaticEntity entity) {
        addContract(DiplomaticContract.TYPE_BLACK_MARK, initiator, entity);

        onRelationsChanged();
    }


    public void onUserRequestedFriendship(DiplomaticEntity selectedEntity) {
        makeFriends(getMainEntity(), selectedEntity);
    }


    public void onUserRequestedToMakeRelationsWorse(DiplomaticEntity selectedEntity) {
        onEntityRequestedToMakeRelationsWorse(getMainEntity(), selectedEntity);
    }


    void onEntityRequestedToMakeRelationsWorse(DiplomaticEntity initiator, DiplomaticEntity entity) {
        int previousRelation = initiator.getRelation(entity);

        requestWorseRelations(initiator, entity);

        int relation = initiator.getRelation(entity);
        if (relation == previousRelation) return;

        if (relation == DiplomaticRelation.ENEMY) {
            onWarStarted(initiator, entity);
        }
    }


    public void onUserRequestedToStopWar(DiplomaticEntity selectedEntity) {
        DiplomaticEntity mainEntity = getMainEntity();

        addContract(DiplomaticContract.TYPE_PIECE, mainEntity, selectedEntity);
        makeNeutral(mainEntity, selectedEntity);
        mainEntity.pay(calculatePayToStopWar(mainEntity, selectedEntity));
    }


    public void onContractExpired(DiplomaticContract contract) {
        removeContract(contract);

        if (contract.type == DiplomaticContract.TYPE_FRIENDSHIP) {
            int relation = contract.one.getRelation(contract.two);
            if (relation == DiplomaticRelation.FRIEND) {
                makeNeutral(contract.one, contract.two);
            }
        }
    }


    public void updateEntityAliveStatus(int color) {
        if (!GameRules.diplomacyEnabled) return;

        DiplomaticEntity entity = getEntity(color);
        if (entity != null) {
            entity.updateAlive();
        }
    }


    public void onTurnStarted() {
        if (!GameRules.diplomacyEnabled) return;

        if (fieldController.gameController.isPlayerTurn()) {
            onHumanTurnStarted();
        }
    }


    private void onHumanTurnStarted() {
        if (YioGdxGame.random.nextInt(8) == 0) {
            performAiToHumanFriendshipProposal();
            return;
        }

        if (YioGdxGame.random.nextInt(4) == 0) {
            performAiToHumanBlackMark();
            return;
        }
    }


    public void performAiToHumanBlackMark() {
        DiplomaticEntity aiEntity = findAiEntityThatIsCloseToWin();
        if (aiEntity == null) return;

        DiplomaticEntity mainEntity = getMainEntity();

        int relation = aiEntity.getRelation(mainEntity);
        if (relation == DiplomaticRelation.FRIEND) return;

        makeBlackMarked(aiEntity, mainEntity);
    }


    public boolean performAiToHumanFriendshipProposal() {
        DiplomaticEntity mainEntity = getMainEntity();
        if (mainEntity.isOneFriendAwayFromDiplomaticVictory()) return false;
        if (!mainEntity.isHuman()) return false;
        if (!mainEntity.alive) return false;

        for (int i = 0; i < 25; i++) {
            DiplomaticEntity randomEntity = getRandomEntity();
            if (!randomEntity.alive) continue;
            if (randomEntity == mainEntity) continue;
            if (randomEntity.isOneFriendAwayFromDiplomaticVictory()) continue; // no tricky friend requests

            int relation = mainEntity.getRelation(randomEntity);
            if (relation != DiplomaticRelation.NEUTRAL) continue;
            if (!mainEntity.acceptsFriendsRequest(randomEntity)) continue;

            Scenes.sceneFriendshipDialog.create();
            Scenes.sceneFriendshipDialog.dialog.setEntities(mainEntity, randomEntity);
            return true;
        }

        return false;
    }


    public DiplomaticEntity findAiEntityThatIsCloseToWin() {
        for (DiplomaticEntity entity : entities) {
            if (entity.isHuman()) continue;
            if (!entity.alive) continue;

            if (entity.isOneFriendAwayFromDiplomaticVictory()) {
                return entity;
            }
        }

        return null;
    }


    public void onTurnEnded() {
        if (!GameRules.diplomacyEnabled) return;

        DiplomaticEntity entity = getEntity(fieldController.gameController.turn);
        entity.updateAlive();

        checkToChangeRelations();

        if (fieldController.gameController.turn == 0) {
            onFirstPlayerTurnEnded();
        }
    }


    private void checkToChangeRelations() {
        DiplomaticEntity mainEntity = getMainEntity();
        if (mainEntity.isHuman()) return;

        mainEntity.thinkAboutChangingRelations();
    }


    void onFirstPlayerTurnEnded() {
        for (int i = contracts.size() - 1; i >= 0; i--) {
            contracts.get(i).onFirstPlayerTurnEnded();
        }

        DiplomacyElement diplomacyElement = Scenes.sceneDiplomacy.diplomacyElement;
        if (diplomacyElement != null) {
            diplomacyElement.onFirstPlayerTurnEnded();
        }
    }


    public boolean canUnitAttackHex(int unitStrength, int unitColor, Hex hex) {
        DiplomaticEntity one = getEntity(unitColor);
        DiplomaticEntity two = getEntity(hex.colorIndex);

        if (one == null || two == null) return true;

        int relation = one.getRelation(two);

        return relation == DiplomaticRelation.ENEMY;
    }


    public boolean isProvinceAllowedToBuildUnit(Province province, int unitStrength) {
        int color = province.getColor();
        DiplomaticEntity entity = getEntity(color);

        if (entity.isAtWar()) return true;

        // piece
        if (unitStrength > 1) return false;
        if (numberOfPeasantsInProvince(province) > 4) return false;

        return true;
    }


    private int numberOfPeasantsInProvince(Province province) {
        int c = 0;

        for (Hex hex : province.hexList) {
            if (hex.containsUnit() && hex.unit.strength == 1) {
                c++;
            }
        }

        return c;
    }


    public int calculateDotationsForFriendship(DiplomaticEntity initiator, DiplomaticEntity entity) {
        int money1 = entity.getStateBalance() * entity.getNumberOfFriends();
        int money2 = initiator.getStateBalance() * initiator.getNumberOfFriends();
        int max = Math.max(money1, money2);
        int cutValue = (int) (0.2 * ((float) max));

        int difference = Math.abs(money1 - money2);
        if (difference < cutValue || difference < 5) return 0;

        if (money1 > money2) {
            return cutValue;
        } else {
            return -cutValue;
        }
    }


    public int getProvinceDotations(Province province) {
        int color = province.getColor();
        DiplomaticEntity entity = getEntity(color);
        int stateDotations = entity.getStateDotations();

        return (int) (province.getIncomeCoefficient() * stateDotations);
    }


    public DiplomaticEntity getMainEntity() {
        int turn = fieldController.gameController.turn;
        return getEntity(turn);
    }


    public DiplomaticEntity getRandomEntity() {
        int size = entities.size();
        int index = YioGdxGame.random.nextInt(size);
        return entities.get(index);
    }


    void requestBetterRelations(DiplomaticEntity one, DiplomaticEntity two) {
        int relation = one.getRelation(two);

        if (relation == DiplomaticRelation.ENEMY) {
            if (canWarBeStopped(one, two)) {
                Scenes.sceneStopWarDialog.create();
                Scenes.sceneStopWarDialog.dialog.setSelectedEntity(two);
            } else {
                Scenes.sceneDipMessage.create();
                Scenes.sceneDipMessage.dialog.setMessage(two.capitalName, "refuse_stop_war");
            }
        }

        if (relation == DiplomaticRelation.NEUTRAL) {
            if (isFriendshipPossible(one, two)) {
                Scenes.sceneFriendshipDialog.create();
                Scenes.sceneFriendshipDialog.dialog.setEntities(one, two);
            } else {
                Scenes.sceneDipMessage.create();
                Scenes.sceneDipMessage.dialog.setMessage(two.capitalName, "refuse_friendship");
            }
        }
    }


    boolean isFriendshipPossible(DiplomaticEntity one, DiplomaticEntity two) {
        if (one.isOneFriendAwayFromDiplomaticVictory()) return false;
        if (two.isOneFriendAwayFromDiplomaticVictory()) return false;

        return one.acceptsFriendsRequest(two) && two.acceptsFriendsRequest(one);
    }


    boolean canWarBeStopped(DiplomaticEntity one, DiplomaticEntity two) {
        return one.acceptsToStopWar(two) && two.acceptsToStopWar(one);
    }


    void requestWorseRelations(DiplomaticEntity initiator, DiplomaticEntity two) {
        int relation = two.getRelation(initiator);

        if (relation == DiplomaticRelation.FRIEND) {
            makeNeutral(two, initiator);
        }

        if (relation == DiplomaticRelation.NEUTRAL) {
            makeEnemies(initiator, two);
        }
    }


    private void onWarStarted(DiplomaticEntity initiator, DiplomaticEntity one) {
        punishAggressor(initiator, one);
    }


    private void punishAggressor(DiplomaticEntity initiator, DiplomaticEntity one) {
        for (Map.Entry<DiplomaticEntity, Integer> entry : initiator.relations.entrySet()) {
            DiplomaticEntity entity = entry.getKey();
            if (one.isFriendTo(entity)) {
                requestWorseRelations(initiator, entity);
            }
        }
    }


    DiplomaticContract addContract(int contractType, DiplomaticEntity initiator, DiplomaticEntity entity) {
        DiplomaticContract next = poolContracts.getNext();

        next.setOne(entity);
        next.setTwo(initiator);
        next.setType(contractType);
        next.setDotations(getDotationsByContractType(contractType, initiator, entity));
        next.setExpireCountDown(DiplomaticContract.getDurationByType(contractType));

        contracts.add(next);
        return next;
    }


    int getDotationsByContractType(int contractType, DiplomaticEntity initiator, DiplomaticEntity two) {
        switch (contractType) {
            default:
                return 0;
            case DiplomaticContract.TYPE_FRIENDSHIP:
                return calculateDotationsForFriendship(initiator, two);
            case DiplomaticContract.TYPE_PIECE:
                return calculateReparations(initiator, two);
            case DiplomaticContract.TYPE_BLACK_MARK:
                return 0;
        }
    }


    void removeContract(int contractType, DiplomaticEntity one, DiplomaticEntity two) {
        DiplomaticContract contract = findContract(contractType, one, two);
        if (contract == null) return;

        removeContract(contract);
    }


    private void removeContract(DiplomaticContract contract) {
        poolContracts.add(contract);
        contracts.remove(contract);
    }


    public int calculateReparations(DiplomaticEntity initiator, DiplomaticEntity two) {
        int stateBalance = initiator.getStateBalance();

        if (stateBalance < 5) return 0;
        if (two.getStateBalance() < 10) return 0;

        return - stateBalance / 2;
    }


    public int calculatePayToStopWar(DiplomaticEntity initiator, DiplomaticEntity two) {
        return (int) Math.min(0.6 * initiator.getStateFullMoney(), 0.5 * two.getStateFullMoney());
    }


    public DiplomaticContract findContract(int type, DiplomaticEntity one, DiplomaticEntity two) {
        for (DiplomaticContract contract : contracts) {
            if (contract.equals(one, two, type)) {
                return contract;
            }
        }

        return null;
    }


    public void setRelation(DiplomaticEntity one, DiplomaticEntity two, int relation) {
        switch (relation) {
            case DiplomaticRelation.FRIEND:
                makeFriends(one, two);
                break;
            case DiplomaticRelation.NEUTRAL:
                makeNeutral(one, two);
                break;
            case DiplomaticRelation.ENEMY:
                makeEnemies(one, two);
                break;
        }
    }


    public void makeFriends(DiplomaticEntity initiator, DiplomaticEntity entity) {
        if (!initiator.alive || !entity.alive) return;
        if (initiator.getRelation(entity) == DiplomaticRelation.FRIEND) return;

        // should be before relations change because they will influence dotations
        addContract(DiplomaticContract.TYPE_FRIENDSHIP, initiator, entity);
        removeContract(DiplomaticContract.TYPE_PIECE, initiator, entity);

        initiator.setRelation(entity, DiplomaticRelation.FRIEND);
        entity.setRelation(initiator, DiplomaticRelation.FRIEND);

        onRelationsChanged();
    }


    public void makeNeutral(DiplomaticEntity one, DiplomaticEntity two) {
        if (!one.alive || !two.alive) return;
        if (one.getRelation(two) == DiplomaticRelation.NEUTRAL) return;

        one.setRelation(two, DiplomaticRelation.NEUTRAL);
        two.setRelation(one, DiplomaticRelation.NEUTRAL);

        removeContract(DiplomaticContract.TYPE_FRIENDSHIP, one, two);
        // piece contract shouldn't be added here

        onRelationsChanged();
    }


    public boolean makeEnemies(DiplomaticEntity initiator, DiplomaticEntity entity) {
        if (!initiator.alive || !entity.alive) return false;
        if (initiator.getRelation(entity) == DiplomaticRelation.ENEMY) return false;

        initiator.setRelation(entity, DiplomaticRelation.ENEMY);
        entity.setRelation(initiator, DiplomaticRelation.ENEMY);

        removeContract(DiplomaticContract.TYPE_FRIENDSHIP, initiator, entity);
        removeContract(DiplomaticContract.TYPE_PIECE, initiator, entity);

        onRelationsChanged();
        return true;
    }


    public void onRelationsChanged() {
        DiplomacyElement diplomacyElement = Scenes.sceneDiplomacy.diplomacyElement;
        if (diplomacyElement != null) {
            diplomacyElement.onRelationsChanged();
        }
    }


    public DiplomaticEntity getEntity(int color) {
        for (DiplomaticEntity entity : entities) {
            if (entity.color == color) {
                return entity;
            }
        }

        return null;
    }


    public void showContractsInConsole(int colorFilter) {
        System.out.println();
        System.out.println("DiplomacyManager.showContractsInConsole");
        DiplomaticEntity entity = getEntity(colorFilter);
        for (DiplomaticContract contract : contracts) {
            if (entity != null && !contract.contains(entity)) continue;
            System.out.println("- " + contract);
        }
    }
}
