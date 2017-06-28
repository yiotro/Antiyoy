package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.RectangleYio;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneLanguageMenu extends AbstractScene{


    public SceneLanguageMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        menuControllerYio.spawnBackButton(330, ReactBehavior.rbMoreSettings);

        double buttonHeight = 0.07;
        int langNumber = 11;
        RectangleYio base = new RectangleYio(0.1, 0.5 * (0.9 - buttonHeight * langNumber), 0.8, buttonHeight * langNumber);
        double y = base.y + base.height;

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(base.x, base.y, base.width, base.height), 331, " ");
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio engButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 332, "English");
        engButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        engButton.setShadow(false);
        engButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio rusButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 333, "Russian");
        rusButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        rusButton.setShadow(false);
        rusButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio uaButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 334, "Ukrainian");
        uaButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        uaButton.setShadow(false);
        uaButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio gerButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 335, "German");
        gerButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        gerButton.setShadow(false);
        gerButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio czeButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 336, "Czech");
        czeButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        czeButton.setShadow(false);
        czeButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio polButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 337, "Polish");
        polButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        polButton.setShadow(false);
        polButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio itaButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 338, "Italian");
        itaButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        itaButton.setShadow(false);
        itaButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio freButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 340, "French");
        freButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        freButton.setShadow(false);
        freButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio spButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 341, "Spanish");
        spButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        spButton.setShadow(false);
        spButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio skButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 342, "Slovak");
        skButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        skButton.setShadow(false);
        skButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio chButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 343, "Chinese");
        chButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        chButton.setShadow(false);
        chButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        menuControllerYio.endMenuCreation();
    }
}