package yio.tro.antiyoy.menu.customizable_list;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.menu.render.AbstractRenderCustomListItem;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class ScrollListItem extends AbstractSingleLineItem{

    SliReaction clickReaction;
    SliReaction longTapReaction;
    public String key;
    private float height;
    boolean centered;
    public boolean highlightEnabled;


    public ScrollListItem() {
        clickReaction = null;
        longTapReaction = null;
        key = null;
        height = 0.1f * GraphicsYio.height;
        centered = false;
        highlightEnabled = false;
    }


    @Override
    protected BitmapFont getTitleFont() {
        return Fonts.smallerMenuFont;
    }


    @Override
    protected double getHeight() {
        return height;
    }


    @Override
    protected void onClicked() {
        if (clickReaction != null) {
            clickReaction.apply(this);
        }
    }


    @Override
    protected void onLongTapped() {
        super.onLongTapped();
        if (longTapReaction != null) {
            longTapReaction.apply(this);
        }
    }


    public void setClickReaction(SliReaction clickReaction) {
        this.clickReaction = clickReaction;
    }


    public void setLongTapReaction(SliReaction longTapReaction) {
        this.longTapReaction = longTapReaction;
    }


    public void setKey(String key) {
        this.key = key;
    }


    public void setHeight(float height) {
        this.height = height;
    }


    public void setCentered(boolean centered) {
        this.centered = centered;
    }


    @Override
    protected void onPositionChanged() {
        if (centered) {
            title.delta.x = (float) (getWidth() / 2 - title.width / 2);
            title.delta.y = (float) (getHeight() / 2 + title.height / 2);
            return;
        }

        super.onPositionChanged();
    }


    public void setHighlightEnabled(boolean highlightEnabled) {
        this.highlightEnabled = highlightEnabled;
    }


    @Override
    public AbstractRenderCustomListItem getRender() {
        return MenuRender.renderScrollListItem;
    }
}
