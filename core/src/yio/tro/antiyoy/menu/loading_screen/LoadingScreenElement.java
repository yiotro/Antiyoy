package yio.tro.antiyoy.menu.loading_screen;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.*;

public class LoadingScreenElement extends InterfaceElement{

    public MenuControllerYio menuControllerYio;
    public RectangleYio viewPosition;
    public RenderableTextYio title;
    public FactorYio appearFactor;
    LoadingParameters loadingParameters;
    boolean readyToApply;
    FactorYio waitFactor;


    public LoadingScreenElement(MenuControllerYio menuControllerYio) {
        super(-1);
        this.menuControllerYio = menuControllerYio;

        viewPosition = new RectangleYio();
        appearFactor = new FactorYio();
        loadingParameters = new LoadingParameters();
        waitFactor = new FactorYio();

        title = new RenderableTextYio();
        title.setFont(Fonts.gameFont);
        title.setString("...");
        title.updateMetrics();
    }


    @Override
    public void move() {
        appearFactor.move();
        waitFactor.move();
        checkToLaunchWaitFactor();
        updateViewPosition();
        updateTitlePosition();
    }


    private void updateTitlePosition() {
        title.centerHorizontal(viewPosition);
        title.centerVertical(viewPosition);
    }


    private void checkToLaunchWaitFactor() {
        if (appearFactor.get() < 1) return;
        if (waitFactor.get() > 0) return;

        waitFactor.setValues(0.01, 0);
        waitFactor.appear(1, 1);
    }


    private void updateViewPosition() {
        if (appearFactor.getGravity() < 0) {
            viewPosition.set(0, 0, GraphicsYio.width, GraphicsYio.height);
            return;
        }

        viewPosition.width = appearFactor.get() * GraphicsYio.width;
        viewPosition.height = appearFactor.get() * GraphicsYio.height;
        viewPosition.x = GraphicsYio.width / 2 - viewPosition.width / 2;
        viewPosition.y = GraphicsYio.height / 2 - viewPosition.height / 2;
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(1, 1.2);
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(2, 1.5);
        onAppear();
    }


    private void onAppear() {
        readyToApply = true;

        waitFactor.setValues(0, 0);
        waitFactor.destroy(1, 0);
    }


    @Override
    public boolean isAnotherSceneCreationIgnored() {
        return true;
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        if (readyToApply && waitFactor.get() == 1) {
            readyToApply = false;

            destroy();
            LoadingManager.getInstance().startGame(loadingParameters);
            menuControllerYio.yioGdxGame.gameView.forceAppearance();

            return true;
        }

        return false;
    }


    @Override
    public boolean isTouchable() {
        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    @Override
    public void setPosition(RectangleYio position) {

    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderLoadingScreenElement;
    }


    public void setLoadingParameters(LoadingParameters srcParams) {
        loadingParameters.copyFrom(srcParams);
    }
}
