package yio.tro.antiyoy.menu;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class TextViewElement extends InterfaceElement {

    public RectangleYio position, viewPosition;
    public FactorYio appearFactor;
    Animation animType;
    public BitmapFont font;
    float textWidth, textHeight;
    public PointYio textPosition;
    public String textValue;


    public TextViewElement() {
        super(-1);

        position = new RectangleYio();
        viewPosition = new RectangleYio();
        appearFactor = new FactorYio();
        animType = Animation.none;
        font = Fonts.gameFont;
        textWidth = 0;
        textHeight = 0;
        textValue = null;
        textPosition = new PointYio();
    }


    public void setTextValue(String textValue) {
        this.textValue = textValue;
        textWidth = GraphicsYio.getTextWidth(font, textValue);
        textHeight = GraphicsYio.getTextHeight(font, textValue);
    }


    public void setFont(BitmapFont font) {
        this.font = font;
    }


    @Override
    public void move() {
        appearFactor.move();
        updateViewPosition();
        updateTextPosition();
    }


    private void updateTextPosition() {
        textPosition.x = (float) (viewPosition.x + viewPosition.width / 2 - textWidth / 2);
        textPosition.y = (float) (viewPosition.y + viewPosition.height / 2 + textHeight / 2);
    }


    private void updateViewPosition() {
        viewPosition.setBy(position);
        if (appearFactor.get() == 1) return;

        switch (animType) {
            default:
            case none:
            case def:
                // nothing
                break;
            case down:
            case fixed_down:
                viewPosition.y -= (1 - appearFactor.get()) * 0.15f * GraphicsYio.height;
                break;
            case fixed_up:
            case up:
                viewPosition.y += (1 - appearFactor.get()) * 0.15f * GraphicsYio.height;
                break;
        }
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    public void setAnimation(Animation animType) {
        this.animType = animType;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(MenuControllerYio.DESTROY_ANIM, MenuControllerYio.DESTROY_SPEED);
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);
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
        this.position.setBy(position);
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderTextViewElement;
    }
}
