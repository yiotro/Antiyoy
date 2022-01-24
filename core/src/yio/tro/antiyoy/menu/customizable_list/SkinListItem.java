package yio.tro.antiyoy.menu.customizable_list;

import yio.tro.antiyoy.gameplay.skins.SkinManager;
import yio.tro.antiyoy.gameplay.skins.SkinType;
import yio.tro.antiyoy.menu.render.AbstractRenderCustomListItem;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.*;

import java.util.ArrayList;

public class SkinListItem extends AbstractCustomListItem {

    public RenderableTextYio title;
    private int skinIndex;
    public SkinType skinType;
    public CircleYio iconPosition;
    public boolean darken;
    AtlasLoader atlasLoader;
    public ArrayList<SkinLiPreviewIcon> previewIcons;


    public SkinListItem(AtlasLoader atlasLoader) {
        super();
        this.atlasLoader = atlasLoader;
    }


    @Override
    protected void initialize() {
        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        iconPosition = new CircleYio();
        darken = false;
    }


    private void initPreview() {
        previewIcons = new ArrayList<>();
        addPreviewIcon("man0");
        addPreviewIcon("man1");
        addPreviewIcon("man2");
        addPreviewIcon("farm1");
        addPreviewIcon("palm");
        addPreviewIcon("tower");
    }


    private void addPreviewIcon(String name) {
        SkinLiPreviewIcon skinLiPreviewIcon = new SkinLiPreviewIcon(this);
        skinLiPreviewIcon.loadTexture(name);
        previewIcons.add(skinLiPreviewIcon);
    }


    @Override
    protected void move() {
        moveRenderableTextByDefault(title);
        updateIconPosition();
        movePreviewIcons();
    }


    private void movePreviewIcons() {
        for (SkinLiPreviewIcon previewIcon : previewIcons) {
            previewIcon.move();
        }
    }


    private void updateIconPosition() {
        iconPosition.setRadius(0.15 * viewPosition.height);
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
        return 0.11f * GraphicsYio.height;
    }


    public void setSkinInfo(SkinType skinType) {
        this.skinType = skinType;
        skinIndex = skinType.ordinal();

        String string = LanguagesManager.getInstance().getString("" + skinType);
        setTitle(string);
    }


    public void setDarken(boolean darken) {
        this.darken = darken;
    }


    private void setTitle(String string) {
        title.setString(string);
        title.updateMetrics();
    }


    @Override
    protected void onPositionChanged() {
        title.delta.x = 0.04f * GraphicsYio.width;
        title.delta.y = (float) (getHeight() - 0.02f * GraphicsYio.height);
        initPreview();
        preparePreviewMetrics();
    }


    private void preparePreviewMetrics() {
        double radius = 0.018 * GraphicsYio.height;
        for (SkinLiPreviewIcon previewIcon : previewIcons) {
            previewIcon.viewPosition.setRadius(radius);
        }
        double dx = 0.035 * GraphicsYio.width + radius;
        double dy = 0.2 * getHeight() + radius;
        for (SkinLiPreviewIcon previewIcon : previewIcons) {
            previewIcon.delta.set(dx, dy);
            dx += 2.5 * radius;
        }
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
