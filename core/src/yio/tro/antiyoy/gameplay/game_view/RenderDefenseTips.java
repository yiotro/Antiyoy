package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.Color;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.stuff.CircleYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;

import java.util.ArrayList;

public class RenderDefenseTips extends GameRender{

    CircleYio iconCircle;


    public RenderDefenseTips(GameRendersList gameRendersList) {
        super(gameRendersList);

        iconCircle = new CircleYio();
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        float f = gameController.fieldManager.defenseTipFactor.get();
        if (f == 0) return;

        ArrayList<Hex> defenseTips = gameController.fieldManager.defenseTips;
        if (defenseTips.size() == 0) return;

        Color c = batchMovable.getColor();
        float a = c.a;
        batchMovable.setColor(c.r, c.g, c.b, f);
        for (Hex defenseTip : defenseTips) {
            PointYio tipPos = defenseTip.getPos();
            PointYio srcPos;

            Hex defSrcHex = gameController.selectionManager.getDefSrcHex(defenseTip);
            srcPos = updateSrcPos(defSrcHex);

            updateIconCircle(f, tipPos, srcPos);
            GraphicsYio.drawByCircle(batchMovable, gameView.texturesManager.defenseIcon, iconCircle);
        }
        batchMovable.setColor(c.r, c.g, c.b, a);
    }


    private void updateIconCircle(float f, PointYio tipPos, PointYio srcPos) {
        if (gameController.fieldManager.defenseTipFactor.getDy() >= 0) {
            iconCircle.set(
                    srcPos.x + f * (tipPos.x - srcPos.x),
                    srcPos.y + f * (tipPos.y - srcPos.y),
                    (0.5f + 0.1f * f) * gameView.hexViewSize
            );
            return;
        }

        iconCircle.set(
                tipPos.x,
                tipPos.y,
                (0.7f - 0.1f * f) * gameView.hexViewSize
        );
    }


    private PointYio updateSrcPos(Hex defSrcHex) {
        PointYio cPos;
        if (defSrcHex != null) {
            cPos = defSrcHex.getPos();
        } else {
            cPos = gameController.fieldManager.defTipHex.getPos();
        }
        return cPos;
    }


    @Override
    public void disposeTextures() {

    }
}
