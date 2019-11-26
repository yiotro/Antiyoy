package yio.tro.antiyoy.gameplay.game_view;

import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderSelectedHexes extends GameRender{

    public RenderSelectedHexes(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        renderSelectionBorders();
        renderObjectsOnSelectedHexes();
    }


    private void renderSelectionBorders() {
        for (Hex hex : gameController.fieldManager.selectedHexes) {
            if (hex.selectionFactor.get() < 0.01) continue;

            for (int dir = 0; dir < 6; dir++) {
                renderBorderBetweenAdjacentHexes(hex, dir);
            }
        }
    }


    private void renderBorderBetweenAdjacentHexes(Hex hex, int dir) {
        Hex adj = hex.getAdjacentHex(dir);
        if (!isSelectionBorderNeeded(hex, adj)) return;

        renderLineBetweenHexesWithOffset(
                batchMovable, gameView.texturesManager.selectionBorder, hex,
                adj,
                hex.selectionFactor.get() * 0.01 * GraphicsYio.width,
                -(1d - hex.selectionFactor.get()) * 0.01 * GraphicsYio.width,
                dir,
                hex.selectionFactor.get()
        );
    }


    private boolean isSelectionBorderNeeded(Hex hex, Hex adj) {
        return adj != null && !adj.isNullHex() && (!adj.active || !adj.sameFraction(hex));
    }


    private void renderObjectsOnSelectedHexes() {
        for (Hex hex : gameController.fieldManager.selectedHexes) {
            if (!hex.containsObject()) continue;

            renderSolidObject(batchMovable, hex.getPos(), hex);
        }
    }


    @Override
    public void disposeTextures() {

    }
}
