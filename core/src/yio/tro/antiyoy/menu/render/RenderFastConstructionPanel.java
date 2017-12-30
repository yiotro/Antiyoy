package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.fast_construction.FastConstructionPanel;
import yio.tro.antiyoy.menu.fast_construction.FcpItem;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class RenderFastConstructionPanel extends MenuRender{


    private TextureRegion backgroundTexture;
    private TextureRegion selectionPixel;
    private FastConstructionPanel panel;
    private float factor;
    private RectangleYio pos;
    private TextureRegion man0;
    private TextureRegion man1;
    private TextureRegion man2;
    private TextureRegion man3;
    private TextureRegion tower;
    private TextureRegion strongTower;
    private TextureRegion house;
    private TextureRegion shrMan0;
    private TextureRegion shrMan1;
    private TextureRegion shrMan2;
    private TextureRegion shrMan3;
    private TextureRegion shrTower;
    private TextureRegion shrStrongTower;
    private TextureRegion shrHouse;
    private TextureRegion sideShadow;
    private float smDelta;
    private TextureRegion endTurnIcon;
    private TextureRegion undoIcon;
    private TextureRegion diplomacyIcon;


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("pixels/gray_pixel.png", false);
        selectionPixel = GraphicsYio.loadTextureRegion("pixels/black_pixel.png", false);
        sideShadow = GraphicsYio.loadTextureRegion("money_shadow.png", true);
        endTurnIcon = GraphicsYio.loadTextureRegion("end_turn.png", true);
        undoIcon = GraphicsYio.loadTextureRegion("undo.png", true);
        diplomacyIcon = GraphicsYio.loadTextureRegion("diplomacy/flag.png", true);

        man0 = GraphicsYio.loadTextureRegion("field_elements/man0.png", true);
        man1 = GraphicsYio.loadTextureRegion("field_elements/man1.png", true);
        man2 = GraphicsYio.loadTextureRegion("field_elements/man2.png", true);
        man3 = GraphicsYio.loadTextureRegion("field_elements/man3.png", true);
        tower = GraphicsYio.loadTextureRegion("field_elements/tower.png", true);
        strongTower = GraphicsYio.loadTextureRegion("field_elements/strong_tower.png", true);
        house = GraphicsYio.loadTextureRegion("field_elements/house.png", true);

        shrMan0 = GraphicsYio.loadTextureRegion("skins/ant/field_elements/man0.png", true);
        shrMan1 = GraphicsYio.loadTextureRegion("skins/ant/field_elements/man1.png", true);
        shrMan2 = GraphicsYio.loadTextureRegion("skins/ant/field_elements/man2.png", true);
        shrMan3 = GraphicsYio.loadTextureRegion("skins/ant/field_elements/man3.png", true);
        shrTower = GraphicsYio.loadTextureRegion("skins/ant/field_elements/tower.png", true);
        shrStrongTower = GraphicsYio.loadTextureRegion("skins/ant/field_elements/strong_tower.png", true);
        shrHouse = GraphicsYio.loadTextureRegion("skins/ant/field_elements/house.png", true);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        panel = (FastConstructionPanel) element;
        factor = panel.getFactor().get();
        pos = panel.viewPosition;

        GraphicsYio.setBatchAlpha(batch, factor);

        renderShadow();
        renderBackground();
        renderItems();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderShadow() {
        smDelta = 0.1f * h * (1 - factor);
        batch.draw(sideShadow, 0, -smDelta + 0.03f * h, w, 0.1f * h);
    }


    private void renderItems() {
        for (FcpItem item : panel.items) {
            if (!item.isVisible()) continue;

            GraphicsYio.drawFromCenter(
                    batch,
                    getItemTexture(item),
                    item.position.x,
                    item.position.y,
                    item.radius
            );

            if (item.isSelected()) {
                GraphicsYio.setBatchAlpha(batch, 0.5f * item.selectionFactor.get());

                GraphicsYio.drawFromCenter(
                        batch,
                        selectionPixel,
                        item.position.x,
                        item.position.y,
                        item.radius
                );

                GraphicsYio.setBatchAlpha(batch, factor);
            }
        }
    }


    private void renderBackground() {
        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                pos
        );
    }


    private TextureRegion getItemTexture(FcpItem item) {
        if (item.action == FcpItem.ACTION_UNDO) {
            return undoIcon;
        }

        if (item.action == FcpItem.ACTION_END_TURN) {
            return endTurnIcon;
        }

        if (item.action == FcpItem.ACTION_DIPLOMACY) {
            return diplomacyIcon;
        }

        if (Settings.isShroomArtsEnabled()) {
            return getShroomItemTexture(item);
        } else {
            return getDefaultItemTexture(item);
        }
    }


    private TextureRegion getDefaultItemTexture(FcpItem item) {
        switch (item.action) {
            default: return null;
            case FcpItem.ACTION_UNIT_1: return man0;
            case FcpItem.ACTION_UNIT_2: return man1;
            case FcpItem.ACTION_UNIT_3: return man2;
            case FcpItem.ACTION_UNIT_4: return man3;
            case FcpItem.ACTION_FARM: return house;
            case FcpItem.ACTION_TOWER: return tower;
            case FcpItem.ACTION_STRONG_TOWER: return strongTower;
        }
    }


    private TextureRegion getShroomItemTexture(FcpItem item) {
        switch (item.action) {
            default: return null;
            case FcpItem.ACTION_UNIT_1: return shrMan0;
            case FcpItem.ACTION_UNIT_2: return shrMan1;
            case FcpItem.ACTION_UNIT_3: return shrMan2;
            case FcpItem.ACTION_UNIT_4: return shrMan3;
            case FcpItem.ACTION_FARM: return shrHouse;
            case FcpItem.ACTION_TOWER: return shrTower;
            case FcpItem.ACTION_STRONG_TOWER: return shrStrongTower;
        }
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
