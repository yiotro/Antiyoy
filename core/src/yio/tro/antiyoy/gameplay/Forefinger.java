package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.factor_yio.FactorYio;


public class Forefinger {

    private final GameController gameController;
    private final Unit jumpingUnit;
    private int pointingTo;
    private final FactorYio sizeFactor;
    public static final int POINTING_TO_HEX = 0;
    public static final int POINTING_TO_MENU = 1;
    private Hex pointedHex;
    public PointYio pointPos, animPos;
    float w, h;
    private double rotation;


    public Forefinger(GameController gameController) {
        this.gameController = gameController;
        jumpingUnit = new Unit(gameController, gameController.fieldManager.nullHex, 0);
        sizeFactor = new FactorYio();
        jumpingUnit.startJumping();
        jumpingUnit.jumpGravity /= 2;
        pointPos = new PointYio();
        animPos = new PointYio();
        w = GraphicsYio.width;
        h = GraphicsYio.height;
    }


    void move() {
        jumpingUnit.moveJumpAnim();
        if (sizeFactor.hasToMove()) sizeFactor.move();
        double deltaX = 0.05 * w * jumpingUnit.jumpPos * Math.cos(rotation - Math.PI / 2);
        double deltaY = 0.05 * w * jumpingUnit.jumpPos * Math.sin(rotation - Math.PI / 2);
        animPos.set(pointPos.x + deltaX, pointPos.y + deltaY);
    }


    private void beginSpawnAnimation() {
        sizeFactor.setValues(0, 0);
        sizeFactor.appear(4, 2);
    }


    public float getAlpha() {
        if (sizeFactor.get() <= 1) return sizeFactor.get();
        return 1;
    }


    void hide() {
        sizeFactor.destroy(1, 3);
    }


    public boolean isPointingToHex() {
        return pointingTo == POINTING_TO_HEX;
    }


    boolean isPointingToButton() {
        return pointingTo == POINTING_TO_MENU;
    }


    public float getSize() {
        return 0.5f + 0.5f * sizeFactor.get();
    }


    void setPointTo(Hex hex) {
        pointedHex = hex;
        pointingTo = POINTING_TO_HEX;
        beginSpawnAnimation();
        pointPos.set(hex.pos.x + 0.035 * w, hex.pos.y - 0.035 * w);
        rotation = 0.5;
    }


    void setPointTo(double x, double y, double rotation) {
        this.rotation = rotation;
        pointingTo = POINTING_TO_MENU;
        beginSpawnAnimation();
        pointPos.set(x, y);
    }


    public double getRotation() {
        return rotation;
    }


    public PointYio getPointPos() {
        return pointPos;
    }
}
