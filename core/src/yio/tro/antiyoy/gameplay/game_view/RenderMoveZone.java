package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.MoveZoneManager;
import yio.tro.antiyoy.gameplay.Obj;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;

import java.util.ArrayList;

public class RenderMoveZone extends GameRender {


    private PointYio pos;
    private TextureRegion currentHexTexture;
    private TextureRegion currentHexLastTexture;
    private Color c;
    private float hvSize;
    TextureRegion moveZonePixel;
    private MoveZoneManager moveZoneManager;
    private ArrayList<Hex> moveZone;
    private FactorYio appearFactor;


    public RenderMoveZone(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {
        String moveZoneTexturePath = gameRendersList.gameView.yioGdxGame.skinManager.getMoveZoneTexturePath();
        moveZonePixel = loadTextureRegion(moveZoneTexturePath, false);
    }


    @Override
    public void render() {
        prepare();

        renderHexBackgrounds();
        renderBuildingsAndBlackLines();
        gameView.rList.renderResponseAnimHex.render();
        renderMoveZoneBorderAndSomeObjects();
        renderSelectionBorder();

        batchMovable.setColor(c.r, c.g, c.b, 1);
    }


    private void prepare() {
        c = batchMovable.getColor();
        hvSize = gameView.hexViewSize;
        moveZoneManager = gameController.fieldManager.moveZoneManager;
        moveZone = moveZoneManager.moveZone;
        appearFactor = moveZoneManager.appearFactor;
    }


    private void renderSelectionBorder() {
        if (gameController.fieldManager.selectedHexes.size() == 0) return;

        batchMovable.setColor(c.r, c.g, c.b, 1f - appearFactor.get());

        for (Hex hex : moveZone) {
            for (int dir = 0; dir < 6; dir++) {
                Hex adj = hex.getAdjacentHex(dir);
                if (adj == null || adj.isNullHex() || (adj.active && adj.sameFraction(hex))) continue;

                renderLineBetweenHexesWithOffset(batchMovable, gameView.texturesManager.selectionBorder, hex,
                        adj,
                        hex.selectionFactor.get() * 0.01 * GraphicsYio.width,
                        -(1d - hex.selectionFactor.get()) * 0.01 * GraphicsYio.width,
                        dir,
                        hex.selectionFactor.get()
                );
            }
        }
    }


    private void renderMoveZoneBorderAndSomeObjects() {
        if (gameController.selectionManager.selectedUnit == null && gameController.selectionManager.tipFactor.get() <= 0 && appearFactor.get() <= 0)
            return;

        for (Hex hex : moveZone) {
            for (int dir = 0; dir < 6; dir++) {
                Hex adj = hex.getAdjacentHex(dir);
                if (adj == null || adj.isNullHex() || (adj.active && adj.inMoveZone == hex.inMoveZone)) continue;

                renderLineBetweenHexesWithOffset(batchMovable, moveZonePixel, hex,
                        adj,
                        appearFactor.get() * 0.02 * GraphicsYio.width,
                        -(1d - appearFactor.get()) * 0.01 * GraphicsYio.width,
                        dir,
                        appearFactor.get()
                );
            }

            if (hex.containsUnit()) {
                gameView.rList.renderUnits.renderUnit(batchMovable, hex.unit);
            }

            if (hex.containsTree()) {
                renderSolidObject(batchMovable, hex.pos, hex);
            }
        }
    }


    private void renderBuildingsAndBlackLines() {
        batchMovable.setColor(c.r, c.g, c.b, 1);

        for (Hex hex : moveZone) {
            pos = hex.getPos();
            if (!isPosInViewFrame(pos, hvSize)) continue;

            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (adjacentHex == null || (adjacentHex.active && adjacentHex.sameFraction(hex))) continue;

                renderLineBetweenHexes(batchMovable, adjacentHex, hex, gameView.borderLineThickness, dir);
            }

            if (hex.containsBuilding() || hex.objectInside == Obj.GRAVE) {
                renderSolidObject(batchMovable, pos, hex);
            }
        }
    }


    private void renderHexBackgrounds() {
        GameTexturesManager texturesManager = gameView.texturesManager;
        for (Hex hex : moveZone) {
            pos = hex.getPos();
            if (!isPosInViewFrame(pos, hvSize)) continue;

            if (gameController.isPlayerTurn(hex.fraction) && hex.animFactor.get() < 1 && hex.animFactor.getDy() > 0) {
                if (hex.animFactor.get() < 1) {
                    currentHexLastTexture = texturesManager.getHexTextureByFraction(hex.previousFraction);
                    batchMovable.setColor(c.r, c.g, c.b, 1f - hex.animFactor.get());
                    batchMovable.draw(currentHexLastTexture, pos.x - hvSize, pos.y - hvSize, 2 * hvSize, 2 * hvSize);
                }
                currentHexTexture = texturesManager.getHexTextureByFraction(hex.fraction);
                batchMovable.setColor(c.r, c.g, c.b, hex.animFactor.get());
                batchMovable.draw(currentHexTexture, pos.x - hvSize, pos.y - hvSize, 2 * hvSize, 2 * hvSize);
                continue;
            }

            batchMovable.setColor(c.r, c.g, c.b, 1);
            currentHexTexture = texturesManager.getHexTextureByFraction(hex.fraction);
            batchMovable.draw(currentHexTexture, pos.x - hvSize, pos.y - hvSize, 2 * hvSize, 2 * hvSize);
        }
    }


    @Override
    public void disposeTextures() {
        moveZonePixel.getTexture().dispose();
    }
}
