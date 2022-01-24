package yio.tro.antiyoy.menu.income_view;

import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.menu.AbstractRectangularUiElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.*;

public class MoneyViewElement extends AbstractRectangularUiElement{

    public RenderableTextYio title;
    public SelectionEngineYio selectionEngineYio;
    Reaction reaction;
    MveBehavior behavior;
    boolean centered;
    RepeatYio<MoneyViewElement> repeatUpdate;
    int currentValue;
    boolean plusNeeded;
    public RectangleYio outerBounds;


    public MoneyViewElement(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        title = new RenderableTextYio();
        title.setFont(Fonts.gameFont);
        selectionEngineYio = new SelectionEngineYio();
        reaction = null;
        behavior = null;
        centered = false;
        currentValue = 0;
        plusNeeded = false;
        outerBounds = new RectangleYio();
        initRepeats();
    }


    private void initRepeats() {
        repeatUpdate = new RepeatYio<MoneyViewElement>(this, 4) {
            @Override
            public void performAction() {
                parent.updateTitle();
            }
        };
    }


    @Override
    protected void onMove() {
        repeatUpdate.move();
        updateTextPosition();
        selectionEngineYio.move();
        updateOuterBounds();
    }


    private void updateOuterBounds() {
        outerBounds.setBy(title.bounds);
        outerBounds.increase(0.03f * GraphicsYio.width);
    }


    private void updateTextPosition() {
        title.centerVertical(viewPosition);
        if (centered) {
            title.centerHorizontal(viewPosition);
        } else {
            title.position.x = (float) viewPosition.x;
        }

        title.updateBounds();
    }


    @Override
    protected void onDestroy() {
        appearFactor.setDy(0);
        appearFactor.destroy(3, 2);
    }


    @Override
    protected void onAppear() {
        updateTitle();
        appearFactor.appear(3, 2);
    }


    private void updateTitle() {
        if (behavior == null) return;

        int titleValue = behavior.getTitleValue();
        if (currentValue != 0 && titleValue == currentValue) return;

        title.setString(castValue(titleValue));
        title.updateMetrics();
        currentValue = titleValue;
    }


    private String castValue(int titleValue) {
        String compactedString = Yio.getCompactMoneyString(titleValue);

        if (plusNeeded && titleValue > 0) {
            return "+" + compactedString;
        }

        return "" + compactedString;
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
        if (reaction != null) {
            SoundManagerYio.playSound(SoundManagerYio.soundPressButton);
            reaction.perform(null);
        }
    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderMoneyViewElement;
    }


    public void setReaction(Reaction reaction) {
        this.reaction = reaction;
    }


    public void setBehavior(MveBehavior behavior) {
        this.behavior = behavior;
    }


    public void setCentered(boolean centered) {
        this.centered = centered;
    }


    public void setPlusNeeded(boolean plusNeeded) {
        this.plusNeeded = plusNeeded;
    }
}
