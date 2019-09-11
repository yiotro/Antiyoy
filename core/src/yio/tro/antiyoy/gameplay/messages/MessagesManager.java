package yio.tro.antiyoy.gameplay.messages;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.data_storage.DecodeManager;
import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.RepeatYio;

import java.util.ArrayList;

public class MessagesManager implements EncodeableYio{

    GameController gameController;
    public ArrayList<PreparedMessage> messages;
    RepeatYio<MessagesManager> repeatApply;


    public MessagesManager(GameController gameController) {
        this.gameController = gameController;
        messages = new ArrayList<>();
        initRepeats();
    }


    private void initRepeats() {
        repeatApply = new RepeatYio<MessagesManager>(this, 30) {
            @Override
            public void performAction() {
                parent.checkToApply();
            }
        };
    }


    public void defaultValues() {
        messages.clear();
    }


    private void checkToApply() {
        if (messages.size() == 0) return;
        if (Scenes.sceneDipMessage.isCurrentlyVisible()) return;

        PreparedMessage preparedMessage = messages.get(0);
        messages.remove(0);
        Scenes.sceneDipMessage.showMessage("message", preparedMessage.value);
    }


    public void addMessage(String value) {
        PreparedMessage preparedMessage = new PreparedMessage(this);
        preparedMessage.setValue(value);
        preparedMessage.setKey(getKeyForNewMessage());
        messages.add(preparedMessage);
    }


    private int getKeyForNewMessage() {
        int max = -1;
        for (PreparedMessage message : messages) {
            if (max == -1 || message.key > max) {
                max = message.key;
            }
        }
        return max + 1;
    }


    public PreparedMessage getMessage(int key) {
        for (PreparedMessage message : messages) {
            if (message.key != key) continue;
            return message;
        }
        return null;
    }


    public void modifyMessage(int key, String value) {
        PreparedMessage message = getMessage(key);
        if (message == null) return;
        message.setValue(value);
    }


    public void removeMessage(int key) {
        PreparedMessage message = getMessage(key);
        if (message == null) return;
        removeMessage(message);
    }


    public void removeMessage(PreparedMessage preparedMessage) {
        messages.remove(preparedMessage);
    }


    public void onEndCreation() {

    }


    public void move() {
        if (gameController.isInEditorMode()) return;
        repeatApply.move();
    }


    public void onLevelImported(String levelCode) {
        DecodeManager decodeManager = gameController.decodeManager;
        decodeManager.setSource(levelCode);
        String messagesSection = decodeManager.getSection("messages");
        if (messagesSection == null) return;

        decode(messagesSection);
    }


    @Override
    public String encode() {
        StringBuilder builder = new StringBuilder();
        for (PreparedMessage message : messages) {
            builder.append(message.encode()).append("@");
        }
        return builder.toString();
    }


    @Override
    public void decode(String source) {
        if (source.length() < 2) return;
        for (String token : source.split("@")) {
            if (token.length() == 0) continue;
            addMessage(token);
        }
    }


    public void checkToApplyAdditionalData() {
        String source = gameController.initialParameters.preparedMessagesData;
        if (source == null) return;
        if (source.length() < 5) return;

        onEndCreation();
        decode(source);
    }
}
