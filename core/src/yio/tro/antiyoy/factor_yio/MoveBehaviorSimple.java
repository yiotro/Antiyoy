package yio.tro.antiyoy.factor_yio;

/**
 * Created by yiotro on 21.04.2015.
 */
class MoveBehaviorSimple extends MoveBehavior {

    public MoveBehaviorSimple() {
    }


    @Override
    void alertAboutSpawning(FactorYio fy) {
        super.alertAboutSpawning(fy);
        fy.speedMultiplier *= 20;
    }


    @Override
    void alertAboutDestroying(FactorYio fy) {
        super.alertAboutDestroying(fy);
        fy.speedMultiplier *= 20;
    }


    boolean needsToMove(FactorYio fy) {
        if (fy.dy >= 0 && fy.f < 1) return true;
        if (fy.dy <= 0 && fy.f > 0) return true;
        return false;
    }


    void strictBounds(FactorYio fy) {
        if (fy.dy > 0 && fy.f > 1) fy.f = 1;
        if (fy.dy < 0 && fy.f < 0) fy.f = 0;
    }


    @Override
    void move(FactorYio fy) {
        if (fy.dy == 0) fy.dy = fy.gravity;
        if (needsToMove(fy)) {
            fy.f += fy.speedMultiplier * fy.dy;
        }
        strictBounds(fy);
    }
}
