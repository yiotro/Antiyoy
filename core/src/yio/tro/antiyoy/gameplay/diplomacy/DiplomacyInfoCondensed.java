package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class DiplomacyInfoCondensed implements ReusableYio {


    private static DiplomacyInfoCondensed instance;

    String full;
    String relations;
    String contracts;
    DiplomacyManager diplomacyManager;


    public DiplomacyInfoCondensed() {

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
    }


    public void update(DiplomacyManager diplomacyManager) {
        this.diplomacyManager = diplomacyManager;

        updateRelations();
        updateContracts();
        updateFull();
    }


    private void updateContracts() {
        StringBuilder builder = new StringBuilder();

        for (DiplomaticContract contract : diplomacyManager.contracts) {
            builder.append(getSingleContractCode(contract)).append(",");
        }

        contracts = builder.toString();
    }


    private String getSingleContractCode(DiplomaticContract contract) {
        return contract.type + " " + contract.one.color + " " + contract.two.color + " " + contract.dotations + " " + contract.expireCountDown;
    }


    public void apply(DiplomacyManager diplomacyManager) {
        this.diplomacyManager = diplomacyManager;

        if (full.equals("-")) return;

        applyFull();
        applyRelations();
        applyContracts();

        diplomacyManager.onRelationsChanged();
    }


    private void applyContracts() {
        for (String s : contracts.split(",")) {
            applySingleContract(s);
        }
    }


    private void applySingleContract(String s) {
        String[] split = s.split(" ");

        int type = Integer.valueOf(split[0]);
        int color1 = Integer.valueOf(split[1]);
        int color2 = Integer.valueOf(split[2]);
        int dotations = Integer.valueOf(split[3]);
        int expire = Integer.valueOf(split[4]);

        DiplomaticEntity entity1 = diplomacyManager.getEntity(color1);
        DiplomaticEntity entity2 = diplomacyManager.getEntity(color2);

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
    }


    private void updateFull() {
        full = relations + "#" +
                contracts + "#";
    }


    void updateRelations() {
        ArrayList<DiplomaticEntity> entities = diplomacyManager.entities;

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                String singleRelationCode = getSingleRelationCode(entities.get(i), entities.get(j));
                builder.append(singleRelationCode).append(",");
            }
        }

        relations = builder.toString();
    }


    String getSingleRelationCode(DiplomaticEntity one, DiplomaticEntity two) {
        return one.color + " " + two.color + " " + one.getRelation(two);
    }


    void applyRelations() {
        for (String token : relations.split(",")) {
            applySingleRelation(token);
        }
    }


    void applySingleRelation(String token) {
        String[] split = token.split(" ");

        int color1 = Integer.valueOf(split[0]);
        int color2 = Integer.valueOf(split[1]);
        int relation = Integer.valueOf(split[2]);

        DiplomaticEntity entity1 = diplomacyManager.getEntity(color1);
        DiplomaticEntity entity2 = diplomacyManager.getEntity(color2);
        diplomacyManager.setRelation(entity1, entity2, relation);
    }


    public String getFull() {
        return full;
    }


    public void setFull(String full) {
        this.full = full;
    }
}
