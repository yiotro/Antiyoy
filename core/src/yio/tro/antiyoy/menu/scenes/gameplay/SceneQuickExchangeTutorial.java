package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.diplomatic_exchange.QuickExchangeTutorialElement;

public class SceneQuickExchangeTutorial extends AbstractModalScene{

    public QuickExchangeTutorialElement quickExchangeTutorialElement;


    public SceneQuickExchangeTutorial(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        quickExchangeTutorialElement = null;
    }


    @Override
    public void create() {
        createQuickTutorialElement();
    }


    private void createQuickTutorialElement() {
        initQuickTutorialElement();
        quickExchangeTutorialElement.appear();
    }


    private void initQuickTutorialElement() {
        if (quickExchangeTutorialElement != null) return;

        quickExchangeTutorialElement = new QuickExchangeTutorialElement(menuControllerYio);
        quickExchangeTutorialElement.setAnimation(Animation.down);
        quickExchangeTutorialElement.setPosition(generateRectangle(0, 0, 1, 1));
        menuControllerYio.addElementToScene(quickExchangeTutorialElement);
    }


    @Override
    public void hide() {
        if (quickExchangeTutorialElement != null) {
            quickExchangeTutorialElement.destroy();
        }
    }
}
