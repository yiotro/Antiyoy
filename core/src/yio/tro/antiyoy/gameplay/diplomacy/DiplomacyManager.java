package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.KeyboardManager;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangePerformer;
import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeType;
import yio.tro.antiyoy.gameplay.replays.ReplayManager;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.SingleMessages;
import yio.tro.antiyoy.menu.diplomacy_element.DipActionType;
import yio.tro.antiyoy.menu.diplomacy_element.DiplomacyElement;
import yio.tro.antiyoy.menu.diplomatic_exchange.ExchangeUiElement;
import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.Yio;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiplomacyManager {

    public FieldManager fieldManager;
    public ArrayList<DiplomaticEntity> entities;
    ObjectPoolYio<DiplomaticEntity> poolEntities;
    public ArrayList<DiplomaticContract> contracts;
    ObjectPoolYio<DiplomaticContract> poolContracts;
    public ArrayList<DiplomaticCooldown> cooldowns;
    ObjectPoolYio<DiplomaticCooldown> poolCooldowns;
    public DiplomaticLog log;
    HashMap<Hex, Integer> tempMap;
    private ArrayList<Hex> tempHexList;
    public DiplomaticAI diplomaticAI;
    public ExchangePerformer exchangePerformer;


    public DiplomacyManager(FieldManager fieldManager) {
        this.fieldManager = fieldManager;

        entities = new ArrayList<>();
        contracts = new ArrayList<>();
        cooldowns = new ArrayList<>();
        log = new DiplomaticLog(this);
        tempMap = new HashMap<>();
        tempHexList = new ArrayList<>();
        diplomaticAI = new DiplomaticAI(this);
        exchangePerformer = new ExchangePerformer(this);

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
        updateAllAliveStatuses();

        updateDiplomacyElement();
    }


    public void updateDiplomacyElement() {
        DiplomacyElement diplomacyElement = Scenes.sceneDiplomacy.diplomacyElement;
        if (diplomacyElement == null) return;
        diplomacyElement.updateAll();
    }


    void clearCooldowns() {
        for (DiplomaticCooldown cooldown : cooldowns) {
            poolCooldowns.add(cooldown);
        }

        cooldowns.clear();
    }


    void clearContracts() {
        for (DiplomaticContract contract : contracts) {
            poolContracts.addWithCheck(contract);
        }

        contracts.clear();
    }


    public void resetDebts() {
        for (DiplomaticEntity entity : entities) {
            entity.resetDebts();
        }
    }


    public void checkForWinConditionsMessage() {
        if (SingleMessages.diplomacyWinConditions) return;

        SingleMessages.diplomacyWinConditions = true;
        SingleMessages.save();

        Scenes.sceneDipMessage.showMessage("win_conditions", "diplomatic_win_conditions");
    }


    public void onDiplomaticLogButtonPressed() {
        fieldManager.gameController.selectionManager.deselectAll();

        if (!log.hasSomethingToRead()) {
            System.out.println("DiplomacyManager.onDiplomaticLogButtonPressed: log button shouldn't be visible when log is empty");
            return;
        }

        Scenes.sceneDiplomaticLog.create();
    }


    public void onDiplomacyButtonPressed() {
        fieldManager.gameController.selectionManager.deselectAll();

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

        for (int fraction = 0; fraction < GameRules.fractionsQuantity; fraction++) {
            if (fraction == GameRules.NEUTRAL_FRACTION) continue;
            DiplomaticEntity next = poolEntities.getNext();

            next.setFraction(fraction);
            next.updateCapitalName();
            next.setHuman(fieldManager.gameController.isPlayerTurn(fraction));

            entities.add(next);
        }

        onEntitiesUpdated();
    }


    private void onEntitiesUpdated() {
        for (DiplomaticEntity entity : entities) {
            entity.initRelations();
            entity.initDebts();
        }
    }


    public void changeDebt(DiplomaticEntity source, DiplomaticEntity target, int delta) {
        source.getDebt(target).value += delta;
        target.getDebt(source).value -= delta;
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

        return bestEntity.fraction;
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


    public void onUserClickedContextIcon(int selectedFraction, DipActionType action) {
        DiplomaticEntity mainEntity = getMainEntity();
        DiplomaticEntity selectedEntity = getEntity(selectedFraction);

        switch (action) {
            case like:
                requestBetterRelations(mainEntity, selectedEntity);
                break;
            case dislike:
                Scenes.sceneConfirmDislike.create();
                Scenes.sceneConfirmDislike.dialog.setSelectedEntity(selectedEntity);
                break;
            case black_mark:
                Scenes.sceneConfirmBlackMarkDialog.create();
                Scenes.sceneConfirmBlackMarkDialog.dialog.setSelectedEntity(selectedEntity);
                break;
            case info:
                Scenes.sceneDiplomacy.hide();
                Scenes.sceneDiplomaticRelations.create();
                Scenes.sceneDiplomaticRelations.setChosenFraction(selectedFraction);
                break;
            case transfer_money:
                Scenes.sceneTransferMoneyDialog.create();
                Scenes.sceneTransferMoneyDialog.dialog.setEntities(mainEntity, selectedEntity);
                break;
            case buy_hexes:
                showExchangeElement(
                        mainEntity, selectedEntity,
                        ExchangeType.lands, ExchangeType.money
                );
                break;
            case mail:
                applySendCustomLetter(mainEntity, selectedEntity);
                break;
            case attack:
                showExchangeElement(
                        mainEntity, selectedEntity,
                        ExchangeType.war_declaration, ExchangeType.money
                );
                break;
            case exchange:
                showExchangeElement(
                        mainEntity, selectedEntity,
                        ExchangeType.nothing, ExchangeType.nothing
                );
                break;
        }
    }


    private void prepareToSendAttackProposition(DiplomaticEntity mainEntity, DiplomaticEntity selectedEntity) {
        Scenes.sceneDiplomacy.hide();
        Scenes.scenePrepareForAttackProposition.create();
        Scenes.scenePrepareForAttackProposition.dialog.setEntities(mainEntity, selectedEntity);
    }


    private void applySendCustomLetter(final DiplomaticEntity mainEntity, final DiplomaticEntity selectedEntity) {
        Scenes.sceneDiplomacy.hide();
        KeyboardManager.getInstance().apply(new AbstractKbReaction() {
            @Override
            public void onInputFromKeyboardReceived(String input) {
                if (input.length() == 0) return;
                DiplomaticMessage diplomaticMessage = log.addMessage(DipMessageType.message, mainEntity, selectedEntity);
                diplomaticMessage.setArg1(input);
                showLetterSentNotification();
            }
        });
    }


    public void enableAreaSelectionMode(int filterFraction) {
        GameController gameController = fieldManager.gameController;
        Province province = gameController.fieldManager.findProvince(gameController.turn);
        if (province == null) return;
        Hex capital = province.getCapital();
        gameController.selectionManager.setAreaSelectionMode(true);
        gameController.selectionManager.setAsFilterFraction(filterFraction);
        MoveZoneManager moveZoneManager = fieldManager.moveZoneManager;
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
        if (fieldManager.fogOfWarManager.isHexCoveredByFog(hex)) return;

        MoveZoneManager moveZoneManager = fieldManager.moveZoneManager;
        moveZoneManager.addHexToMoveZoneManually(hex);
    }


    private Hex getRandomFilteredHex(boolean inViewFrame) {
        int filterFraction = fieldManager.gameController.selectionManager.getAsFilterFraction();
        int index;
        int c = 100;
        while (c > 0) {
            c--;
            index = YioGdxGame.random.nextInt(fieldManager.provinces.size());
            Province province = fieldManager.provinces.get(index);
            if (province.getFraction() != filterFraction) continue;

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
        int filterFraction = fieldManager.gameController.selectionManager.getAsFilterFraction();
        for (Province province : fieldManager.provinces) {
            if (province.getFraction() != filterFraction) continue;
            for (Hex hex : province.hexList) {
                if (!isHexInViewFrame(hex)) continue;
                return true;
            }
        }

        return false;
    }


    private boolean isHexInViewFrame(Hex hex) {
        return fieldManager.gameController.cameraController.frame.isPointInside(hex.pos, 0);
    }


    public void disableAreaSelectionMode() {
        GameController gameController = fieldManager.gameController;
        gameController.selectionManager.setAreaSelectionMode(false);
        fieldManager.moveZoneManager.hide();
        Scenes.sceneAreaSelectionUI.hide();
    }


    public int calculatePriceForHexes(ArrayList<Hex> hexList) {
        int price = 0;

        for (Hex hex : hexList) {
            if (!isHexNearList(hex, hexList)) continue;
            price += getHexPrice(hex);
        }

        return price;
    }


    public int getHexPrice(Hex hex) {
        if (hex.hasUnit()) {
            return 25 + 15 * hex.unit.strength;
        }

        switch (hex.objectInside) {
            default:
            case 0:
            case Obj.GRAVE:
                return 25;
            case Obj.PINE:
            case Obj.PALM:
                return 15;
            case Obj.TOWN:
                return getCapitalPrice(hex);
            case Obj.TOWER:
                return 50;
            case Obj.FARM:
                return 100;
            case Obj.STRONG_TOWER:
                return 75;
        }
    }


    private int getCapitalPrice(Hex hex) {
        Province provinceByHex = fieldManager.getProvinceByHex(hex);
        return 10 * provinceByHex.hexList.size();
    }


    private boolean isHexNearList(Hex hex, ArrayList<Hex> list) {
        if (list.size() > 15) return true;
        if (list.size() == 1) return list.get(0) == hex;
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (!isWorkable(adjacentHex)) continue;
            if (!list.contains(adjacentHex)) continue;
            return true;
        }
        return false;
    }


    boolean isWorkable(Hex hex) {
        if (hex == null) return false;
        if (hex.isNullHex()) return false;
        if (!hex.active) return false;
        return true;
    }


    public void applyHexPurchase(DiplomaticMessage message) {
        ArrayList<Hex> hexList = convertStringToHexList(message.arg1);
        int price = Integer.valueOf(message.arg2);

        switch (message.type) {
            case hex_purchase:
                applyHexPurchase(message.sender, message.recipient, hexList, price);
                break;
            case hex_sale:
                applyHexPurchase(message.recipient, message.sender, hexList, price);
                break;
        }
    }


    public void applyHexPurchase(DiplomaticEntity buyer, DiplomaticEntity seller, ArrayList<Hex> hexList, int price) {
        if (buyer.getStateFullMoney() < price) {
            Scenes.sceneNotification.show("buyer_not_enough_money");
            return;
        }

        transferMoney(buyer, seller, price);
        transferLands(seller, buyer, hexList);
    }


    public void transferLands(DiplomaticEntity src, DiplomaticEntity dst, ArrayList<Hex> hexList) {
        updateTempMap(hexList);

        for (Hex hex : hexList) {
            if (!hex.sameFraction(src.fraction)) continue;

            int objectInside = tempMap.get(hex);
            int unitStrength = -1;
            if (hex.containsUnit()) {
                unitStrength = hex.unit.strength;
            }

            fieldManager.setHexFraction(hex, dst.fraction);
            ReplayManager replayManager = fieldManager.gameController.replayManager;
            replayManager.onHexChangedFractionWithoutObviousReason(hex);

            if (unitStrength > 0) {
                Unit unit = fieldManager.addUnit(hex, unitStrength);
                if (unit != null) {
                    unit.setReadyToMove(false);
                    unit.stopJumping();
                }
                replayManager.onUnitSpawned(hex, unitStrength);
                continue;
            }

            if (objectInside > 0 && objectInside != Obj.TOWN) {
                fieldManager.addSolidObject(hex, objectInside);

                switch (objectInside) {
                    case Obj.PINE:
                        replayManager.onPineSpawned(hex);
                        break;
                    case Obj.PALM:
                        replayManager.onPalmSpawned(hex);
                        break;
                    case Obj.FARM:
                        replayManager.onFarmBuilt(hex);
                        break;
                    case Obj.GRAVE:
                        replayManager.onUnitDiedFromStarvation(hex);
                        break;
                    case Obj.STRONG_TOWER:
                        replayManager.onTowerBuilt(hex, true);
                        break;
                    case Obj.TOWER:
                        replayManager.onTowerBuilt(hex, false);
                        break;
                }
            }
        }

        fieldManager.tryToDetectAdditionalProvinces();
        stopLoneUnitsAroundHexList(hexList);
    }


    private void stopLoneUnitsAroundHexList(ArrayList<Hex> hexList) {
        for (Hex hex : hexList) {
            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (!isLoneHexWithUnit(adjacentHex)) continue;
                if (hexList.contains(adjacentHex)) continue;

                Unit unit = adjacentHex.unit;
                unit.setReadyToMove(false);
                unit.stopJumping();
            }
        }
    }


    private boolean isLoneHexWithUnit(Hex hex) {
        if (hex == null) return false;
        if (hex.isNullHex()) return false;
        if (!hex.active) return false;
        if (hex.isNeutral()) return false;
        if (!hex.hasUnit()) return false;

        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (adjacentHex == null) continue;
            if (adjacentHex.isNullHex()) continue;
            if (!adjacentHex.active) continue;
            if (adjacentHex.fraction != hex.fraction) continue;
            return false;
        }

        return true;
    }


    public void onEntityRequestedHexSell(DiplomaticEntity initiator, DiplomaticEntity entity, ArrayList<Hex> hexList, int price) {
        log.addMessage(DipMessageType.hex_sale, initiator, entity)
                .setArg1(convertHexListToString(hexList))
                .setArg2("" + price);

        showLetterSentNotification();
    }


    public void onEntityRequestedHexPurchase(DiplomaticEntity initiator, DiplomaticEntity entity, ArrayList<Hex> hexList, int price) {
        if (initiator.getStateFullMoney() < price) return;

        log.addMessage(DipMessageType.hex_purchase, initiator, entity)
                .setArg1(convertHexListToString(hexList))
                .setArg2("" + price);

        showLetterSentNotification();
    }


    public ArrayList<Hex> convertStringToHexList(String source) {
        tempHexList.clear();

        for (String token : source.split("@")) {
            String[] split = token.split("%");
            if (split.length < 2) continue;
            if (!Yio.isNumeric(split[0])) continue;
            int index1 = Integer.valueOf(split[0]);
            if (!Yio.isNumeric(split[1])) continue;
            int index2 = Integer.valueOf(split[1]);
            Hex hex = fieldManager.getHex(index1, index2);
            if (hex == null) continue;
            tempHexList.add(hex);
        }

        return tempHexList;
    }


    public String convertHexListToString(ArrayList<Hex> hexList) {
        StringBuilder builder = new StringBuilder();

        for (Hex hex : hexList) {
            builder.append(hex.index1).append("%").append(hex.index2).append("@");
        }

        return builder.toString();
    }


    private void updateTempMap(ArrayList<Hex> hexList) {
        tempMap.clear();

        for (Hex hex : hexList) {
            tempMap.put(hex, hex.objectInside);
        }
    }


    public void onUserRequestedBlackMark(DiplomaticEntity selectedEntity) {
        makeBlackMarked(getMainEntity(), selectedEntity);
    }


    public void removeBlackMark(DiplomaticEntity one, DiplomaticEntity two) {
        DiplomaticContract contract = getContract(DiplomaticContract.TYPE_BLACK_MARK, one, two);
        removeContract(contract);
        addContract(DiplomaticContract.TYPE_FORBID_BLACK_MARK, one, two);
        onRelationsChanged();
    }


    public void makeBlackMarked(DiplomaticEntity initiator, DiplomaticEntity entity) {
        log.addMessage(DipMessageType.black_marked, initiator, entity);

        addContract(DiplomaticContract.TYPE_BLACK_MARK, initiator, entity);

        onRelationsChanged();
    }


    public void requestedFriendship(DiplomaticEntity sender, DiplomaticEntity recipient) {
        DiplomaticEntity mainEntity = getMainEntity();

        if (mainEntity == sender) {
            DiplomaticMessage diplomaticMessage = log.addMessage(DipMessageType.friendship_proposal, sender, recipient);
            if (diplomaticMessage == null) {
                diplomaticMessage = log.getSimilarMessage(DipMessageType.friendship_proposal, sender, recipient);
            }
            diplomaticMessage.setArg1(calculateDotationsForFriendship(sender, recipient) + "");
            showLetterSentNotification();
        } else {
            if (!recipient.acceptsFriendsRequest(sender)) return;

            makeFriends(sender, recipient);
        }
    }


    public void onUserRequestedToMakeRelationsWorse(DiplomaticEntity selectedEntity) {
        onEntityRequestedToMakeRelationsWorse(getMainEntity(), selectedEntity);
    }


    public void onEntityRequestedToMakeRelationsWorse(DiplomaticEntity initiator, DiplomaticEntity entity) {
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

            DiplomaticMessage diplomaticMessage = log.addMessage(DipMessageType.friendship_ended, contract.one, contract.two);
            int dotations = calculateDotationsForFriendship(contract.two, contract.one);
            diplomaticMessage.setArg1("" + dotations);

            if (relation == DiplomaticRelation.FRIEND) {
                makeNeutral(contract.one, contract.two);
            }
        }
    }


    public void updateEntityAliveStatus(int fraction) {
        if (!GameRules.diplomacyEnabled) return;

        DiplomaticEntity entity = getEntity(fraction);
        if (entity != null) {
            entity.updateAliveState();
        }
    }


    public void updateAllAliveStatuses() {
        for (DiplomaticEntity entity : entities) {
            entity.updateAliveState();
        }
    }


    public void onTurnStarted() {
        if (!GameRules.diplomacyEnabled) return;

        log.removeInvalidMessages();

        DiplomacyElement diplomacyElement = Scenes.sceneDiplomacy.diplomacyElement;
        if (diplomacyElement != null) {
            diplomacyElement.onTurnStarted();
        }

        if (fieldManager.gameController.isPlayerTurn()) {
            onHumanTurnStarted();
        } else {
            onAiTurnStarted();
        }

        payDebts();
        moveCooldowns();
    }


    private void payDebts() {
        DiplomaticEntity mainEntity = getMainEntity();
        int debtPaySum = getMainEntity().getDebtPaySum();
        if (debtPaySum == 0) return;

        if (mainEntity.getStateFullMoney() >= debtPaySum) {
            for (Debt debt : mainEntity.debts) {
                if (!debt.isCurrentlyActive()) continue;
                payDebt(debt.source, debt.target, debt.value);
            }
            return;
        }

        payDebtsWhenMainEntityDoesntHaveEnoughMoney();
    }


    public void payDebt(DiplomaticEntity source, DiplomaticEntity target, int payValue) {
        transferMoney(source, target, payValue);
        changeDebt(source, target, -payValue);
    }


    private void payDebtsWhenMainEntityDoesntHaveEnoughMoney() {
        DiplomaticEntity mainEntity = getMainEntity();
        while (mainEntity.getStateFullMoney() > 0) {
            Debt biggestDebtToPay = mainEntity.getBiggestDebtToPay();
            if (biggestDebtToPay == null) break;
            if (biggestDebtToPay.value == 0) break;
            int payValue = Math.min(mainEntity.getStateFullMoney(), biggestDebtToPay.value);
            payDebt(mainEntity, biggestDebtToPay.target, payValue);
        }
    }


    public boolean isBlackMarkAllowed(DiplomaticEntity entity1, DiplomaticEntity entity2) {
        if (entity1.isBlackMarkedWith(entity2)) return false;
        for (DiplomaticContract contract : contracts) {
            if (!contract.equals(entity1, entity2, DiplomaticContract.TYPE_FORBID_BLACK_MARK)) continue;
            return false;
        }
        return true;
    }


    public void updateAllNames() {
        for (DiplomaticEntity entity : entities) {
            entity.updateCapitalName();
        }
    }


    private void onAiTurnStarted() {
        if (!getMainEntity().alive) return;

        diplomaticAI.onAiTurnStarted();
    }


    private void moveCooldowns() {
        if (fieldManager.gameController.turn != 0) return;

        for (DiplomaticCooldown cooldown : cooldowns) {
            cooldown.decreaseCounter();
        }

        checkToRemoveCooldowns();
    }


    private void checkToRemoveCooldowns() {
        if (fieldManager.gameController.turn != 0) return;

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


    public void onTurnEnded() {
        if (!GameRules.diplomacyEnabled) return;

        DiplomaticEntity entity = getEntity(fieldManager.gameController.turn);
        entity.updateAliveState();

        log.removeMessagesByRecipient(entity, true);

        if (fieldManager.gameController.turn == 0) {
            onFirstPlayerTurnEnded();
        }
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


    public boolean canUnitAttackHex(int unitStrength, int unitFraction, Hex hex) {
        boolean rulesetDecision = fieldManager.gameController.ruleset.canUnitAttackHex(unitStrength, hex);
        if (hex.isNeutral() || isHexSingle(hex)) return rulesetDecision;

        DiplomaticEntity attacker = getEntity(unitFraction);
        DiplomaticEntity defender = getEntity(hex.fraction);

        if (attacker == null || defender == null) return rulesetDecision;

        int relation = attacker.getRelation(defender);

        switch (relation) {
            default:
            case DiplomaticRelation.ENEMY:
                return rulesetDecision;
            case DiplomaticRelation.NEUTRAL:
                return false;
            case DiplomaticRelation.FRIEND:
                return false;
        }
    }


    private boolean isHexSingle(Hex hex) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (adjacentHex == null) continue;
            if (adjacentHex == fieldManager.nullHex) continue;
            if (!adjacentHex.active) continue;
            if (!adjacentHex.sameFraction(hex)) continue;

            return false;
        }

        return true;
    }


    public boolean isProvinceAllowedToBuildUnit(Province province, int unitStrength) {
        int fraction = province.getFraction();
        DiplomaticEntity entity = getEntity(fraction);

        if (entity.isAtWar()) return true;

        // peace
        if (unitStrength > 1) return false;
        if (numberOfPeasantsInProvince(province) > 4) return false;

        return true;
    }


    public boolean areKingdomsTouching(DiplomaticEntity one, DiplomaticEntity two) {
        return areKingdomsTouching(one.fraction, two.fraction);
    }


    public boolean areKingdomsTouching(int fraction1, int fraction2) {
        for (Province province : fieldManager.provinces) {
            if (province.getFraction() != fraction1) continue;
            if (!province.isNearFraction(fraction2)) continue;
            return true;
        }
        return false;
    }


    private int numberOfPeasantsInProvince(Province province) {
        int c = 0;

        for (Hex hex : province.hexList) {
            if (!hex.containsUnit()) continue;
            if (hex.unit.strength != 1) continue;

            c++;
        }

        return c;
    }


    public int calculateDotationsForFriendship(DiplomaticEntity initiator, DiplomaticEntity entity) {
        int money1 = entity.getStateProfit() * Math.max(1, entity.getNumberOfFriends());
        int money2 = initiator.getStateProfit() * Math.max(1, initiator.getNumberOfFriends());
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
        int fraction = province.getFraction();
        DiplomaticEntity entity = getEntity(fraction);
        int stateDotations = entity.getStateDotations();

        return (int) (province.getIncomeCoefficient() * stateDotations);
    }


    public void transferMoney(DiplomaticEntity sender, DiplomaticEntity recipient, int value) {
        transferMoney(sender, recipient, value, false);
    }


    public void transferMoney(DiplomaticEntity sender, DiplomaticEntity recipient, int value, boolean gift) {
        int senderMoney = sender.getStateFullMoney();
        int recipientMoney = recipient.getStateFullMoney();

        if (value > senderMoney) {
            value = senderMoney;
        }

        if (gift) {
            DiplomaticMessage diplomaticMessage = log.addMessage(DipMessageType.gift, sender, recipient);
            if (diplomaticMessage == null) return;
            diplomaticMessage.setArg1("" + value);
        }

        float f;
        for (Province province : fieldManager.provinces) {
            int money = province.money;

            if (province.getFraction() == sender.fraction) {
                f = (float) money / (float) senderMoney;
                province.money -= f * value;
                continue;
            }

            if (province.getFraction() == recipient.fraction) {
                f = (float) money / (float) recipientMoney;
                province.money += f * value;
            }
        }
    }


    public DiplomaticEntity getMainEntity() {
        int turn = fieldManager.gameController.turn;
        return getEntity(turn);
    }


    public DiplomaticEntity getRandomEntity() {
        int size = entities.size();
        int index = YioGdxGame.random.nextInt(size);
        return entities.get(index);
    }


    public void showLetterSentNotification() {
        Scenes.sceneNotification.show("letter_sent");
    }


    void requestBetterRelations(DiplomaticEntity initiator, DiplomaticEntity two) {
        int relation = initiator.getRelation(two);

        if (relation == DiplomaticRelation.ENEMY) {
            showExchangeElement(
                    initiator, two,
                    ExchangeType.stop_war, ExchangeType.dotations
            );
        }

        if (relation == DiplomaticRelation.NEUTRAL) {
            showExchangeElement(
                    initiator, two,
                    ExchangeType.friendship, ExchangeType.dotations
            );

            int friendshipPrice = diplomaticAI.getFriendshipPrice(initiator);
            Scenes.sceneDiplomaticExchange.exchangeUiElement.applyOptimalDotaions(friendshipPrice);
        }
    }


    public ExchangeUiElement showExchangeElement(DiplomaticEntity sender, DiplomaticEntity receiver, ExchangeType topType, ExchangeType bottomType) {
        Scenes.sceneDiplomacy.hide();
        Scenes.sceneDiplomaticExchange.create();
        Scenes.sceneDiplomaticExchange.setParentScene(Scenes.sceneDiplomacy);
        ExchangeUiElement exchangeUiElement = Scenes.sceneDiplomaticExchange.exchangeUiElement;
        exchangeUiElement.resetData();
        exchangeUiElement.setMainEntity(sender);
        exchangeUiElement.setTargetEntity(receiver);
        exchangeUiElement.setReadMode(false);
        exchangeUiElement.topView.setExchangeType(topType);
        exchangeUiElement.bottomView.setExchangeType(bottomType);
        exchangeUiElement.updateSize();

        Scenes.sceneDiplomaticExchange.checkToShowQuickTutorial();

        return exchangeUiElement;
    }


    boolean isFriendshipPossible(DiplomaticEntity one, DiplomaticEntity two) {
        if (one.isOneFriendAwayFromDiplomaticVictory()) return false;
        if (two.isOneFriendAwayFromDiplomaticVictory()) return false;

        return one.acceptsFriendsRequest(two) && two.acceptsFriendsRequest(one);
    }


    public boolean canWarBeStopped(DiplomaticEntity one, DiplomaticEntity two) {
        if (one.isHuman() && two.isHuman()) return true;
        if (!checkForStopWarCooldown(one, two)) return false;

        return one.acceptsToStopWar(two) && two.acceptsToStopWar(one);
    }


    void requestWorseRelations(DiplomaticEntity initiator, DiplomaticEntity two) {
        int relation = two.getRelation(initiator);

        if (relation == DiplomaticRelation.FRIEND) {
            log.addMessage(DipMessageType.friendship_canceled, initiator, two);
            fieldManager.gameController.matchStatistics.onFriendshipBroken();
            makeNeutral(two, initiator);
        }

        if (relation == DiplomaticRelation.NEUTRAL) {
            log.addMessage(DipMessageType.war_declaration, initiator, two);
            makeEnemies(initiator, two);
        }
    }


    public void onWarStarted(DiplomaticEntity initiator, DiplomaticEntity one) {
        punishAggressor(initiator, one);
        addCooldown(DiplomaticCooldown.TYPE_STOP_WAR, 10, initiator, one);
    }


    public void resetDebtsBetweenEntities(DiplomaticEntity one, DiplomaticEntity two) {
        Debt debt = one.getDebt(two);
        if (debt == null) return;
        if (debt.value == 0) return;
        one.getDebt(two).value = 0;
        two.getDebt(one).value = 0;

        DiplomacyElement diplomacyElement = Scenes.sceneDiplomacy.diplomacyElement;
        if (diplomacyElement != null) {
            diplomacyElement.onRelationsChanged();
        }
    }


    private void punishAggressor(DiplomaticEntity initiator, DiplomaticEntity one) {
        for (Map.Entry<DiplomaticEntity, Integer> entry : initiator.relations.entrySet()) {
            DiplomaticEntity entity = entry.getKey();
            if (entity == initiator) continue;
            if (!one.isFriendTo(entity)) continue;
            requestWorseRelations(initiator, entity);
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


    public DiplomaticContract getContract(int type, DiplomaticEntity one, DiplomaticEntity two) {
        return findContract(type, one, two);
    }


    public DiplomaticContract addContract(int contractType, DiplomaticEntity initiator, DiplomaticEntity entity) {
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
                return 0; // friendship was separated from dotations
            case DiplomaticContract.TYPE_PIECE:
                return calculateReparations(initiator, two);
            case DiplomaticContract.TYPE_BLACK_MARK:
                return 0;
            case DiplomaticContract.TYPE_TRAITOR:
                return calculateTraitorFine(initiator);
        }
    }


    public int calculateTraitorFine(DiplomaticEntity initiator) {
        int stateBalance = initiator.getStateProfit();

        return Math.min(-stateBalance / 3, -5);
    }


    void removeContract(int contractType, DiplomaticEntity one, DiplomaticEntity two) {
        DiplomaticContract contract = findContract(contractType, one, two);
        if (contract == null) return;

        removeContract(contract);
    }


    private void removeContract(DiplomaticContract contract) {
        poolContracts.addWithCheck(contract);
        contracts.remove(contract);
    }


    public int calculateReparations(DiplomaticEntity initiator, DiplomaticEntity two) {
        if (!areKingdomsTouching(initiator, two)) return 0;

        int stateBalance = initiator.getStateProfit();

        if (stateBalance < 5) return 0;
        if (two.getStateProfit() < 10) return 0;

        return -stateBalance / 2;
    }


    public int calculatePayToStopWar(DiplomaticEntity initiator, DiplomaticEntity two) {
        return (int) Math.min(0.6 * initiator.getStateFullMoney(), 0.5 * two.getStateFullMoney());
    }


    public DiplomaticContract findContract(int type, DiplomaticEntity one, DiplomaticEntity two) {
        for (DiplomaticContract contract : contracts) {
            if (!contract.equals(one, two, type)) continue;
            return contract;
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


    public void makeFriends(DiplomaticEntity initiator, DiplomaticEntity entity, int duration) {
        if (!initiator.alive || !entity.alive) return;
        if (initiator.getRelation(entity) == DiplomaticRelation.FRIEND) return;
        if (GameRules.diplomaticRelationsLocked) return;

        // should be before relations change because they will influence dotations
        DiplomaticContract contract = addContract(DiplomaticContract.TYPE_FRIENDSHIP, initiator, entity);
        contract.setExpireCountDown(duration);
        removeContract(DiplomaticContract.TYPE_PIECE, initiator, entity);

        initiator.setRelation(entity, DiplomaticRelation.FRIEND);
        entity.setRelation(initiator, DiplomaticRelation.FRIEND);

        onRelationsChanged();
    }


    public void makeFriends(DiplomaticEntity initiator, DiplomaticEntity entity) {
        makeFriends(initiator, entity, 12);
    }


    public void makeNeutral(DiplomaticEntity one, DiplomaticEntity two) {
        if (!one.alive || !two.alive) return;
        if (one.getRelation(two) == DiplomaticRelation.NEUTRAL) return;
        if (GameRules.diplomaticRelationsLocked) return;

        one.setRelation(two, DiplomaticRelation.NEUTRAL);
        two.setRelation(one, DiplomaticRelation.NEUTRAL);

        removeContract(DiplomaticContract.TYPE_FRIENDSHIP, one, two);
        // piece contract shouldn't be added here

        onRelationsChanged();
    }


    public boolean makeEnemies(DiplomaticEntity initiator, DiplomaticEntity entity) {
        if (!initiator.alive || !entity.alive) return false;
        if (initiator.getRelation(entity) == DiplomaticRelation.ENEMY) return false;
        if (GameRules.diplomaticRelationsLocked) return false;

        initiator.setRelation(entity, DiplomaticRelation.ENEMY);
        entity.setRelation(initiator, DiplomaticRelation.ENEMY);

        removeContract(DiplomaticContract.TYPE_FRIENDSHIP, initiator, entity);
        removeContract(DiplomaticContract.TYPE_PIECE, initiator, entity);
        resetDebtsBetweenEntities(initiator, entity);
        removeDotationsBetweenEntities(initiator, entity);

        onRelationsChanged();
        return true;
    }


    void removeDotationsBetweenEntities(DiplomaticEntity one, DiplomaticEntity two) {
        while (true) {
            DiplomaticContract contract = getContract(DiplomaticContract.TYPE_DOTATIONS, one, two);
            if (contract == null) break;
            removeContract(contract);
        }
    }


    public void onRelationsChanged() {
        DiplomacyElement diplomacyElement = Scenes.sceneDiplomacy.diplomacyElement;
        if (diplomacyElement != null) {
            diplomacyElement.onRelationsChanged();
        }

        if (GameRules.fogOfWarEnabled) {
            fieldManager.fogOfWarManager.updateFog();
        }
    }


    public DiplomaticEntity getEntity(int fraction) {
        for (DiplomaticEntity entity : entities) {
            if (entity.fraction != fraction) continue;
            return entity;
        }

        return null;
    }


    public void showCooldownsInConsole(int fractionFilter) {
        System.out.println();
        System.out.println("DiplomacyManager.showCooldownsInConsole");
        DiplomaticEntity entity = getEntity(fractionFilter);
        for (DiplomaticCooldown cooldown : cooldowns) {
            if (entity != null && !cooldown.contains(entity)) continue;
            System.out.println("- " + cooldown);
        }
    }


    public void showContractsInConsole(int fractionFilter) {
        System.out.println();
        System.out.println("DiplomacyManager.showContractsInConsole");
        DiplomaticEntity entity = getEntity(fractionFilter);
        for (DiplomaticContract contract : contracts) {
            if (entity != null && !contract.contains(entity)) continue;
            System.out.println("- " + contract);
        }
    }


    public String encodeDebts() {
        StringBuilder builder = new StringBuilder();
        for (DiplomaticEntity entity : entities) {
            for (Debt debt : entity.debts) {
                if (debt.value <= 0) continue;
                builder.append(debt.encode()).append(",");
            }
        }
        return builder.toString();
    }


    public void decodeDebts(String source) {
        resetDebts();

        if (source == null) return;
        if (source.length() < 3) return;

        for (String token : source.split(",")) {
            decodeSingleDebt(token);
        }
    }


    private void decodeSingleDebt(String token) {
        if (token.length() < 3) return;
        String[] split = token.split(" ");
        if (split.length < 3) return;

        int sFraction = Integer.valueOf(split[0]);
        int tFraction = Integer.valueOf(split[1]);
        int value = Integer.valueOf(split[2]);

        DiplomaticEntity source = getEntity(sFraction);
        if (source == null) return;
        DiplomaticEntity target = getEntity(tFraction);
        if (target == null) return;

        changeDebt(source, target, value);
    }


    public ColorsManager getColorsManager() {
        return fieldManager.gameController.colorsManager;
    }
}
