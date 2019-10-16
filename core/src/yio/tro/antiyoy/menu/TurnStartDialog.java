package yio.tro.antiyoy.menu;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.ClickDetector;
import yio.tro.antiyoy.gameplay.ColorsManager;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.*;

public class TurnStartDialog extends InterfaceElement{

    MenuControllerYio menuControllerYio;
    public RectangleYio position;
    public FactorYio appearFactor, alphaFactor;
    PointYio currentTouch, clickPoint, tempPoint;
    public int color;
    ClickDetector clickDetector;
    boolean readyToDie;
    public boolean circleModeEnabled, inDestroyState;
    public PointYio circleCenter;
    public float circleRadius, maxCircleRadius;
    public String titleString, descString;
    public BitmapFont titleFont, descFont;
    public PointYio titlePosition, descPosition;


    public TurnStartDialog(MenuControllerYio menuControllerYio) {
        super(-1);
        this.menuControllerYio = menuControllerYio;

        position = new RectangleYio();
        appearFactor = new FactorYio();
        currentTouch = new PointYio();
        clickDetector = new ClickDetector();
        readyToDie = false;
        circleModeEnabled = false;
        color = 0;
        circleCenter = new PointYio();
        circleRadius = 0;
        maxCircleRadius = 0;
        alphaFactor = new FactorYio();

        titleString = null;
        titleFont = Fonts.gameFont;
        titlePosition = new PointYio();

        descString = null;
        descFont = Fonts.smallerMenuFont;
        descPosition = new PointYio();

        clickPoint = new PointYio();
        tempPoint = new PointYio();
    }


    @Override
    public void move() {
        appearFactor.move();
        alphaFactor.move();
        moveCircle();
        checkToCancelCircleMode();
        moveDestroyState();
    }


    private void moveDestroyState() {
        if (!inDestroyState) return;

        circleRadius = (1 - appearFactor.get()) * maxCircleRadius;
    }


    private void moveCircle() {
        if (!circleModeEnabled) return;

        circleRadius = appearFactor.get() * maxCircleRadius;
    }


    private void checkToCancelCircleMode() {
        if (!circleModeEnabled) return;

        if (appearFactor.get() == 1) {
            circleModeEnabled = false;
            menuControllerYio.yioGdxGame.gameController.applyReadyToEndTurn();
        }
    }


    public boolean isCircleModeEnabled() {
        return circleModeEnabled;
    }


    public boolean isInDestroyState() {
        return inDestroyState;
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(2, 1);

        onDestroy();
    }


    private void onDestroy() {
        inDestroyState = true;
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.001, 0);
        appearFactor.appear(3, 0.7);

        alphaFactor.setValues(0, 0);
        alphaFactor.appear(3, 1.2);

        onAppear();
    }


    public float getVerticalTextViewDelta() {
        if (!circleModeEnabled) return 0;

        return - (1 - appearFactor.get()) * 0.1f * GraphicsYio.width;
    }


    private void onAppear() {
        readyToDie = false;
        circleModeEnabled = true;
        inDestroyState = false;

        updateCircleMetricsToStartFromCorner();
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        if (readyToDie) {
            destroy();
            updateCirleMetricsByClickPoint();
            return true;
        }

        return false;
    }


    private void updateCirleMetricsByClickPoint() {
        circleCenter.setBy(clickPoint);

        maxCircleRadius = 0;
        updateMaxCircleRadiusByPoint(0, 0);
        updateMaxCircleRadiusByPoint(0, GraphicsYio.height);
        updateMaxCircleRadiusByPoint(GraphicsYio.width, 0);
        updateMaxCircleRadiusByPoint(GraphicsYio.width, GraphicsYio.height);

        maxCircleRadius += 0.1f * GraphicsYio.width;
        moveDestroyState();
    }


    private void updateMaxCircleRadiusByPoint(double x, double y) {
        tempPoint.set(x, y);
        maxCircleRadius = (float) Math.max(maxCircleRadius, circleCenter.distanceTo(tempPoint));
    }


    @Override
    public boolean isTouchable() {
        return true;
    }


    public void setColor(int color) {
        this.color = color;

        ColorsManager colorsManager = menuControllerYio.yioGdxGame.gameController.colorsManager;
        descString = (colorsManager.getFractionByColor(color) + 1) + "";
        float textWidth = GraphicsYio.getTextWidth(descFont, descString);
        descPosition.x = GraphicsYio.width / 2 - textWidth / 2;
        descPosition.y = 0.55f * GraphicsYio.height;
    }


    private void updateCurrentTouch(int screenX, int screenY) {
        currentTouch.set(screenX, screenY);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        updateCurrentTouch(screenX, screenY);

        clickDetector.onTouchDown(currentTouch);

        return true;
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        updateCurrentTouch(screenX, screenY);

        clickDetector.onTouchDrag(currentTouch);

        return true;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        updateCurrentTouch(screenX, screenY);

        clickDetector.onTouchUp(currentTouch);

        if (clickDetector.isClicked() && !circleModeEnabled) {
            onClick();
        }

        return true;
    }


    private void onClick() {
        readyToDie = true;
        clickPoint.setBy(currentTouch);
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    @Override
    public void setPosition(RectangleYio position) {
        this.position.setBy(position);

        onPositionChanged();
    }


    private void onPositionChanged() {
        titleString = LanguagesManager.getInstance().getString("player_turn");
        float textWidth = GraphicsYio.getTextWidth(titleFont, titleString);
        titlePosition.x = GraphicsYio.width / 2 - textWidth / 2;
        titlePosition.y = 0.6f * GraphicsYio.height;
    }


    private void updateCircleMetricsToStartFromCorner() {
        maxCircleRadius = (float) Yio.distance(0, 0, position.width, position.height) + 0.1f * GraphicsYio.width;

        circleCenter.x = (float) (position.x + position.width);
        circleCenter.y = (float) position.y;

        moveCircle();
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderTurnStartDialog;
    }
}
