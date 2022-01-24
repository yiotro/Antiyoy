package yio.tro.antiyoy.menu.diplomatic_exchange;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.menu.AbstractRectangularUiElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.*;

import java.util.ArrayList;

public class QuickExchangeTutorialElement extends AbstractRectangularUiElement{

    ExchangeUiElement targetElement;
    int step;
    public ExchangeProfitView currentProfitView;
    public ArrayList<RectangleYio> blackouts;
    public FactorYio realFactor;
    public RenderableTextYio title;


    public QuickExchangeTutorialElement(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        targetElement = null;
        step = 0;
        currentProfitView = null;
        realFactor = new FactorYio();
        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        createBlackouts();
    }


    private void createBlackouts() {
        blackouts = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            blackouts.add(new RectangleYio());
        }
    }


    public void setTargetElement(ExchangeUiElement targetElement) {
        this.targetElement = targetElement;
        step = 0;
        currentProfitView = targetElement.topView;
        title.setString(LanguagesManager.getInstance().getString("what_you_get"));
        title.updateMetrics();
    }


    @Override
    protected void onMove() {
        checkToLaunchRealFactor();
        realFactor.move();
        moveBlackouts();
        moveTitle();
    }


    private void moveTitle() {
        title.centerHorizontal(position);
        RectangleYio src = currentProfitView.position;
        title.position.y = (float) (src.y + src.height + 0.01f * GraphicsYio.height + title.height);
        title.updateBounds();
    }


    private void checkToLaunchRealFactor() {
        if (realFactor.getGravity() > 0) return;
        if (appearFactor.get() < 0.95) return;

        realFactor.appear(3, 0.8);
    }


    private void moveBlackouts() {
        RectangleYio src = currentProfitView.position;

        blackouts.get(0).set(0, 0, GraphicsYio.width, src.y);
        blackouts.get(1).set(0, src.y + src.height, GraphicsYio.width, 2 * GraphicsYio.height);
        blackouts.get(2).set(0, src.y, src.x, src.height);
        blackouts.get(3).set(src.x + src.width, src.y, GraphicsYio.width, src.height);
    }


    @Override
    protected void onDestroy() {

    }


    @Override
    protected void onAppear() {
        realFactor.reset();
    }


    @Override
    protected void onTouchDown() {
        applyNextStep();
    }


    private void applyNextStep() {
        switch (step) {
            default:
                break;
            case 0:
                title.setString(LanguagesManager.getInstance().getString("what_you_give"));
                title.updateMetrics();
                currentProfitView = targetElement.bottomView;
                step++;
                break;
            case 1:
                Scenes.sceneQuickExchangeTutorial.hide();
                step++;
                break;
        }
    }


    @Override
    protected void onTouchDrag() {

    }


    @Override
    protected void onTouchUp() {

    }


    @Override
    protected void onClick() {

    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderQuickExchangeTutorialElement;
    }
}
