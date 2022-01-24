package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.*;

public class EndTurnButtonElement extends AbstractRectangularUiElement {

    public SelectionEngineYio selectionEngineYio;
    LongTapDetector longTapDetector;
    int hintCounter;
    public CircleYio ltPosition;
    public FactorYio ltFactor;
    public boolean touchable;
    boolean readyToApply;
    public boolean readyToMoveMode; // enbaled when there are ready to move units
    float rmmValue;
    public RenderableTextYio rmmSign;
    public RectangleYio rmmBounds;


    public EndTurnButtonElement(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        selectionEngineYio = new SelectionEngineYio();
        ltPosition = new CircleYio();
        ltFactor = new FactorYio();
        rmmBounds = new RectangleYio();
        initSign();
        initLongTapDetector();
    }


    private void initSign() {
        rmmSign = new RenderableTextYio();
        rmmSign.setFont(Fonts.smallerMenuFont);
        rmmSign.setString(LanguagesManager.getInstance().getString("some_units_can_move"));
        rmmSign.updateMetrics();
    }


    private void initLongTapDetector() {
        longTapDetector = new LongTapDetector() {
            @Override
            public void onLongTapDetected() {
                EndTurnButtonElement.this.onLongTapDetected();
            }
        };
        updateLongTapDelay();
    }


    private void updateLongTapDelay() {
        updateReadyToMoveMode();
        longTapDetector.setDelay((int) (rmmValue * 300));
    }


    private void updateReadyToMoveMode() {
        readyToMoveMode = getGameController().fieldManager.atLeastOneUnitIsReadyToMove();
        rmmValue = 1;
        if (readyToMoveMode) {
            rmmValue = 3;
        }
    }


    @Override
    protected void onMove() {
        moveSelection();
        longTapDetector.move();
        moveLtStuff();
        moveSign();
    }


    private void moveSign() {
        rmmSign.position.x = GraphicsYio.width / 2 - rmmSign.width / 2;
        rmmSign.position.y = 0.92f * GraphicsYio.height;
        rmmSign.updateBounds();

        rmmBounds.setBy(rmmSign.bounds);
        rmmBounds.increase(0.015f * GraphicsYio.width);
    }


    public float getRmmAlpha() {
        return ltFactor.get();
    }


    private void moveLtStuff() {
        ltFactor.move();
        if (longTapDetector.isTouched() && !longTapDetector.isCheckPerformed()) {
            ltPosition.radius += (0.012f * GraphicsYio.width) / rmmValue;
        }
    }


    private void onLongTapDetected() {
        if (isInSimpleMode()) return;
        ltFactor.destroy(1, 3);
        applyEndTurn();
    }


    private void applyEndTurn() {
        SoundManagerYio.playSound(SoundManagerYio.soundEndTurn);
        readyToApply = true;
    }


    private void moveSelection() {
        if (touched) return;
        selectionEngineYio.move();
    }


    @Override
    protected void onDestroy() {

    }


    @Override
    protected void onAppear() {
        hintCounter = 0;
        ltFactor.reset();
        touchable = true;
        readyToApply = false;
        readyToMoveMode = false;
    }


    @Override
    protected void onTouchDown() {
        if (!isTouchAllowedByScripts()) return;
        selectionEngineYio.select();
        if (!isInSimpleMode()) {
            SoundManagerYio.playSound(SoundManagerYio.soundKeyboardPress);
            updateLongTapDelay();
            longTapDetector.onTouchDown(currentTouch);
            checkToLaunchLtStuff();
        }
    }


    private void checkToLaunchLtStuff() {
        if (!longTapDetector.isTouched()) return;
        ltPosition.center.set(
                viewPosition.x + viewPosition.width / 2,
                viewPosition.y + viewPosition.height / 2
        );
        ltPosition.setRadius(viewPosition.width / 2);
        ltFactor.reset();
        ltFactor.setValues(1, 0);
        ltFactor.appear(1, 1); // it's stop
    }


    @Override
    protected void onTouchDrag() {
        if (!isTouchAllowedByScripts()) return;
        if (!isInSimpleMode()) {
            longTapDetector.onTouchDrag(currentTouch);
        }
    }


    @Override
    protected void onTouchUp() {
        if (!isTouchAllowedByScripts()) return;
        if (!isInSimpleMode()) {
            longTapDetector.onTouchUp(currentTouch);
            ltFactor.destroy(3, 3);
        }
    }


    public void onSpaceButtonPressed() {
        if (isInSimpleMode()) {
            selectionEngineYio.select();
        } else {
            ltPosition.center.set(
                    viewPosition.x + viewPosition.width / 2,
                    viewPosition.y + viewPosition.height / 2
            );
            ltPosition.setRadius(viewPosition.width);
            ltFactor.reset();
            ltFactor.setValues(1, 0);
            ltFactor.destroy(1, 3);
        }
        applyEndTurn();
    }


    @Override
    protected void onClick() {
        if (!isTouchAllowedByScripts()) return;
        if (!isInSimpleMode()) {
            checkForHint();
            return;
        }
        applyEndTurn();
    }


    private void checkForHint() {
        hintCounter++;
        if (hintCounter == 4) {
            Scenes.sceneNotification.show("long_tap_to_end_turn");
        }
    }


    public boolean isTouchAllowedByScripts() {
        if (!touchable) return false;
        if (Scenes.sceneTutorialTip.isCurrentlyVisible()) return false;
        return true;
    }


    private GameController getGameController() {
        return menuControllerYio.yioGdxGame.gameController;
    }


    public boolean isTouched() {
        return touched;
    }


    public boolean isSelected() {
        return selectionEngineYio.isSelected();
    }


    public boolean isInSimpleMode() {
        if (GameRules.tutorialMode) return true;
        return !SettingsManager.cautiosEndTurnEnabled;
    }


    @Override
    public boolean checkToPerformAction() {
        if (readyToApply) {
            readyToApply = false;
            getGameController().onEndTurnButtonPressed();
            return true;
        }
        return false;
    }


    @Override
    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderEndTurnButtonElement;
    }
}
