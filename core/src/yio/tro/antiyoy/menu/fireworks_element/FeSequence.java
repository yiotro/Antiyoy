package yio.tro.antiyoy.menu.fireworks_element;

import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

public class FeSequence implements ReusableYio{

    PointYio position;
    boolean activated;
    long time;


    public FeSequence() {
        position = new PointYio();
    }


    @Override
    public void reset() {
        position.set(0, 0);
        activated = false;
        time = 0;
    }


    boolean isReady() {
        return System.currentTimeMillis() > time;
    }

}
