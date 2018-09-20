package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.SingleMessages;
import yio.tro.antiyoy.menu.diplomacy_element.DeIcon;
import yio.tro.antiyoy.menu.diplomacy_element.DiplomacyElement;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;
import java.util.Map;

public class DiplomacyManager {

    FieldController fieldController;
    public ArrayList<DiplomaticEntity> entities;
    ObjectPoolYio<DiplomaticEntity> poolEntities;
    public ArrayList<DiplomaticContract> contracts;
    ObjectPoolYio<DiplomaticContract> poolContracts;
    public ArrayList<DiplomaticCooldown> cooldowns;
    ObjectPoolYio<DiplomaticCooldown> poolCooldowns;
    public DiplomaticLog log;


    public DiplomacyManager(FieldController fieldController) {
        this.fieldController = fieldController;

        entities = new ArrayList<>();
        contracts = new ArrayList<>();
        cooldowns = new ArrayList<>();
        log = new DiplomaticLog(this);

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

        poolCooldowns = new ObjectPoolYio<DiplomaticCooldown>() {
            @Override
            public DiplomaticCooldown makeNewObject() {
                return new DiplomaticCooldown();
            }
        };
    }


    public void onEndCreation() {
        if (!GameRules.diplomacyEnabled) return;

        updateEntities();
        clearContracts();
        clearCooldowns();
        log.clear();

        DiplomacyElement diplomacyElement = Scenes.sceneDiplomacy.diplomacyElement;
        if (diplomacyElement != null) {
            diplomacyElement.updateAll();
        }
    }


    private void clearCooldowns() {
        for (DiplomaticCooldown cooldown : cooldowns) {
            poolCooldowns.add(cooldown);
        }

        cooldowns.clear();
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


    public void onDiplomacyButtonPressed() {
        fieldController.gameController.selectionController.deselectAll();

        if (log.hasSomethingToRead()) {
            Scenes.sceneDiplomaticLog.create();
            return;
        }

        Scenes.sceneDiplomacy.create();
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
            if (!isEntityWinner(entity)) continue;

            if (bestEntity == null || entity.getNumberOfLands() > bestEntity.getNumberOfLands()) {
                bestEntity = entity;
            }
        }

        if (bestEntity == null) {
            return -1;
        }

        return bestEntity.color;
    }


    boolean isThereAtLeastOneDiplomaticWinner() {
        for (DiplomaticEntity entity : entities) {
            if (!isEntityWinner(entity)) continue;

            return true;
        }

        return false;
    }


    boolean isEntityWinner(DiplomaticEntity entity) {
        if (!entity.hasOnlyFriends()) return false;
        if (!entity.alive) return false;

        return true;
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
            case DeIcon.ACTION_TRANSFER_MONEY:
                Scenes.sceneTransferMoneyDialog.create();
                Scenes.sceneTransferMoneyDialog.dialog.setEntities(mainEntity, selectedEntity);
                break;
            case DeIcon.ACTION_BUY_HEXES:
                Scenes.sceneDiplomacy.hide();
                enableAreaSelectionMode(selectedEntity.color);
                doAreaSelectRandomHex(); // to show player
                break;
        }
    }


    public void enableAreaSelectionMode(int filterColor) {
        GameController gameController = fieldController.gameController;
        Province province = gameController.fieldController.findProvince(0);
        Hex capital = province.getCapital();
        gameController.selectionController.setAreaSelectionMode(true);
        gameController.selectionController.setAsFilterColor(filterColor);
        MoveZoneManager moveZoneManager = fieldController.moveZoneManager;
        moveZoneManager.detectAndShowMoveZone(capital, 0, 0);
        moveZoneManager.clear();

        Scenes.sceneAreaSelectionUI.create();
    }


    public void doAreaSelectRandomHex() {
        Hex hex;
        if (isThereAtLeastOneFilteredHexInViewFrame()) {
            hex = getRandomFilteredHex(true);
        } else {
            hex = getRandomFilteredHex(false);
        }

        if (hex == null) return;

        MoveZoneManager moveZoneManager = fieldController.moveZoneManager;
        moveZoneManager.addHexToMoveZoneManually(hex);
    }





    private Hex getRandomFilteredHex(boolean inViewFrame) {
        int asFilterColor = fieldController.gameController.selectionController.getAsFilterColor();
        int index;
        int c = 100;
        while (c > 0) {
            c--;
            index = YioGdxGame.random.nextInt(fieldController.provinces.size());
            Province province = fieldController.provinces.get(index);
            if (province.getColor() != asFilterColor) continue;

            c = 100;
            while (c > 0) {
                c--;
                index = YioGdxGame.random.nextInt(province.hexList.size());
                Hex hex = province.hexList.get(index);
                if (!isHexInViewFrame(hex) && inViewFrame) continue;

                return hex;
            }
        }

        return null;
    }


    private boolean isThereAtLeastOneFilteredHexInViewFrame() {
        int asFilterColor = fieldController.gameController.selectionController.getAsFilterColor();
        for (Province province : fieldController.provinces) {
            if (province.getColor() != asFilterColor) continue;
            for (Hex hex : province.hexList) {
                if (!isHexInViewFrame(hex)) continue;
                return true;
            }
        }

        return false;
    }


    private boolean isHexInViewFrame(Hex hex) {
        return fieldController.gameController.cameraController.frame.isPointInside(hex.pos, 0);
    }


    public void disableAreaSelectionMode() {
        GameController gameController = fieldController.gameController;
        gameController.selectionController.setAreaSelectionMode(false);
        fieldController.moveZoneManager.hide();
        Scenes.sceneAreaSelectionUI.hide();
    }


    public int calculatePriceForHexes(ArrayList<Hex> hexList) {
        int price = 0;

        for (Hex hex : hexList) {
            if (hex.containsTower()) {
                price += 40;
            }

            if (hex.objectInside == Obj.FARM) {
                price += 75;
            }

            if (hex.objectInside == Obj.TOWN) {
                price += 500;
            }

            if (hex.containsTree()) {
                price -= 10;
            }

            if (hex.containsUnit()) {
                price += 15 * hex.unit.strength;
            }

            price += 25;
        }

        return price;
    }


    public void onUserRequestedBuyHexes(DiplomaticEntity initiator, DiplomaticEntity entity, ArrayList<Hex> hexList, int price) {
        if (initiator.getStateFullMoney() < price) return;

        transferMoney(initiator, entity, price);

        for (Hex hex : hexList) {
            int objectInside = hex.objectInside;
            int unitStrength = -1;
            if (hex.containsUnit()) {
                unitStrength = hex.unit.strength;
            }

            fieldController.setHexColor(hex, initiator.color);
            fieldController.gameController.replayManager.onHexChangedColorWithoutObviousReason(hex);

            if (objectInside > 0) {
                hex.setObjectInside(objectInside);
            } else if (unitStrength > 0) {
                fieldController.addUnit(hex, unitStrength);
            }
        }

        fieldController.tryToDetectAddiotionalProvinces();
    }


    public void onUserRequestedBlackMark(DiplomaticEntity selectedEntity) {
        makeBlackMarked(getMainEntity(), selectedEntity);
    }


    public void makeBlackMarked(DiplomaticEntity initiator, DiplomaticEntity entity) {
        log.addMessage(DipMessageType.black_marked, initiator, entity);

        addContract(DiplomaticContract.TYPE_BLACK_MARK, initiator, entity);

        onRelationsChanged();
    }


    public void requestedFriendship(DiplomaticEntity sender, DiplomaticEntity recipient) {
        DiplomaticEntity mainEntity = getMainEntity();

        if (mainEntity == sender) {
            log.addMessage(DipMessageType.friendship_proposal, sender, recipient);
            showLetterSentNotification();
        } else {
            if (!recipient.acceptsFriendsRequest(sender)) return;

            makeFriends(sender, recipient);
        }
    }


    public void onUserRequestedToMakeRelationsWorse(DiplomaticEntity selectedEntity) {
        onEntityRequestedToMakeRelationsWorse(getMainEntity(), selectedEntity);
    }


    void onEntityRequestedToMakeRelationsWorse(DiplomaticEntity initiator, DiplomaticEntity entity) {
        int previousRelation = initiator.getRelation(entity);

        if (previousRelation == DiplomaticRelation.FRIEND) {
            punishFriendshipTraitor(initiator, entity);
        }

        requestWorseRelations(initiator, entity);

        int relation = initiator.getRelation(entity);
        if (relation == previousRelation) return;

        if (relation == DiplomaticRelation.ENEMY) {
            onWarStarted(initiator, entity);
        }
    }


    private void punishFriendshipTraitor(DiplomaticEntity initiator, DiplomaticEntity entity) {
        int stateBalance = initiator.getStateBalance();
        if (stateBalance <= 0) return;

        addContract(DiplomaticContract.TYPE_TRAITOR, initiator, entity);
        onRelationsChanged();
    }


    public void onEntityRequestedToStopWar(DiplomaticEntity initiator, DiplomaticEntity entity) {
        addContract(DiplomaticContract.TYPE_PIECE, initiator, entity);
        makeNeutral(initiator, entity);
        initiator.pay(calculatePayToStopWar(initiator, entity));
    }


    public void onUserRequestedToStopWar(DiplomaticEntity user, DiplomaticEntity recipient) {
        log.addMessage(DipMessageType.stop_war, user, recipient);
        showLetterSentNotification();
    }


    public void onContractExpired(DiplomaticContract contract) {
        removeContract(contract);

        if (contract.type == DiplomaticContract.TYPE_FRIENDSHIP) {
            int relation = contract.one.getRelation(contract.two);

            log.addMessage(DipMessageType.friendship_ended, contract.one, contract.two);

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

        log.checkToClearAbuseMessages();

        DiplomacyElement diplomacyElement = Scenes.sceneDiplomacy.diplomacyElement;
        if (diplomacyElement != null) {
            diplomacyElement.onTurnStarted();
        }

        if (fieldController.gameController.isPlayerTurn()) {
            onHumanTurnStarted();
        } else {
            onAiTurnStarted();
        }

        moveCooldowns();
    }


    private void onAiTurnStarted() {
        if (!getMainEntity().alive) return;

        aiProcessMessages();

        if (YioGdxGame.random.nextInt(8) == 0) {
            performAiToHumanFriendshipProposal();
        }

        if (YioGdxGame.random.nextInt(4) == 0) {
            performAiToHumanBlackMark();
        }

        if (getMainEntity().getStateFullMoney() > 100 && YioGdxGame.random.nextInt(10) == 0) {
            performAiToHumanGift();
        }

        if (DebugFlags.cheatCharisma) {
            applyCharismaCheat();
        }
    }


    private void applyCharismaCheat() {
        DiplomaticEntity mainEntity = getMainEntity();
        DiplomaticEntity randomHumanEntity = getRandomHumanEntity();
        if (randomHumanEntity == null) return;

        transferMoney(mainEntity, randomHumanEntity, mainEntity.getStateFullMoney() / 2);
    }


    private void performAiToHumanGift() {
        DiplomaticEntity mainEntity = getMainEntity();
        DiplomaticEntity randomHumanEntity = getRandomHumanEntity();
        if (randomHumanEntity == null) return;

        transferMoney(mainEntity, randomHumanEntity, 10 + YioGdxGame.random.nextInt(11));
    }


    private void aiProcessMessages() {
        DiplomaticEntity mainEntity = getMainEntity();

        for (int i = log.messages.size() - 1; i >= 0; i--) {
            DiplomaticMessage message = log.messages.get(i);

            if (message.recipient != mainEntity) continue;

            switch (message.type) {
                case friendship_proposal:
                    if (isFriendshipPossible(message.sender, message.recipient)) {
                        makeFriends(message.sender, message.recipient);
                    }
                    break;
                case stop_war:
                    onEntityRequestedToStopWar(message.sender, message.recipient);
                    break;
            }

            log.removeMessage(message);
        }
    }


    private void moveCooldowns() {
        if (fieldController.gameController.turn != 0) return;

        for (DiplomaticCooldown cooldown : cooldowns) {
            cooldown.decreaseCounter();
        }

        checkToRemoveCooldowns();
    }


    private void checkToRemoveCooldowns() {
        if (fieldController.gameController.turn != 0) return;

        for (int i = cooldowns.size() - 1; i >= 0; i--) {
            DiplomaticCooldown cooldown = cooldowns.get(i);
            if (!cooldown.isReady()) continue;

            removeCooldown(cooldown);
        }
    }


    private void removeCooldown(DiplomaticCooldown cooldown) {
        cooldowns.remove(cooldown);
        poolCooldowns.addWithCheck(cooldown);
    }


    public boolean checkForStopWarCooldown(DiplomaticEntity one, DiplomaticEntity two) {
        for (DiplomaticCooldown cooldown : cooldowns) {
            if (cooldown.type != DiplomaticCooldown.TYPE_STOP_WAR) continue;
            if (!cooldown.contains(one)) continue;
            if (!cooldown.contains(two)) continue;
            if (cooldown.isReady()) continue;

            return false;
        }

        return true;
    }


    private void onHumanTurnStarted() {

    }


    public void performAiToHumanBlackMark() {
        DiplomaticEntity aiEntity = findAiEntityThatIsCloseToWin();
        if (aiEntity == null) return;

        DiplomaticEntity randomHumanEntity = getRandomHumanEntity();
        if (randomHumanEntity == null) return;

        int relation = aiEntity.getRelation(randomHumanEntity);
        if (relation == DiplomaticRelation.FRIEND) return;
        if (randomHumanEntity.isBlackMarkedWith(aiEntity)) return;

        log.addMessage(DipMessageType.black_marked, aiEntity, randomHumanEntity);

        makeBlackMarked(aiEntity, randomHumanEntity);
    }


    public boolean performAiToHumanFriendshipProposal() {
        DiplomaticEntity humanEntity = getRandomHumanEntity();
        if (humanEntity == null) return false;
        if (humanEntity.isOneFriendAwayFromDiplomaticVictory()) return false;
        if (!humanEntity.alive) return false;

        for (int i = 0; i < 25; i++) {
            DiplomaticEntity randomEntity = getRandomEntity();
            if (!randomEntity.alive) continue;
            if (randomEntity.isHuman()) continue;
            if (randomEntity.isOneFriendAwayFromDiplomaticVictory()) continue; // no tricky friend requests

            int relation = humanEntity.getRelation(randomEntity);
            if (relation != DiplomaticRelation.NEUTRAL) continue;
            if (!humanEntity.acceptsFriendsRequest(randomEntity)) continue;

            log.addMessage(DipMessageType.friendship_proposal, randomEntity, humanEntity);

//            Scenes.sceneFriendshipDialog.create();
//            Scenes.sceneFriendshipDialog.dialog.setEntities(humanEntity, randomEntity);
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

        log.removeMessagesByRecipient(entity);

        if (fieldController.gameController.turn == 0) {
            onFirstPlayerTurnEnded();
        }
    }


    private void checkToChangeRelations() {
        DiplomaticEntity mainEntity = getMainEntity();
        if (mainEntity.isHuman()) return;
        if (!mainEntity.alive) return;

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
        if (isHexSingle(hex)) return true;

        DiplomaticEntity one = getEntity(unitColor);
        DiplomaticEntity two = getEntity(hex.colorIndex);

        if (one == null || two == null) return true;

        int relation = one.getRelation(two);

        return relation == DiplomaticRelation.ENEMY;
    }


    private boolean isHexSingle(Hex hex) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (adjacentHex == null) continue;
            if (adjacentHex == fieldController.nullHex) continue;
            if (adjacentHex.sameColor(hex)) return false;
        }

        return true;
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


    public void transferMoney(DiplomaticEntity sender, DiplomaticEntity recipient, int value) {
        int senderMoney = sender.getStateFullMoney();
        int recipientMoney = recipient.getStateFullMoney();

        if (value > senderMoney) {
            value = senderMoney;
        }

        DiplomaticMessage diplomaticMessage = log.addMessage(DipMessageType.gift, sender, recipient);
        diplomaticMessage.setArg1("" + value);

        float f;
        for (Province province : fieldController.provinces) {
            int money = province.money;

            if (province.getColor() == sender.color) {
                f = (float) money / (float) senderMoney;
                province.money -= f * value;
                continue;
            }

            if (province.getColor() == recipient.color) {
                f = (float) money / (float) recipientMoney;
                province.money += f * value;
            }
        }
    }


    public DiplomaticEntity getRandomHumanEntity() {
        if (!isAtLeastOneHumanEntity()) return null;

        while (true) {
            DiplomaticEntity randomEntity = getRandomEntity();
            if (randomEntity.isHuman()) {
                return randomEntity;
            }
        }
    }


    private boolean isAtLeastOneHumanEntity() {
        for (DiplomaticEntity entity : entities) {
            if (entity.isHuman()) {
                return true;
            }
        }

        return false;
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


    public void showLetterSentNotification() {
        Scenes.sceneNotification.showNotification("letter_sent");
    }


    void requestBetterRelations(DiplomaticEntity initiator, DiplomaticEntity two) {
        int relation = initiator.getRelation(two);

        if (relation == DiplomaticRelation.ENEMY) {
            if (canWarBeStopped(initiator, two)) {
                Scenes.sceneStopWarDialog.create();
                Scenes.sceneStopWarDialog.dialog.setEntities(initiator, two);
            } else {
                Scenes.sceneDipMessage.create();
                Scenes.sceneDipMessage.dialog.setMessage(two.capitalName, "refuse_stop_war");
            }
        }

        if (relation == DiplomaticRelation.NEUTRAL) {
            if (isFriendshipPossible(initiator, two)) {
                Scenes.sceneFriendshipDialog.create();
                Scenes.sceneFriendshipDialog.dialog.setEntities(initiator, two);
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
        if (!checkForStopWarCooldown(one, two)) return false;

        return one.acceptsToStopWar(two) && two.acceptsToStopWar(one);
    }


    void requestWorseRelations(DiplomaticEntity initiator, DiplomaticEntity two) {
        int relation = two.getRelation(initiator);

        if (relation == DiplomaticRelation.FRIEND) {
            log.addMessage(DipMessageType.friendship_canceled, initiator, two);

            makeNeutral(two, initiator);
        }

        if (relation == DiplomaticRelation.NEUTRAL) {
            log.addMessage(DipMessageType.war_declaration, initiator, two);

            makeEnemies(initiator, two);
        }
    }


    private void onWarStarted(DiplomaticEntity initiator, DiplomaticEntity one) {
        punishAggressor(initiator, one);
        addCooldown(DiplomaticCooldown.TYPE_STOP_WAR, 10, initiator, one);
    }


    private void punishAggressor(DiplomaticEntity initiator, DiplomaticEntity one) {
        for (Map.Entry<DiplomaticEntity, Integer> entry : initiator.relations.entrySet()) {
            DiplomaticEntity entity = entry.getKey();
            if (one.isFriendTo(entity)) {
                requestWorseRelations(initiator, entity);
            }
        }
    }


    DiplomaticCooldown addCooldown(int type, int counter, DiplomaticEntity one, DiplomaticEntity two) {
        DiplomaticCooldown next = poolCooldowns.getNext();

        next.setType(type);
        next.setCounter(counter);
        next.setOne(one);
        next.setTwo(two);

        cooldowns.add(next);

        if (cooldowns.size() > 25) {
            cooldowns.remove(0);
        }

        return next;
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
            case DiplomaticContract.TYPE_TRAITOR:
                return calculateTraitorFine(initiator);
        }
    }


    public int calculateTraitorFine(DiplomaticEntity initiator) {
        int stateBalance = initiator.getStateBalance();

        return -stateBalance / 3;
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

        return -stateBalance / 2;
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

        if (GameRules.fogOfWarEnabled) {
            fieldController.fogOfWarManager.updateFog();
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


    public void showCooldownsInConsole(int colorFilter) {
        System.out.println();
        System.out.println("DiplomacyManager.showCooldownsInConsole");
        DiplomaticEntity entity = getEntity(colorFilter);
        for (DiplomaticCooldown cooldown : cooldowns) {
            if (entity != null && !cooldown.contains(entity)) continue;
            System.out.println("- " + cooldown);
        }
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
