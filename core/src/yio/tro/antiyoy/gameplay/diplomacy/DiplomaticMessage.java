package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.gameplay.Hex;
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

        return true;
    }


    public String getListName() {
        switch (type) {
            default:
                return LanguagesManager.getInstance().getString(type.name());
            case gift:
                return LanguagesManager.getInstance().getString(type.name()) + ": $" + arg1;
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
        return type.name() + getSenderFraction() + getRecipientFraction();
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
        ArrayList<Hex> hexList = diplomaticLog.diplomacyManager.convertStringToPurchaseList(arg1);
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
                type.name() + " " +
                sender + " " +
                recipient +
                "]";
    }
}
