package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.AbstractScene;

public abstract class AbstractModalScene extends AbstractScene{

    public AbstractModalScene(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public abstract void create(); // for order in children classes


    public abstract void hide();


    protected void createInvisibleCloseButton(int id, Reaction reaction) {
        ButtonYio invCloseButton = buttonFactory.getButton(generateRectangle(0, 0, 1, 1), id, null);
        invCloseButton.setRenderable(false);
        invCloseButton.setReaction(reaction);
        invCloseButton.tagAsBackButton();
    }

}
