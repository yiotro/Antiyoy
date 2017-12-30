package yio.tro.antiyoy.factor_yio;


abstract class MoveBehavior {

    static final MoveBehavior moveBehaviorSimple = new MoveBehaviorSimple();
    static final MoveBehavior moveBehaviorLighty = new MoveBehaviorLighty();
    static final MoveBehavior moveBehaviorMaterial = new MoveBehaviorMaterial();
    static final MoveBehavior moveBehaviorApproach = new MoveBehaviorApproach();
    static final MoveBehavior moveBehaviorPlayful = new MoveBehaviorPlayful();


    public MoveBehavior() {
    }


    boolean needsToMove(FactorYio fy) {
        if (fy.gravity > 0 && fy.f < 1) return true;
        if (fy.gravity < 0 && fy.f > 0) return true;

        if (fy.gravity == 0) {
            if (fy.dy > 0 && fy.f < 1) return true;
            if (fy.dy < 0 && fy.f > 0) return true;
        }

        return false;
    }


    void strictBounds(FactorYio fy) {
        if (fy.gravity > 0 && fy.f > 1) fy.f = 1;
        if (fy.gravity < 0 && fy.f < 0) fy.f = 0;
    }


    void stopMoving(FactorYio fy) {
        fy.dy = 0;
        fy.gravity = 0;
    }


    abstract void move(FactorYio fy);


    void alertAboutSpawning(FactorYio fy) {

    }


    void alertAboutDestroying(FactorYio fy) {

    }
}
