package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.KeyboardManager;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.replays.ReplayManager;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.SingleMessages;
import yio.tro.antiyoy.menu.diplomacy_element.DipActionType;
import yio.tro.antiyoy.menu.diplomacy_element.DiplomacyElement;
import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;
import java.util.HashMap;
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
    HashMap<Hex, Integer> tempMap;
    private ArrayList<Hex> tempHexList;
    public DiplomaticAI diplomaticAI;


    public DiplomacyManager(FieldController fieldController) {
        this.fieldController = fieldController;

        entities = new ArrayList<>();
        contracts = new ArrayList<>();
        cooldowns = new ArrayList<>();
        log = new DiplomaticLog(this);
        tempMap = new HashMap<>();
        tempHexList = new ArrayList<>();
        diplomaticAI = new DiplomaticAI(this);

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


    public void checkForWinConditionsMessage() {
        if (SingleMessages.diplomacyWinConditions) return;

        SingleMessages.diplomacyWinConditions = true;
        SingleMessages.save();

        Scenes.sceneDipMessage.showMessage("win_conditions", "diplomatic_win_conditions");
    }


    public void onDiplomaticLogButtonPressed() {
        fieldController.gameController.selectionManager.deselectAll();

        if (!log.hasSomethingToRead()) {
            System.out.println("DiplomacyManager.onDiplomaticLogButtonPressed: log button shouldn't be visible when log is empty");
            return;
        }

        Scenes.sceneDiplomaticLog.create();
    }


    public void onDiplomacyButtonPressed() {
        fieldController.gameController.selectionManager.deselectAll();

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
            next.setHuman(fieldController.gameController.isPlayerTurn(fraction));

            entities.add(next);
        }

        initEntityRelations();
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
                Scenes.sceneDiplomacy.hide();
                enableAreaSelectionMode(selectedEntity.fraction);
                doAreaSelectRandomHex(); // to show player
                break;
            case mail:
                applySendCustomLetter(mainEntity, selectedEntity);
                break;
        }
    }


    private void applySendCustomLetter(DiplomaticEntity mainEntity, DiplomaticEntity selectedEntity) {
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
        GameController gameController = fieldController.gameController;
        Province province = gameController.fieldController.findProvince(gameController.turn);
        Hex capital = province.getCapital();
        gameController.selectionManager.setAreaSelectionMode(true);
        gameController.selectionManager.setAsFilterFraction(filterFraction);
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
        int filterFraction = fieldController.gameController.selectionManager.getAsFilterFraction();
        int index;
        int c = 100;
        while (c > 0) {
            c--;
            index = YioGdxGame.random.nextInt(fieldController.provinces.size());
            Province province = fieldController.provinces.get(index);
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
        int filterFraction = fieldController.gameController.selectionManager.getAsFilterFraction();
        for (Province province : fieldController.provinces) {
            if (province.getFraction() != filterFraction) continue;
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
        gameController.selectionManager.setAreaSelectionMode(false);
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


    public void applyHexPurchase(DiplomaticMessage message) {
        ArrayList<Hex> hexList = convertStringToPurchaseList(message.arg1);
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

        fieldController.gameController.takeSnapshot();
        updateTempMap(hexList);
        transferMoney(buyer, seller, price);

        for (Hex hex : hexList) {
            if (!hex.sameFraction(seller.fraction)) continue;

            int objectInside = tempMap.get(hex);
            int unitStrength = -1;
            if (hex.containsUnit()) {
                unitStrength = hex.unit.strength;
            }

            fieldController.setHexFraction(hex, buyer.fraction);
            ReplayManager replayManager = fieldController.gameController.replayManager;
            replayManager.onHexChangedFractionWithoutObviousReason(hex);

            if (unitStrength > 0) {
                fieldController.addUnit(hex, unitStrength);
                replayManager.onUnitSpawned(hex, unitStrength);
                continue;
            }

            if (objectInside > 0 && objectInside != Obj.TOWN) {
                fieldController.addSolidObject(hex, objectInside);

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

        fieldController.tryToDetectAddiotionalProvinces();
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


    public ArrayList<Hex> convertStringToPurchaseList(String source) {
        tempHexList.clear();

        for (String token : source.split("@")) {
            String[] split = token.split("%");
            if (split.length < 2) continue;
            int index1 = Integer.valueOf(split[0]);
            int index2 = Integer.valueOf(split[1]);
            tempHexList.add(fieldController.getHex(index1, index2));
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


    public void updateEntityAliveStatus(int fraction) {
        if (!GameRules.diplomacyEnabled) return;

        DiplomaticEntity entity = getEntity(fraction);
        if (entity != null) {
            entity.updateAlive();
        }
    }


    public void updateAllAliveStatuses() {
        for (DiplomaticEntity entity : entities) {
            entity.updateAlive();
        }
    }


    public void onTurnStarted() {
        if (!GameRules.diplomacyEnabled) return;

        log.checkToClearAbuseMessages();
        log.checkToClearMutuallyExclusiveMessages();
        log.checkToRemoveInvalidHexSaleMessages();

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


    public void onTurnEnded() {
        if (!GameRules.diplomacyEnabled) return;

        DiplomaticEntity entity = getEntity(fieldController.gameController.turn);
        entity.updateAlive();

        diplomaticAI.checkToChangeRelations();

        log.removeMessagesByRecipient(entity, true);

        if (fieldController.gameController.turn == 0) {
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
        if (isHexSingle(hex)) return true;

        boolean rulesetDecision = fieldController.gameController.ruleset.canUnitAttackHex(unitStrength, hex);
        if (hex.isNeutral()) return rulesetDecision;

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
            if (adjacentHex == fieldController.nullHex) continue;
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

        // piece
        if (unitStrength > 1) return false;
        if (numberOfPeasantsInProvince(province) > 4) return false;

        return true;
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
        int fraction = province.getFraction();
        DiplomaticEntity entity = getEntity(fraction);
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
        if (diplomaticMessage == null) return;
        diplomaticMessage.setArg1("" + value);

        float f;
        for (Province province : fieldController.provinces) {
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
        int turn = fieldController.gameController.turn;
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
            if (canWarBeStopped(initiator, two)) {
                Scenes.sceneStopWarDialog.create();
                Scenes.sceneStopWarDialog.dialog.setEntities(initiator, two);
            } else {
                Scenes.sceneDipMessage.showMessage(two.capitalName, "refuse_stop_war");
            }
        }

        if (relation == DiplomaticRelation.NEUTRAL) {
            if (isFriendshipPossible(initiator, two)) {
                Scenes.sceneFriendshipDialog.create();
                Scenes.sceneFriendshipDialog.dialog.setEntities(initiator, two);
            } else {
                Scenes.sceneDipMessage.showMessage(two.capitalName, "refuse_friendship");
            }
        }
    }


    boolean isFriendshipPossible(DiplomaticEntity one, DiplomaticEntity two) {
        if (one.isOneFriendAwayFromDiplomaticVictory()) return false;
        if (two.isOneFriendAwayFromDiplomaticVictory()) return false;

        return one.acceptsFriendsRequest(two) && two.acceptsFriendsRequest(one);
    }


    boolean canWarBeStopped(DiplomaticEntity one, DiplomaticEntity two) {
        if (one.isHuman() && two.isHuman()) return true;
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

        return Math.min(-stateBalance / 3, -5);
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


    public ColorsManager getColorsManager() {
        return fieldController.gameController.colorsManager;
    }
}
