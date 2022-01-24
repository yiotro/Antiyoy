package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.gameplay.ColorsManager;
import yio.tro.antiyoy.menu.GoalViewElement;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;

public class RenderGoalView extends MenuRender{


    private GoalViewElement goalViewElement;
    private float alpha;
    private RectangleYio viewPosition;
    RectangleYio tempRectangle;
    private TextureRegion backgroundTexture;


    public RenderGoalView() {
        tempRectangle = new RectangleYio();
    }


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("diplomacy/background.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        goalViewElement = (GoalViewElement) element;
        alpha = goalViewElement.getAlpha();
        viewPosition = goalViewElement.viewPosition;

        MenuRender.renderShadow.renderShadow(viewPosition, alpha);
        GraphicsYio.setBatchAlpha(batch, alpha);
        GraphicsYio.drawByRectangle(batch, backgroundTexture, viewPosition);
        renderBlackTextOptimized(batch, getBlackPixel(), goalViewElement.title, alpha);
        renderBlackTextOptimized(batch, getBlackPixel(), goalViewElement.description, alpha);
        renderArgument();
        renderColorIcon();
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderColorIcon() {
        if (!goalViewElement.isColorIconNeeded()) return;
        GraphicsYio.setBatchAlpha(batch, alpha);
        tempRectangle.setBy(goalViewElement.colorIconPosition);
        GraphicsYio.renderBorder(batch, getBlackPixel(), tempRectangle);
        ColorsManager colorsManager = getGameView().gameController.colorsManager;
        int colorByFraction = colorsManager.getColorByFraction(goalViewElement.chosenFraction);
        GraphicsYio.drawByCircle(
                batch,
                MenuRender.renderIncomeGraphElement.getPixelByColor(colorByFraction),
                goalViewElement.colorIconPosition
        );
    }


    private void renderArgument() {
        if (!goalViewElement.isStringArgumentNeeded()) return;
        RenderableTextYio argument = goalViewElement.argument;
        renderBlackTextOptimized(batch, getBlackPixel(), argument, alpha);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
