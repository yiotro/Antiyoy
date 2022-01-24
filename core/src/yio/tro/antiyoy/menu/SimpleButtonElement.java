package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.*;

public class SimpleButtonElement extends AbstractRectangularUiElement{

    public RenderableTextYio title;
    public AbstractRectangularUiElement parent;
    double pOffset;
    public SelectionEngineYio selectionEngineYio;
    Reaction reaction;
    boolean ready;


    public SimpleButtonElement(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        parent = null;
        pOffset = 0;
        selectionEngineYio = new SelectionEngineYio();
        reaction = null;
    }


    public void setTitle(String key) {
        title.setString(LanguagesManager.getInstance().getString(key));
        title.updateMetrics();
    }


    @Override
    protected void onMove() {
        checkToApplyParent();
        moveTitle();
        moveSelection();
    }


    private void moveSelection() {
        if (touched) return;
        selectionEngineYio.move();
    }


    private void checkToApplyParent() {
        if (parent == null) return;
        viewPosition.y = parent.viewPosition.y + pOffset;
    }


    private void moveTitle() {
        title.centerVertical(viewPosition);
        title.centerHorizontal(viewPosition);
        title.updateBounds();
    }


    @Override
    protected void onDestroy() {
        ready = false;
    }


    @Override
    protected void onAppear() {
        ready = false;
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
        ready = true;
    }


    @Override
    public boolean checkToPerformAction() {
        if (ready) {
            ready = false;
            applyClickReaction();
            return true;
        }

        return false;
    }


    private void applyClickReaction() {
        if (reaction == null) return;
        SoundManagerYio.playSound(SoundManagerYio.soundPressButton);
        reaction.perform(null);
    }


    public void setParent(AbstractRectangularUiElement parent, double verticalOffset) {
        this.parent = parent;
        pOffset = verticalOffset * GraphicsYio.height;
    }


    public void setReaction(Reaction reaction) {
        this.reaction = reaction;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderSimpleButtonElement;
    }
}
