package yio.tro.antiyoy.gameplay.messages;

import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;

public class PreparedMessage implements EncodeableYio{

    MessagesManager messagesManager;
    public String value;
    int key;


    public PreparedMessage(MessagesManager messagesManager) {
        this.messagesManager = messagesManager;
        value = "";
        key = -1;
    }


    public void setValue(String value) {
        this.value = value;
    }


    public int getKey() {
        return key;
    }


    public void setKey(int key) {
        this.key = key;
    }


    @Override
    public String encode() {
        return value;
    }


    @Override
    public void decode(String source) {
        value = source;
    }
}
