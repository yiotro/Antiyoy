package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.diplomacy_element.DeIcon;
import yio.tro.antiyoy.menu.diplomacy_element.DeItem;
import yio.tro.antiyoy.menu.diplomacy_element.DeLabel;
import yio.tro.antiyoy.menu.diplomacy_element.DiplomacyElement;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class RenderDiplomacyElement extends MenuRender {

    private TextureRegion backgroundTexture;
    private TextureRegion selectionPixel;
    DiplomacyElement diplomacyElement;
    private RectangleYio viewPosition;
    private float factor;
    private TextureRegion neutralIcon;
    private TextureRegion friendIcon;
    private TextureRegion enemyIcon;
    private TextureRegion deadIcon;
    private TextureRegion bckColors[];
    private final float shadowThickness;
    private TextureRegion likeIcon;
    private TextureRegion dislikeIcon;
    private TextureRegion skullIcon;
    private TextureRegion infoIcon;


    public RenderDiplomacyElement() {
        shadowThickness = 0.08f * GraphicsYio.width;
    }


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("diplomacy/background.png", false);
        selectionPixel = GraphicsYio.loadTextureRegion("pixels/black_pixel.png", false);

        neutralIcon = GraphicsYio.loadTextureRegion("diplomacy/face_neutral.png", true);
        friendIcon = GraphicsYio.loadTextureRegion("diplomacy/face_friend.png", true);
        enemyIcon = GraphicsYio.loadTextureRegion("diplomacy/face_enemy.png", true);
        deadIcon = GraphicsYio.loadTextureRegion("diplomacy/face_dead.png", true);

        likeIcon = GraphicsYio.loadTextureRegion("diplomacy/like_icon.png", true);
        dislikeIcon = GraphicsYio.loadTextureRegion("diplomacy/dislike_icon.png", true);
        skullIcon = GraphicsYio.loadTextureRegion("diplomacy/skull_icon.png", true);
        infoIcon = GraphicsYio.loadTextureRegion("diplomacy/info_icon.png", true);

        bckColors = new TextureRegion[7];
        for (int i = 0; i < bckColors.length; i++) {
            bckColors[i] = GraphicsYio.loadTextureRegion("diplomacy/color" + (i + 1) + ".png", false);
        }
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        diplomacyElement = (DiplomacyElement) element;
        viewPosition = diplomacyElement.viewPosition;
        factor = diplomacyElement.getFactor().get();

        renderShadow();

        renderInternals();
    }


    private void renderShadow() {
        batch.draw(
                menuViewYio.shadowSide,
                (float) viewPosition.x,
                (float) (viewPosition.y + viewPosition.height - shadowThickness / 2),
                (float) viewPosition.width,
                shadowThickness
        );
    }


    private void renderInternals() {
        renderBackground();
        renderItems();
        renderCover();
        renderLabel();
        renderIcons();
    }


    private void renderIcons() {
        for (DeIcon icon : diplomacyElement.icons) {
            if (!icon.isVisible()) continue;

            GraphicsYio.setBatchAlpha(batch, icon.appearFactor.get());

            GraphicsYio.drawFromCenter(
                    batch,
                    getIconTexture(icon),
                    icon.position.x,
                    icon.position.y,
                    icon.radius
            );

            GraphicsYio.setBatchAlpha(batch, 1);

            if (icon.isSelected()) {
                GraphicsYio.setBatchAlpha(batch, 0.4 * icon.selectionFactor.get());

                GraphicsYio.drawFromCenter(
                        batch,
                        getGameView().blackPixel,
                        icon.position.x,
                        icon.position.y,
                        icon.radius
                );

                GraphicsYio.setBatchAlpha(batch, 1);
            }
        }
    }


    private TextureRegion getIconTexture(DeIcon deIcon) {
        switch (deIcon.action) {
            default:
                return null;
            case DeIcon.ACTION_LIKE:
                return likeIcon;
            case DeIcon.ACTION_DISLIKE:
                return dislikeIcon;
            case DeIcon.ACTION_BLACK_MARK:
                return skullIcon;
            case DeIcon.ACTION_INFO:
                return infoIcon;
        }
    }


    private void renderCover() {
        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                diplomacyElement.topCover
        );
    }


    private void renderBackground() {
        if (!diplomacyElement.needToRenderInternalBackground()) return;

        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                diplomacyElement.internalBackground
        );
    }


    private void renderLabel() {
        DeLabel label = diplomacyElement.label;
        if (!label.visible) return;

        GraphicsYio.setFontAlpha(diplomacyElement.titleFont, label.appearFactor.get());

        diplomacyElement.titleFont.draw(
                batch,
                label.text,
                label.position.x,
                label.position.y
        );

        GraphicsYio.setFontAlpha(diplomacyElement.titleFont, 1);
    }


    private void renderItems() {
        diplomacyElement.descFont.setColor(Color.BLACK);
        Color titleColor = diplomacyElement.titleFont.getColor();
        diplomacyElement.titleFont.setColor(Color.BLACK);

        for (DeItem item : diplomacyElement.items) {
            renderSingleItem(item);
        }

        diplomacyElement.descFont.setColor(Color.WHITE);
        diplomacyElement.titleFont.setColor(titleColor);
    }


    private void renderSingleItem(DeItem item) {
        if (!item.isTopVisible()) {
            if (item.isBottomVisible()) {
                renderItemBackground(item, item.bottomRectangle);
                renderItemDescription(item);
                renderItemSelection(item, item.bottomRectangle);
            }
            return;
        }

        renderItemBackground(item, item.position);
        renderItemTitle(item);
        renderItemDescription(item);
        renderBlackMark(item);
        renderItemStatus(item);
        renderItemSelection(item, item.position);
    }


    private void renderBlackMark(DeItem item) {
        if (!item.blackMarkEnabled) return;

        GraphicsYio.drawFromCenter(
                batch,
                skullIcon,
                item.blackMarkPosition.x,
                item.blackMarkPosition.y,
                item.blackMarkRadius
        );
    }


    private void renderItemDescription(DeItem item) {
        diplomacyElement.descFont.draw(
                batch,
                item.descriptionString,
                item.descPosition.x,
                item.descPosition.y
        );
    }


    private void renderItemBackground(DeItem item, RectangleYio pos) {
        GraphicsYio.drawByRectangle(
                batch,
                bckColors[menuViewYio.yioGdxGame.gameController.getColorIndexWithOffset(item.colorIndex)],
                pos
        );
    }


    private void renderItemStatus(DeItem item) {
        GraphicsYio.drawFromCenter(
                batch,
                getItemStatusIcon(item),
                item.statusPosition.x,
                item.statusPosition.y,
                item.statusRadius
        );
    }


    private TextureRegion getItemStatusIcon(DeItem item) {
        switch (item.status) {
            default:
                return null;
            case DeItem.STATUS_NEUTRAL:
                return neutralIcon;
            case DeItem.STATUS_FRIEND:
                return friendIcon;
            case DeItem.STATUS_ENEMY:
                return enemyIcon;
            case DeItem.STATUS_DEAD:
                return deadIcon;
        }
    }


    private void renderItemSelection(DeItem item, RectangleYio pos) {
        if (!item.isSelected()) return;

        GraphicsYio.setBatchAlpha(batch, 0.5 * item.getSelectionFactor().get());

        GraphicsYio.drawByRectangle(
                batch,
                selectionPixel,
                pos
        );

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderItemTitle(DeItem item) {
        if (diplomacyElement.appearFactor.get() == 0) return;

        diplomacyElement.titleFont.draw(
                batch,
                item.title,
                item.titlePosition.x,
                item.titlePosition.y
        );
    }


    public TextureRegion getBackgroundPixel(int colorIndex) {
        return bckColors[colorIndex];
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
