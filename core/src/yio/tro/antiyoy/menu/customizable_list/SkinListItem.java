package yio.tro.antiyoy.menu.customizable_list;

import yio.tro.antiyoy.menu.render.AbstractRenderCustomListItem;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.*;

public class SkinListItem extends AbstractCustomListItem{

    public RenderableTextYio title;
    private int skinIndex;
    public CircleYio iconPosition;


    @Override
    protected void initialize() {
        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        iconPosition = new CircleYio();
    }


    @Override
    protected void move() {
        moveRenderableTextByDefault(title);
        updateIconPosition();
    }


    private void updateIconPosition() {
        iconPosition.setRadius(0.25 * viewPosition.height);
        iconPosition.center.x = (float) (viewPosition.x + viewPosition.width - viewPosition.height / 2);
        iconPosition.center.y = (float) (viewPosition.y + viewPosition.height / 2);
    }


    public boolean isChosen() {
        return skinIndex == Scenes.sceneMoreSettings.chosenSkinIndex;
    }


    @Override
    protected double getWidth() {
        return getDefaultWidth();
    }


    @Override
    protected double getHeight() {
        return 0.08f * GraphicsYio.height;
    }


    public void setSkinInfo(int skinIndex, String key) {
        this.skinIndex = skinIndex;
        setTitle(LanguagesManager.getInstance().getString(key));
    }


    private void setTitle(String string) {
        title.setString(string);
        title.updateMetrics();
    }


    @Override
    protected void onPositionChanged() {
        title.delta.x = 0.04f * GraphicsYio.width;
        title.delta.y = (float) (getHeight() / 2 + title.height / 2);
    }


    @Override
    protected void onClicked() {
        Scenes.sceneMoreSettings.create();
        Scenes.sceneMoreSettings.chosenSkinIndex = skinIndex;
    }


    @Override
    protected void onLongTapped() {

    }


    @Override
    public AbstractRenderCustomListItem getRender() {
        return MenuRender.renderSkinListItem;
    }
}
