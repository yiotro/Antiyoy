package yio.tro.antiyoy.menu.diplomatic_exchange;

import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeType;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.*;

public class AvFriendship extends AbstractExchangeArgumentView{

    public CustomArgViewSlider slider; // for write mode
    public RenderableTextYio title; // for read mode


    public AvFriendship() {
        super();
    }


    @Override
    protected void init() {
        slider = new CustomArgViewSlider(exchangeProfitView);
        slider.verticalDelta = 0.02f * GraphicsYio.height;
        slider.setMode(CavsMode.duration);
        slider.setValues(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        slider.setTitle(LanguagesManager.getInstance().getString("duration"));
        slider.setValueIndex(slider.values.length - 1);

        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
    }


    @Override
    public ExchangeType getExchangeType() {
        return ExchangeType.friendship;
    }


    public void setTitle(int duration) {
        title.setString(LanguagesManager.getInstance().getString("duration") + ": " + duration + "x");
        title.updateMetrics();
    }


    @Override
    public float getHeight() {
        if (isInReadMode()) {
            return 0.12f * GraphicsYio.height;
        }
        return 0.14f * GraphicsYio.height;
    }


    @Override
    void move() {
        slider.move();
        moveTitle();
    }


    private void moveTitle() {
        title.centerHorizontal(position);
        title.centerVertical(position);
        title.updateBounds();
    }


    @Override
    void onAppear() {

    }


    @Override
    void onDestroy() {

    }


    @Override
    void onTouchDown(PointYio touchPoint) {
        slider.onTouchDown(touchPoint);
    }


    @Override
    void onTouchDrag(PointYio touchPoint) {
        slider.onTouchDrag(touchPoint);
    }


    @Override
    void onTouchUp(PointYio touchPoint) {
        slider.onTouchUp(touchPoint);
    }


    @Override
    void onClick(PointYio touchPoint) {
        Scenes.sceneDiplomaticExchange.hide();
        Scenes.sceneDiplomaticRelations.create();
        int fraction = exchangeProfitView.exchangeUiElement.targetEntity.fraction;
        Scenes.sceneDiplomaticRelations.setChosenFraction(fraction);
        Scenes.sceneDiplomaticRelations.setParentScene(Scenes.sceneDiplomaticExchange);
    }


    @Override
    boolean isSelectionAllowed() {
        return isInReadMode();
    }
}
