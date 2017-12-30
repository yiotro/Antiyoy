package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.Storage3xTexture;
import yio.tro.antiyoy.gameplay.FieldController;
import yio.tro.antiyoy.gameplay.fog_of_war.FogOfWarManager;
import yio.tro.antiyoy.gameplay.fog_of_war.FogPoint;
import yio.tro.antiyoy.gameplay.fog_of_war.FogSlice;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.MenuViewYio;
import yio.tro.antiyoy.stuff.*;

public class RenderFogOfWar extends GameRender {


    public static boolean FOG_AS_MASK = true;

    private FogOfWarManager fogOfWarManager;
    private Storage3xTexture fogTexture;
    float size;
    private FieldController fieldController;
    private TextureRegion whitePixel;
    private AtlasLoader smallAtlasLoader;
    private ShapeRenderer shapeRenderer;
    private float hexSize;
    private float hexStep1;


    public RenderFogOfWar(GrManager grManager) {
        super(grManager);
    }


    @Override
    public void loadTextures() {
        whitePixel = GraphicsYio.loadTextureRegion("pixels/white_pixel.png", false);

        if (!FOG_AS_MASK) {
            smallAtlasLoader = createAtlasLoader();
            fogTexture = new Storage3xTexture(smallAtlasLoader, "fog.png");
        }
    }


    @Override
    public void render() {
        if (!GameRules.fogOfWarEnabled) return;

        updateReferences();
        size = 1.25f * fieldController.hexSize;

        // this is quite slow
        renderFogPoints();
        renderBlocks();

        // this is probably faster
        endFog();

//        renderDebug();
    }


    private void updateReferences() {
        fieldController = gameView.gameController.fieldController;
        fogOfWarManager = fieldController.fogOfWarManager;
    }


    public void beginFog() {
        if (!GameRules.fogOfWarEnabled) return;
        if (!FOG_AS_MASK) return;

        updateReferences();

        batchMovable.begin();
        GraphicsYio.drawByRectangle(
                batchMovable,
                gameView.blackPixel,
                gameController.cameraController.frame
        );
        batchMovable.end();

        Masking.begin();

        MenuViewYio menuViewYio = gameController.yioGdxGame.menuViewYio;
        shapeRenderer = menuViewYio.shapeRenderer;
        shapeRenderer.setProjectionMatrix(gameView.orthoCam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawShapeRendererStuff();
        shapeRenderer.end();

        shapeRenderer.setProjectionMatrix(menuViewYio.orthoCam.combined);
    }


    private void drawShapeRendererStuff() {
//        float hexSize = fogOfWarManager.fieldController.hexSize;
//        for (FogSlice viewSlice : fogOfWarManager.viewSlices) {
//            shapeRenderer.rect(
//                    viewSlice.bottomPoint.position.x - hexSize,
//                    viewSlice.bottomPoint.position.y - hexSize,
//                    2 * hexSize,
//                    viewSlice.topPoint.position.y - viewSlice.bottomPoint.position.y + 2 * hexSize
//            );
//        }

        hexSize = fogOfWarManager.fieldController.hexSize;
        hexStep1 = fogOfWarManager.fieldController.hexStep1;
        PointYio pos;

        for (FogSlice viewSlice : fogOfWarManager.viewSlices) {
            for (FogPoint point : viewSlice.points) {
                pos = point.position;
                shapeRenderer.rect(
                        pos.x - hexSize / 2, pos.y - hexStep1 / 2,
                        hexSize, hexStep1
                );
                shapeRenderer.triangle(
                        pos.x + hexSize / 2, pos.y - hexStep1 / 2,
                        pos.x + hexSize / 2, pos.y + hexStep1 / 2,
                        pos.x + hexSize, pos.y
                );
                shapeRenderer.triangle(
                        pos.x - hexSize / 2, pos.y - hexStep1 / 2,
                        pos.x - hexSize / 2, pos.y + hexStep1 / 2,
                        pos.x - hexSize, pos.y
                );
            }
        }
    }


    public void continueFog() {
        if (!GameRules.fogOfWarEnabled) return;
        if (!FOG_AS_MASK) return;

        Masking.continueAfterBatchBegin();
    }


    public void endFog() {
        if (!FOG_AS_MASK) return;

        Masking.end(batchMovable);
    }


    private void renderFogPoints() {
        if (FOG_AS_MASK) return;

        for (FogPoint fogPoint : fogOfWarManager.fogMap.values()) {
            if (!fogPoint.isVisible()) continue;

            renderSingleFogPoint(fogPoint);
        }
    }


    private AtlasLoader createAtlasLoader() {
        String path = "fog_of_war/";
        return new AtlasLoader(path + "atlas_texture.png", path + "atlas_structure.txt", false);
    }


    private void renderBlocks() {
        if (FOG_AS_MASK) return;

        renderSingleBlock(fogOfWarManager.topBlock);
        renderSingleBlock(fogOfWarManager.rightBlock);
        renderSingleBlock(fogOfWarManager.bottomBlock);
        renderSingleBlock(fogOfWarManager.leftBlock);
    }


    private void renderSingleBlock(RectangleYio block) {
        GraphicsYio.drawByRectangle(
                batchMovable,
                gameView.blackPixel,
                block
        );
    }


    private void renderDebug() {
        renderVisibleFogPoints();
        renderVisibleArea();
        renderDebugSlices();
    }


    private void renderDebugSlices() {
        for (FogSlice viewSlice : fogOfWarManager.viewSlices) {
            GraphicsYio.drawLine(
                    viewSlice.bottomPoint.position,
                    viewSlice.topPoint.position,
                    GraphicsYio.borderThickness,
                    batchMovable,
                    whitePixel
            );
        }


        MenuViewYio menuViewYio = gameController.yioGdxGame.menuViewYio;
        shapeRenderer = menuViewYio.shapeRenderer;
        shapeRenderer.setProjectionMatrix(gameView.orthoCam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        hexSize = fogOfWarManager.fieldController.hexSize;
        hexStep1 = fogOfWarManager.fieldController.hexStep1;
        PointYio pos;

        for (FogSlice viewSlice : fogOfWarManager.viewSlices) {
            for (FogPoint point : viewSlice.points) {
                pos = point.position;
                shapeRenderer.rect(
                        pos.x - hexSize / 2, pos.y - hexStep1 / 2,
                        hexSize, hexStep1
                );
                shapeRenderer.triangle(
                        pos.x + hexSize / 2, pos.y - hexStep1 / 2,
                        pos.x + hexSize / 2, pos.y + hexStep1 / 2,
                        pos.x + hexSize, pos.y
                );
                shapeRenderer.triangle(
                        pos.x - hexSize / 2, pos.y - hexStep1 / 2,
                        pos.x - hexSize / 2, pos.y + hexStep1 / 2,
                        pos.x - hexSize, pos.y
                );
            }
        }

        shapeRenderer.end();

        shapeRenderer.setProjectionMatrix(menuViewYio.orthoCam.combined);
    }


    private void renderVisibleFogPoints() {
        for (FogPoint fogPoint : fogOfWarManager.fogMap.values()) {
            if (fogPoint.status) continue;

            GraphicsYio.drawFromCenter(
                    batchMovable,
                    whitePixel,
                    fogPoint.position.x,
                    fogPoint.position.y,
                    size / 15
            );
        }
    }


    private void renderVisibleArea() {
        GraphicsYio.renderBorder(
                fogOfWarManager.visibleArea,
                batchMovable,
                whitePixel
        );
    }


    private void renderSingleFogPoint(FogPoint fogPoint) {
        GraphicsYio.drawFromCenter(
                batchMovable,
                fogTexture.getTexture(gameView.currentZoomQuality),
                fogPoint.position.x,
                fogPoint.position.y,
                size
        );
    }


    @Override
    public void disposeTextures() {
        whitePixel.getTexture().dispose();

        if (!FOG_AS_MASK) {
            smallAtlasLoader.disposeAtlasRegion();
        }
    }
}
