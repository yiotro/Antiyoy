package yio.tro.antiyoy.gameplay.highlight;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.stuff.CircleYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

public class HighlightItem implements ReusableYio{

    public CircleYio viewPosition;
    public FactorYio appearFactor;
    boolean stuck;


    public HighlightItem() {
        viewPosition = new CircleYio();
        appearFactor = new FactorYio();
    }


    @Override
    public void reset() {
        viewPosition.reset();
        appearFactor.reset();
        stuck = false;
    }


    public void move() {
        if (stuck) return;
        appearFactor.move();
    }


    public void setBy(Hex one, Hex two) {
        viewPosition.center.x = (one.pos.x + two.pos.x) / 2;
        viewPosition.center.y = (one.pos.y + two.pos.y) / 2;
        viewPosition.setRadius(0.15 * one.pos.distanceTo(two.pos));
        viewPosition.setAngle(one.pos.angleTo(two.pos) - Math.PI / 2);

        appearFactor.setValues(1, 0);
        appearFactor.destroy(1, 1.2);
    }


    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    public boolean isStuck() {
        return stuck;
    }


    public void setStuck(boolean stuck) {
        this.stuck = stuck;
    }
}
