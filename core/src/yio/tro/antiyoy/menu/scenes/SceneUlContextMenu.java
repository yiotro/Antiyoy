package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.gameplay.user_levels.UserLevelsManager;
import yio.tro.antiyoy.menu.*;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.gameplay.AbstractModalScene;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneUlContextMenu extends AbstractModalScene{

    DownsidePanelElement downsidePanelElement;
    SimpleButtonElement sbHide;
    String key;


    public SceneUlContextMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        downsidePanelElement = null;
        sbHide = null;
    }


    @Override
    public void create() {
        key = null;
        createInvisibleCloseButton(getCloseReaction());
        createDownsidePanelElement();
        createSbHide();
    }


    private void createSbHide() {
        initSbHide();
        sbHide.appear();
    }


    private void initSbHide() {
        if (sbHide != null) return;
        sbHide = new SimpleButtonElement(menuControllerYio);
        sbHide.setParent(downsidePanelElement, 0.05);
        sbHide.setPosition(generateRectangle(0.25, 0, 0.5, 0.05));
        sbHide.setTouchOffset(0.04f);
        sbHide.setReaction(getHideReaction());
        sbHide.setTitle("hide");
        menuControllerYio.addElementToScene(sbHide);
    }


    private Reaction getHideReaction() {
        return new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                UserLevelsManager.getInstance().hideLevel(key);
                hide();
                Scenes.sceneUserLevels.onLevelBecameHidden();
            }
        };
    }


    private void createDownsidePanelElement() {
        initDownsidePanelElement();
        downsidePanelElement.appear();
    }


    private void initDownsidePanelElement() {
        if (downsidePanelElement != null) return;
        downsidePanelElement = new DownsidePanelElement(menuControllerYio);
        downsidePanelElement.setAnimation(Animation.down);
        downsidePanelElement.setPosition(generateRectangle(0, 0, 1, 0.15));
        menuControllerYio.addElementToScene(downsidePanelElement);
    }


    private Reaction getCloseReaction() {
        return new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                hide();
            }
        };
    }


    public void setKey(String key) {
        this.key = key;
    }


    @Override
    public void hide() {
        if (invisibleCloseElement != null) {
            invisibleCloseElement.destroy();
        }
        if (downsidePanelElement != null) {
            downsidePanelElement.destroy();
        }
        if (sbHide != null) {
            sbHide.destroy();
        }
    }
}
