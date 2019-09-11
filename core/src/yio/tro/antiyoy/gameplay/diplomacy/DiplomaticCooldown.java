package yio.tro.antiyoy.gameplay.diplomacy;

import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

public class DiplomaticCooldown implements ReusableYio{

    public static final int TYPE_STOP_WAR = 0;
    public DiplomaticEntity one, two;
    public int type, counter;


    public DiplomaticCooldown() {
        reset();
    }


    @Override
    public String toString() {
        return "[Cooldown: " +
                "type: " + type +
                " other: " + counter +
                " " + getOneFraction() +
                " " + getTwoFraction() +
                "]";
    }


    public void decreaseCounter() {
        if (counter > 0) {
            counter--;
        }
    }


    public boolean isReady() {
        return counter == 0;
    }


    public boolean contains(DiplomaticEntity entity) {
        return entity == one || entity == two;
    }


    public int getOneFraction() {
        if (one == null) {
            return -1;
        }

        return one.fraction;
    }


    public int getTwoFraction() {
        if (two == null) {
            return -1;
        }

        return two.fraction;
    }


    @Override
    public void reset() {
        type = -1;
        counter = 0;
        one = null;
        two = null;
    }


    public void setOne(DiplomaticEntity one) {
        this.one = one;
    }


    public void setTwo(DiplomaticEntity two) {
        this.two = two;
    }


    public void setType(int type) {
        this.type = type;
    }


    public void setCounter(int counter) {
        this.counter = counter;
    }
}
