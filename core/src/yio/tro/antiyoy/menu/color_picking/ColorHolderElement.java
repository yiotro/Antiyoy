package yio.tro.antiyoy.menu.color_picking;

import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.AbstractRectangularUiElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.editor_elements.color_picker.IColorChoiceListener;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.*;

public class ColorHolderElement extends AbstractRectangularUiElement implements IColorChoiceListener{

    public RenderableTextYio title;
    public int valueIndex;
    public RectangleYio tagPosition;
    public SelectionEngineYio selectionEngineYio;
    boolean touchable;
    Reaction rbChange;


    public ColorHolderElement(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        valueIndex = 0;
        tagPosition = new RectangleYio();
        selectionEngineYio = new SelectionEngineYio();
        touchable = true;
        rbChange = null;
    }


    public void setTitle(String string) {
        title.setString(string);
        title.updateMetrics();
    }


    @Override
    protected void onMove() {
        updateTitlePosition();
        updateTagPosition();
        selectionEngineYio.move();
    }


    private void updateTagPosition() {
        tagPosition.height = viewPosition.height / 2;
        tagPosition.width = tagPosition.height;
        tagPosition.x = viewPosition.x + viewPosition.width - viewPosition.height / 3 - tagPosition.width;
        tagPosition.y = viewPosition.y + viewPosition.height / 2 - tagPosition.height / 2;
    }


    private void updateTitlePosition() {
        title.position.x = (float) (viewPosition.x + viewPosition.height / 3);
        title.centerVertical(viewPosition);
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
        selectionEngineYio.select();
    }


    @Override
    protected void onTouchDrag() {

    }


    @Override
    protected void onTouchUp() {

    }


    @Override
    protected void onClick() {
        SoundManagerYio.playSound(SoundManagerYio.soundPressButton);
        Scenes.sceneColorPicker.create();
        Scenes.sceneColorPicker.setListener(this);
        Scenes.sceneColorPicker.addRandomColorItem();
    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderColorHolderElement;
    }


    public void setValueIndex(int valueIndex) {
        this.valueIndex = valueIndex;
    }


    public static int getColor(int valueIndex, int fractionsQuantity) {
        if (valueIndex == 0) {
            return YioGdxGame.random.nextInt(Math.min(fractionsQuantity, GameRules.NEUTRAL_FRACTION));
        }
        return valueIndex - 1;
    }


    public int getColor(int fractionsQuantity) {
        return getColor(valueIndex, fractionsQuantity);
    }


    public int getValueIndex() {
        return valueIndex;
    }


    @Override
    public boolean isTouchable() {
        return touchable;
    }


    @Override
    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }


    @Override
    public void onColorChosen(int color) {
        setValueIndex(color + 1);
        if (rbChange != null) {
            rbChange.perform(null);
        }
    }


    public void setChangeReaction(Reaction rbChange) {
        this.rbChange = rbChange;
    }
}
