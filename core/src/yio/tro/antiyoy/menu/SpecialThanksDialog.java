package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scrollable_list.ScrollableListYio;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SpecialThanksDialog extends ScrollableListYio {

    public SpecialThanksDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderSpecialThanksDialog;
    }


    @Override
    protected float getItemHeight() {
        return 0.08f * GraphicsYio.height;
    }


    @Override
    protected void updateEdgeRectangles() {
        super.updateEdgeRectangles();

        topEdge.height = 1.2f * GraphicsYio.height;
    }
}
