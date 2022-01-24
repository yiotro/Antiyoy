package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeType;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

import java.util.ArrayList;

public class DiplomaticMessage implements ReusableYio {

    DiplomaticLog diplomaticLog;
    public DipMessageType type;
    public DiplomaticEntity sender;
    public DiplomaticEntity recipient;
    public String arg1, arg2, arg3;


    public DiplomaticMessage(DiplomaticLog diplomaticLog) {
        this.diplomaticLog = diplomaticLog;
        reset();
    }


    public void copyFrom(DiplomaticMessage src) {
        type = src.type;
        sender = src.sender;
        recipient = src.recipient;
        arg1 = src.arg1;
        arg2 = src.arg2;
        arg3 = src.arg3;
    }


    public int getSenderFraction() {
        if (sender == null) {
            return -1;
        }

        return sender.fraction;
    }


    public int getRecipientFraction() {
        if (recipient == null) {
            return -1;
        }

        return recipient.fraction;
    }


    public boolean equals(DiplomaticMessage message) {
        if (type != message.type) return false;
        if (sender != message.sender) return false;
        if (recipient != message.recipient) return false;
        if (arg1 != null && !arg1.equals(message.arg1)) return false;
        if (arg2 != null && !arg2.equals(message.arg2)) return false;

        return true;
    }


    public String getListName() {
        LanguagesManager instance = LanguagesManager.getInstance();
        switch (type) {
            default:
                return instance.getString(type.name());
            case gift:
                return instance.getString(type.name()) + ": $" + arg1;
            case exchange:
                String receiveTypeString = arg1.split(" ")[0];
                ExchangeType type1 = ExchangeType.valueOf(receiveTypeString);
                String giveTypeString = arg2.split(" ")[0];
                ExchangeType type2 = ExchangeType.valueOf(giveTypeString);
                return instance.getString("" + type2) + " <-> " + instance.getString("" + type1);
        }
    }


    @Override
    public void reset() {
        type = null;
        sender = null;
        recipient = null;
        arg1 = "-1";
        arg2 = "-1";
        arg3 = "-1";
    }


    public String getKey() {
        if (type == DipMessageType.exchange) {
            return type.name() + getSenderFraction() + getRecipientFraction() + getExchangeTypeFromArgument(arg1) + getExchangeTypeFromArgument(arg2);
        }
        return type.name() + getSenderFraction() + getRecipientFraction();
    }


    private ExchangeType getExchangeTypeFromArgument(String arg) {
        String[] split = arg.split(" ");
        if (split.length < 1) return null;
        ExchangeType exchangeType = null;
        try {
            exchangeType = ExchangeType.valueOf(split[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exchangeType;
    }


    public void setType(DipMessageType type) {
        this.type = type;
    }


    public void setSender(DiplomaticEntity sender) {
        this.sender = sender;
    }


    public void setRecipient(DiplomaticEntity recipient) {
        this.recipient = recipient;
    }


    public DiplomaticMessage setArg1(String arg1) {
        this.arg1 = arg1;
        return this;
    }


    public DiplomaticMessage setArg2(String arg2) {
        this.arg2 = arg2;
        return this;
    }


    public DiplomaticMessage setArg3(String arg3) {
        this.arg3 = arg3;
        return this;
    }


    public boolean containsLandOwnedByThirdParty() {
        ArrayList<Hex> hexList = diplomaticLog.diplomacyManager.convertStringToHexList(arg1);
        for (Hex hex : hexList) {
            if (hex.sameFraction(sender.fraction)) continue;
            if (hex.sameFraction(recipient.fraction)) continue;
            return true;
        }
        return false;
    }


    public boolean isNot(DipMessageType dipMessageType) {
        return type != dipMessageType;
    }


    public boolean isImportant() {
        switch (type) {
            default:
                return false;
            case war_declaration:
                return true;
        }
    }


    @Override
    public String toString() {
        return "[Message: " +
                type.name() + " from " +
                sender + " to " +
                recipient + ", arg1=" +
                arg1 + ", arg2=" +
                arg2 +
                "]";
    }
}
