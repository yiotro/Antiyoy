package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

import java.util.ArrayList;
import java.util.Arrays;

public class DiplomacyInfoCondensed implements ReusableYio {


    private static DiplomacyInfoCondensed instance;

    String full;
    String relations;
    String contracts;
    String cooldowns;
    String messages;
    DiplomacyManager diplomacyManager;
    private StringBuilder builder;


    public DiplomacyInfoCondensed() {
        builder = new StringBuilder();
    }


    public static DiplomacyInfoCondensed getInstance() {
        if (instance == null) {
            instance = new DiplomacyInfoCondensed();
        }

        instance.reset();

        return instance;
    }


    public static void onGeneralInitialization() {
        instance = null;
    }


    @Override
    public void reset() {
        full = null;
        relations = null;
        diplomacyManager = null;
        contracts = null;
        cooldowns = null;
        messages = null;
    }


    public void update(DiplomacyManager diplomacyManager) {
        this.diplomacyManager = diplomacyManager;

        updateRelations();
        updateContracts();
        updateCooldowns();
        updateMessages();
        updateFull();
    }


    private void updateMessages() {
        builder.setLength(0);

        for (DiplomaticMessage message : diplomacyManager.log.messages) {
            builder.append(getSingleMessageCode(message)).append(",");
        }

        if (builder.length() == 0) {
            builder.append("#");
        }

        messages = builder.toString();
    }


    private String getSingleMessageCode(DiplomaticMessage message) {
        return message.type + "=" + message.getSenderFraction() + "=" + message.getRecipientFraction() + "=" + message.arg1 + "=" + message.arg2 + "=" + message.arg3;
    }


    private void updateCooldowns() {
        builder.setLength(0);

        for (DiplomaticCooldown cooldown : diplomacyManager.cooldowns) {
            builder.append(getSingleCooldownCode(cooldown)).append(",");
        }

        if (builder.length() == 0) {
            builder.append(" ");
        }

        cooldowns = builder.toString();
    }


    private String getSingleCooldownCode(DiplomaticCooldown cooldown) {
        return cooldown.type + " " + cooldown.counter + " " + cooldown.getOneFraction() + " " + cooldown.getTwoFraction();
    }


    private void updateContracts() {
        builder.setLength(0);

        for (DiplomaticContract contract : diplomacyManager.contracts) {
            builder.append(getSingleContractCode(contract)).append(",");
        }

        if (builder.length() == 0) {
            builder.append(" ");
        }

        contracts = builder.toString();
    }


    private String getSingleContractCode(DiplomaticContract contract) {
        return contract.type + " " + contract.getOneFraction() + " " + contract.getTwoFraction() + " " + contract.dotations + " " + contract.expireCountDown;
    }


    public void apply(DiplomacyManager diplomacyManager) {
        this.diplomacyManager = diplomacyManager;

        if (full.equals("-")) return;

        applyFull();
        applyRelations();
        applyContracts();
        applyCooldowns();
        applyMessages();

        diplomacyManager.onRelationsChanged();
    }


    private void applyMessages() {
        if (messages == null) return;

        diplomacyManager.log.clear();
        for (String s : messages.split(",")) {
            applySingleMessage(s);
        }
    }


    private void applySingleMessage(String s) {
        if (!s.contains("=")) return;
        if (countSymbol(s, '=') < 5) return;
        String[] split = s.split("=");

        if (split.length < 3) return;

        DipMessageType type = DipMessageType.valueOf(split[0]);
        int fraction1 = Integer.valueOf(split[1]);
        int fraction2 = Integer.valueOf(split[2]);
        String arg1 = getSplitPart(split, 3);
        String arg2 = getSplitPart(split, 4);
        String arg3 = getSplitPart(split, 5);

        DiplomaticEntity entity1 = diplomacyManager.getEntity(fraction1);
        DiplomaticEntity entity2 = diplomacyManager.getEntity(fraction2);
        if (entity1 == null || entity2 == null) return;

        DiplomaticMessage diplomaticMessage = diplomacyManager.log.addMessage(type, entity1, entity2);
        if (diplomaticMessage == null) return;

        diplomaticMessage.setArg1(arg1);
        diplomaticMessage.setArg2(arg2);
        diplomaticMessage.setArg3(arg3);
    }


    private int countSymbol(String string, char c) {
        int counter = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != c) continue;
            counter++;
        }
        return counter;
    }


    private String getSplitPart(String[] split, int index) {
        if (index >= split.length) return "";

        return split[index];
    }


    private void applyCooldowns() {
        if (cooldowns == null) return;

        for (String s : cooldowns.split(",")) {
            applySingleCooldown(s);
        }
    }


    private void applySingleCooldown(String s) {
        String[] split = s.split(" ");

        if (split.length == 0) return;

        int type = Integer.valueOf(split[0]);
        int counter = Integer.valueOf(split[1]);
        int fraction1 = Integer.valueOf(split[2]);
        int fraction2 = Integer.valueOf(split[3]);

        DiplomaticEntity entity1 = diplomacyManager.getEntity(fraction1);
        DiplomaticEntity entity2 = diplomacyManager.getEntity(fraction2);
        if (entity1 == null || entity2 == null) return;

        diplomacyManager.addCooldown(type, counter, entity1, entity2);
    }


    private void applyContracts() {
        for (String s : contracts.split(",")) {
            applySingleContract(s);
        }
    }


    private void applySingleContract(String s) {
        String[] split = s.split(" ");

        if (split.length == 0) return;

        int type = Integer.valueOf(split[0]);
        int fraction1 = Integer.valueOf(split[1]);
        int fraction2 = Integer.valueOf(split[2]);
        int dotations = Integer.valueOf(split[3]);
        int expire = Integer.valueOf(split[4]);

        DiplomaticEntity entity1 = diplomacyManager.getEntity(fraction1);
        DiplomaticEntity entity2 = diplomacyManager.getEntity(fraction2);
        if (entity1 == null || entity2 == null) return;

        DiplomaticContract contract = diplomacyManager.findContract(type, entity1, entity2);

        if (contract == null) {
            contract = diplomacyManager.addContract(type, entity1, entity2);
        }

        if (contract.one != entity1) {
            // need to swap
            dotations *= -1;
        }

        contract.setDotations(dotations);
        contract.setExpireCountDown(expire);
    }


    private void applyFull() {
        String[] split = full.split("#");

        relations = split[0];
        contracts = split[1];

        if (split.length > 2) {
            cooldowns = split[2];
        } else {
            cooldowns = null;
        }

        if (split.length > 3) {
            messages = split[3];
        } else {
            messages = null;
        }
    }


    private void updateFull() {
        full = relations + "#" +
                contracts + "#" +
                cooldowns + "#" +
                messages + "#";
    }


    void updateRelations() {
        ArrayList<DiplomaticEntity> entities = diplomacyManager.entities;

        builder.setLength(0);

        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                String singleRelationCode = getSingleRelationCode(entities.get(i), entities.get(j));
                builder.append(singleRelationCode).append(",");
            }
        }

        relations = builder.toString();
    }


    String getSingleRelationCode(DiplomaticEntity one, DiplomaticEntity two) {
        return one.fraction + " " + two.fraction + " " + one.getRelation(two);
    }


    void applyRelations() {
        for (String token : relations.split(",")) {
            applySingleRelation(token);
        }
    }


    void applySingleRelation(String token) {
        String[] split = token.split(" ");

        int fraction1 = Integer.valueOf(split[0]);
        int fraction2 = Integer.valueOf(split[1]);
        int relation = Integer.valueOf(split[2]);

        DiplomaticEntity entity1 = diplomacyManager.getEntity(fraction1);
        DiplomaticEntity entity2 = diplomacyManager.getEntity(fraction2);
        diplomacyManager.setRelation(entity1, entity2, relation);
    }


    public String getFull() {
        return full;
    }


    public void setFull(String full) {
        this.full = full;
    }
}
