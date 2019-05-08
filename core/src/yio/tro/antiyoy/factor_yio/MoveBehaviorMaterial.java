package yio.tro.antiyoy.factor_yio;

/**
 * Created by yiotro on 09.04.2016.
 */
public class MoveBehaviorMaterial extends MoveBehavior {

    @Override
    void move(FactorYio fy) {
        if (fy.gravity > 0) {
            moveSpawn(fy);
        } else {
            moveDestroy(fy);
        }
    }


    private void moveDestroy(FactorYio fy) {
        if (fy.f > 0.99) fy.f = 0.99;
        if (fy.f < 0.01) fy.f = 0;

        if (fy.f > 0.5) {
            fy.f -= 0.05 * fy.speedMultiplier * (1 - fy.f);
        } else {
            fy.f -= 0.05 * fy.speedMultiplier * fy.f;
        }
    }


    private void moveSpawn(FactorYio fy) {
        if (fy.f < 0.01) {
            fy.f = 0.01;
        }

        if (fy.f > 0.99) {
            fy.f += 0.001;

            if (fy.f > 1) {
                fy.f = 1;
            }

            return;
        }

        if (fy.f < 0.5) {
            fy.f += 0.05 * fy.speedMultiplier * fy.f;
        } else {
            fy.f += 0.05 * fy.speedMultiplier * (1 - fy.f);
        }
    }


    @Override
    void alertAboutSpawning(FactorYio fy) {
        fy.speedMultiplier *= 15;
    }


    @Override
    void alertAboutDestroying(FactorYio fy) {
        fy.speedMultiplier *= 15;
    }
}
