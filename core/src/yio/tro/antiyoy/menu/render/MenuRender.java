package yio.tro.antiyoy.menu.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.gameplay.game_view.GameView;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuViewYio;
import yio.tro.antiyoy.menu.RenderLoadingScreenElement;

import java.util.ArrayList;
import java.util.ListIterator;

public abstract class MenuRender {

    static ArrayList<MenuRender> list = new ArrayList<>();

    public static RenderLevelSelector renderLevelSelector = new RenderLevelSelector();
    public static RenderSpeedPanel renderSpeedPanel = new RenderSpeedPanel();
    public static RenderFastConstructionPanel renderFastConstructionPanel = new RenderFastConstructionPanel();
    public static RenderFireworksElement renderFireworksElement = new RenderFireworksElement();
    public static RenderNotificationElement renderNotificationElement = new RenderNotificationElement();
    public static RenderSaveSlotSelector renderSaveSlotSelector = new RenderSaveSlotSelector();
    public static RenderSlider renderSlider = new RenderSlider();
    public static RenderDiplomacyElement renderDiplomacyElement = new RenderDiplomacyElement();
    public static RenderDiplomaticDialog renderDiplomaticDialog = new RenderDiplomaticDialog();
    public static RenderScrollableList renderScrollableList = new RenderScrollableList();
    public static RenderSpecialThanksDialog renderSpecialThanksDialog = new RenderSpecialThanksDialog();
    public static RenderTurnStartDialog renderTurnStartDialog = new RenderTurnStartDialog();
    public static RenderDiplomaticLogPanel renderDiplomaticLogPanel = new RenderDiplomaticLogPanel();
    public static RenderBasicKeyboardElement renderBasicKeyboardElement = new RenderBasicKeyboardElement();
    public static RenderContextListMenuElement renderContextListMenuElement = new RenderContextListMenuElement();
    public static RenderShadow renderShadow = new RenderShadow();
    public static RenderTextViewElement renderTextViewElement = new RenderTextViewElement();
    public static RenderLoadingScreenElement renderLoadingScreenElement = new RenderLoadingScreenElement();
    public static RenderCheckButton renderCheckButton = new RenderCheckButton();
    public static RenderNativeKeyboard renderNativeKeyboard = new RenderNativeKeyboard();
    public static RenderCustomizableList renderCustomizableList = new RenderCustomizableList();
    public static RenderSingleListItem renderSingleListItem = new RenderSingleListItem();
    public static RenderTitleListItem renderTitleListItem = new RenderTitleListItem();
    public static RenderSkinListItem renderSkinListItem = new RenderSkinListItem();
    public static RenderTextLabel renderTextLabel = new RenderTextLabel();
    public static RenderRelationListItem renderRelationListItem = new RenderRelationListItem();
    public static RenderAddEditorRelationElement renderAddEditorRelationElement = new RenderAddEditorRelationElement();
    public static RenderColorPickerElement renderColorPickerElement = new RenderColorPickerElement();
    public static RenderEditLandElement renderEditLandElement = new RenderEditLandElement();
    public static RenderIncomeGraphElement renderIncomeGraphElement = new RenderIncomeGraphElement();
    public static RenderColorHolderElement renderColorHolderElement = new RenderColorHolderElement();
    public static RenderNothing renderNothing = new RenderNothing();
    public static RenderMoneyViewElement renderMoneyViewElement = new RenderMoneyViewElement();
    public static RenderProfitDetailItem renderProfitDetailItem = new RenderProfitDetailItem();
    public static RenderReplayListItem renderReplayListItem = new RenderReplayListItem();
    public static RenderSimpleDipEntityItem renderSimpleDipEntityItem = new RenderSimpleDipEntityItem();

    protected MenuViewYio menuViewYio;
    protected SpriteBatch batch;
    protected Color c;
    public float w, h, shadowOffset;


    public MenuRender() {
        ListIterator iterator = list.listIterator();
        iterator.add(this);
    }


    public static void updateRenderSystems(MenuViewYio menuViewYio) {
        for (MenuRender menuRender : list) {
            menuRender.update(menuViewYio);
        }
    }


    void update(MenuViewYio menuViewYio) {
        this.menuViewYio = menuViewYio;
        batch = menuViewYio.batch;
        c = batch.getColor();
        w = menuViewYio.w;
        h = menuViewYio.h;
        shadowOffset = (int) (0.01 * h);
        loadTextures();
    }


    public abstract void loadTextures();


    public abstract void renderFirstLayer(InterfaceElement element);


    public abstract void renderSecondLayer(InterfaceElement element);


    public abstract void renderThirdLayer(InterfaceElement element);


    public GameView getGameView() {
        return menuViewYio.yioGdxGame.gameView;
    }


    protected TextureRegion getBlackPixel() {
        return getGameView().texturesManager.blackPixel;
    }
}
