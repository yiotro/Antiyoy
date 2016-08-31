package yio.tro.antiyoy;

import yio.tro.antiyoy.factor_yio.FactorYio;

public class CheckButtonYio extends ButtonYio{

    boolean checked;
    RectangleYio touchPosition;
    FactorYio viewFactor;


    public CheckButtonYio(RectangleYio position, int id, MenuControllerYio menuControllerYio) {
        super(position, id, menuControllerYio);
        checked = false;
        touchPosition = new RectangleYio(0, 0, 0, 0);
        viewFactor = new FactorYio();
    }


    public static CheckButtonYio getCheckButton(MenuControllerYio menuControllerYio, RectangleYio position, int id) {
        CheckButtonYio checkButtonYioYio = menuControllerYio.getCheckButtonById(id);
        if (checkButtonYioYio == null) { // if it's the first time
            checkButtonYioYio = new CheckButtonYio(position, id, menuControllerYio);
            checkButtonYioYio.setShadow(false);
            menuControllerYio.addCheckButtonToArray(checkButtonYioYio);
        }
        checkButtonYioYio.position = position;
        checkButtonYioYio.setVisible(true);
        checkButtonYioYio.setTouchable(true);
        checkButtonYioYio.appear();
        return checkButtonYioYio;
    }


    public FactorYio getFactor() {
        return factorModel;
    }


    public void appear() {
        factorModel.beginSpawning(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);
        factorModel.setValues(0, 0.001);
    }


    @Override
    public void move() {
        super.move();
        viewFactor.move();
    }


    protected boolean isTouched(int screenX, int screenY) {
        return isTouchInsideRectangle(screenX, screenY, touchPosition, 0);
    }


    @Override
    public boolean checkTouch(int screenX, int screenY, int pointer, int button) {
        if (!touchable) return false;
        if (isTouchInsideRectangle(screenX, screenY, touchPosition, 0)) {
            press(screenX, screenY);
            return true;
        }
        return false;
    }


    @Override
    void press(int screenX, int screenY) {
        super.press(screenX, screenY);
        viewFactor.setValues(1, 0);
        viewFactor.beginDestroying(1, 3);
        if (checked) { // uncheck
            checked = false;
            selectionFactor.setValues(1, 0);
            selectionFactor.beginDestroying(1, 3);
        } else { // check
            checked = true;
            selectionFactor.setValues(0, 0);
            selectionFactor.beginSpawning(1, 2);
        }
    }


    public void setChecked(boolean checked) {
        this.checked = checked;
        if (checked) {
            selectionFactor.setValues(1, 0);
            selectionFactor.beginSpawning(1, 1);
        } else {
            selectionFactor.setValues(0, 0);
            selectionFactor.beginDestroying(1, 1);
        }
    }


    public void setTouchPosition(RectangleYio touchPosition) {
        this.touchPosition.set(touchPosition.x, touchPosition.y, touchPosition.width, touchPosition.height);
    }


    @Override
    public void setPosition(RectangleYio position) {
        super.setPosition(position);
        touchPosition.set(position.x, position.y, position.width, position.height);
    }


    public FactorYio getViewFactor() {
        return viewFactor;
    }


    public RectangleYio getTouchPosition() {
        return touchPosition;
    }


    public boolean isChecked() {
        return checked;
    }


    boolean isTouchInsideRectangle(float touchX, float touchY, RectangleYio rectangleYio, float offset) {
        return isTouchInsideRectangle(touchX, touchY, (float)rectangleYio.x, (float)rectangleYio.y, (float)rectangleYio.width, (float)rectangleYio.height, offset);
    }


    boolean isTouchInsideRectangle(float touchX, float touchY, float x, float y, float width, float height, float offset) {
        if (touchX < x - offset) return false;
        if (touchX > x + width + offset) return false;
        if (touchY < y - offset) return false;
        if (touchY > y + height + offset) return false;
        return true;
    }
}
