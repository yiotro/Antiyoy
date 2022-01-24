package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.gameplay.ColorsManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeType;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.diplomatic_exchange.*;
import yio.tro.antiyoy.stuff.*;

public class RenderExchangeUiElement extends MenuRender{


    private ExchangeUiElement exchangeUiElement;
    private TextureRegion mainBackgroundTexture;
    private TextureRegion redBackground;
    private TextureRegion greenBackground;
    PointYio one, two;
    RectangleYio increasedBounds;
    private TextureRegion upArrowTexture;
    private TextureRegion downArrowTexture;
    private TextureRegion okBackgroundTexture;
    private float alpha;
    CircleYio tempCircle;
    private TextureRegion blackCircleTexture;
    boolean highPerformanceMode;
    private TextureRegion refuseButtonTexture;


    public RenderExchangeUiElement() {
        one = new PointYio();
        two = new PointYio();
        increasedBounds = new RectangleYio();
        tempCircle = new CircleYio();
        highPerformanceMode = false;
    }


    @Override
    public void loadTextures() {
        mainBackgroundTexture = GraphicsYio.loadTextureRegion("diplomacy/background.png", false);
        redBackground = GraphicsYio.loadTextureRegion("diplomacy/exchange_red.png", false);
        greenBackground = GraphicsYio.loadTextureRegion("diplomacy/exchange_green.png", false);
        upArrowTexture = GraphicsYio.loadTextureRegion("diplomacy/exchange_up.png", false);
        downArrowTexture = GraphicsYio.loadTextureRegion("diplomacy/exchange_down.png", false);
        okBackgroundTexture = GraphicsYio.loadTextureRegion("button_background_3.png", false);
        blackCircleTexture = GraphicsYio.loadTextureRegion("menu/slider/black_circle.png", true);
        refuseButtonTexture = GraphicsYio.loadTextureRegion("button_background_1.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {
        if (element.getFactor().getGravity() >= 0) return;
        highPerformanceMode = true;
        renderElement(element);
        highPerformanceMode = false;
    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        if (element.getFactor().getGravity() < 0) return;
        renderElement(element);
    }


    private void renderElement(InterfaceElement element) {
        exchangeUiElement = (ExchangeUiElement) element;
        if (exchangeUiElement.getFactor().get() < 0.05) return;
        alpha = exchangeUiElement.getAlpha();

        renderShadow();
        renderBackground();
        renderProfitView(exchangeUiElement.topView);
        renderProfitView(exchangeUiElement.bottomView);
        renderName(exchangeUiElement.mainEntity, exchangeUiElement.bottomName);
        renderName(exchangeUiElement.targetEntity, exchangeUiElement.topName);
        renderRelationIcon();
        renderButtons();
    }


    private void renderRelationIcon() {
        if (alpha < 0.5) return;
        GraphicsYio.drawByCircle(
                batch,
                MenuRender.renderRelationListItem.getRelationTexture(exchangeUiElement.relation),
                exchangeUiElement.relationIconPosition
        );
    }


    private void renderButtons() {
        for (ExUiButton button : exchangeUiElement.buttons) {
            renderButton(button);
        }
    }


    private void renderButton(ExUiButton button) {
        if (!button.isVisible()) return;

        GraphicsYio.drawByRectangle(batch, getButtonBackgroundTexture(button), button.position);
        renderBlackText(button.title);

        if (button.selectionEngineYio.isSelected()) {
            GraphicsYio.setBatchAlpha(batch, alpha * button.selectionEngineYio.getAlpha());
            GraphicsYio.drawByRectangle(batch, getBlackPixel(), button.position);
            GraphicsYio.setBatchAlpha(batch, 1);
        }
    }


    private TextureRegion getButtonBackgroundTexture(ExUiButton button) {
        switch (button.actionType) {
            default:
            case apply:
                return okBackgroundTexture;
            case refuse:
                return refuseButtonTexture;
        }
    }


    private void renderName(DiplomaticEntity diplomaticEntity, RenderableTextYio renderableTextYio) {
        increasedBounds.setBy(renderableTextYio.bounds);
        increasedBounds.increase(0.01f * GraphicsYio.width);
        ColorsManager colorsManager = menuViewYio.yioGdxGame.gameController.colorsManager;
        int color = colorsManager.getColorByFraction(diplomaticEntity.fraction);
        GraphicsYio.drawByRectangle(
                batch,
                MenuRender.renderDiplomacyElement.getBackgroundPixelByColor(color),
                increasedBounds
        );

        renderBlackText(renderableTextYio);
    }


    private void renderBlackText(RenderableTextYio renderableTextYio) {
        if (highPerformanceMode) {
            GraphicsYio.setBatchAlpha(batch, 0.15 * alpha);
            GraphicsYio.drawByRectangle(batch, getBlackPixel(), renderableTextYio.bounds);
            GraphicsYio.setBatchAlpha(batch, 1);
            return;
        }

        Color fontBackupColor = renderableTextYio.font.getColor();
        renderableTextYio.font.setColor(Color.BLACK);
        GraphicsYio.renderTextOptimized(batch, getBlackPixel(), renderableTextYio, alpha);
        renderableTextYio.font.setColor(fontBackupColor);
    }


    private void renderProfitView(ExchangeProfitView profitView) {
        RectangleYio pos = profitView.position;

        renderProfitBackground(profitView, pos);
        renderProfitLeftLine(pos);
        renderArrow(profitView);
        renderProfitViewTitle(profitView);
        renderProfitSelection(profitView);
        renderArgumentView(profitView.argumentView);

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderProfitViewTitle(ExchangeProfitView profitView) {
        if (profitView.argumentView != null && profitView.argumentView.isExchangeTypeTitleHidden()) return;
        renderBlackText(profitView.title);
    }


    private void renderArgumentView(AbstractExchangeArgumentView argumentView) {
        ExchangeType exchangeType = argumentView.getExchangeType();
        if (exchangeType == null) return;

        switch (exchangeType) {
            default:
                break;
            case nothing:
                AvNothing avNothing = (AvNothing) argumentView;
                if (!exchangeUiElement.readMode) {
                    renderBlackText(avNothing.title);
                }
                break;
            case lands:
                AvLands avLands = (AvLands) argumentView;
                renderBlackText(avLands.title);
                break;
            case war_declaration:
                AvWarDeclaration avWarDeclaration = (AvWarDeclaration) argumentView;
                renderBlackText(avWarDeclaration.title);
                break;
            case money:
                AvMoney avMoney = (AvMoney) argumentView;
                if (exchangeUiElement.readMode) {
                    renderBlackText(avMoney.title);
                } else {
                    renderCustomArgViewSlider(avMoney.slider);
                }
                break;
            case dotations:
                AvDotations avDotations = (AvDotations) argumentView;
                if (exchangeUiElement.readMode) {
                    renderBlackText(avDotations.title);
                } else {
                    renderCustomArgViewSlider(avDotations.moneySlider);
                    renderCustomArgViewSlider(avDotations.durationSlider);
                }
                break;
            case friendship:
                AvFriendship avFriendship = (AvFriendship) argumentView;
                if (exchangeUiElement.readMode) {
                    renderBlackText(avFriendship.title);
                } else {
                    renderCustomArgViewSlider(avFriendship.slider);
                }
                break;
        }
    }


    void renderCustomArgViewSlider(CustomArgViewSlider customArgViewSlider) {
        GraphicsYio.drawLine(batch, getBlackPixel(), customArgViewSlider.leftLinePoint, customArgViewSlider.rightLinePoint, GraphicsYio.borderThickness);

        tempCircle.setRadius(2 * GraphicsYio.borderThickness);
        tempCircle.center.setBy(customArgViewSlider.leftLinePoint);
        GraphicsYio.drawByCircle(batch, blackCircleTexture, tempCircle);
        tempCircle.center.setBy(customArgViewSlider.rightLinePoint);
        GraphicsYio.drawByCircle(batch, blackCircleTexture, tempCircle);

        renderBlackText(customArgViewSlider.title);
        renderBlackText(customArgViewSlider.tag);
        GraphicsYio.drawByCircle(batch, blackCircleTexture, customArgViewSlider.accentPosition);
    }


    private void renderProfitSelection(ExchangeProfitView profitView) {
        if (!profitView.selectionEngineYio.isSelected()) return;
        GraphicsYio.setBatchAlpha(batch, alpha * profitView.selectionEngineYio.getAlpha());
        GraphicsYio.drawByRectangle(batch, getBlackPixel(), profitView.position);
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderArrow(ExchangeProfitView profitView) {
        GraphicsYio.drawByCircle(batch, getArrowTexture(profitView), profitView.arrowPosition);
    }


    private TextureRegion getArrowTexture(ExchangeProfitView profitView) {
        if (profitView.incoming) {
            return downArrowTexture;
        }
        return upArrowTexture;
    }


    private void renderProfitLeftLine(RectangleYio pos) {
        GraphicsYio.setBatchAlpha(batch, 0.33);
        one.set(pos.x, pos.y);
        two.set(pos.x, pos.y + pos.height);
        GraphicsYio.drawLine(batch, getBlackPixel(), one, two, GraphicsYio.borderThickness);
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderProfitBackground(ExchangeProfitView profitView, RectangleYio pos) {
        GraphicsYio.setBatchAlpha(batch, 0.25f * exchangeUiElement.getAlpha());
        GraphicsYio.drawByRectangle(batch, getBackgroundTexture(profitView), pos);
    }


    private TextureRegion getBackgroundTexture(ExchangeProfitView profitView) {
        if (profitView.incoming) {
            return greenBackground;
        }
        return redBackground;
    }


    private void renderBackground() {
        GraphicsYio.drawByRectangle(
                batch,
                mainBackgroundTexture,
                exchangeUiElement.viewPosition
        );
    }


    private void renderShadow() {
        MenuRender.renderShadow.disableInternalFillForOneDraw();
        MenuRender.renderShadow.renderShadow(exchangeUiElement.viewPosition, 1);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
