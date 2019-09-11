package yio.tro.antiyoy.factor_yio;


public class FactorYio {
    boolean itsTimeToStop;
    double f, gravity, dy, speedMultiplier;
    MoveBehavior moveBehavior;


    public FactorYio() {
        // empty constructor
        moveBehavior = MoveBehavior.moveBehaviorLighty;
    }


    public void reset() {
        setValues(0, 0);
        destroy(0, 1);
    }


    public void move() {
        moveBehavior.move(this);
    }


    public void appear(int moveMode, double speed) {
        // speed == 1 is default
        setMoveBehaviorByMoveMode(moveMode);
        gravity = 0.01;
        speedMultiplier = 0.3 * speed;
        moveBehavior.alertAboutSpawning(this);
    }


    public void destroy(int moveMode, double speed) {
        // speed == 1 is default
        setMoveBehaviorByMoveMode(moveMode);
        gravity = -0.01;
        speedMultiplier = 0.3 * speed;
        moveBehavior.alertAboutDestroying(this);
    }


    private MoveBehavior getMoveBehaviorByIndex(int index) {
        switch (index) {
            default:
            case 0: return MoveBehavior.moveBehaviorSimple;
            case 1: return MoveBehavior.moveBehaviorLighty;
            case 2: return MoveBehavior.moveBehaviorMaterial;
            case 3: return MoveBehavior.moveBehaviorApproach;
            case 4: return MoveBehavior.moveBehaviorPlayful;
        }
    }


    private void setMoveBehaviorByMoveMode(int moveMode) {
        moveBehavior = getMoveBehaviorByIndex(moveMode);
    }


    public void setValues(double f, double dy) {
        this.f = f;
        this.dy = dy;
    }


    public void setDy(double dy) {
        this.dy = dy;
    }


    public double getDy() {
        return dy;
    }


    public double getGravity() {
        return gravity;
    }


    public void stopMoving() {
        moveBehavior.stopMoving(this);
    }


    public boolean hasToMove() {
        return moveBehavior.needsToMove(this);
    }


    public float get() {
        return (float) f;
    }
}