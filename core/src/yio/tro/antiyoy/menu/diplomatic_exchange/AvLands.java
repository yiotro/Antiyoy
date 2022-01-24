package yio.tro.antiyoy.menu.diplomatic_exchange;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeType;
import yio.tro.antiyoy.gameplay.touch_mode.TouchMode;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.*;

import java.util.ArrayList;

public class AvLands extends AbstractExchangeArgumentView{

    public RenderableTextYio title;
    public ArrayList<Hex> hexList;


    public AvLands() {
        super();
    }


    @Override
    protected void init() {
        hexList = new ArrayList<>();
        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        updateTitle();
    }


    void updateTitle() {
        String prefix = LanguagesManager.getInstance().getString("lands") + " x" + hexList.size();
        String postfix = "";
        if (!isInReadMode() && isSelectionAllowed()) {
            postfix = " [" + LanguagesManager.getInstance().getString("choose") + "]";
        }
        if (isInReadMode()) {
            postfix = "[" + LanguagesManager.getInstance().getString("show") + "]";
        }
        title.setString(prefix + postfix);
        title.updateMetrics();
    }


    @Override
    public ExchangeType getExchangeType() {
        return ExchangeType.lands;
    }


    @Override
    public float getHeight() {
        return 0.08f * GraphicsYio.height;
    }


    @Override
    void move() {
        title.centerVertical(exchangeProfitView.position);
        title.centerHorizontal(exchangeProfitView.position);
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

    }


    @Override
    void onTouchDrag(PointYio touchPoint) {

    }


    @Override
    void onTouchUp(PointYio touchPoint) {

    }


    @Override
    void onClick(PointYio touchPoint) {
        Scenes.sceneDiplomaticExchange.hide();
        if (isInReadMode()) {
            GameController gameController = getGameController();
            gameController.setTouchMode(TouchMode.tmShowChosenHexes);
            TouchMode.tmShowChosenHexes.highlightHexList(hexList);
            TouchMode.tmShowChosenHexes.setParentScene(Scenes.sceneDiplomaticExchange);
            gameController.cameraController.focusOnHexList(hexList);
        } else {
            int fraction = exchangeProfitView.diplomaticEntity.fraction;
            getDiplomacyManager().enableAreaSelectionMode(fraction);
            getDiplomacyManager().doAreaSelectRandomHex();
        }
    }


    @Override
    boolean isSelectionAllowed() {
        return hexList.size() == 0 || isInReadMode();
    }


    @Override
    public boolean isApplyAllowed() {
        return hexList.size() > 0;
    }


    @Override
    public boolean isExchangeTypeTitleHidden() {
        return true;
    }
}
