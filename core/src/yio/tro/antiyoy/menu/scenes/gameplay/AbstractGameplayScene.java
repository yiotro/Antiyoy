package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.scenes.AbstractScene;

public abstract class AbstractGameplayScene extends AbstractScene{

    public AbstractGameplayScene(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public abstract void create(); // for order in children classes


    public abstract void hide();
}
