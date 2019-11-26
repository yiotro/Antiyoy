package yio.tro.antiyoy.menu.customizable_list;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.render.AbstractRenderCustomListItem;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;
import yio.tro.antiyoy.stuff.SelectionEngineYio;

public abstract class AbstractCustomListItem {


    public CustomizableListYio customizableListYio;
    public RectangleYio viewPosition;
    public PointYio positionDelta;
    public SelectionEngineYio selectionEngineYio;


    public AbstractCustomListItem() {
        customizableListYio = null;
        viewPosition = new RectangleYio();
        positionDelta = new PointYio();
        selectionEngineYio = new SelectionEngineYio();
        initialize();
    }


    public void moveItem() {
        updateViewPosition();
        move();
    }


    protected abstract void initialize();


    protected abstract void move();


    private void updateViewPosition() {
        viewPosition.x = customizableListYio.position.x + positionDelta.x;
        viewPosition.y = customizableListYio.maskPosition.y + positionDelta.y + customizableListYio.hook;
        viewPosition.width = (float) getWidth();
        viewPosition.height = (float) getHeight();
    }


    public boolean isCurrentlyVisible() {
        if (viewPosition.y + viewPosition.height < customizableListYio.getPosition().y) return false;
        if (viewPosition.y > customizableListYio.getPosition().y + customizableListYio.getPosition().height) return false;

        return true;
    }


    public boolean isTouched(PointYio touchPoint) {
        return viewPosition.isPointInside(touchPoint);
    }


    protected abstract double getWidth();


    protected double getDefaultWidth() {
        return 0.98 * customizableListYio.maskPosition.width;
    }


    protected abstract double getHeight();


    protected abstract void onPositionChanged();


    protected abstract void onClicked();


    protected GameController getGameController() {
        return customizableListYio.menuControllerYio.yioGdxGame.gameController;
    }


    protected abstract void onLongTapped();


    public abstract AbstractRenderCustomListItem getRender();


    protected void moveRenderableTextByDefault(RenderableTextYio renderableTextYio) {
        renderableTextYio.position.x = (float) (viewPosition.x + renderableTextYio.delta.x);
        renderableTextYio.position.y = (float) (viewPosition.y + renderableTextYio.delta.y);
        renderableTextYio.updateBounds();
    }


    public void setCustomizableListYio(CustomizableListYio customizableListYio) {
        this.customizableListYio = customizableListYio;
    }

}
