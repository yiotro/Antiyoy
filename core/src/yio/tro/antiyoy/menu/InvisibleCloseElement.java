package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.render.MenuRender;

public class InvisibleCloseElement extends AbstractRectangularUiElement{

    Reaction reaction;
    boolean ready;


    public InvisibleCloseElement(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        reaction = null;
    }


    @Override
    protected void onMove() {

    }


    @Override
    protected void onDestroy() {

    }


    @Override
    protected void onAppear() {
        ready = false;
    }


    @Override
    protected void onTouchDown() {
        if (!viewPosition.isPointInside(currentTouch)) return;
        ready = true;
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
        if (ready) {
            ready = false;
            reaction.perform(null);
        }

        return false;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderNothing;
    }


    public void setReaction(Reaction reaction) {
        this.reaction = reaction;
    }
}
