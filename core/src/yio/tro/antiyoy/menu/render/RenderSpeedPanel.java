package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.SpeedManager;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.speed_panel.SpItem;
import yio.tro.antiyoy.menu.speed_panel.SpeedPanel;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class RenderSpeedPanel extends MenuRender{

    private TextureRegion backgroundTexture;
    private TextureRegion playIcon;
    private TextureRegion stopIcon;
    private TextureRegion pauseIcon;
    private TextureRegion fastForwardIcon;
    private TextureRegion selectionPixel;
    private SpeedPanel speedPanel;
    private FactorYio factor;
    private RectangleYio viewPosition;
    private TextureRegion saveIcon;


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("pixels/gray_pixel.png", false);
        playIcon = GraphicsYio.loadTextureRegion("menu/replays/play.png", true);
        stopIcon = GraphicsYio.loadTextureRegion("menu/replays/stop.png", true);
        pauseIcon = GraphicsYio.loadTextureRegion("menu/replays/pause.png", true);
        fastForwardIcon = GraphicsYio.loadTextureRegion("menu/replays/fast_forward.png", true);
        saveIcon = GraphicsYio.loadTextureRegion("menu/replays/save_icon.png", true);
        selectionPixel = GraphicsYio.loadTextureRegion("pixels/black_pixel.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        speedPanel = (SpeedPanel) element;
        factor = speedPanel.getFactor();
        viewPosition = speedPanel.viewPosition;

        GraphicsYio.setBatchAlpha(batch, factor.get());

        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                viewPosition
        );

        renderItems();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderItems() {
        for (SpItem item : speedPanel.items) {
            if (!item.isVisible()) continue;

            GraphicsYio.setBatchAlpha(batch, item.appearFactor.get());

            GraphicsYio.drawFromCenter(
                    batch,
                    getItemTexture(item),
                    item.position.x,
                    item.position.y,
                    item.radius
            );

            GraphicsYio.setBatchAlpha(batch, 1);

            if (item.isSelected()) {
                GraphicsYio.setBatchAlpha(batch, 0.5f * item.selectionFactor.get());

                GraphicsYio.drawFromCenter(
                        batch,
                        selectionPixel,
                        item.position.x,
                        item.position.y,
                        item.radius
                );

                GraphicsYio.setBatchAlpha(batch, speedPanel.getFactor().get());
            }
        }
    }


    private TextureRegion getItemTexture(SpItem item) {
        switch (item.action) {
            default:
                return null;
            case SpItem.ACTION_STOP:
                return stopIcon;
            case SpItem.ACTION_PLAY_PAUSE:
                if (getGameView().gameController.speedManager.getSpeed() != SpeedManager.SPEED_PAUSED) {
                    return pauseIcon;
                } else {
                    return playIcon;
                }
            case SpItem.ACTION_FAST_FORWARD:
                return fastForwardIcon;
            case SpItem.ACTION_SAVE:
                return saveIcon;
        }
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
