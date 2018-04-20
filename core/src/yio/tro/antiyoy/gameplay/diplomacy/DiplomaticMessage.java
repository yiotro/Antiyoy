package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

import java.util.ArrayList;

public class DiplomaticMessage implements ReusableYio {

    public DipMessageType type;
    public DiplomaticEntity sender;
    public DiplomaticEntity recipient;
    public String arg1, arg2, arg3;


    public DiplomaticMessage() {
        reset();
    }


    @Override
    public String toString() {
        return "[Message: " +
                type.name() + " " +
                sender + " " +
                recipient +
                "]";
    }


    public int getSenderColor() {
        if (sender == null) {
            return -1;
        }

        return sender.color;
    }


    public int getRecipientColor() {
        if (recipient == null) {
            return -1;
        }

        return recipient.color;
    }


    public boolean equals(DiplomaticMessage message) {
        if (type != message.type) return false;
        if (sender != message.sender) return false;
        if (recipient != message.recipient) return false;

        return true;
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
        return type.name() + getSenderColor() + getRecipientColor();
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


    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }


    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }


    public void setArg3(String arg3) {
        this.arg3 = arg3;
    }
}
