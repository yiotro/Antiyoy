package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;

public class Debt implements EncodeableYio{

    public DiplomaticEntity source;
    public DiplomaticEntity target;
    public int value;


    public Debt(DiplomaticEntity source, DiplomaticEntity target, int value) {
        this.source = source;
        this.target = target;
        this.value = value;
    }


    public boolean isCurrentlyActive() {
        return source.getRelation(target) != DiplomaticRelation.ENEMY;
    }


    @Override
    public String toString() {
        return "[Debt: " +
                source + " -> " +
                target + ": " +
                value +
                "]";
    }


    @Override
    public String encode() {
        return source.fraction + " " + target.fraction + " " + value;
    }


    @Override
    public void decode(String source) {

    }
}
