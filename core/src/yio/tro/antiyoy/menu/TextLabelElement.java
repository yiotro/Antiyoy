package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;

public class TextLabelElement extends AbstractRectangularUiElement{

    public RenderableTextYio title;
    PointYio delta;
    UiChildrenHolder parent;


    public TextLabelElement(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        delta = new PointYio();
        parent = null;
    }


    @Override
    protected void onMove() {
        updateTitlePosition();
    }


    public void setTitle(String string) {
        title.setString(string);
        title.updateMetrics();
    }


    private void updateTitlePosition() {
        if (parent != null) {
            title.position.x = (float) (parent.getHookPosition().x + delta.x);
            title.position.y = (float) (parent.getHookPosition().y + delta.y);
        } else {
            title.centerHorizontal(viewPosition);
            title.centerVertical(viewPosition);
        }
        title.updateBounds();
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


    public void alignTitleLeft(double offset) {
        delta.x = (float) (offset * GraphicsYio.width);
    }


    public void centerTitleHorizontal() {
        if (parent != null) {
            delta.x = (float) (parent.getTargetPosition().width / 2 - title.width / 2);
            return;
        }
        delta.x = GraphicsYio.width / 2 - title.width / 2;
    }


    public void alignTitleTop(double offset) {
        if (parent != null) {
            delta.y = (float) (parent.getTargetPosition().height - offset * GraphicsYio.height);
            return;
        }
        delta.y = (float) (GraphicsYio.height * (1f - offset));
    }


    public void setParent(UiChildrenHolder parent) {
        this.parent = parent;
    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderTextLabel;
    }
}
