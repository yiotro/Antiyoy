package yio.tro.antiyoy.factor_yio;

/**
 * Created by yiotro on 26.04.2015.
 */
public class MoveBehaviorApproach extends MoveBehavior {

    public MoveBehaviorApproach() {
    }


    @Override
    void alertAboutSpawning(FactorYio fy) {
        super.alertAboutSpawning(fy);
        fy.speedMultiplier /= 0.3;
    }


    @Override
    boolean needsToMove(FactorYio fy) {
        if (fy.gravity > 0 && fy.f < 1) return true;
        if (fy.gravity < 0 && fy.f > 0) return true;
        return false;
    }


    @Override
    void move(FactorYio fy) {
        if (needsToMove(fy)) {
            if (fy.gravity > 0) {
                fy.f += Math.max(fy.speedMultiplier * 0.15 * (1 - fy.f), 0.01);
                if (fy.f > 0.99) fy.f = 1;
            } else {
                fy.f += Math.min(fy.speedMultiplier * 0.15 * (0 - fy.f), -0.01);
                if (fy.f < 0.01) fy.f = 0;
            }
        }
    }
}
