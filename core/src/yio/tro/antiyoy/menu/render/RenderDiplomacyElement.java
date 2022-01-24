package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.gameplay.skins.SkinManager;
import yio.tro.antiyoy.gameplay.skins.SkinType;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.diplomacy_element.*;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

import java.util.HashMap;

public class RenderDiplomacyElement extends MenuRender {

    private TextureRegion backgroundTexture;
    private TextureRegion selectionPixel;
    DiplomacyElement diplomacyElement;
    private RectangleYio viewPosition;
    private float factor;
    public TextureRegion neutralIcon;
    public TextureRegion friendIcon;
    public TextureRegion enemyIcon;
    private TextureRegion deadIcon;
    private TextureRegion bckColors[];
    private final float shadowThickness;
    HashMap<DipActionType, TextureRegion> mapIconTextures;


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

        mapIconTextures = new HashMap<>();
        for (DipActionType dipActionType : DipActionType.values()) {
            mapIconTextures.put(dipActionType, GraphicsYio.loadTextureRegion("diplomacy/" + dipActionType + "_icon.png", true));
        }

        loadBackgroundColors();
    }


    public void loadBackgroundColors() {
        bckColors = new TextureRegion[GameRules.MAX_FRACTIONS_QUANTITY];
        SkinManager skinManager = menuViewYio.yioGdxGame.skinManager;
        for (int i = 0; i < bckColors.length; i++) {
            bckColors[i] = GraphicsYio.loadTextureRegion(skinManager.getDiplomacyFolderPath() + "color" + (i + 1) + ".png", false);
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
                        getBlackPixel(),
                        icon.position.x,
                        icon.position.y,
                        icon.radius
                );

                GraphicsYio.setBatchAlpha(batch, 1);
            }
        }
    }


    private TextureRegion getIconTexture(DeIcon deIcon) {
        return mapIconTextures.get(deIcon.action);
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
                mapIconTextures.get(DipActionType.black_mark),
                item.blackMarkPosition.x,
                item.blackMarkPosition.y,
                item.blackMarkRadius
        );
    }


    private void renderItemDescription(DeItem item) {
        if (item.descriptionString == null) return;

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
                bckColors[menuViewYio.yioGdxGame.gameController.getColorByFraction(item.fraction)],
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
            case DeItem.STATUS_DEAD:
                return deadIcon;
            case DeItem.STATUS_NEUTRAL:
                return neutralIcon;
            case DeItem.STATUS_FRIEND:
                return friendIcon;
            case DeItem.STATUS_ENEMY:
                return enemyIcon;
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


    public TextureRegion getBackgroundPixelByColor(int color) {
        return bckColors[color];
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
