package yio.tro.antiyoy.menu.diplomatic_exchange;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeType;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.gameplay.touch_mode.TouchMode;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.menu.scenes.gameplay.choose_entity.IDipEntityReceiver;
import yio.tro.antiyoy.stuff.*;

public class AvWarDeclaration extends AbstractExchangeArgumentView implements IDipEntityReceiver{

    public RenderableTextYio title;
    public DiplomaticEntity victim;


    public AvWarDeclaration() {
        super();
    }


    @Override
    protected void init() {
        victim = null;
        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        updateTitle();
    }


    void updateTitle() {
        String prefix = LanguagesManager.getInstance().getString("state") + ": ";
        String postfix;
        if (victim == null) {
            postfix = " [" + LanguagesManager.getInstance().getString("choose") + "]";
        } else {
            postfix = victim.getName();
        }
        title.setString(prefix + postfix);
        title.updateMetrics();
    }


    @Override
    public ExchangeType getExchangeType() {
        return ExchangeType.war_declaration;
    }


    @Override
    public float getHeight() {
        return 0.12f * GraphicsYio.height;
    }


    @Override
    void move() {
        title.centerVertical(position);
        title.centerHorizontal(position);
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
        if (isInReadMode()) {
            Province targetProvince = getGameController().fieldManager.getBiggestProvince(victim.fraction);
            if (targetProvince == null) return;
            GameController gameController = getGameController();

            Scenes.sceneDiplomaticExchange.hide();
            Scenes.sceneDiplomaticRelations.create();
            Scenes.sceneDiplomaticRelations.setChosenFraction(victim.fraction);
            Scenes.sceneDiplomaticRelations.setParentScene(null);

            if (GameRules.fogOfWarEnabled) {
                Scenes.sceneDiplomaticRelations.setParentScene(Scenes.sceneDiplomaticExchange);
            } else {
                gameController.setTouchMode(TouchMode.tmShowChosenHexes);
                TouchMode.tmShowChosenHexes.highlightHexList(targetProvince.hexList);
                TouchMode.tmShowChosenHexes.setParentScene(Scenes.sceneDiplomaticExchange);
                gameController.cameraController.focusOnHexList(targetProvince.hexList);
            }
        } else {
            Scenes.sceneChooseDiplomaticEntity.create();
            Scenes.sceneChooseDiplomaticEntity.setiDipEntityReceiver(this);
            Scenes.sceneChooseDiplomaticEntity.loadValues();
            Scenes.sceneChooseDiplomaticEntity.excludeEntity(exchangeProfitView.diplomaticEntity);
        }
    }


    @Override
    public void onDiplomaticEntityChosen(DiplomaticEntity entity) {
        victim = entity;
        updateTitle();
    }


    @Override
    public boolean canDiplomaticEntityBeChosen(DiplomaticEntity entity) {
        return false;
    }


    @Override
    public boolean isApplyAllowed() {
        return victim != null;
    }


    @Override
    boolean isSelectionAllowed() {
        return victim == null || isInReadMode();
    }
}
