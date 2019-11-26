package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;

public class DiplomaticLog {


    public static final int TURNS_BEFORE_EASY_WIN_IS_POSSIBLE = 25;
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
                return new DiplomaticMessage(DiplomaticLog.this);
            }
        };
    }


    public void onClearMessagesButtonClicked() {
        removeMessagesByRecipient(diplomacyManager.getMainEntity(), false);
    }


    public void removeMessagesByRecipient(DiplomaticEntity recipient, boolean leaveImportant) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            DiplomaticMessage diplomaticMessage = messages.get(i);

            if (diplomaticMessage.recipient != recipient) continue;
            if (leaveImportant && diplomaticMessage.isImportant()) continue;

            removeMessage(diplomaticMessage);
        }
    }


    public void onListItemClicked(String key) {
        DiplomaticMessage message = findMessage(key);
        if (message == null) return;

        switch (message.type) {
            case friendship_proposal:
                applyFriendshipProposal(message);
                break;
            case friendship_ended:
                applyFriendshipEnded(message);
                break;
            case friendship_canceled:
                // nothing
                break;
            case war_declaration:
                // nothing
                break;
            case stop_war:
                applyStopWar(message);
                break;
            case black_marked:
                // nothing
                break;
            case gift:
                // nothing
                break;
            case hex_purchase:
                applyHexPurchase(message);
                break;
            case hex_sale:
                applyHexSale(message);
                break;
            case message:
                applyReadCustomMessage(message);
                break;
            case attack_proposition:
                applyReceiveAttackPropositionDialog(message);
                break;
        }

        removeMessage(message);
    }


    private void applyReadCustomMessage(DiplomaticMessage message) {
        Scenes.sceneDipMessage.showMessage(message.sender.capitalName + ": ", message.arg1);
    }


    private void applyHexSale(DiplomaticMessage message) {
        int price;ArrayList<Hex> hexList = diplomacyManager.convertStringToPurchaseList(message.arg1);
        price = Integer.valueOf(message.arg2);
        Scenes.sceneAgreeToBuyHexes.create();
        Scenes.sceneAgreeToBuyHexes.dialog.setData(message.recipient, hexList, price);
    }


    private void applyHexPurchase(DiplomaticMessage message) {
        int price;ArrayList<Hex> hexesToBuy = diplomacyManager.convertStringToPurchaseList(message.arg1);
        price = Integer.valueOf(message.arg2);
        Scenes.sceneAgreeToSellHexes.create();
        Scenes.sceneAgreeToSellHexes.dialog.setData(message.sender, hexesToBuy, price);
    }


    private void applyStopWar(DiplomaticMessage message) {
        Scenes.sceneStopWarDialog.create();
        Scenes.sceneStopWarDialog.dialog.setEntities(message.sender, message.recipient);
    }


    private void applyFriendshipEnded(DiplomaticMessage message) {
        Scenes.sceneFriendshipDialog.create();
        Scenes.sceneFriendshipDialog.dialog.setValues(message.sender, message.recipient, message);
    }


    private void applyFriendshipProposal(DiplomaticMessage message) {
        Scenes.sceneFriendshipDialog.create();
        Scenes.sceneFriendshipDialog.dialog.setValues(message.sender, message.recipient, message);
    }


    void applyReceiveAttackPropositionDialog(DiplomaticMessage message) {
        Scenes.sceneReceiveAttackPropositionDialog.create();
        int price = Integer.valueOf(message.arg1);
        int targetFraction = Integer.valueOf(message.arg2);
        DiplomaticEntity targetEntity = diplomacyManager.getEntity(targetFraction);
        Scenes.sceneReceiveAttackPropositionDialog.dialog.setData(message.sender, message.recipient, targetEntity, price);
    }


    void checkToRemoveInvalidHexSaleMessages() {
        for (int i = messages.size() - 1; i >= 0; i--) {
            DiplomaticMessage diplomaticMessage = messages.get(i);
            if (diplomaticMessage.isNot(DipMessageType.hex_purchase) && diplomaticMessage.isNot(DipMessageType.hex_sale)) continue;
            if (!diplomaticMessage.containsLandOwnedByThirdParty()) continue;

            removeMessage(diplomaticMessage);
        }
    }


    void checkToClearMutuallyExclusiveMessages() {
        for (int i = messages.size() - 1; i >= 0; i--) {
            DiplomaticMessage diplomaticMessage = messages.get(i);
            if (!isFriendshipProposalToMainEntity(diplomaticMessage)) continue;

            int senderFraction = diplomaticMessage.getSenderFraction();
            DiplomaticMessage warMessage = findMessage(DipMessageType.war_declaration, senderFraction);
            if (warMessage == null) continue;

            removeMessage(diplomaticMessage);
        }
    }


    DiplomaticMessage findMessage(DipMessageType type, int senderFraction) {
        for (DiplomaticMessage message : messages) {
            if (message.type != type) continue;
            if (message.getSenderFraction() != senderFraction) continue;
            return message;
        }
        return null;
    }


    void checkToClearAbuseMessages() {
        DiplomaticEntity mainEntity = diplomacyManager.getMainEntity();
        boolean oneFriendAwayFromDiplomaticVictory = mainEntity.isOneFriendAwayFromDiplomaticVictory();
        int turnsMade = diplomacyManager.fieldManager.gameController.matchStatistics.turnsMade;

        for (int i = messages.size() - 1; i >= 0; i--) {
            DiplomaticMessage diplomaticMessage = messages.get(i);
            if (!isFriendshipProposalToMainEntity(diplomaticMessage)) continue;

            if (oneFriendAwayFromDiplomaticVictory && turnsMade < TURNS_BEFORE_EASY_WIN_IS_POSSIBLE) {
                removeMessage(diplomaticMessage);
                continue;
            }

            if (diplomaticMessage.sender.isOneFriendAwayFromDiplomaticVictory()) {
                removeMessage(diplomaticMessage);
                continue;
            }
        }

        // it's possible that player can be 2 friends away from win and receive 2 friendship proposals at 1 turn
        if (countNumberOfFriendshipProposals() >= mainEntity.numberOfNotFriends()) {
            removeAnyFriendshipProposalToMainEntity();
        }
    }


    private void removeAnyFriendshipProposalToMainEntity() {
        for (int i = messages.size() - 1; i >= 0; i--) {
            DiplomaticMessage diplomaticMessage = messages.get(i);

            if (!isFriendshipProposalToMainEntity(diplomaticMessage)) continue;

            removeMessage(diplomaticMessage);
            break;
        }
    }


    int countNumberOfFriendshipProposals() {
        int c = 0;

        for (DiplomaticMessage diplomaticMessage : messages) {
            if (!isFriendshipProposalToMainEntity(diplomaticMessage)) continue;

            c++;
        }

        return c;
    }


    private boolean isFriendshipProposalToMainEntity(DiplomaticMessage message) {
        return message.recipient == diplomacyManager.getMainEntity() && message.type == DipMessageType.friendship_proposal;
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
        GameController gameController = diplomacyManager.fieldManager.gameController;
        if (!gameController.isPlayerTurn()) return false;

        DiplomaticEntity mainEntity = diplomacyManager.getMainEntity();

        for (DiplomaticMessage message : messages) {
            if (message.recipient != mainEntity) continue;
            return true;
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


    public DiplomaticMessage getSimilarMessage(DipMessageType type, DiplomaticEntity sender, DiplomaticEntity recipient) {
        for (DiplomaticMessage message : messages) {
            if (message.type != type) continue;
            if (message.sender != sender) continue;
            if (message.recipient != recipient) continue;
            return message;
        }

        return null;
    }


    private boolean containsSimilarMessage(DiplomaticMessage message) {
        for (DiplomaticMessage diplomaticMessage : messages) {
            if (!message.equals(diplomaticMessage)) continue;
            return true;
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
