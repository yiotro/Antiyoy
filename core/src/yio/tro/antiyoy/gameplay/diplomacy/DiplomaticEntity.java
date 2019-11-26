package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.name_generator.CityNameGenerator;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

import java.util.HashMap;
import java.util.Map;

public class DiplomaticEntity implements ReusableYio {


    DiplomacyManager diplomacyManager;
    public int fraction;
    public String capitalName;
    public HashMap<DiplomaticEntity, Integer> relations;
    public boolean human;
    public boolean alive;
    public boolean hidden;


    public DiplomaticEntity(DiplomacyManager diplomacyManager) {
        this.diplomacyManager = diplomacyManager;

        relations = new HashMap<>();
    }


    @Override
    public void reset() {
        fraction = -1;
        capitalName = null;
        relations.clear();
        human = false;
        alive = true;
        hidden = false;
    }


    void initRelations() {
        relations.clear();

        for (DiplomaticEntity entity : diplomacyManager.entities) {
            if (entity == this) continue;

            relations.put(entity, DiplomaticRelation.NEUTRAL);
        }
    }


    public void setFraction(int fraction) {
        this.fraction = fraction;
    }


    public boolean isMain() {
        return fraction == diplomacyManager.fieldManager.gameController.turn;
    }


    public boolean isHuman() {
        return human;
    }


    public void setHuman(boolean human) {
        this.human = human;
    }


    public void setRelation(DiplomaticEntity entity, int relation) {
        relations.put(entity, relation);
    }


    public int getRelation(DiplomaticEntity entity) {
        if (!relations.containsKey(entity)) return -1;

        return relations.get(entity);
    }


    public int getStateBalance() {
        int balance = 0;

        for (Province province : diplomacyManager.fieldManager.provinces) {
            if (province.getFraction() != fraction) continue;

            balance += province.getProfit();
        }

        return balance;
    }


    public int getStateFullMoney() {
        int money = 0;

        for (Province province : diplomacyManager.fieldManager.provinces) {
            if (province.getFraction() != fraction) continue;

            money += province.money;
        }

        return money;
    }


    public void pay(int amount) {
        for (Province province : diplomacyManager.fieldManager.provinces) {
            if (province.getFraction() != fraction) continue;

            float incomeCoefficient = province.getIncomeCoefficient();
            province.money -= incomeCoefficient * amount;
        }
    }


    public int getStateDotations() {
        int dotations = 0;

        for (DiplomaticContract contract : diplomacyManager.contracts) {
            if (!contract.contains(this)) continue;

            switch (contract.type) {
                case DiplomaticContract.TYPE_FRIENDSHIP:
                    dotations += contract.getDotationsFromEntityPerspective(this);
                    break;
                case DiplomaticContract.TYPE_PIECE:
                    dotations += contract.getDotationsFromEntityPerspective(this);
                    break;
                case DiplomaticContract.TYPE_TRAITOR:
                    dotations += contract.getDotationsFromEntityPerspective(this);
                    break;
            }
        }

        return dotations;
    }


    boolean isAtWar() {
        for (int relation : relations.values()) {
            if (relation == DiplomaticRelation.ENEMY) {
                return true;
            }
        }

        return false;
    }


    public void updateCapitalName() {
        Province largestProvince = getLargestProvince(fraction);

        Hex capital;
        if (largestProvince == null) {
            capital = diplomacyManager.fieldManager.getRandomActivehex();
            capitalName = CityNameGenerator.getInstance().generateName(capital);
            return;
        }

        GameController gameController = diplomacyManager.fieldManager.gameController;
        capitalName = gameController.namingManager.getProvinceName(largestProvince);
    }


    private Province getLargestProvince(int fraction) {
        Province largestProvince = null;

        for (Province province : diplomacyManager.fieldManager.provinces) {
            if (province.getFraction() != fraction) continue;

            if (largestProvince == null || province.hexList.size() > largestProvince.hexList.size()) {
                largestProvince = province;
            }
        }

        return largestProvince;
    }


    public int getNumberOfFriends() {
        int c = 0;

        for (Map.Entry<DiplomaticEntity, Integer> entry : relations.entrySet()) {
            if (!entry.getKey().alive) continue;

            if (entry.getValue() == DiplomaticRelation.FRIEND) {
                c++;
            }
        }

        return c;
    }


    public int getNumberOfMutualFriends(DiplomaticEntity entity) {
        int c = 0;

        for (Map.Entry<DiplomaticEntity, Integer> entry : relations.entrySet()) {
            if (!entry.getKey().alive) continue;
            if (entry.getValue() != DiplomaticRelation.FRIEND) continue;

            int relation = entity.getRelation(entry.getKey());
            if (relation != DiplomaticRelation.FRIEND) continue;

            c++;
        }

        return c;
    }


    boolean acceptsFriendsRequest(DiplomaticEntity initiator) {
        if (isAnyFriendBlackMarkedWithHim(initiator)) return false;
        if (initiator.isAnyFriendBlackMarkedWithHim(this)) return false;
        if (isBlackMarkedWith(initiator)) return false;

        if (human) return true;

        int dotations = diplomacyManager.calculateDotationsForFriendship(initiator, this);
        if (dotations < -10) return false;

        return true;
    }


    private boolean isAnyFriendBlackMarkedWithHim(DiplomaticEntity entity) {
        for (Map.Entry<DiplomaticEntity, Integer> entry : relations.entrySet()) {
            DiplomaticEntity friendEntity = entry.getKey();
            if (!friendEntity.alive) continue;
            if (!friendEntity.isFriendTo(this)) continue;

            if (friendEntity.isBlackMarkedWith(entity)) return true;
        }
        return false;
    }


    public void updateAliveState() {
        boolean previousAliveState = alive;

        alive = false;

        for (Province province : diplomacyManager.fieldManager.provinces) {
            if (province.getFraction() == fraction) {
                alive = true;
                break;
            }
        }

        if (previousAliveState != alive && !alive) {
            onDeath();
        }
    }


    private void onDeath() {
        diplomacyManager.onEntityDied(this);
    }


    void thinkAboutChangingRelations() {
        if (YioGdxGame.random.nextInt(3) == 0) {
            tryToFindFriend();
            return;
        }

        if (YioGdxGame.random.nextInt(9) == 0) {
            tryToStartWar();
        }
    }


    private void tryToStartWar() {
        if (!alive) return;

        for (int i = 0; i < 10; i++) {
            DiplomaticEntity randomEntity = diplomacyManager.getRandomEntity();
            if (randomEntity == this) continue;
            if (!randomEntity.alive) continue;

            int relation = getRelation(randomEntity);
            if (relation != DiplomaticRelation.NEUTRAL) continue;

            DiplomaticContract contract = diplomacyManager.findContract(DiplomaticContract.TYPE_PIECE, this, randomEntity);
            if (contract != null) continue;

            if (isGoodIdeaToAttackEntity(randomEntity)) {
                diplomacyManager.log.addMessage(DipMessageType.war_declaration, this, randomEntity);

                diplomacyManager.onEntityRequestedToMakeRelationsWorse(this, randomEntity);
            }

            break;
        }
    }


    private boolean isGoodIdeaToAttackEntity(DiplomaticEntity entity) {
        if (YioGdxGame.random.nextDouble() < 0.05) return true;

        if (entity.getStateBalance() > 2 * getStateBalance()) return false;
        if (entity.getStateFullMoney() > 5 * getStateFullMoney()) return false;
        if (entity.getNumberOfFriends() > getNumberOfFriends() + 1) return false;

        return true;
    }


    private void tryToFindFriend() {
        for (int i = 0; i < 10; i++) {
            DiplomaticEntity randomEntity = diplomacyManager.getRandomEntity();
            if (randomEntity == this) continue;
            if (!randomEntity.alive) continue;
            if (randomEntity.isHuman()) continue;

            int relation = getRelation(randomEntity);
            if (relation != DiplomaticRelation.NEUTRAL) continue;

            if (diplomacyManager.isFriendshipPossible(this, randomEntity)) {
                diplomacyManager.makeFriends(this, randomEntity);
            }

            break;
        }
    }


    boolean acceptsToStopWar(DiplomaticEntity entity) {
        if (human) return true;

        int stateFullMoney = getStateFullMoney();
        if (stateFullMoney < 15) return true;

        int pay = diplomacyManager.calculatePayToStopWar(entity, this);

        if (pay < stateFullMoney / 4) return false;
        if (pay < 15) return false;

        return true;
    }


    boolean isOneFriendAwayFromDiplomaticVictory() {
        return numberOfNotFriends() == 1;
    }


    int numberOfNotFriends() {
        int c = 0;

        for (Map.Entry<DiplomaticEntity, Integer> entry : relations.entrySet()) {
            if (!entry.getKey().alive) continue;
            if (entry.getValue() == DiplomaticRelation.FRIEND) continue;

            c++;
        }

        return c;
    }


    boolean isFriendTo(DiplomaticEntity entity) {
        return getRelation(entity) == DiplomaticRelation.FRIEND;
    }


    int getNumberOfLands() {
        int c = 0;

        for (Province province : diplomacyManager.fieldManager.provinces) {
            if (province.getFraction() != fraction) continue;

            c += province.hexList.size();
        }

        return c;
    }


    public boolean isBlackMarkedWith(DiplomaticEntity entity) {
        return diplomacyManager.findContract(DiplomaticContract.TYPE_BLACK_MARK, this, entity) != null;
    }


    public boolean isHidden() {
        return hidden;
    }


    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }


    public boolean hasOnlyFriends() {
        for (Map.Entry<DiplomaticEntity, Integer> entry : relations.entrySet()) {
            if (!entry.getKey().alive) continue;

            if (entry.getValue() != DiplomaticRelation.FRIEND) {
                return false;
            }
        }

        return true;
    }


    @Override
    public String toString() {
        return "[Entity: " +
                capitalName + " " +
                "(" + fraction + ")" +
                "]";
    }
}
