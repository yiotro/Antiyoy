package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeType;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.diplomatic_exchange.*;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;

public class DiplomaticAI {

    DiplomacyManager diplomacyManager;
    private ArrayList<Province> tempProvinceList;
    private ArrayList<Hex> propagationList;
    String customMessageKeys[];
    String sadSmileys[];
    ExchangeProfitView tempProfitView;


    public DiplomaticAI(DiplomacyManager diplomacyManager) {
        this.diplomacyManager = diplomacyManager;

        tempProvinceList = new ArrayList<>();
        propagationList = new ArrayList<>();
        tempProfitView = new ExchangeProfitView(Scenes.sceneDiplomaticExchange.exchangeUiElement);

        initCustomMessageKeys();
        initSadSmileys();
    }


    private void initSadSmileys() {
        sadSmileys = new String[]{
                ":(",
                ":(",
                ";(",
                ">:(",
        };
    }


    private void initCustomMessageKeys() {
        customMessageKeys = new String[]{
                ":P",
                ":D",
                ">:D",
                "o_O",
                ":P",
                ":D",
                ">:D",
                "o_O",
                ":P",
                "!!!",
                ">:D",
                ":)",
                "gen_end_turn",
                "gen_lets_build_farm",
                "tut_good_luck",
                "how_to_play",
        };
    }


    void checkToChangeRelations() {
        DiplomaticEntity mainEntity = getMainEntity();
        if (mainEntity.isHuman()) return;
        if (!mainEntity.alive) return;

        mainEntity.thinkAboutChangingRelations();
    }


    void onAiTurnStarted() {
        if (!getMainEntity().alive) return;

        aiSendMessages();
        aiProcessMessages();
        checkToChangeRelations();
    }


    private void aiSendMessages() {
        if (compareRandomToZero(4)) {
            performAiToHumanFriendshipProposal();
        }

        if (compareRandomToZero(4)) {
            performAiToHumanBlackMark();
        }

        if (compareRandomToZero(5)) {
            checkToProposePeaceToHuman();
        }

        if (getMainEntity().getStateFullMoney() > 100 && compareRandomToZero(15)) {
            performAiToHumanGift();
        }

        if (DebugFlags.cheatCharisma) {
            applyCharismaCheat();
        }

        if (compareRandomToZero(20 * GameRules.fractionsQuantity) || doesLogContainMessageToMe()) {
            sendCustomMessageToHuman();
        }

        if (compareRandomToZero(3)) {
            checkToProposePeaceToAnotherAI();
        }

        if (compareRandomToZero(3)) {
            performRandomHumanExchangeProposal();
        }

        if (compareRandomToZero(3)) {
            performRandomAiExchangeProposal();
        }
    }


    private void performRandomAiExchangeProposal() {
        DiplomaticEntity randomEntity = getRandomEntity();
        if (randomEntity == getMainEntity()) return;
        performRandomExchangeProposal(randomEntity);
    }


    private void performRandomHumanExchangeProposal() {
        performRandomExchangeProposal(getRandomHumanEntity());
    }


    private void performRandomExchangeProposal(DiplomaticEntity targetEntity) {
        if (targetEntity == null) return;

        ExchangeType exchangeType1 = getRandomExchangeType();
        ExchangeType exchangeType2 = getRandomExchangeType();
        if (exchangeType1 == exchangeType2) return;
        if (containsExchangeType(exchangeType1, exchangeType2, ExchangeType.stop_war) && getMainEntity().getRelation(targetEntity) != DiplomaticRelation.ENEMY) return;
        if (containsExchangeType(exchangeType1, exchangeType2, ExchangeType.remove_black_mark) && !getMainEntity().isBlackMarkedWith(targetEntity)) return;
        if (containsExchangeType(exchangeType1, exchangeType2, ExchangeType.friendship) && isFriendshipForbidden(targetEntity)) return;

        DiplomaticMessage diplomaticMessage = getLog().addMessage(DipMessageType.exchange, getMainEntity(), targetEntity);

        String arg1 = getRandomizedExchangeProfitArgument(targetEntity, getMainEntity(), exchangeType1);
        String arg2 = getRandomizedExchangeProfitArgument(getMainEntity(), targetEntity, exchangeType2);

        diplomaticMessage.setArg1(arg1);
        diplomaticMessage.setArg2(arg2);

        if (!isExchangeGood(diplomaticMessage)) {
            getLog().removeMessage(diplomaticMessage);
        }
    }


    private boolean isFriendshipForbidden(DiplomaticEntity targetEntity) {
        DiplomaticEntity mainEntity = getMainEntity();
        if (mainEntity.isOneFriendAwayFromDiplomaticVictory()) return true;
        if (!mainEntity.acceptsFriendsRequest(targetEntity)) return true;
        if (targetEntity.isOneFriendAwayFromDiplomaticVictory()) return true;
        return false;
    }


    private boolean containsExchangeType(ExchangeType type1, ExchangeType type2, ExchangeType comparisonType) {
        return type1 == comparisonType || type2 == comparisonType;
    }


    private String getRandomizedExchangeProfitArgument(DiplomaticEntity giver, DiplomaticEntity profiter, ExchangeType exchangeType) {
        tempProfitView.reset();
        tempProfitView.setExchangeType(exchangeType);

        switch (exchangeType) {
            default:
            case remove_black_mark:
            case nothing:
            case stop_war:
                break;
            case friendship:
                AvFriendship avFriendship = (AvFriendship) tempProfitView.argumentView;
                avFriendship.slider.setIndexByActualValue(8 + YioGdxGame.random.nextInt(5));
                break;
            case lands:
                Province province = getRandomProvinceToTradeHexes(giver.fraction);
                if (province == null) break;
                preparePropagationListToTrade(province);
                AvLands avLands = (AvLands) tempProfitView.argumentView;
                avLands.hexList.clear();
                avLands.hexList.addAll(propagationList);
                break;
            case war_declaration:
                AvWarDeclaration avWarDeclaration = (AvWarDeclaration) tempProfitView.argumentView;
                avWarDeclaration.victim = getRandomAliveEntityExceptOne(giver);
                break;
            case dotations:
                AvDotations avDotations = (AvDotations) tempProfitView.argumentView;
                avDotations.moneySlider.setValueIndex(1 + YioGdxGame.random.nextInt(4));
                avDotations.durationSlider.setIndexByActualValue(7 + YioGdxGame.random.nextInt(14));
                break;
            case money:
                AvMoney avMoney = (AvMoney) tempProfitView.argumentView;
                avMoney.slider.setValueIndex(1 + YioGdxGame.random.nextInt(21));
                break;
        }

        return tempProfitView.encode();
    }


    private ExchangeType getRandomExchangeType() {
        ExchangeType[] values = ExchangeType.values();
        int i = YioGdxGame.random.nextInt(values.length);
        return values[i];
    }


    private void checkToSendAttackPropositionToHuman() {
        DiplomaticEntity mainEntity = getMainEntity();
        DiplomaticEntity randomHumanEntity = getRandomHumanEntity();
        if (randomHumanEntity == null) return;
        if (mainEntity.getRelation(randomHumanEntity) != DiplomaticRelation.FRIEND) return;

        int price = 100;
        if (mainEntity.getStateFullMoney() > 200) {
            price = 200;
        }
        if (mainEntity.getStateFullMoney() > 300) {
            price = 300;
        }

        DiplomaticEntity targetEntity = getRandomEntity();
        if (targetEntity == null) return;
        if (targetEntity == mainEntity) return;
        if (targetEntity == randomHumanEntity) return;
        if (!targetEntity.alive) return;
        if (randomHumanEntity.getRelation(targetEntity) == DiplomaticRelation.ENEMY) return;

        DiplomaticMessage message = getLog().addMessage(DipMessageType.attack_proposition, mainEntity, randomHumanEntity);
        message.setArg1("" + price);
        message.setArg2("" + targetEntity.fraction);
    }


    private void checkToProposePeaceToAnotherAI() {
        DiplomaticEntity mainEntity = getMainEntity();

        for (DiplomaticEntity entity : diplomacyManager.entities) {
            if (entity == mainEntity) continue;
            if (entity.isHuman()) continue;

            int relation = mainEntity.getRelation(entity);
            if (relation != DiplomaticRelation.ENEMY) continue;
            if (diplomacyManager.areKingdomsTouching(mainEntity, entity)) continue;

            getLog().addMessage(DipMessageType.stop_war, mainEntity, entity);
            break;
        }
    }


    private boolean compareRandomToZero(int maxRandomValue) {
        if (doesLogContainMessageToMe()) {
            return YioGdxGame.random.nextInt(maxRandomValue) <= 2;
        }
        return YioGdxGame.random.nextInt(maxRandomValue) == 0;
    }


    private boolean doesLogContainMessageToMe() {
        for (DiplomaticMessage message : getLog().messages) {
            if (message.recipient != getMainEntity()) continue;
            if (!message.sender.isHuman()) continue;
            if (message.isNot(DipMessageType.message) && message.isNot(DipMessageType.war_declaration)) continue;
            return true;
        }
        return false;
    }


    private void sendCustomMessageToHuman() {
        DiplomaticEntity mainEntity = getMainEntity();

        DiplomaticEntity randomHumanEntity = getRandomHumanEntity();
        if (randomHumanEntity == null) return;
        if (!randomHumanEntity.alive) return;

        int index = YioGdxGame.random.nextInt(customMessageKeys.length);
        String string = LanguagesManager.getInstance().getString(customMessageKeys[index]);

        DiplomaticMessage diplomaticMessage = getLog().addMessage(DipMessageType.message, mainEntity, randomHumanEntity);
        diplomaticMessage.setArg1(string);
    }


    private void checkToProposePeaceToHuman() {
        DiplomaticEntity mainEntity = getMainEntity();

        DiplomaticEntity randomHumanEntity = getRandomHumanEntity();
        if (randomHumanEntity == null) return;
        if (!randomHumanEntity.alive) return;

        int relation = mainEntity.getRelation(randomHumanEntity);
        if (relation != DiplomaticRelation.ENEMY) return;
        if (mainEntity.getNumberOfLands() > randomHumanEntity.getNumberOfLands()) return;
        if (!diplomacyManager.canWarBeStopped(mainEntity, randomHumanEntity)) return;

        for (int i = 0; i < 5; i++) {
            ExchangeType randomExchangeType = getExchangeTypeForFriendship();
            if (YioGdxGame.random.nextDouble() < 0.33) {
                randomExchangeType = ExchangeType.lands;
            }

            DiplomaticMessage diplomaticMessage = getLog().addMessage(DipMessageType.exchange, mainEntity, randomHumanEntity);
            String arg1 = getRandomizedExchangeProfitArgument(randomHumanEntity, getMainEntity(), ExchangeType.stop_war);
            String arg2 = getRandomizedExchangeProfitArgument(getMainEntity(), randomHumanEntity, randomExchangeType);

            diplomaticMessage.setArg1(arg1);
            diplomaticMessage.setArg2(arg2);

            if (isExchangeGood(diplomaticMessage)) break;

            getLog().removeMessage(diplomaticMessage);
        }
    }


    private void performAiToHumanHexSellProposal() {
        DiplomaticEntity humanFriend = getRandomHumanFriend();
        if (humanFriend == null) return;
        if (countHowManyHexSaleProposalsEntityHas(humanFriend) > 3) return;

        Province province = getRandomProvinceToTradeHexes(getMainEntity().fraction);
        if (province == null) return;

        preparePropagationListToTrade(province);

        getLog().addMessage(DipMessageType.hex_sale, getMainEntity(), humanFriend)
                .setArg1(diplomacyManager.convertHexListToString(propagationList))
                .setArg2("" + diplomacyManager.calculatePriceForHexes(propagationList));
    }


    private int countHowManyHexSaleProposalsEntityHas(DiplomaticEntity diplomaticEntity) {
        int c = 0;
        for (DiplomaticMessage message : diplomacyManager.log.messages) {
            if (message.recipient != diplomaticEntity) continue;
            if (message.type != DipMessageType.hex_sale) continue;
            c++;
        }
        return c;
    }


    private void performAiToHumanHexBuyProposal() {
        DiplomaticEntity humanFriend = getRandomHumanFriend();
        if (humanFriend == null) return;

        Province province = getRandomProvinceToTradeHexes(humanFriend.fraction);
        if (province == null) return;

        preparePropagationListToTrade(province);

        int price = diplomacyManager.calculatePriceForHexes(propagationList);
        if (YioGdxGame.random.nextDouble() < 0.7) {
            price /= 6 + YioGdxGame.random.nextInt(5);
        }
        getLog().addMessage(DipMessageType.hex_purchase, getMainEntity(), humanFriend)
                .setArg1(diplomacyManager.convertHexListToString(propagationList))
                .setArg2("" + price);
    }


    private void preparePropagationListToTrade(Province province) {
        propagationList.clear();

        Hex randomHex = province.getRandomHex();
        propagationList.add(randomHex);

        int goalQuantity = YioGdxGame.random.nextInt(4) + 3;
        if (goalQuantity > province.hexList.size()) {
            goalQuantity = province.hexList.size();
        }

        while (propagationList.size() < goalQuantity) {
            Hex newHex = getRandomHexNearPropagationList(province);
            propagationList.add(newHex);
        }
    }


    private Hex getRandomHexNearPropagationList(Province province) {
        while (true) {
            Hex randomHex = province.getRandomHex();
            if (propagationList.contains(randomHex)) continue;
            if (!isHexNearPropagationList(randomHex)) continue;

            return randomHex;
        }
    }


    private boolean isHexNearPropagationList(Hex hex) {
        for (int dir = 0; dir < 6; dir++) {
            Hex adjacentHex = hex.getAdjacentHex(dir);
            if (adjacentHex == null) continue;
            if (adjacentHex.isNullHex()) continue;
            if (!propagationList.contains(adjacentHex)) continue;

            return true;
        }

        return false;
    }


    private Province getRandomProvinceToTradeHexes(int filterFraction) {
        int c = 1000;

        while (c > 0) {
            c--;

            Province randomProvince = getFieldManager().getRandomProvince();
            if (randomProvince.getFraction() != filterFraction) continue;

            return randomProvince;
        }

        return null;
    }


    private DiplomaticEntity getRandomHumanFriend() {
        if (!hasAtLeastOnceHumanFriend()) return null;

        while (true) {
            DiplomaticEntity randomHumanEntity = getRandomHumanEntity();
            if (!getMainEntity().isFriendTo(randomHumanEntity)) continue;

            return randomHumanEntity;
        }
    }


    public boolean hasAtLeastOnceHumanFriend() {
        for (DiplomaticEntity entity : getEntities()) {
            if (!entity.isHuman()) continue;
            if (!getMainEntity().isFriendTo(entity)) continue;

            return true;
        }

        return false;
    }


    private void applyCharismaCheat() {
        DiplomaticEntity mainEntity = getMainEntity();
        DiplomaticEntity randomHumanEntity = getRandomHumanEntity();
        if (randomHumanEntity == null) return;

        diplomacyManager.transferMoney(mainEntity, randomHumanEntity, mainEntity.getStateFullMoney() / 2, true);
    }


    private void performAiToHumanGift() {
        DiplomaticEntity mainEntity = getMainEntity();
        DiplomaticEntity randomHumanEntity = getRandomHumanEntity();
        if (randomHumanEntity == null) return;
        if (mainEntity.getRelation(randomHumanEntity) == DiplomaticRelation.ENEMY) return;
        if (mainEntity.isBlackMarkedWith(randomHumanEntity)) return;

        diplomacyManager.transferMoney(mainEntity, randomHumanEntity, 10 + YioGdxGame.random.nextInt(11), true);
    }


    private void aiProcessMessages() {
        DiplomaticEntity mainEntity = getMainEntity();

        for (int i = getLog().messages.size() - 1; i >= 0; i--) {
            DiplomaticMessage message = getLog().messages.get(i);

            if (message.recipient != mainEntity) continue;

            switch (message.type) {
                case exchange:
                    processExchange(message);
                    break;
                case friendship_proposal:
                    if (diplomacyManager.isFriendshipPossible(message.sender, message.recipient)) {
                        diplomacyManager.makeFriends(message.sender, message.recipient);
                    }
                    break;
                case stop_war:
                    diplomacyManager.onEntityRequestedToStopWar(message.sender, message.recipient);
                    break;
                case hex_purchase:
                    if (doesAiAllowToBuyItsHexes(message)) {
                        diplomacyManager.applyHexPurchase(message);
                    } else {
                        DiplomaticMessage diplomaticMessage = getLog().addMessage(DipMessageType.message, getMainEntity(), message.sender);
                        diplomaticMessage.setArg1(":P");
                    }
                    break;
                case hex_sale:
                    if (doesAiWantToBuyOthersHexes(message)) {
                        diplomacyManager.applyHexPurchase(message);
                    }
                    break;
                case attack_proposition:
                    if (!processAttackProposition(message)) {
                        DiplomaticMessage diplomaticMessage = getLog().addMessage(DipMessageType.message, getMainEntity(), message.sender);
                        diplomaticMessage.setArg1(getRandomSadSmiley());
                    }
                    break;
            }

            getLog().removeMessage(message);
        }
    }


    private void processExchange(DiplomaticMessage message) {
        if (!isExchangeGood(message)) {
            DiplomaticMessage diplomaticMessage = getLog().addMessage(DipMessageType.message, getMainEntity(), message.sender);
            diplomaticMessage.setArg1("refuse_offer");
            return;
        }

        diplomacyManager.exchangePerformer.apply(message);
    }


    private boolean isExchangeGood(DiplomaticMessage message) {
        DiplomaticEntity sender = message.sender;
        DiplomaticEntity recipient = message.recipient;

        int recipientToSenderScore = calculateExchangeProfitScore(recipient, sender, message.arg1);
        int senderToRecipientScore = calculateExchangeProfitScore(sender, recipient, message.arg2);

        int loss, gain;
        DiplomaticEntity mainEntity = getMainEntity();
        if (mainEntity == recipient) {
            loss = recipientToSenderScore;
            gain = senderToRecipientScore;
        } else {
            loss = senderToRecipientScore;
            gain = recipientToSenderScore;
        }

        if (gain > loss) return true;

        int maxScore = Math.max(loss, gain);
        int allowedDelta = (int) (0.25 * maxScore);

        return Math.abs(loss - gain) <= allowedDelta;
    }


    private int calculateExchangeProfitScore(DiplomaticEntity giver, DiplomaticEntity profiter, String argument) {
        String[] split = argument.split(" ");
        ExchangeType type = ExchangeType.valueOf(split[0]);

        int moneyPerTurnValue;
        int durationValue;
        int fraction;

        switch (type) {
            default:
            case nothing:
                return 0;
            case remove_black_mark:
                if (giver == getMainEntity()) {
                    return 200;
                }
                return -200;
            case stop_war:
                int relation = giver.getRelation(profiter);
                if (relation != DiplomaticRelation.ENEMY) return 0;
                if (profiter == getMainEntity() && profiter.getStateIncome() < 3 * giver.getStateIncome()) {
                    return -150; // don't want
                }
                if (!diplomacyManager.canWarBeStopped(giver, profiter)) return 900;
                return 120;
            case money:
                moneyPerTurnValue = Integer.valueOf(split[1]);
                return moneyPerTurnValue;
            case dotations:
                moneyPerTurnValue = Integer.valueOf(split[1]);
                durationValue = Integer.valueOf(split[2]);
                int realPayPerTurn = Math.min(moneyPerTurnValue, giver.getStateIncome());
                return realPayPerTurn * durationValue;
            case war_declaration:
                fraction = Integer.valueOf(split[1]);
                if (getMainEntity().fraction == fraction) return -800;
                return 180;
            case lands:
                if (split.length < 2) return 999999;
                ArrayList<Hex> hexList = diplomacyManager.convertStringToHexList(split[1]);
                if (giver == getMainEntity() && !doesAiAllowToBuyItsHexes(hexList, 1000)) return 999999;
                return diplomacyManager.calculatePriceForHexes(hexList);
            case friendship:
                if (!diplomacyManager.isFriendshipPossible(giver, profiter)) return 999999;
                return getFriendshipPrice(giver);
        }
    }


    public int getFriendshipPrice(DiplomaticEntity giver) {
        return (int) (2.5 * giver.getStateIncome());
    }


    private String getRandomSadSmiley() {
        int index = YioGdxGame.random.nextInt(sadSmileys.length);
        return sadSmileys[index];
    }


    private boolean processAttackProposition(DiplomaticMessage message) {
        int price = Integer.valueOf(message.arg1);
        if (price < 50) return false;
        if (message.sender.getStateFullMoney() < price) return false;

        DiplomaticEntity mainEntity = getMainEntity();
        int targetFraction = Integer.valueOf(message.arg2);
        if (targetFraction == mainEntity.fraction) return false;

        DiplomaticEntity targetEntity = diplomacyManager.getEntity(targetFraction);
        if (targetEntity == null) return false;
        if (mainEntity.getRelation(targetEntity) == DiplomaticRelation.FRIEND) return false;
        if (price < 250 && YioGdxGame.random.nextDouble() < 0.25) return false;

        diplomacyManager.transferMoney(message.sender, mainEntity, price);
        diplomacyManager.onEntityRequestedToMakeRelationsWorse(mainEntity, targetEntity);
        return true;
    }


    private boolean doesAiWantToBuyOthersHexes(DiplomaticMessage message) {
        ArrayList<Hex> hexList = diplomacyManager.convertStringToHexList(message.arg1);
        if (hexList.size() < 2) return false;

        int price = Integer.valueOf(message.arg2);
        DiplomaticEntity buyer = message.recipient;
        return buyer.getStateFullMoney() > price && buyer.getStateProfit() > 0;
    }


    public boolean doesAiAllowToBuyItsHexes(ArrayList<Hex> hexList, int price) {
        if (hexList.size() == 0) return false;
        int fraction = hexList.get(0).fraction;
        if (hexList.size() >= getQuantityOfOwnedHexes(fraction)) return false;

        int realPrice = diplomacyManager.calculatePriceForHexes(hexList);
        if (price < 0.7 * realPrice) return false;

        tempProvinceList.clear();
        for (Hex hex : hexList) {
            Province provinceByHex = getFieldManager().getProvinceByHex(hex);
            if (provinceByHex == null) continue;
            if (tempProvinceList.contains(provinceByHex)) continue;
            tempProvinceList.add(provinceByHex);
        }

        for (Province province : tempProvinceList) {
            if (doesHexListSplitProvince(province, hexList)) {
                return false;
            }
        }

        return true;
    }


    private int getQuantityOfOwnedHexes(int fraction) {
        int c = 0;
        for (Province province : getFieldManager().provinces) {
            if (province.getFraction() != fraction) continue;
            c += province.hexList.size();
        }
        return c;
    }


    private boolean doesAiAllowToBuyItsHexes(DiplomaticMessage message) {
        ArrayList<Hex> hexList = diplomacyManager.convertStringToHexList(message.arg1);
        int price = Integer.valueOf(message.arg2);

        return doesAiAllowToBuyItsHexes(hexList, price);
    }


    private boolean doesHexListSplitProvince(Province province, ArrayList<Hex> restrictionList) {
        for (Hex hex : province.hexList) {
            hex.flag = false;
        }

        propagationList.clear();
        propagationList.add(province.hexList.get(0));

        while (propagationList.size() > 0) {
            Hex hex = propagationList.get(0);
            propagationList.remove(0);
            hex.flag = true;

            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (adjacentHex == null) continue;
                if (adjacentHex == getFieldManager().nullHex) continue;
                if (!adjacentHex.active) continue;
                if (!adjacentHex.sameFraction(hex)) continue;
                if (adjacentHex.flag) continue;
                if (restrictionList.contains(adjacentHex)) continue;
                if (propagationList.contains(adjacentHex)) continue;

                propagationList.add(adjacentHex);
            }
        }

        for (Hex hex : province.hexList) {
            if (hex.flag) continue;
            if (restrictionList.contains(hex)) continue;
            return true;
        }

        for (Hex hex : restrictionList) {
            if (hex == province.hexList.get(0)) {
                hex.flag = false;
            }
        }

        if (getFlaggedHexesQuantity(province.hexList) < 2) return true; // 1 hex is flagged by default

        return false;
    }


    private FieldManager getFieldManager() {
        return diplomacyManager.fieldManager;
    }


    private int getFlaggedHexesQuantity(ArrayList<Hex> list) {
        int c = 0;

        for (Hex hex : list) {
            if (!hex.flag) continue;
            c++;
        }

        return c;
    }


    public void performAiToHumanBlackMark() {
        DiplomaticEntity aiEntity = findAiEntityThatIsCloseToWin();
        if (aiEntity == null) return;

        DiplomaticEntity randomHumanEntity = getRandomHumanEntity();
        if (randomHumanEntity == null) return;

        int relation = aiEntity.getRelation(randomHumanEntity);
        if (relation == DiplomaticRelation.FRIEND) return;
        if (!diplomacyManager.isBlackMarkAllowed(randomHumanEntity, aiEntity)) return;

        getLog().addMessage(DipMessageType.black_marked, aiEntity, randomHumanEntity);

        diplomacyManager.makeBlackMarked(aiEntity, randomHumanEntity);
    }


    public void performAiToHumanFriendshipProposal() {
        DiplomaticEntity humanEntity = getRandomHumanEntity();
        if (humanEntity == null) return;
        if (humanEntity.isOneFriendAwayFromDiplomaticVictory()) return;
        if (!humanEntity.alive) return;

        DiplomaticEntity mainEntity = getMainEntity();
        if (mainEntity.isOneFriendAwayFromDiplomaticVictory()) return; // no tricky friend requests

        int relation = humanEntity.getRelation(mainEntity);
        if (relation != DiplomaticRelation.NEUTRAL) return;
        if (!humanEntity.acceptsFriendsRequest(mainEntity)) return;

        for (int i = 0; i < 5; i++) {
            ExchangeType randomExchangeType = getExchangeTypeForFriendship();

            DiplomaticMessage diplomaticMessage = getLog().addMessage(DipMessageType.exchange, mainEntity, humanEntity);
            String arg1 = getRandomizedExchangeProfitArgument(humanEntity, getMainEntity(), ExchangeType.friendship);
            String arg2 = getRandomizedExchangeProfitArgument(getMainEntity(), humanEntity, randomExchangeType);

            diplomaticMessage.setArg1(arg1);
            diplomaticMessage.setArg2(arg2);

            if (isExchangeGood(diplomaticMessage)) break;

            getLog().removeMessage(diplomaticMessage);
        }
    }


    private ExchangeType getExchangeTypeForFriendship() {
        switch (YioGdxGame.random.nextInt(3)) {
            default:
            case 0:
            case 1:
                return ExchangeType.dotations;
            case 2:
                return ExchangeType.money;
        }
    }


    public DiplomaticEntity findAiEntityThatIsCloseToWin() {
        for (DiplomaticEntity entity : getEntities()) {
            if (entity.isHuman()) continue;
            if (!entity.alive) continue;

            if (entity.isOneFriendAwayFromDiplomaticVictory()) {
                return entity;
            }
        }

        return null;
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
        for (DiplomaticEntity entity : getEntities()) {
            if (entity.isHuman()) {
                return true;
            }
        }

        return false;
    }


    private DiplomaticLog getLog() {
        return diplomacyManager.log;
    }


    private ArrayList<DiplomaticEntity> getEntities() {
        return diplomacyManager.entities;
    }


    private DiplomaticEntity getMainEntity() {
        return diplomacyManager.getMainEntity();
    }


    public DiplomaticEntity getRandomAliveEntityExceptOne(DiplomaticEntity excludedEntity) {
        int c = 200;
        while (c > 0) {
            c--;
            DiplomaticEntity randomEntity = diplomacyManager.getRandomEntity();
            if (randomEntity == excludedEntity) continue;
            if (!randomEntity.alive) continue;
            return randomEntity;
        }
        return getMainEntity();
    }


    public DiplomaticEntity getRandomEntity() {
        return diplomacyManager.getRandomEntity();
    }
}
