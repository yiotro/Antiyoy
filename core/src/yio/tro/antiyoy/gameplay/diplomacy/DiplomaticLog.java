package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;

public class DiplomaticLog {


    DiplomacyManager diplomacyManager;
    public ArrayList<DiplomaticMessage> messages;
    ObjectPoolYio<DiplomaticMessage> poolMessages;


    public DiplomaticLog(DiplomacyManager diplomacyManager) {
        this.diplomacyManager = diplomacyManager;

        messages = new ArrayList<>();

        initPools();
    }


    private void initPools() {
        poolMessages = new ObjectPoolYio<DiplomaticMessage>() {
            @Override
            public DiplomaticMessage makeNewObject() {
                return new DiplomaticMessage();
            }
        };
    }


    public void onClearMessagesButtonClicked() {
        removeMessagesByRecipient(diplomacyManager.getMainEntity());
    }


    public void removeMessagesByRecipient(DiplomaticEntity recipient) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            DiplomaticMessage diplomaticMessage = messages.get(i);

            if (diplomaticMessage.recipient != recipient) continue;

            removeMessage(diplomaticMessage);
        }
    }


    public void onListItemClicked(String key) {
        DiplomaticMessage message = findMessage(key);
        if (message == null) return;

        switch (message.type) {
            case friendship_proposal:
                Scenes.sceneFriendshipDialog.create();
                Scenes.sceneFriendshipDialog.dialog.setEntities(message.sender, message.recipient);
                break;
            case friendship_ended:
                Scenes.sceneFriendshipDialog.create();
                Scenes.sceneFriendshipDialog.dialog.setEntities(message.sender, message.recipient);
                break;
            case friendship_canceled:
                // nothing
                break;
            case war_declaration:
                // nothing
                break;
            case stop_war:
                Scenes.sceneStopWarDialog.create();
                Scenes.sceneStopWarDialog.dialog.setSelectedEntity(message.recipient);
                break;
            case black_marked:
                // nothing
                break;
        }

        removeMessage(message);
    }


    public void removeMessage(DiplomaticMessage message) {
        poolMessages.addWithCheck(message);
        messages.remove(message);
    }


    public DiplomaticMessage findMessage(String key) {
        for (DiplomaticMessage message : messages) {
            if (message.getKey().equals(key)) {
                return message;
            }
        }

        return null;
    }


    public boolean hasSomethingToRead() {
        GameController gameController = diplomacyManager.fieldController.gameController;
        if (!gameController.isPlayerTurn()) return false;

        DiplomaticEntity mainEntity = diplomacyManager.getMainEntity();

        for (DiplomaticMessage message : messages) {
            if (message.recipient == mainEntity) {
                return true;
            }
        }

        return false;
    }


    public DiplomaticMessage addMessage(DipMessageType type, DiplomaticEntity sender, DiplomaticEntity recipient) {
        DiplomaticMessage next = poolMessages.getNext();

        next.setType(type);
        next.setSender(sender);
        next.setRecipient(recipient);

        if (containsSimilarMessage(next)) {
            poolMessages.add(next);
            return null;
        }

        messages.add(next);

        return next;
    }


    private boolean containsSimilarMessage(DiplomaticMessage message) {
        for (DiplomaticMessage diplomaticMessage : messages) {
            if (diplomaticMessage.equals(message)) {
                return true;
            }
        }

        return false;
    }


    public void clear() {
        for (DiplomaticMessage message : messages) {
            poolMessages.add(message);
        }

        messages.clear();
    }


    public void showInConsole() {
        System.out.println();
        System.out.println("DiplomaticLog.showInConsole");
        for (DiplomaticMessage message : messages) {
            System.out.println("- " + message);
        }
    }
}
