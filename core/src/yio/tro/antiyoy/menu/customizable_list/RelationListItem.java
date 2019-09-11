package yio.tro.antiyoy.menu.customizable_list;

import yio.tro.antiyoy.gameplay.editor.EditorRelation;
import yio.tro.antiyoy.menu.render.AbstractRenderCustomListItem;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.*;

public class RelationListItem extends AbstractCustomListItem{

    public RenderableTextYio nameOne;
    public RenderableTextYio nameTwo;
    public RectangleYio outerBoundsOne;
    public RectangleYio outerBoundsTwo;
    public CircleYio iconPosition;
    SliReaction clickReaction;
    public EditorRelation editorRelation;


    @Override
    protected void initialize() {
        editorRelation = null;
        nameOne = new RenderableTextYio();
        nameOne.setFont(Fonts.smallerMenuFont);
        nameTwo = new RenderableTextYio();
        nameTwo.setFont(Fonts.smallerMenuFont);
        outerBoundsOne = new RectangleYio();
        outerBoundsTwo = new RectangleYio();
        iconPosition = new CircleYio();
        clickReaction = null;
    }


    public void set(EditorRelation editorRelation, String name1, String name2) {
        this.editorRelation = editorRelation;
        nameOne.setString(name1);
        nameOne.updateMetrics();
        nameTwo.setString(name2);
        nameTwo.updateMetrics();
    }


    @Override
    protected void move() {
        updateIconPosition();
        updateTextPositions();
        updateOuterBounds();
    }


    private void updateOuterBounds() {
        outerBoundsOne.setBy(nameOne.bounds);
        outerBoundsOne.increase(2 * GraphicsYio.borderThickness);

        outerBoundsTwo.setBy(nameTwo.bounds);
        outerBoundsTwo.increase(2 * GraphicsYio.borderThickness);
    }


    private void updateTextPositions() {
        nameOne.position.x = (float) (iconPosition.center.x - viewPosition.height - nameOne.width);
        nameOne.position.y = (float) (viewPosition.y + viewPosition.height / 2 + nameOne.height / 2);
        nameOne.updateBounds();

        nameTwo.position.x = (float) (iconPosition.center.x + viewPosition.height);
        nameTwo.position.y = (float) (viewPosition.y + viewPosition.height / 2 + nameOne.height / 2);
        nameTwo.updateBounds();
    }


    private void updateIconPosition() {
        iconPosition.setRadius(0.3 * viewPosition.height);
        iconPosition.center.x = (float) (viewPosition.x + viewPosition.width / 2);
        iconPosition.center.y = (float) (viewPosition.y + viewPosition.height / 2);
    }


    @Override
    protected double getWidth() {
        return getDefaultWidth();
    }


    @Override
    protected double getHeight() {
        return 0.08f * GraphicsYio.height;
    }


    @Override
    protected void onPositionChanged() {

    }


    @Override
    protected void onClicked() {
        if (clickReaction != null) {
            clickReaction.apply(this);
        } else {
            System.out.println("RelationListItem.onClicked: reaction not set");
        }
    }


    public void setClickReaction(SliReaction clickReaction) {
        this.clickReaction = clickReaction;
    }


    @Override
    protected void onLongTapped() {

    }


    @Override
    public AbstractRenderCustomListItem getRender() {
        return MenuRender.renderRelationListItem;
    }
}
