package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.OneTimeInfo;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.diplomatic_exchange.ExchangeUiElement;
import yio.tro.antiyoy.menu.scenes.AbstractScene;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class SceneDiplomaticExchange extends AbstractModalScene{

    public ExchangeUiElement exchangeUiElement;
    private Reaction rbHide;
    AbstractScene parentScene;


    public SceneDiplomaticExchange(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        exchangeUiElement = null;
        initReactions();
    }


    @Override
    public void create() {
        createInvisibleCloseButton(rbHide);
        createExchangeUiElement();

        parentScene = null;
    }


    public void checkToShowQuickTutorial() {
        if (OneTimeInfo.getInstance().quickExchangeTutorial) return;
        if (exchangeUiElement == null) return;

        OneTimeInfo.getInstance().quickExchangeTutorial = true;
        OneTimeInfo.getInstance().save();

        Scenes.sceneQuickExchangeTutorial.create();
        Scenes.sceneQuickExchangeTutorial.quickExchangeTutorialElement.setTargetElement(exchangeUiElement);
    }


    private void createExchangeUiElement() {
        initExchangeUiElement();
        exchangeUiElement.appear();
    }


    private void initExchangeUiElement() {
        if (exchangeUiElement != null) return;

        exchangeUiElement = new ExchangeUiElement(menuControllerYio);
        exchangeUiElement.setAnimation(Animation.down);
        exchangeUiElement.setPosition(generateRectangle(0, 0.12, 1, 0.5)); // height will be updated later
        menuControllerYio.addElementToScene(exchangeUiElement);

        exchangeUiElement.resetData();
    }


    private void initReactions() {
        rbHide = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onHideButtonPressed();
            }
        };
    }


    private void onHideButtonPressed() {
        hide();
        restoreParentScene();
    }


    private void restoreParentScene() {
        if (parentScene == null) return;
        parentScene.create();
    }


    public void setParentScene(AbstractScene parentScene) {
        this.parentScene = parentScene;
    }


    @Override
    public void hide() {
        if (exchangeUiElement != null) {
            exchangeUiElement.destroy();
        }
        if (invisibleCloseElement != null) {
            invisibleCloseElement.destroy();
        }
    }
}
