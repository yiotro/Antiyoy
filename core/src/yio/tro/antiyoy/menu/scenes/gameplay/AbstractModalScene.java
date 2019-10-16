package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.InvisibleCloseElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.AbstractScene;

public abstract class AbstractModalScene extends AbstractScene{

    protected InvisibleCloseElement invisibleCloseElement;


    public AbstractModalScene(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        invisibleCloseElement = null;
    }


    @Override
    public abstract void create(); // for order in children classes


    public abstract void hide();


    protected void createInvisibleCloseButton(Reaction reaction) {
        initInvisibleCloseElement(reaction);
        invisibleCloseElement.appear();
    }


    private void initInvisibleCloseElement(Reaction reaction) {
        if (invisibleCloseElement != null) return;
        invisibleCloseElement = new InvisibleCloseElement(menuControllerYio);
        invisibleCloseElement.setPosition(generateRectangle(0, 0, 1, 1));
        invisibleCloseElement.setAnimation(Animation.none);
        invisibleCloseElement.setReaction(reaction);
        menuControllerYio.addElementToScene(invisibleCloseElement);
    }


    @Override
    protected void destroyByIndex(int startIndex, int endIndex) {
        super.destroyByIndex(startIndex, endIndex);
        if (invisibleCloseElement != null) {
            invisibleCloseElement.destroy();
        }
    }
}
