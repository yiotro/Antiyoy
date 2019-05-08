package yio.tro.antiyoy.gameplay.game_view;

import yio.tro.antiyoy.stuff.PointYio;

public class RenderSelectedUnit extends GameRender{

    private float ar;


    public RenderSelectedUnit(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        PointYio pos;
        if (gameController.selectionManager.selectedUnit == null) return;

        pos = gameController.selectionManager.selectedUnit.currentPos;
        ar = 0.35f * hexViewSize * gameController.selectionManager.selUnitFactor.get();

        batchMovable.draw(
                gameView.texturesManager.selUnitShadow,
                pos.x - 0.7f * hexViewSize - 2 * ar,
                pos.y - 0.6f * hexViewSize - 2 * ar,
                1.4f * hexViewSize + 4 * ar,
                1.6f * hexViewSize + 4 * ar
        );

        batchMovable.draw(
                gameView.texturesManager.manTextures[gameController.selectionManager.selectedUnit.strength - 1].getNormal(),
                pos.x - 0.7f * hexViewSize - ar,
                pos.y - 0.6f * hexViewSize - ar,
                1.4f * hexViewSize + 2 * ar,
                1.6f * hexViewSize + 2 * ar
        );
    }


    @Override
    public void disposeTextures() {

    }
}
