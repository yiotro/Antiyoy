package yio.tro.antiyoy.factor_yio;

/**
 * Created by yiotro on 23.04.2015.
 */
public class MoveBehaviorSpringy extends MoveBehavior {

    public MoveBehaviorSpringy() {
    }


    void springyBounds(FactorYio fy) {
        if (fy.gravity > 0 && fy.f > 1) {
            if (fy.dy > 0.1) {
                fy.f = 0.999;
                fy.dy = -0.25 * fy.dy;
            } else {
                fy.f = 1;
                stopMoving(fy);
            }
        }
        if (fy.gravity < 0 && fy.f < 0) {
            if (fy.dy < -0.1) {
                fy.f = 0.001;
                fy.dy = -0.25 * fy.dy;
            } else {
                fy.f = 0;
                stopMoving(fy);
            }
        }
    }


    @Override
    void move(FactorYio fy) {
        if (needsToMove(fy)) {
            fy.f += fy.speedMultiplier * fy.dy;
            fy.dy += fy.gravity;
        }
        springyBounds(fy);
//        System.out.println("f = " + fy.f + "; dy = " + fy.dy);
    }
}
