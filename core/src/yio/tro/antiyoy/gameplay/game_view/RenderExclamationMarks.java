package yio.tro.antiyoy.gameplay.game_view;

import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.PointYio;

public class RenderExclamationMarks extends GameRender{


    public RenderExclamationMarks(GameRendersList gameRendersList) {
        super(gameRendersList);
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        if (!gameController.isPlayerTurn()) return;

        for (Province province : gameController.fieldManager.provinces) {
            if (!gameController.isCurrentTurn(province.getFraction())) continue;
            if (province.money < GameRules.PRICE_UNIT) continue;

            Hex capitalHex = province.getCapital();
            PointYio pos = capitalHex.getPos();
            if (!isPosInViewFrame(pos, hexViewSize)) continue;

            batchMovable.draw(
                    gameView.texturesManager.exclamationMarkTexture,
                    pos.x - 0.5f * hexViewSize,
                    pos.y + 0.3f * hexViewSize + gameController.jumperUnit.jumpPos * hexViewSize,
                    0.35f * hexViewSize,
                    0.6f * hexViewSize
            );
        }
    }


    @Override
    public void disposeTextures() {

    }
}
