package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;

public class DiplomaticAI {

    DiplomacyManager diplomacyManager;
    private ArrayList<Province> tempProvinceList;
    private ArrayList<Hex> propagationList;
    String customMessageKeys[];
    String sadSmileys[];


    public DiplomaticAI(DiplomacyManager diplomacyManager) {
        this.diplomacyManager = diplomacyManager;

        tempProvinceList = new ArrayList<>();
        propagationList = new ArrayList<>();
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
    }


    private void aiSendMessages() {
        if (compareRandomToZero(8)) {
            performAiToHumanFriendshipProposal();
        }

        if (compareRandomToZero(4)) {
            performAiToHumanBlackMark();
        }

        if (compareRandomToZero(6)) {
            checkToProposePeaceToHuman();
        }

        if (getMainEntity().getStateFullMoney() > 100 && compareRandomToZero(15)) {
            performAiToHumanGift();
        }

        if (DebugFlags.cheatCharisma) {
            applyCharismaCheat();
        }

        if (getMainEntity().getStateBalance() > 50 && compareRandomToZero(4)) {
            performAiToHumanHexBuyProposal();
        }

        if (getMainEntity().getStateBalance() < 9 && compareRandomToZero(4)) {
            performAiToHumanHexSellProposal();
        }

        if (compareRandomToZero(15 * GameRules.fractionsQuantity) || doesLogContainMessageToMe()) {
            sendCustomMessageToHuman();
        }

        if (compareRandomToZero(3)) {
            checkToProposePeaceToAnotherAI();
        }

        if (getMainEntity().getStateFullMoney() > 100 && compareRandomToZero(4)) {
            checkToSendAttackPropositionToHuman();
        }
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

        getLog().addMessage(DipMessageType.stop_war, mainEntity, randomHumanEntity);
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

            Province randomProvince = getFieldController().getRandomProvince();
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

        diplomacyManager.transferMoney(mainEntity, randomHumanEntity, mainEntity.getStateFullMoney() / 2);
    }


    private void performAiToHumanGift() {
        DiplomaticEntity mainEntity = getMainEntity();
        DiplomaticEntity randomHumanEntity = getRandomHumanEntity();
        if (randomHumanEntity == null) return;
        if (mainEntity.getRelation(randomHumanEntity) == DiplomaticRelation.ENEMY) return;
        if (mainEntity.isBlackMarkedWith(randomHumanEntity)) return;

        diplomacyManager.transferMoney(mainEntity, randomHumanEntity, 10 + YioGdxGame.random.nextInt(11));
    }


    private void aiProcessMessages() {
        DiplomaticEntity mainEntity = getMainEntity();

        for (int i = getLog().messages.size() - 1; i >= 0; i--) {
            DiplomaticMessage message = getLog().messages.get(i);

            if (message.recipient != mainEntity) continue;

            switch (message.type) {
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
        ArrayList<Hex> hexList = diplomacyManager.convertStringToPurchaseList(message.arg1);
        if (hexList.size() < 2) return false;

        int price = Integer.valueOf(message.arg2);
        DiplomaticEntity buyer = message.recipient;
        return buyer.getStateFullMoney() > price && buyer.getStateBalance() > 0;
    }


    private boolean doesAiAllowToBuyItsHexes(DiplomaticMessage message) {
        DiplomaticEntity buyer = message.sender;
        DiplomaticEntity seller = message.recipient;
        ArrayList<Hex> hexList = diplomacyManager.convertStringToPurchaseList(message.arg1);

        int realPrice = diplomacyManager.calculatePriceForHexes(hexList);
        int price = Integer.valueOf(message.arg2);
        if (price < 0.7 * realPrice) return false;

        tempProvinceList.clear();
        for (Hex hex : hexList) {
            Province provinceByHex = getFieldController().getProvinceByHex(hex);
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
                if (adjacentHex == getFieldController().nullHex) continue;
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


    private FieldManager getFieldController() {
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
        if (randomHumanEntity.isBlackMarkedWith(aiEntity)) return;

        getLog().addMessage(DipMessageType.black_marked, aiEntity, randomHumanEntity);

        diplomacyManager.makeBlackMarked(aiEntity, randomHumanEntity);
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

            DiplomaticMessage diplomaticMessage = getLog().addMessage(DipMessageType.friendship_proposal, randomEntity, humanEntity);
            if (diplomaticMessage != null) {
                diplomaticMessage.setArg1("" + diplomacyManager.calculateDotationsForFriendship(randomEntity, humanEntity));
            }
            return true;
        }

        return false;
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


    public DiplomaticEntity getRandomEntity() {
        return diplomacyManager.getRandomEntity();
    }
}
