package yio.tro.antiyoy.gameplay.diplomacy.exchange;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.diplomacy.*;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.Yio;

import java.util.ArrayList;

public class ExchangePerformer {

    DiplomacyManager diplomacyManager;
    private DiplomaticEntity sender;
    private DiplomaticEntity recipient;
    private DiplomaticMessage diplomaticMessage;


    public ExchangePerformer(DiplomacyManager diplomacyManager) {
        this.diplomacyManager = diplomacyManager;
        diplomaticMessage = new DiplomaticMessage(null);
    }


    public void apply(DiplomaticMessage msg) {
        diplomaticMessage.copyFrom(msg);
        sender = diplomaticMessage.sender;
        recipient = diplomaticMessage.recipient;

        if (!isExchangeAllowed()) return;

        applyProfit(recipient, sender, diplomaticMessage.arg1);
        applyProfit(sender, recipient, diplomaticMessage.arg2);
    }


    private boolean isExchangeForbiddenByWar() {
        if (sender.getRelation(recipient) != DiplomaticRelation.ENEMY) return false;

        ExchangeType type1 = getExchangeTypeFromArgument(diplomaticMessage.arg1);
        if (type1 == ExchangeType.stop_war) return false;

        ExchangeType type2 = getExchangeTypeFromArgument(diplomaticMessage.arg2);
        if (type2 == ExchangeType.stop_war) return false;

        return true;
    }


    boolean isExchangeAllowed() {
        if (isExchangeForbiddenByWar()) return false;
        ExchangeType type1 = getExchangeTypeThatStopsDeal(recipient, sender, diplomaticMessage.arg1);
        if (type1 != null) return false;
        ExchangeType type2 = getExchangeTypeThatStopsDeal(sender, recipient, diplomaticMessage.arg2);
        if (type2 != null) return false;
        return true;
    }


    private ExchangeType getExchangeTypeFromArgument(String argument) {
        String[] split = argument.split(" ");
        if (split.length == 0) return null;
        try {
            return ExchangeType.valueOf(split[0]);
        } catch (IllegalArgumentException e) {
            return ExchangeType.nothing;
        }
    }


    ExchangeType getExchangeTypeThatStopsDeal(DiplomaticEntity giver, DiplomaticEntity profiter, String argument) {
        String[] split = argument.split(" ");

        ExchangeType type;
        try {
            type = ExchangeType.valueOf(split[0]);
        } catch (IllegalArgumentException e) {
            return ExchangeType.nothing;
        }

        if (GameRules.diplomaticRelationsLocked && !isExchangeTypeAllowedWhenRelationsLocked(type)) return type;

        int fraction;

        switch (type) {
            default:
            case nothing:
            case remove_black_mark:
            case stop_war:
            case money:
            case dotations:
                return null;
            case friendship:
                if (!giver.acceptsFriendsRequest(profiter)) return ExchangeType.friendship;
                return null;
            case lands:
                ArrayList<Hex> hexList = diplomacyManager.convertStringToHexList(split[1]);
                for (Hex hex : hexList) {
                    if (hex.fraction != giver.fraction) return ExchangeType.lands;
                }
                return null;
            case war_declaration:
                fraction = Integer.valueOf(split[1]);
                DiplomaticEntity victim = diplomacyManager.getEntity(fraction);
                if (victim == giver) return ExchangeType.war_declaration;
                return null;
        }
    }


    public boolean isExchangeTypeAllowedWhenRelationsLocked(ExchangeType exchangeType) {
        switch (exchangeType) {
            default:
                return true;
            case friendship:
            case war_declaration:
            case stop_war:
            case remove_black_mark:
                return false;
        }
    }


    void applyProfit(DiplomaticEntity giver, DiplomaticEntity profiter, String argument) {
        String[] split = argument.split(" ");
        ExchangeType type = ExchangeType.valueOf(split[0]);

        int moneyValue;
        int durationValue;
        int fraction;
        DiplomaticContract contract;

        switch (type) {
            default:
            case nothing:
                break;
            case remove_black_mark:
                diplomacyManager.removeBlackMark(giver, profiter);
                break;
            case stop_war:
                diplomacyManager.makeNeutral(giver, profiter);
                break;
            case money:
                moneyValue = Integer.valueOf(split[1]);
                applyMoneyTransfer(giver, profiter, moneyValue);
                break;
            case dotations:
                moneyValue = Integer.valueOf(split[1]);
                durationValue = Integer.valueOf(split[2]);
                contract = diplomacyManager.addContract(DiplomaticContract.TYPE_DOTATIONS, profiter, giver);
                contract.setExpireCountDown(durationValue);
                contract.setDotations(moneyValue);
                break;
            case war_declaration:
                fraction = Integer.valueOf(split[1]);
                DiplomaticEntity victim = diplomacyManager.getEntity(fraction);
                diplomacyManager.setRelation(giver, victim, DiplomaticRelation.ENEMY);
                diplomacyManager.onWarStarted(giver, victim);
                break;
            case lands:
                ArrayList<Hex> hexList = diplomacyManager.convertStringToHexList(split[1]);
                diplomacyManager.transferLands(giver, profiter, hexList);
                break;
            case friendship:
                durationValue = Integer.valueOf(split[1]);
                if (giver.getRelation(profiter) == DiplomaticRelation.FRIEND) {
                    diplomacyManager.setRelation(giver, profiter, DiplomaticRelation.NEUTRAL);
                }
                diplomacyManager.makeFriends(giver, profiter, durationValue);
                break;
        }
    }


    private void applyMoneyTransfer(DiplomaticEntity giver, DiplomaticEntity profiter, int moneyValue) {
        int stateFullMoney = giver.getStateFullMoney();
        if (stateFullMoney >= moneyValue) {
            diplomacyManager.transferMoney(giver, profiter, moneyValue);
            return;
        }

        int debtValue = moneyValue - stateFullMoney;
        if (stateFullMoney > 0) {
            diplomacyManager.transferMoney(giver, profiter, stateFullMoney);
        }
        diplomacyManager.changeDebt(giver, profiter, debtValue);

        DiplomaticEntity mainEntity = diplomacyManager.getMainEntity();
        if (mainEntity.isHuman()) {
            String debtSource = LanguagesManager.getInstance().getString("debt");
            debtSource = debtSource.substring(0, 1).toLowerCase() + debtSource.substring(1);
            String middleString = " [" + debtSource + "]: ";
            if (mainEntity == giver) {
                Scenes.sceneNotification.show(profiter.getName() + middleString + Yio.getDeltaMoneyString(-debtValue));
            }
            if (mainEntity == profiter) {
                Scenes.sceneNotification.show(giver.getName() + middleString + Yio.getDeltaMoneyString(debtValue));
            }
        }
    }
}
