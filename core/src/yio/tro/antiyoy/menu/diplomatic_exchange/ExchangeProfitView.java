package yio.tro.antiyoy.menu.diplomatic_exchange;

import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeType;
import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeTypeListener;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.*;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

import java.util.ArrayList;

public class ExchangeProfitView implements ExchangeTypeListener, EncodeableYio, ReusableYio {

    ExchangeUiElement exchangeUiElement;
    public boolean incoming;
    float verticalDelta;
    public RectangleYio position;
    public ExchangeType exchangeType;
    public CircleYio arrowPosition;
    public RenderableTextYio title;
    public SelectionEngineYio selectionEngineYio;
    private boolean ready;
    public AbstractExchangeArgumentView argumentView;
    public DiplomaticEntity diplomaticEntity;


    public ExchangeProfitView(ExchangeUiElement exchangeUiElement) {
        this.exchangeUiElement = exchangeUiElement;
        position = new RectangleYio();
        arrowPosition = new CircleYio();
        selectionEngineYio = new SelectionEngineYio();

        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);

        reset();
    }


    @Override
    public void reset() {
        incoming = false;
        verticalDelta = 0;
        exchangeType = null;
        argumentView = null;
        diplomaticEntity = null;

        position.reset();
        arrowPosition.reset();

        arrowPosition.setRadius(0.02f * GraphicsYio.height);
    }


    void move() {
        updatePosition();
        updateArrowPosition();
        moveTitle();
        selectionEngineYio.move();
        argumentView.moveView();
    }


    private void moveTitle() {
        title.position.x = GraphicsYio.width / 2 - title.width / 2;
        title.position.y = (float) (position.y + position.height - 0.01f * GraphicsYio.height);
        title.updateBounds();
    }


    private void updateArrowPosition() {
        arrowPosition.center.x = (float) (position.x + arrowPosition.radius);
        arrowPosition.center.y = (float) (position.y + position.height - arrowPosition.radius);
    }


    protected void onAppear() {
        ready = false;
        argumentView.onAppear();
    }


    protected void onDestroy() {
        argumentView.onDestroy();
    }


    void onTouchDown(PointYio touchPoint) {
        if (!isTouchedBy(touchPoint)) return;
        if (argumentView != null && argumentView.isSelectionAllowed()) {
            selectionEngineYio.select();
        }
        argumentView.onTouchDown(touchPoint);
    }


    void onTouchDrag(PointYio touchPoint) {
        argumentView.onTouchDrag(touchPoint);
    }


    void onTouchUp(PointYio touchPoint) {
        argumentView.onTouchUp(touchPoint);
    }


    void onClick(PointYio touchPoint) {
        if (!isTouchedBy(touchPoint)) return;
        if (argumentView != null && argumentView.isSelectionAllowed()) {
            ready = true;
            SoundManagerYio.playSound(SoundManagerYio.soundPressButton);
            argumentView.onClick(touchPoint);
        }
    }


    boolean checkToPerformAction() {
        if (!ready) return false;
        ready = false;
        applyTouchReaction();
        return true;
    }


    private void applyTouchReaction() {
        switch (exchangeType) {
            default:
                break;
            case nothing:
                Scenes.sceneChooseExchangeType.setGiveMode(!incoming);
                Scenes.sceneChooseExchangeType.create();
                Scenes.sceneChooseExchangeType.setListener(this);
                break;
        }
    }


    @Override
    public void onExchangeTypeChosen(ExchangeType exchangeType) {
        setExchangeType(exchangeType);
        exchangeUiElement.updateSize();
    }


    private boolean isTouchedBy(PointYio touchPoint) {
        return position.isPointInside(touchPoint);
    }


    private void updatePosition() {
        position.x = GraphicsYio.width / 2 - position.width / 2;

        RectangleYio src = exchangeUiElement.viewPosition;
        if (incoming) {
            position.y = src.y + src.height - verticalDelta - position.height;
        } else {
            position.y = src.y + verticalDelta;
        }
    }


    public void resetExchangeType() {
        setExchangeType(ExchangeType.nothing);
    }


    public void setExchangeType(ExchangeType type) {
        exchangeType = type;

        title.setString(LanguagesManager.getInstance().getString("" + exchangeType));
        title.updateMetrics();
        moveTitle();

        updateArgumentView();
    }


    void updateArgumentView() {
        if (argumentView != null && argumentView.getExchangeType() == exchangeType) return;

        if (argumentView != null) {
            argumentView.onDestroy();
            ArgumentViewFactory.getInstance().onArgumentViewDestroyed(argumentView);
        }

        argumentView = ArgumentViewFactory.getInstance().createArgumentView(this, exchangeType);
        argumentView.onAppear();
    }


    void updateSize() {
        position.width = 0.95f * GraphicsYio.width;
        position.height = 0.2f * GraphicsYio.height;

        if (argumentView != null) {
            position.height = argumentView.getHeight();
        }
    }


    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }


    public ExchangeProfitView getOppositeView() {
        return exchangeUiElement.getOppositeView(this);
    }


    public void setDiplomaticEntity(DiplomaticEntity diplomaticEntity) {
        this.diplomaticEntity = diplomaticEntity;
    }


    public void setVerticalDelta(float verticalDelta) {
        this.verticalDelta = verticalDelta;
    }


    @Override
    public String encode() {
        StringBuilder builder = new StringBuilder();
        builder.append(exchangeType).append(" ");
        switch (exchangeType) {
            default:
            case stop_war:
            case remove_black_mark:
            case nothing:
                break;
            case money:
                AvMoney avMoney = (AvMoney) argumentView;
                builder.append(avMoney.slider.getActualValue());
                break;
            case dotations:
                AvDotations avDotations = (AvDotations) argumentView;
                int moneyValue = avDotations.moneySlider.getActualValue();
                int durationValue = avDotations.durationSlider.getActualValue();
                builder.append(moneyValue).append(" ").append(durationValue);
                break;
            case war_declaration:
                AvWarDeclaration avWarDeclaration = (AvWarDeclaration) argumentView;
                builder.append(avWarDeclaration.victim.fraction);
                break;
            case lands:
                AvLands avLands = (AvLands) argumentView;
                String convertedList = avLands.getDiplomacyManager().convertHexListToString(avLands.hexList);
                builder.append(convertedList);
                break;
            case friendship:
                AvFriendship avFriendship = (AvFriendship) argumentView;
                builder.append(avFriendship.slider.getActualValue());
                break;
        }
        return builder.toString();
    }


    @Override
    public void decode(String source) {
        String[] split = source.split(" ");
        ExchangeType type = ExchangeType.valueOf(split[0]);
        setExchangeType(type);

        int moneyValue;
        int durationValue;
        int fraction;
        MenuControllerYio menuControllerYio = exchangeUiElement.menuControllerYio;
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
        switch (type) {
            default:
            case stop_war:
            case remove_black_mark:
            case nothing:
                break;
            case money:
                AvMoney avMoney = (AvMoney) argumentView;
                moneyValue = Integer.valueOf(split[1]);
                avMoney.setTitle(moneyValue);
                break;
            case dotations:
                AvDotations avDotations = (AvDotations) argumentView;
                moneyValue = Integer.valueOf(split[1]);
                durationValue = Integer.valueOf(split[2]);
                avDotations.setTitle(moneyValue, durationValue);
                break;
            case war_declaration:
                AvWarDeclaration avWarDeclaration = (AvWarDeclaration) argumentView;
                fraction = Integer.valueOf(split[1]);
                avWarDeclaration.victim = diplomacyManager.getEntity(fraction);
                avWarDeclaration.updateTitle();
                break;
            case lands:
                AvLands avLands = (AvLands) argumentView;
                ArrayList<Hex> hexList = diplomacyManager.convertStringToHexList(split[1]);
                avLands.hexList.clear();
                avLands.hexList.addAll(hexList);
                avLands.updateTitle();
                break;
            case friendship:
                AvFriendship avFriendship = (AvFriendship) argumentView;
                durationValue = Integer.valueOf(split[1]);
                avFriendship.setTitle(durationValue);
                break;
        }
    }
}
