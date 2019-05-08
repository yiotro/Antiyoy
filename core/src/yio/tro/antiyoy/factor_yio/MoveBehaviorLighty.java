package yio.tro.antiyoy.factor_yio;

/**
 * Created by yiotro on 23.04.2015.
 */
public class MoveBehaviorLighty extends MoveBehavior {

    public MoveBehaviorLighty() {
    }


    @Override
    void move(FactorYio fy) {
        if (needsToMove(fy)) {
            fy.f += fy.speedMultiplier * fy.dy;
            fy.dy += fy.gravity;
        }
        strictBounds(fy);
    }
}
