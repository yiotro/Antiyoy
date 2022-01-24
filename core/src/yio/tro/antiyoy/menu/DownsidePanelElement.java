package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class DownsidePanelElement extends AbstractRectangularUiElement{

    public RectangleYio renderPosition;
    public RectangleYio blackoutPosition;


    public DownsidePanelElement(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        renderPosition = new RectangleYio();
        blackoutPosition = new RectangleYio();
        blackoutPosition.set(0, 0, GraphicsYio.width, GraphicsYio.height);
        blackoutPosition.increase(GraphicsYio.borderThickness);
    }


    @Override
    protected void onMove() {
        updateRenderPosition();
    }


    private void updateRenderPosition() {
        renderPosition.setBy(viewPosition);
        renderPosition.increase(GraphicsYio.borderThickness);
        renderPosition.height -= GraphicsYio.borderThickness;
    }


    @Override
    protected void onDestroy() {

    }


    @Override
    protected void onAppear() {

    }


    @Override
    protected void onTouchDown() {

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
        return MenuRender.renderDownsidePanelElement;
    }
}
