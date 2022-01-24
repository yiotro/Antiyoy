package yio.tro.antiyoy.menu.diplomatic_exchange;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.diplomacy.*;
import yio.tro.antiyoy.menu.AbstractRectangularUiElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.*;

import java.util.ArrayList;

public class ExchangeUiElement extends AbstractRectangularUiElement {

    public DiplomaticEntity mainEntity;
    public DiplomaticEntity targetEntity;
    public ExchangeProfitView topView;
    public ExchangeProfitView bottomView;
    public RenderableTextYio topName;
    public RenderableTextYio bottomName;
    public ArrayList<ExUiButton> buttons;
    public boolean readMode;
    DiplomaticMessage diplomaticMessage;
    private float buttonSectionHeight;
    public CircleYio relationIconPosition;
    public int relation;


    public ExchangeUiElement(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        buttonSectionHeight = 0.06f * GraphicsYio.height;
        relationIconPosition = new CircleYio();
        relation = -1;

        topView = new ExchangeProfitView(this);
        topView.setVerticalDelta(0.06f * GraphicsYio.height);
        topView.setIncoming(true);

        bottomView = new ExchangeProfitView(this);
        bottomView.setVerticalDelta(buttonSectionHeight + 0.06f * GraphicsYio.height);

        topName = new RenderableTextYio();
        topName.setFont(Fonts.smallerMenuFont);

        bottomName = new RenderableTextYio();
        bottomName.setFont(Fonts.smallerMenuFont);

        createButtons();
        readMode = false;
    }


    private void createButtons() {
        buttons = new ArrayList<>();

        ExUiButton okButton = new ExUiButton(this);
        okButton.setTitle(LanguagesManager.getInstance().getString("accept"));
        okButton.setActionType(ExUiActionType.apply);
        buttons.add(okButton);

        ExUiButton refuseButton = new ExUiButton(this);
        refuseButton.setTitle(LanguagesManager.getInstance().getString("refuse"));
        refuseButton.setActionType(ExUiActionType.refuse);
        buttons.add(refuseButton);
    }


    @Override
    protected void onMove() {
        topView.move();
        bottomView.move();
        moveTopName();
        moveBottomName();
        moveButtons();
        moveRelationIcon();
    }


    private void moveRelationIcon() {
        relationIconPosition.center.x = topName.position.x + topName.width + 0.05f * GraphicsYio.width;
        relationIconPosition.center.y = topName.position.y - topName.height / 2;
        relationIconPosition.radius = 0.015f * GraphicsYio.height;
    }


    private void moveButtons() {
        for (ExUiButton button : buttons) {
            button.move();
        }
    }


    private void moveBottomName() {
        bottomName.position.x = 0.025f * GraphicsYio.width;
        bottomName.position.y = (float) (viewPosition.y + bottomView.verticalDelta - 0.015f * GraphicsYio.height);
        bottomName.updateBounds();
    }


    private void moveTopName() {
        topName.position.x = 0.025f * GraphicsYio.width;
        topName.position.y = (float) (viewPosition.y + viewPosition.height - topView.verticalDelta + 0.015f * GraphicsYio.height + topName.height);
        topName.updateBounds();
    }


    @Override
    protected void onDestroy() {
        topView.onDestroy();
        bottomView.onDestroy();
    }


    @Override
    protected void onAppear() {
        topView.onAppear();
        bottomView.onAppear();
    }


    public void resetData() {
        diplomaticMessage = null;
        mainEntity = null;
        targetEntity = null;
        readMode = false;
        resetExchangeTypes();
    }


    public void updateSize() {
        topView.updateSize();
        bottomView.updateSize();

        double bottomPart = bottomView.verticalDelta + bottomView.position.height;
        float gap = 0.03f * GraphicsYio.height;
        double topPart = topView.position.height + topView.verticalDelta;
        position.height = bottomPart + gap + topPart;

        forceUpdateViewPosition();
    }


    public void setMainEntity(DiplomaticEntity mainEntity) {
        this.mainEntity = mainEntity;
        bottomName.setString(mainEntity.getName());
        bottomName.updateMetrics();
        bottomView.setDiplomaticEntity(mainEntity);
        updateRelation();
    }


    public void setTargetEntity(DiplomaticEntity targetEntity) {
        this.targetEntity = targetEntity;
        topName.setString(targetEntity.getName());
        topName.updateMetrics();
        topView.setDiplomaticEntity(targetEntity);
        updateRelation();
    }


    public void updateRelation() {
        relation = -1;
        if (mainEntity == null) return;
        if (targetEntity == null) return;
        relation = mainEntity.getRelation(targetEntity);
    }


    public void onAreaSelected(ArrayList<Hex> area) {
        if (area.size() == 0) return;
        int fraction = area.get(0).fraction;

        DiplomaticEntity entityByFraction = getEntityByFraction(fraction);
        if (entityByFraction == null) return;

        ExchangeProfitView profitViewByEntity = getProfitViewByEntity(entityByFraction);
        if (profitViewByEntity == null) return;

        if (!(profitViewByEntity.argumentView instanceof AvLands)) return;
        AvLands avLands = (AvLands) profitViewByEntity.argumentView;
        avLands.hexList.clear();
        avLands.hexList.addAll(area);
        avLands.updateTitle();
    }


    public ExchangeProfitView getProfitViewByEntity(DiplomaticEntity diplomaticEntity) {
        if (topView.diplomaticEntity == diplomaticEntity) return topView;
        if (bottomView.diplomaticEntity == diplomaticEntity) return bottomView;
        return null;
    }


    public DiplomaticEntity getEntityByFraction(int fraction) {
        if (mainEntity.fraction == fraction) return mainEntity;
        if (targetEntity.fraction == fraction) return targetEntity;
        return null;
    }


    public void setReadMode(boolean readMode) {
        this.readMode = readMode;
    }


    public void loadArgumentsFromMessage(DiplomaticMessage diplomaticMessage) {
        this.diplomaticMessage = diplomaticMessage;
        topView.decode(diplomaticMessage.arg2);
        bottomView.decode(diplomaticMessage.arg1);
    }


    void resetExchangeTypes() {
        topView.resetExchangeType();
        bottomView.resetExchangeType();
    }


    public ExchangeProfitView getOppositeView(ExchangeProfitView profitView) {
        if (profitView == topView) {
            return bottomView;
        }
        if (profitView == bottomView) {
            return topView;
        }
        return null;
    }


    @Override
    protected void onTouchDown() {
        for (ExUiButton button : buttons) {
            button.onTouchDown(currentTouch);
        }
        topView.onTouchDown(currentTouch);
        bottomView.onTouchDown(currentTouch);
    }


    @Override
    protected void onTouchDrag() {
        topView.onTouchDrag(currentTouch);
        bottomView.onTouchDrag(currentTouch);
    }


    @Override
    protected void onTouchUp() {
        topView.onTouchUp(currentTouch);
        bottomView.onTouchUp(currentTouch);
    }


    public void onExUiButtonPressed(ExUiButton exUiButton) {
        switch (exUiButton.actionType) {
            default:
                break;
            case apply:
                onApplyButtonPressed();
                break;
            case refuse:
                onRefuseButtonPressed();
                break;
        }
    }


    private void onRefuseButtonPressed() {
        removeCurrentMessageFromLog();
        Scenes.sceneDiplomaticExchange.hide();
        checkToReopenDiplomaticLog();
    }


    private void checkToReopenDiplomaticLog() {
        if (!getDiplomacyManager().log.hasSomethingToRead()) return;
        Scenes.sceneDiplomaticLog.create();
    }


    private void removeCurrentMessageFromLog() {
        if (diplomaticMessage == null) return;
        DiplomacyManager diplomacyManager = getDiplomacyManager();
        diplomacyManager.log.removeMessage(diplomaticMessage);
    }


    private DiplomacyManager getDiplomacyManager() {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        return gameController.fieldManager.diplomacyManager;
    }


    private void onApplyButtonPressed() {
        if (!topView.argumentView.isApplyAllowed()) return;
        if (!bottomView.argumentView.isApplyAllowed()) return;

        removeCurrentMessageFromLog();

        DiplomacyManager diplomacyManager = getDiplomacyManager();
        if (readMode) {
            diplomacyManager.exchangePerformer.apply(diplomaticMessage);
        } else {
            DiplomaticLog log = diplomacyManager.log;
            DiplomaticMessage diplomaticMessage = log.addMessage(DipMessageType.exchange, mainEntity, targetEntity);
            diplomaticMessage.setArg1(topView.encode());
            diplomaticMessage.setArg2(bottomView.encode());
            diplomacyManager.showLetterSentNotification();
        }

        Scenes.sceneDiplomaticExchange.hide();
        checkToReopenDiplomaticLog();
    }


    public void applyOptimalDotaions(int totalValue) {
        AvDotations avDotationsView = getAvDotationsView();
        if (avDotationsView == null) return;
        totalValue /= 12;
        avDotationsView.durationSlider.setIndexByActualValue(12);
        avDotationsView.moneySlider.setIndexByActualValue(totalValue);
    }


    private AvDotations getAvDotationsView() {
        if (topView.argumentView instanceof AvDotations) {
            return (AvDotations) topView.argumentView;
        }
        if (bottomView.argumentView instanceof AvDotations) {
            return (AvDotations) bottomView.argumentView;
        }
        return null;
    }


    @Override
    protected void onClick() {
        for (ExUiButton button : buttons) {
            button.onClick(currentTouch);
        }
        topView.onClick(currentTouch);
        bottomView.onClick(currentTouch);
    }


    @Override
    public boolean checkToPerformAction() {
        for (ExUiButton button : buttons) {
            if (button.checkToPerformAction()) return true;
        }
        if (topView.checkToPerformAction()) return true;
        if (bottomView.checkToPerformAction()) return true;
        return false;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderExchangeUiElement;
    }
}
