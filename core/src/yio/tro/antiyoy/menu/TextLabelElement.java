package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.*;

public class TextLabelElement extends InterfaceElement{

    MenuControllerYio menuControllerYio;
    public RenderableTextYio title;
    PointYio delta;
    FactorYio appearFactor;
    UiChildrenHolder parent;


    public TextLabelElement(MenuControllerYio menuControllerYio) {
        super(-1);
        this.menuControllerYio = menuControllerYio;

        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        delta = new PointYio();
        appearFactor = new FactorYio();
        parent = null;
    }


    @Override
    public void move() {
        appearFactor.move();
        updateTitlePosition();

    }


    public void setTitle(String string) {
        title.setString(string);
        title.updateMetrics();
    }


    private void updateTitlePosition() {
        title.position.x = (float) (parent.getHookPosition().x + delta.x);
        title.position.y = (float) (parent.getHookPosition().y + delta.y);
        title.updateBounds();
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(2, 2);
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(2, 2);
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
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


    public void alignLeft(double offset) {
        delta.x = (float) (offset * GraphicsYio.width);
    }


    public void centerHorizontal() {
        delta.x = (float) (parent.getTargetPosition().width / 2 - title.width / 2);
    }


    public void alignTop(double offset) {
        delta.y = (float) (parent.getTargetPosition().height - offset * GraphicsYio.height);
    }


    public void setParent(UiChildrenHolder parent) {
        this.parent = parent;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderTextLabel;
    }
}
