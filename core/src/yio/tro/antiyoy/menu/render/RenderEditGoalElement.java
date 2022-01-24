package yio.tro.antiyoy.menu.render;

import yio.tro.antiyoy.menu.EditGoalElement;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.RenderableTextYio;

public class RenderEditGoalElement extends MenuRender{


    private EditGoalElement editGoalElement;
    private float alpha;
    private RectangleYio viewPosition;
    RectangleYio tempRectangle;


    public RenderEditGoalElement() {
        tempRectangle = new RectangleYio();
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        editGoalElement = (EditGoalElement) element;
        alpha = editGoalElement.getAlpha();
        viewPosition = editGoalElement.viewPosition;

        GraphicsYio.setBatchAlpha(batch, 0.05 * alpha);
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), viewPosition);
        renderBlackTextOptimized(batch, getBlackPixel(), editGoalElement.title, alpha);
        renderBlackTextOptimized(batch, getBlackPixel(), editGoalElement.description, alpha);
        renderArgument();
        renderColorIcon();
        renderSelection();
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderColorIcon() {
        if (!editGoalElement.isColorIconNeeded()) return;
        GraphicsYio.setBatchAlpha(batch, alpha);
        tempRectangle.setBy(editGoalElement.colorIconPosition);
        GraphicsYio.renderBorder(batch, getBlackPixel(), tempRectangle);
        GraphicsYio.drawByCircle(
                batch,
                MenuRender.renderIncomeGraphElement.getPixelByColor(editGoalElement.chosenColor),
                editGoalElement.colorIconPosition
        );
    }


    private void renderArgument() {
        if (!editGoalElement.isStringArgumentNeeded()) return;
        RenderableTextYio argument = editGoalElement.argument;
        renderBlackTextOptimized(batch, getBlackPixel(), argument, alpha);
    }


    private void renderSelection() {
        GraphicsYio.setBatchAlpha(batch, editGoalElement.selectionEngineYio.getAlpha() * alpha);
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), editGoalElement.viewPosition);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
